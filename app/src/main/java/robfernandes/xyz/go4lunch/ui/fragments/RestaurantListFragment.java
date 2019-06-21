package robfernandes.xyz.go4lunch.ui.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.adapters.RestaurantsAdapter;
import robfernandes.xyz.go4lunch.model.NearByPlaces;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;
import robfernandes.xyz.go4lunch.model.UserInformation;
import robfernandes.xyz.go4lunch.ui.activities.RestaurantActivity;

import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LAT;
import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LON;
import static robfernandes.xyz.go4lunch.utils.Constants.NEARBY_PLACES;
import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Constants.USER_INFORMATION_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Utils.restartApp;

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
    private UserInformation userInformation;
    private MenuItem filterItem;

    public RestaurantListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.restaurant_filter_menu, menu);
        filterItem = menu.findItem(R.id.restaurant_filter);
        // Detect SearchView icon clicks
        searchView.setOnSearchClickListener(v -> filterItem.setVisible(false));

        // Detect SearchView close
        searchView.setOnCloseListener(() -> {
            filterItem.setVisible(true);
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restaurant_filter:
                showFilterDialog();
                break;
        }
        return false;
    }

    private void showFilterDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.filter_dialog);
        Button dialogButton = dialog.findViewById(R.id.dialog_button_dismiss);
        dialogButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        getParams();
        if (currentLocationLat != null && currentLocationLon != null && userInformation != null
                && nearByPlaces != null) {
            getEatingPlans();
        } else {
            restartApp(getActivity());
        }

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
        userInformation = getArguments().getParcelable(USER_INFORMATION_EXTRA);
    }

    private void setRecyclerVIew() {
        recyclerView = view.findViewById(R.id.fragment_restaurants_recycler_view);
        LatLng userLatLng = new LatLng(currentLocationLat, currentLocationLon);
        restaurantsAdapter = new RestaurantsAdapter(nearByPlaces.getRestaurantInfoList()
                , userLatLng, eatingPlanList, userInformation, getContext());
        recyclerView.setAdapter(restaurantsAdapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        restaurantsAdapter.getFilter().filter(newText);
        return false;
    }
}
