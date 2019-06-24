package robfernandes.xyz.go4lunch.ui.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.adapters.RestaurantsAdapter;
import robfernandes.xyz.go4lunch.model.NearByPlaces;
import robfernandes.xyz.go4lunch.model.UserInformation;

import static androidx.appcompat.widget.ListPopupWindow.MATCH_PARENT;
import static androidx.appcompat.widget.ListPopupWindow.WRAP_CONTENT;
import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LAT;
import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LON;
import static robfernandes.xyz.go4lunch.utils.Constants.FILTER_PARAMS_RESTAURANT_KEY;
import static robfernandes.xyz.go4lunch.utils.Constants.MAX_DISTANCE;
import static robfernandes.xyz.go4lunch.utils.Constants.MAX_STARS;
import static robfernandes.xyz.go4lunch.utils.Constants.MIN_DISTANCE;
import static robfernandes.xyz.go4lunch.utils.Constants.MIN_STARS;
import static robfernandes.xyz.go4lunch.utils.Constants.NEARBY_PLACES;
import static robfernandes.xyz.go4lunch.utils.Constants.USER_INFORMATION_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Utils.restartApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantListFragment extends BaseFragment {
    private NearByPlaces nearByPlaces;
    private View view;
    private Double currentLocationLat;
    private Double currentLocationLon;
    private RestaurantsAdapter restaurantsAdapter;
    private UserInformation userInformation;
    private MenuItem filterItem;

    public RestaurantListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.restaurant_filter) {
            showFilterDialog();
        }
        return false;
    }

    private void showFilterDialog() {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));

        dialog.show();
        Window window = dialog.getWindow();
        Objects.requireNonNull(window).setLayout(MATCH_PARENT, WRAP_CONTENT);
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
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String type;
                if (progress == 1) {
                    type = getString(R.string.star);
                } else {
                    type = getString(R.string.stars);
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
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String type;
                if (progress == 1) {
                    type = getString(R.string.star);
                } else {
                    type = getString(R.string.stars);
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
            @SuppressLint("DefaultLocale")
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
            @SuppressLint("DefaultLocale")
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
                + ":" + MIN_STARS + "=" + minStars
                + ":" + MAX_STARS + "=" + maxStars
                + ":" + MIN_DISTANCE + "=" + minDistance
                + ":" + MAX_DISTANCE + "=" + maxDistance
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        if (getArguments() != null) {
            currentLocationLat = getArguments().getDouble(DEVICE_LOCATION_LAT);
        }
        if (getArguments() != null) {
            currentLocationLon = getArguments().getDouble(DEVICE_LOCATION_LON);
        }
        if (getArguments() != null) {
            nearByPlaces = getArguments().getParcelable(NEARBY_PLACES);
        }
        if (getArguments() != null) {
            userInformation = getArguments().getParcelable(USER_INFORMATION_EXTRA);
        }
    }

    private void setRecyclerVIew() {
        RecyclerView recyclerView = view.findViewById(R.id.fragment_restaurants_recycler_view);
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
