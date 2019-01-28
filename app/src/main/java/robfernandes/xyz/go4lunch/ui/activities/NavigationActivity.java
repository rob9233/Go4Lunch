package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jgabrielfreitas.core.BlurImageView;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.ui.fragments.ChatFragment;
import robfernandes.xyz.go4lunch.ui.fragments.MapFragment;
import robfernandes.xyz.go4lunch.ui.fragments.RestaurantListFragment;
import robfernandes.xyz.go4lunch.ui.fragments.WorkmatesFragment;
import robfernandes.xyz.go4lunch.utils.Constants;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setViews();
        setListeners();
        //Start on Map Fragment but not when the deice is rotated
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_navigation_frame_layout,
                            new MapFragment()).commit();
        }

        setToolbar();
        configureDrawer();
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
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                blurBackground();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }

    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.activity_navigation_nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Intent intent = null;

                switch (id) {
                    case R.id.nav_drawer_your_lunch:
                        intent = new Intent(getApplicationContext(), LunchActivity.class);
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
            }
        });
    }

    private void logOut() {
        displayToast("Log Out");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem item = menu.findItem(R.id.menu_toolbar_search);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search is expanded
                toggle.setDrawerIndicatorEnabled(false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toggle.setDrawerIndicatorEnabled(true);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    private void setListeners() {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_map:
                        fragment = new MapFragment();
                        break;
                    case R.id.nav_restaurant_list:
                        fragment = new RestaurantListFragment();
                        break;
                    case R.id.nav_workmates:
                        fragment = new WorkmatesFragment();
                        break;
                    case R.id.nav_chat:
                        fragment = new ChatFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.activity_navigation_frame_layout,
                        fragment).commit();
                return true;
            }
        });
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


    private void blurBackground() {
        BlurImageView blurImageView = findViewById(R.id.drawer_header_blur_image);
        blurImageView.setBlur(Constants.BLUR_RADIOUS);
    }
}
