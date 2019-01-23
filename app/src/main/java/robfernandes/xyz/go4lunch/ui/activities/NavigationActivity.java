package robfernandes.xyz.go4lunch.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.ui.fragments.RestaurantListFragment;
import robfernandes.xyz.go4lunch.ui.fragments.MapFragment;
import robfernandes.xyz.go4lunch.ui.fragments.WorkmatesFragment;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

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
                    .replace(R.id.activity_navigation_fragment_container,
                            new MapFragment()).commit();
        }
    }

    private void setViews() {
        bottomNav = findViewById(R.id.activity_navigation_bottom_navigation);
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

                getSupportFragmentManager().beginTransaction().replace(R.id.activity_navigation_fragment_container,
                        fragment).commit();
                return true;
            }
        });
    }
}
