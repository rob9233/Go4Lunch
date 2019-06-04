package robfernandes.xyz.go4lunch.ui.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.adapters.AutocompleteAdapter;
import robfernandes.xyz.go4lunch.model.EatingPlan;
import robfernandes.xyz.go4lunch.model.NearByPlaces;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;
import robfernandes.xyz.go4lunch.ui.activities.RestaurantActivity;
import robfernandes.xyz.go4lunch.utils.Utils;

import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LAT;
import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LON;
import static robfernandes.xyz.go4lunch.utils.Constants.NEARBY_PLACES;
import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Utils.getMarkerIconFromDrawable;

public class MapFragment extends BaseFragment {

    private static final String TAG = MapFragment.class.getSimpleName();
    private View view;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 14f;
    private AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
    private PlacesClient placesClient;
    private AutocompleteAdapter autocompleteAdapter;
    private List<AutocompletePrediction> autocompletePredictionList;
    private RectangularBounds bounds;
    private Double currentLocationLat;
    private Double currentLocationLon;
    private static final long searchRadiousInMetres = 50000;
    private NearByPlaces nearByPlaces;
    private String snippet = "Click here to see more";
    private List<EatingPlan> eatingPlanList = null;
    private CollectionReference plansCollection;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MapFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getParams();
        view = inflater.inflate(R.layout.fragment_map, container, false);
        initMap();
        Places.initialize(getContext(), getString(R.string.google_maps_api_key));
        placesClient = Places.createClient(getContext());
        setAutocompleteAdapter();
        getEatingPlans();
        return view;
    }

    private void getEatingPlans() {
    //TODO
    }

    private void getParams() {
        currentLocationLat = getArguments().getDouble(DEVICE_LOCATION_LAT);
        currentLocationLon = getArguments().getDouble(DEVICE_LOCATION_LON);
        nearByPlaces = getArguments().getParcelable(NEARBY_PLACES);
    }

    private void setAutocompleteAdapter() {
        RecyclerView recyclerViewAdapter = view.findViewById(R.id.fragment_map_autocomplete_recycler_view);
        recyclerViewAdapter.setHasFixedSize(true);
        autocompletePredictionList = new ArrayList<>();
        autocompleteAdapter = new AutocompleteAdapter(autocompletePredictionList);
        autocompleteAdapter.setOnAutoCompleteItemClickListener(autocompletePrediction ->
                setSelectedPlace(autocompletePrediction));
        recyclerViewAdapter.setAdapter(autocompleteAdapter);
    }

    private void setSelectedPlace(AutocompletePrediction autocompletePrediction) {
        searchView.setQuery("", false);
        searchView.setIconified(true);

        Toast.makeText(getContext()
                , autocompletePrediction.getPrimaryText(null).toString(),
                Toast.LENGTH_SHORT).show();

        String placeId = autocompletePrediction.getPlaceId();

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.VIEWPORT,
                Place.Field.ADDRESS,
                Place.Field.RATING,
                Place.Field.PRICE_LEVEL,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS,
                Place.Field.NAME);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            try {
                moveCamera(place);
            } catch (Exception e) {
                Toast.makeText(getContext(), "It is not possible to find the place"
                        , Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener((exception) -> {
            Toast.makeText(getContext(), "It is not possible to find the place"
                    , Toast.LENGTH_SHORT).show();
        });

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            moveCamera(new LatLng(currentLocationLat,
                    currentLocationLon));
            bounds = RectangularBounds.newInstance(Utils.getBounds(
                    new LatLng(currentLocationLat,
                            currentLocationLon)
                    , searchRadiousInMetres
            ));
            if (ActivityCompat.checkSelfPermission(
                    getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnInfoWindowClickListener(marker ->
            {
                goToRestaurantActivity(marker);
            });


            for (RestaurantInfo restaurantInfo : this.nearByPlaces.getRestaurantInfoList()) {
                addMarker(restaurantInfo);
            }
        });
    }

    private void addMarker(RestaurantInfo restaurantInfo) {
        BitmapDescriptor iconBitmap;

        iconBitmap = getMarkerIconFromDrawable(
                getResources().getDrawable(R.drawable.ic_location_on_green_48dp));

        MarkerOptions options = new MarkerOptions()
                .position(
                        new LatLng(restaurantInfo.getLat(), restaurantInfo.getLon())
                )
                .snippet(snippet)
                .icon(iconBitmap)
                .title(restaurantInfo.getName());

        mMap.addMarker(options).setTag(restaurantInfo);
    }

    private void goToRestaurantActivity(Marker marker) {
        RestaurantInfo restaurantInfo = (RestaurantInfo) marker.getTag();
        if (restaurantInfo != null) {
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            intent.putExtra(RESTAURANT_INFO_BUNDLE_EXTRA, restaurantInfo);
            getContext().startActivity(intent);
        } else {
            Toast.makeText(getContext(), "It is not possible to " +
                            "display info about this restaurant",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }


    private void moveCamera(Place place) {
        mMap.clear();

        LatLng latLng = place.getLatLng();
        if (latLng == null) {
            LatLngBounds viewport = place.getViewport();
            latLng = new LatLng(viewport.getCenter().latitude,
                    viewport.getCenter().longitude);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        RestaurantInfo restaurantInfo = new RestaurantInfo();
        restaurantInfo.setId(place.getId());
        restaurantInfo.setLat(place.getLatLng().latitude);
        restaurantInfo.setLon(place.getLatLng().longitude);
        restaurantInfo.setName(place.getName());

        addMarker(restaurantInfo);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        autocompletePlaces(newText);
        return false;
    }

    private void autocompletePlaces(String query) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setLocationRestriction(bounds)
                .setQuery(query)
                .build();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            autocompletePredictionList.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                List<Place.Type> placeTypes = prediction.getPlaceTypes();
                int restaurantIndex = placeTypes.indexOf(Place.Type.RESTAURANT);
                if (restaurantIndex >= 0) {
                    autocompletePredictionList.add(prediction);
                }
            }
            autocompleteAdapter.setAutocompletePredictionList(autocompletePredictionList);
            autocompleteAdapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });

    }

}
