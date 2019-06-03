package robfernandes.xyz.go4lunch.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;

import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;

public class RestaurantActivity extends AppCompatActivity {

    private RestaurantInfo restaurantInfo;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        restaurantInfo = getRestaurantInfo();
        if (restaurantInfo != null) {
            showInfo();
        } else {
           finish();
        }
    }

    private void showInfo() {
        textView = findViewById(R.id.activity_restaurante_textView2);
        textView.setText(restaurantInfo.getName());
    }

    private RestaurantInfo getRestaurantInfo() {
       return getIntent().getParcelableExtra(RESTAURANT_INFO_BUNDLE_EXTRA);
    }
}
