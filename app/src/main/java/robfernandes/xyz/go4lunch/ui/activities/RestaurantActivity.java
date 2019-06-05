package robfernandes.xyz.go4lunch.ui.activities;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.EatingPlan;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;

import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Utils.formatNumberOfStars;
import static robfernandes.xyz.go4lunch.utils.Utils.getFormatedTodaysDate;
import static robfernandes.xyz.go4lunch.utils.Utils.getRestaurantPhotoUrl;
import static robfernandes.xyz.go4lunch.utils.Utils.putImageIntoImageView;

public class RestaurantActivity extends AppCompatActivity {

    private RestaurantInfo restaurantInfo;
    private ImageView restaurantImage, star1, star2, star3, planImageView;
    private TextView restaurantTitle, restaurantDescription;
    private RelativeLayout goOptionContainer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean goingToThisRestaurant = false;
    private CollectionReference plansCollection;
    private static final String TAG = "RestaurantActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        restaurantInfo = getRestaurantInfo();
        if (restaurantInfo != null) {
            init();
        } else {
            finish();
        }
    }

    private void init() {
        setViews();
        showInfo();
        checkPlan();
    }

    private void checkPlan() {
        plansCollection = db.collection("plans");
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        plansCollection.document(year).collection(month).document(day)
                .collection("plan")
                .get().addOnSuccessListener(
                documentSnapshot -> {
                    List<DocumentSnapshot> documents = documentSnapshot.getDocuments();
                    List<EatingPlan> eatingPlans = new ArrayList<>();
                    for (DocumentSnapshot document : documents) {
                        EatingPlan eatingPlan = document.toObject(EatingPlan.class);
                        eatingPlans.add(eatingPlan);
                    }
                    if (eatingPlans.size() > 0) {
                        boolean found = false;
                        for (EatingPlan eatingPlan: eatingPlans) {
                            if (eatingPlan.getRestaurantID().equals(restaurantInfo.getId())) {
                                found = true;
                            }
                        }
                        if (found) {
                            setPlanParams(true);
                        } else {
                            setPlanParams(false);
                        }
                    } else {
                        setPlanParams(false);
                    }
                }
        ).addOnFailureListener(e ->
                setPlanParams(false)
        );


    }

    private void setPlanParams(boolean going) {
        goingToThisRestaurant = going;
        displayPlan();
        setPlanListeners();
    }

    private void setViews() {
        restaurantTitle = findViewById(R.id.restaurant_activity_title);
        restaurantDescription = findViewById(R.id.restaurant_activity_description);
        restaurantImage = findViewById(R.id.restaurant_activity_image_view);
        star1 = findViewById(R.id.restaurant_activity_rating_star_1);
        star2 = findViewById(R.id.restaurant_activity_rating_star_2);
        star3 = findViewById(R.id.restaurant_activity_rating_star_3);
        goOptionContainer = findViewById(R.id.restaurant_activity_go_option_container);
        planImageView = findViewById(R.id.restaurant_activity_go_option_plan);
    }

    private void showInfo() {
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

    private void setPlanListeners() {
        goOptionContainer.setOnClickListener(v -> updatePlanStatus());
    }

    private void updatePlanStatus() {
        if (goingToThisRestaurant) {
            removeUserPlan();
            goingToThisRestaurant = false;
            displayPlan();
        } else {
            removeUserPlan();
            EatingPlan eatingPlan = new EatingPlan();
            eatingPlan.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());
            eatingPlan.setRestaurantID(restaurantInfo.getId());

            String pathName = getFormatedTodaysDate();

            plansCollection.document(pathName)
                    .collection("plan")
                    .document(eatingPlan.getUserID())
                    .set(eatingPlan)
                    .addOnSuccessListener(documentReference -> {
                                goingToThisRestaurant = true;
                                displayPlan();
                            }
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(getBaseContext()
                                    , "It was not possible to login",
                                    Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private RestaurantInfo getRestaurantInfo() {
        return getIntent().getParcelableExtra(RESTAURANT_INFO_BUNDLE_EXTRA);
    }

    private void displayPlan() {
        if (goingToThisRestaurant) {
            planImageView.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_done_green_24dp));
        } else {
            planImageView.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_near_me_yellow_24dp));
        }
    }

    private void removeUserPlan() {
        String pathName = getFormatedTodaysDate();

        plansCollection.document(pathName)
                .collection("plan")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .delete();
    }
}
