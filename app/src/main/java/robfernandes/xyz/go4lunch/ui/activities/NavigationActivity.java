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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
        setSupportActionBar(toolbar);
    }

    private void setViews() {
        toolbar = findViewById(R.id.activity_navigation_toolbar);
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

        switch (id){
            case R.id.nav_drawer_map :
                bottomNav.setSelectedItemId(R.id.nav_map);
                break;
        }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_toolbar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
             //   Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
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
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.activity_navigation_frame_layout,
                        fragment).commit();
                return true;
            }
        });
    }
}
