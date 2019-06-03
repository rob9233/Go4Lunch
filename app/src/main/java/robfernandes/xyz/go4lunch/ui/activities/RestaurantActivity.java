package robfernandes.xyz.go4lunch.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;

import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Utils.formatNumberOfStars;
import static robfernandes.xyz.go4lunch.utils.Utils.getRestaurantPhotoUrl;
import static robfernandes.xyz.go4lunch.utils.Utils.putImageIntoImageView;

public class RestaurantActivity extends AppCompatActivity {

    private RestaurantInfo restaurantInfo;
    private ImageView restaurantImage, star1, star2, star3 ;
    private TextView restaurantTitle, restaurantDescription;

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
        restaurantTitle = findViewById(R.id.restaurant_activity_title);
        restaurantDescription = findViewById(R.id.restaurant_activity_description);
        restaurantImage = findViewById(R.id.restaurant_activity_image_view);
        star1 = findViewById(R.id.restaurant_activity_rating_star_1);
        star2 = findViewById(R.id.restaurant_activity_rating_star_2);
        star3 = findViewById(R.id.restaurant_activity_rating_star_3);

        try {
            int starsNum = formatNumberOfStars(restaurantInfo.getRating());
            if (starsNum < 1) {
                star1.setVisibility(View.INVISIBLE);
            }
            if (starsNum < 2) {
                star2.setVisibility(View.INVISIBLE);
            }
            if (starsNum < 3) {
                star3.setVisibility(View.INVISIBLE);
            }
        } catch (NullPointerException e) {

        }

        restaurantTitle.setText(restaurantInfo.getName());
        restaurantDescription.setText(restaurantInfo.getAdress());
        try {
            String restaurantPhotoUrl = getRestaurantPhotoUrl(
                    restaurantInfo.getPhotoRef()
                    , getApplicationContext(),
                    "1000",
                    "400");
            putImageIntoImageView(restaurantImage, restaurantPhotoUrl);
        } catch (Exception e) {
        }
    }

    private RestaurantInfo getRestaurantInfo() {
        return getIntent().getParcelableExtra(RESTAURANT_INFO_BUNDLE_EXTRA);
    }
}
