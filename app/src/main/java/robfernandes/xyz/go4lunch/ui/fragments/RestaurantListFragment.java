package robfernandes.xyz.go4lunch.ui.fragments;


import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.adapters.RestaurantsAdapter;
import robfernandes.xyz.go4lunch.model.NearByPlaces;
import robfernandes.xyz.go4lunch.model.UserInformation;

import static androidx.appcompat.widget.ListPopupWindow.MATCH_PARENT;
import static androidx.appcompat.widget.ListPopupWindow.WRAP_CONTENT;
import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LAT;
import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LON;
import static robfernandes.xyz.go4lunch.utils.Constants.FILTER_PARAMS_RESTAURANT_KEY;
import static robfernandes.xyz.go4lunch.utils.Constants.NEARBY_PLACES;
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

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.setContentView(R.layout.filter_dialog);

        SeekBar seekBarMinStars = dialog.findViewById(R.id.seekbar_min_num_of_stars);
        TextView minNumStarsTextView = dialog.findViewById(R.id.min_num_of_stars_text_view);
        SeekBar seekBarMaxStars = dialog.findViewById(R.id.seekbar_max_num_of_stars);
        TextView maxNumStarsTextView = dialog.findViewById(R.id.max_num_of_stars_text_view);
        SeekBar seekBarMinDistance = dialog.findViewById(R.id.seekbar_min_distance);
        TextView minDistanceTextView = dialog.findViewById(R.id.min_distance_text_view);
        SeekBar seekBarMaxDistance = dialog.findViewById(R.id.seekbar_max_distance);
        TextView maxDistanceTextView = dialog.findViewById(R.id.max_distance_text_view);
        minNumStarsTextView.setText(String.valueOf(seekBarMinStars.getProgress()));
        seekBarMinStars.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String type;
                if (progress == 1) {
                    type = "star";
                } else {
                    type = "stars";
                }
                minNumStarsTextView.setText(String.format("%d %s", progress, type));
                if (progress > seekBarMaxStars.getProgress()) {
                    seekBarMaxStars.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarMaxStars.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String type;
                if (progress == 1) {
                    type = "star";
                } else {
                    type = "stars";
                }
                maxNumStarsTextView.setText(String.format("%d %s", progress, type));
                if (progress < seekBarMinStars.getProgress()) {
                    seekBarMinStars.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarMinDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String type = "m";
                minDistanceTextView.setText(String.format("%d %s", progress, type));
                if (progress > seekBarMaxDistance.getProgress()) {
                    seekBarMaxDistance.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarMaxDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String type = "m";
                maxDistanceTextView.setText(String.format("%d %s", progress, type));
                if (progress < seekBarMinDistance.getProgress()) {
                    seekBarMinDistance.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button dialogCancelButton = dialog.findViewById(R.id.dialog_button_cancel);
        dialogCancelButton.setOnClickListener(v -> dialog.dismiss());
        Button dialogFilterButton = dialog.findViewById(R.id.dialog_button_filter);
        dialogFilterButton.setOnClickListener(v -> {
            dialog.dismiss();
            filterRestaurantList(seekBarMinStars.getProgress(), seekBarMaxStars.getProgress()
                    , seekBarMinDistance.getProgress(), seekBarMaxDistance.getProgress());
        });
    }

    private void filterRestaurantList(int minStars, int maxStars, int minDistance, int maxDistance) {
        restaurantsAdapter.getFilter().filter(FILTER_PARAMS_RESTAURANT_KEY
                + ":minStars=" + minStars
                + ":maxStars=" + maxStars
                + ":minDistance=" + minDistance
                + ":maxDistance=" + maxDistance
        );
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
