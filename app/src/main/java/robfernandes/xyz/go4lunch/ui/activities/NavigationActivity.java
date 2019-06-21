package robfernandes.xyz.go4lunch.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.EatingPlan;
import robfernandes.xyz.go4lunch.model.NearByPlaces;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;
import robfernandes.xyz.go4lunch.model.UserInformation;
import robfernandes.xyz.go4lunch.model.placesDetailsResponse.PlacesDetailsResponse;
import robfernandes.xyz.go4lunch.model.placesResponse.PlacesResponse;
import robfernandes.xyz.go4lunch.model.placesResponse.Result;
import robfernandes.xyz.go4lunch.services.networkCalls.NearbyRestaurantsService;
import robfernandes.xyz.go4lunch.ui.fragments.MapFragment;
import robfernandes.xyz.go4lunch.ui.fragments.RestaurantListFragment;
import robfernandes.xyz.go4lunch.ui.fragments.WorkmatesFragment;

import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LAT;
import static robfernandes.xyz.go4lunch.utils.Constants.DEVICE_LOCATION_LON;
import static robfernandes.xyz.go4lunch.utils.Constants.NEARBY_PLACES;
import static robfernandes.xyz.go4lunch.utils.Constants.GOOGLE_PLACES_BASE_URL;
import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Constants.USER_INFORMATION_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Utils.putImageIntoImageView;
import static robfernandes.xyz.go4lunch.utils.Utils.restartApp;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 928;
    private static final String TAG = "NavigationActivity";
    private double currentLocationLat;
    private double currentLocationLon;
    private NearByPlaces nearByPlaces;
    private FirebaseUser currentUser;
    private UserInformation userInformation;
    private static final int MAP_FLAG = 0;
    private static final int RESTAURANT_FLAG = 1;
    private static final int WORKERS_FLAG = 2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView profileImageView;
    private boolean isBottomOptionsEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setViews();
        setListeners();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnFailureListener(e -> {
                FirebaseAuth.getInstance().signOut();
                restartApp(NavigationActivity.this);
            }).addOnSuccessListener(documentSnapshot -> init(savedInstanceState));
        } else {
            restartApp(NavigationActivity.this);
        }
    }

    private void init(Bundle savedInstanceState) {
        getUserInfo();
        //Start on Map Fragment but not when the deice is rotated
        if (savedInstanceState == null) {
            getLocationPermission();
        }

        setToolbar();
    }

    private void getUserInfo() {
        DocumentReference docRef = db.collection("users")
                .document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userInformation = document.toObject(UserInformation.class);
                    configureDrawer();
                    if (profileImageView != null) {
                        displayProfileImage();
                    }
                }
            }
        });
    }

    private void getDeviceLocation() {
        if (isServicesOK()) {
            FusedLocationProviderClient mFusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(
                            getBaseContext());

            try {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentLocationLat = ((Location) task.getResult()).getLatitude();
                        currentLocationLon = ((Location) task.getResult()).getLongitude();
                        getNearByRestaurants(MAP_FLAG);
                    }
                });
            } catch (SecurityException e) {
                Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
            }
        } else {
            Toast.makeText(getBaseContext(), "It is not possible to display the map"
                    , Toast.LENGTH_SHORT).show();
        }

    }

    private void getNearByRestaurants(int flag) {
        String location = currentLocationLat + "," + currentLocationLon;
        String apiKey = getString(R.string.google_maps_api_key);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_PLACES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NearbyRestaurantsService service = retrofit.create(NearbyRestaurantsService.class);
        Call<PlacesResponse> nearbyRestaurantsCall = service.getNearbyRestaurants(location, apiKey);

        nearbyRestaurantsCall.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                if (response.code() == 200) {
                    nearByPlaces = new NearByPlaces();
                    List<RestaurantInfo> restaurantInfoList = new ArrayList<>();
                    List<Result> results = response.body().getResults();
                    for (Result result : results) {
                        Call<PlacesDetailsResponse> detailsCall = service.getPlaceDetails(
                                result.getPlaceId()
                                , apiKey
                        );
                        Log.d(TAG, "onResponse: detailsCall " + detailsCall.request().url());
                        detailsCall.enqueue(new Callback<PlacesDetailsResponse>() {
                            @Override
                            public void onResponse(Call<PlacesDetailsResponse> call
                                    , Response<PlacesDetailsResponse> response) {
                                if (response.code() == 200) {
                                    PlacesDetailsResponse placeDetail = response.body();
                                    addRestaurant(restaurantInfoList, result, placeDetail);
                                }
                            }

                            @Override
                            public void onFailure(Call<PlacesDetailsResponse> call, Throwable t) {
                            }
                        });

                    }
                    nearByPlaces.setRestaurantInfoList(restaurantInfoList);
                    switch (flag) {
                        case MAP_FLAG:
                            showMapFragment();
                            break;
                        case RESTAURANT_FLAG:
                            showRestaurants();
                            break;
                        case WORKERS_FLAG:
                            showWorkers();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Log.e("TAG", "onFailure: ");
            }
        });
    }

    private void addRestaurant(List<RestaurantInfo> restaurantInfoList, Result result,
                               PlacesDetailsResponse placesDetailsResponse) {
        RestaurantInfo restaurantInfo = new RestaurantInfo();
        restaurantInfo.setDetailedInfo(true);
        restaurantInfo.setName(result.getName());
        restaurantInfo.setId(result.getId());
        restaurantInfo.setLat(result.getGeometry().getLocation().getLat());
        restaurantInfo.setLon(result.getGeometry().getLocation().getLng());
        restaurantInfo.setAddress(result.getVicinity());
        try {
            List<String> openSchedule = placesDetailsResponse.getResult()
                    .getOpeningHours().getWeekdayText();
            restaurantInfo.setOpenSchedule(openSchedule);
        } catch (NullPointerException e) {
        }
        try {
            restaurantInfo.setOpen(result.getOpeningHours().getOpenNow());
        } catch (NullPointerException e) {
        }
        try {
            restaurantInfo.setPhotoRef(result.getPhotos().get(0).getPhotoReference());
        } catch (NullPointerException e) {
        }
        try {
            restaurantInfo.setWebsite(placesDetailsResponse.getResult().getWebsite());
        } catch (NullPointerException e) {
        }
        try {
            restaurantInfo.setRating(result.getRating());
        } catch (NullPointerException e) {
        }

        try {
            restaurantInfo.setPhone(placesDetailsResponse.getResult().getFormattedPhoneNumber());
        } catch (NullPointerException e) {
        }
        restaurantInfoList.add(restaurantInfo);
    }

    private void showWorkers() {
        WorkmatesFragment workmatesFragment = new WorkmatesFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_navigation_frame_layout,
                        workmatesFragment).commit();
    }

    private void showMapFragment() {
        try {
            MapFragment mapFragment = new MapFragment();
            Bundle bundle = new Bundle();
            bundle.putDouble(DEVICE_LOCATION_LAT, currentLocationLat);
            bundle.putDouble(DEVICE_LOCATION_LON, currentLocationLon);
            bundle.putParcelable(NEARBY_PLACES, nearByPlaces);
            bundle.putParcelable(USER_INFORMATION_EXTRA, userInformation);
            mapFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_navigation_frame_layout,
                            mapFragment).commit();
        } catch (IllegalStateException e) {
            // Can not perform the try block after onSaveInstanceState
        }
    }

    private void configureDrawer() {
        configureDrawerLayout();
        configureNavigationView();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setViews() {
        toolbar = findViewById(R.id.activity_navigation_toolbar);
        bottomNav = findViewById(R.id.activity_navigation_bottom_navigation);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.activity_navigation_drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.activity_navigation_nav_view);

        TextView emailNav = navigationView.getHeaderView(0)
                .findViewById(R.id.drawer_header_user_email);
        TextView nameNav = navigationView.getHeaderView(0)
                .findViewById(R.id.drawer_header_user_name);

        emailNav.setText(userInformation.getEmail());
        Log.d(TAG, "configureNavigationView: " + currentUser.getDisplayName());
        nameNav.setText(userInformation.getName());

        profileImageView = navigationView.getHeaderView(0)
                .findViewById(R.id.drawer_header_user_image);

        displayProfileImage();

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Intent intent = null;

            switch (id) {
                case R.id.nav_drawer_your_lunch:
                    displayUserPlan();
                    break;
                case R.id.nav_drawer_settings:
                    intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    break;
                case R.id.nav_drawer_logout:
                    logOut();
                    displayToast("logout");
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            if (intent != null) {
                startActivity(intent);
            }
            return true;
        });
    }

    private void displayUserPlan() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference document = db.collection("plans")
                .document(year).collection(month).document(day).collection("plan")
                .document(userID);

        document.get().addOnSuccessListener(documentSnapshot -> {
            EatingPlan eatingPlan = documentSnapshot.toObject(EatingPlan.class);
            boolean foundPlace = false;
            List<RestaurantInfo> restaurantInfoList = nearByPlaces.getRestaurantInfoList();
            for (RestaurantInfo restaurantInfo : restaurantInfoList) {
                try {
                    if (restaurantInfo.getId().equals(eatingPlan.getRestaurantID())) {
                        showRestaurantInfo(restaurantInfo);
                        foundPlace = true;
                        break;
                    }
                } catch (NullPointerException e) {
                }
            }
            if (!foundPlace) {
                Toast.makeText(getBaseContext()
                        , "It was not found a lunch plan"
                        , Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(e -> Toast.makeText(getBaseContext()
                        , "It was not found a lunch plan"
                        , Toast.LENGTH_SHORT).show());
    }

    private void showRestaurantInfo(RestaurantInfo restaurantInfo) {
        Intent intent = new Intent(NavigationActivity.this, RestaurantActivity.class);
        intent.putExtra(RESTAURANT_INFO_BUNDLE_EXTRA, restaurantInfo);
        intent.putExtra(USER_INFORMATION_EXTRA, userInformation);
        startActivity(intent);
    }

    private void displayProfileImage() {
        String photoUrl;
        try {
            photoUrl = userInformation.getPhotoUrl();
        } catch (NullPointerException e) {
            photoUrl = getString(R.string.logo_url);
        }
        putImageIntoImageView(profileImageView, photoUrl
                , getResources().getDrawable(R.drawable.logo));
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setListeners() {
        bottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_map:
                    getLocationPermission();
                    break;
                case R.id.nav_restaurant_list:
                    if (nearByPlaces != null) {
                        showRestaurants();
                    } else {
                        getNearByRestaurants(RESTAURANT_FLAG);
                    }
                    break;
                case R.id.nav_workmates:
                    getNearByRestaurants(WORKERS_FLAG);
                    break;
            }
            return true;
        });
    }

    private void showRestaurants() {
        Fragment fragment = new RestaurantListFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble(DEVICE_LOCATION_LAT, currentLocationLat);
        bundle.putDouble(DEVICE_LOCATION_LON, currentLocationLon);
        bundle.putParcelable(NEARBY_PLACES, nearByPlaces);
        bundle.putParcelable(USER_INFORMATION_EXTRA, userInformation);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_navigation_frame_layout,
                        fragment).commit();
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public boolean isServicesOK() {

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                NavigationActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                    NavigationActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    getDeviceLocation();
                }
            }
        }
    }
}
