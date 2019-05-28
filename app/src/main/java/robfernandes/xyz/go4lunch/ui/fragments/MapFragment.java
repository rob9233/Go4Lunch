package robfernandes.xyz.go4lunch.ui.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.ui.adapters.AutocompleteAdapter;

public class MapFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private static final String TAG = MapFragment.class.getSimpleName();
    private View view;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 14f;
    private AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
    private RectangularBounds bounds = RectangularBounds.newInstance(
            new LatLng(-33.880490, 151.184363),
            new LatLng(-33.858754, 151.229596));
    private FindAutocompletePredictionsRequest request;
    private PlacesClient placesClient;
    private RecyclerView recyclerViewAdapter;
    private AutocompleteAdapter autocompleteAdapter;
    private List<AutocompletePrediction> autocompletePredictionList;
    private SearchView searchView;

    public MapFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        initMap();
        Places.initialize(getContext(), getString(R.string.google_maps_api_key));
        placesClient = Places.createClient(getContext());
        setAutocompleteAdapter();
        return view;
    }

    private void setAutocompleteAdapter() {
        recyclerViewAdapter = view.findViewById(R.id.fragment_map_autocomplete_recycler_view);
        recyclerViewAdapter.setHasFixedSize(true);
        autocompletePredictionList = new ArrayList<>();
        autocompleteAdapter = new AutocompleteAdapter(autocompletePredictionList);
        autocompleteAdapter.setOnAutoCompleteItemClickListener(autocompletePrediction -> {
            setSelectedPlace(autocompletePrediction);
        });
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
                Place.Field.NAME);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            String name = place.getName();
            LatLng latLng = place.getLatLng();
            if (name == null) {
                name = "selected place";
            }
            if (latLng == null) {
                LatLngBounds viewport = place.getViewport();
                latLng = new LatLng(viewport.getCenter().latitude,
                        viewport.getCenter().longitude);
            }
            try {
                moveCamera(latLng, name);
            } catch (Exception e) {
                Toast.makeText(getContext(), "It is not possible to find the place"
                        , Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener((exception) -> {
            Toast.makeText(getContext(), "It is not possible to find the place"
                    , Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(
                        getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mMap.setMyLocationEnabled(true);
                getDeviceLocation();
            }
        });
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                getContext());

        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();

                        moveCamera(new LatLng(currentLocation.getLatitude(),
                                        currentLocation.getLongitude()),
                                "My Location");

                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        geoLocate(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        autocompletePlaces(newText);
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void geoLocate(String term) {
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(term, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    address.getAddressLine(0));
        }
    }

    private void autocompletePlaces(String query) {
        request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
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
