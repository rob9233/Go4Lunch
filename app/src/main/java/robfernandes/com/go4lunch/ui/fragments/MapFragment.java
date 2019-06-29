package robfernandes.com.go4lunch.ui.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import robfernandes.com.go4lunch.R;
import robfernandes.com.go4lunch.adapters.AutocompleteAdapter;
import robfernandes.com.go4lunch.model.EatingPlan;
import robfernandes.com.go4lunch.model.NearByPlaces;
import robfernandes.com.go4lunch.model.RestaurantInfo;
import robfernandes.com.go4lunch.model.UserInformation;
import robfernandes.com.go4lunch.ui.activities.RestaurantActivity;
import robfernandes.com.go4lunch.utils.Utils;

import static robfernandes.com.go4lunch.utils.Constants.DEVICE_LOCATION_LAT;
import static robfernandes.com.go4lunch.utils.Constants.DEVICE_LOCATION_LON;
import static robfernandes.com.go4lunch.utils.Constants.NEARBY_PLACES;
import static robfernandes.com.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;
import static robfernandes.com.go4lunch.utils.Constants.USER_INFORMATION_EXTRA;
import static robfernandes.com.go4lunch.utils.Utils.getMarkerIconFromDrawable;
import static robfernandes.com.go4lunch.utils.Utils.restartApp;

public class MapFragment extends BaseFragment {

    private static final String TAG = MapFragment.class.getSimpleName();
    private View view;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 14.5f;
    private AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
    private PlacesClient placesClient;
    private AutocompleteAdapter autocompleteAdapter;
    private List<AutocompletePrediction> autocompletePredictionList;
    private RectangularBounds bounds;
    private Double currentLocationLat;
    private Double currentLocationLon;
    private UserInformation userInformation;
    private static final long searchRadiusInMetres = 5000;
    private NearByPlaces nearByPlaces;
    private String defaultSnippet;

    public MapFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getParams();
        defaultSnippet = getString(R.string.click_here_to_see_more);
        view = inflater.inflate(R.layout.fragment_map, container, false);
        if (currentLocationLat != null && currentLocationLon != null && userInformation != null
                && nearByPlaces != null) {
            initFragment();
        } else {
            restartApp(getActivity());
        }
        return view;
    }

    private void initFragment() {
        initMap();
        Places.initialize(Objects.requireNonNull(getContext())
                , getString(R.string.google_maps_api_key));
        placesClient = Places.createClient(getContext());
        setAutocompleteAdapter();
    }

    private void getParams() {
        currentLocationLat = getArguments() != null ?
                getArguments().getDouble(DEVICE_LOCATION_LAT) : 0;
        currentLocationLon = getArguments().getDouble(DEVICE_LOCATION_LON);
        nearByPlaces = getArguments().getParcelable(NEARBY_PLACES);
        userInformation = getArguments().getParcelable(USER_INFORMATION_EXTRA);
    }

    private void setAutocompleteAdapter() {
        RecyclerView recyclerViewAdapter = view.findViewById(R.id.fragment_map_autocomplete_recycler_view);
        recyclerViewAdapter.setHasFixedSize(true);
        autocompletePredictionList = new ArrayList<>();
        autocompleteAdapter = new AutocompleteAdapter(autocompletePredictionList);
        autocompleteAdapter.setOnAutoCompleteItemClickListener(this::setSelectedPlace);
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
                Toast.makeText(getContext(), getString(R.string.it_is_not_pos_find_place)
                        , Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener((exception) ->
                Toast.makeText(getContext(), getString(R.string.it_is_not_pos_find_place)
                        , Toast.LENGTH_SHORT).show());

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                moveCamera(new LatLng(currentLocationLat,
                        currentLocationLon));
                bounds = RectangularBounds.newInstance(Utils.getBounds(
                        new LatLng(currentLocationLat,
                                currentLocationLon)
                        , searchRadiusInMetres
                ));
                if (ActivityCompat.checkSelfPermission(
                        Objects.requireNonNull(getContext()),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.setOnInfoWindowClickListener(this::goToRestaurantActivity);

                getEatingPlans();
            });
        }
    }

    @Override
    protected void displayEatingPlans() {
        displayMarkers();
    }

    private void displayMarkers() {
        for (RestaurantInfo restaurantInfo : this.nearByPlaces.getRestaurantInfoList()) {
            addMarker(restaurantInfo);
        }
    }

    @SuppressLint("DefaultLocale")
    private void addMarker(RestaurantInfo restaurantInfo) {
        String snippet = defaultSnippet;
        MarkerOptions options = new MarkerOptions()
                .position(
                        new LatLng(restaurantInfo.getLat(), restaurantInfo.getLon())
                )
                .title(restaurantInfo.getName());

        int numOfPlans = numOfPlansInRestaurant(restaurantInfo);

        if (numOfPlans > 0) {
            try {
                BitmapDescriptor iconBitmap = getMarkerIconFromDrawable(
                        Objects.requireNonNull(getActivity()).getResources()
                                .getDrawable(R.drawable.ic_location_on_green_48dp));
                options.icon(iconBitmap);
                String text;
                if (numOfPlans == 1) {
                    text = getString(R.string.person_is);
                } else {
                    text = getString(R.string.persons_are);
                }
                snippet = String.format(getString(R.string.going_here), numOfPlans, text);

            } catch (Exception ignored) {
            }
        }
        options.snippet(snippet);

        mMap.addMarker(options).setTag(restaurantInfo);
    }

    private int numOfPlansInRestaurant(RestaurantInfo restaurantInfo) {
        int num = 0;
        for (EatingPlan eatingPlan : eatingPlanList) {
            try {
                if (eatingPlan.getRestaurantID().equals(restaurantInfo.getId())) {
                    num++;
                }
            } catch (Exception ignored) {
            }
        }

        return num;
    }

    private void goToRestaurantActivity(Marker marker) {
        RestaurantInfo restaurantInfo = (RestaurantInfo) marker.getTag();
        if (restaurantInfo != null) {
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            intent.putExtra(RESTAURANT_INFO_BUNDLE_EXTRA, restaurantInfo);
            intent.putExtra(USER_INFORMATION_EXTRA, userInformation);
            Objects.requireNonNull(getContext()).startActivity(intent);
        } else {
            Toast.makeText(getContext(),
                    getString(R.string.it_is_not_possible_to_display_info_restaurant),
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
            latLng = new LatLng(viewport != null ? viewport.getCenter().latitude : 0,
                    viewport != null ? viewport.getCenter().longitude : 0);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        RestaurantInfo restaurantInfo = new RestaurantInfo();
        restaurantInfo.setId(place.getId());
        restaurantInfo.setLat(place.getLatLng().latitude);
        restaurantInfo.setLon(place.getLatLng().longitude);
        restaurantInfo.setName(place.getName());
        try {
            restaurantInfo.setPhotoMetadata(
                    Objects.requireNonNull(place.getPhotoMetadatas()).get(0));
        } catch (Exception ignored) {
        }

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
                Log.e(TAG, getString(R.string.place_not_found) +
                        apiException.getStatusCode());
            }
        });
    }
}
