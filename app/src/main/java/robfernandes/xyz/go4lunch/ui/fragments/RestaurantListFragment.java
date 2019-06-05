package robfernandes.xyz.go4lunch.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.adapters.RestaurantsAdapter;
import robfernandes.xyz.go4lunch.model.NearByPlaces;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;
import robfernandes.xyz.go4lunch.ui.activities.RestaurantActivity;

import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LAT;
import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LON;
import static robfernandes.xyz.go4lunch.utils.Constants.NEARBY_PLACES;
import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantListFragment extends BaseFragment {
    private NearByPlaces nearByPlaces;
    private View view;
    private RecyclerView recyclerView;
    private Double currentLocationLat;
    private Double currentLocationLon;
    private RestaurantsAdapter restaurantsAdapter;

    public RestaurantListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        getParams();
        getEatingPlans();
        return view;
    }

    @Override
    protected void displayEatingPlans() {
        setRecyclerVIew();
    }

    private void getParams() {
        currentLocationLat = getArguments().getDouble(DEVICE_LOCATION_LAT);
        currentLocationLon = getArguments().getDouble(DEVICE_LOCATION_LON);
        nearByPlaces = getArguments().getParcelable(NEARBY_PLACES);
    }

    private void setRecyclerVIew() {
        recyclerView = view.findViewById(R.id.fragment_restaurants_recycler_view);
        LatLng userLatLng = new LatLng(currentLocationLat, currentLocationLon);
        restaurantsAdapter = new RestaurantsAdapter(nearByPlaces.getRestaurantInfoList()
                , userLatLng, eatingPlanList, getContext());
        recyclerView.setAdapter(restaurantsAdapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        restaurantsAdapter.getFilter().filter(newText);
        return false;
    }
}
