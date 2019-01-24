package robfernandes.xyz.go4lunch.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.ui.fragments.MapFragment;
import robfernandes.xyz.go4lunch.ui.fragments.RestaurantListFragment;
import robfernandes.xyz.go4lunch.ui.fragments.WorkmatesFragment;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        toolbar = findViewById(R.id.activity_navigation_toolbar);
        setSupportActionBar(toolbar);
    }

    private void setViews() {
        bottomNav = findViewById(R.id.activity_navigation_bottom_navigation);
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void configureDrawerLayout(){
        this.drawerLayout = findViewById(R.id.activity_navigation_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        this.navigationView =  findViewById(R.id.activity_navigation_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // 4 - Handle Navigation Item Click
                int id = menuItem.getItemId();

/*        switch (id){
            case R.id.activity_main_drawer_news :
                break;
            case R.id.activity_main_drawer_profile:
                break;
            case R.id.activity_main_drawer_settings:
                break;
            default:
                break;
        }*/

                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
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
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.activity_navigation_frame_layout,
                        fragment).commit();
                return true;
            }
        });
    }
}
