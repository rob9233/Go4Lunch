package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.adapters.WorkmatesAdapter;
import robfernandes.xyz.go4lunch.model.EatingPlan;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;
import robfernandes.xyz.go4lunch.model.UserInformation;

import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Constants.USER_INFORMATION_EXTRA;
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
    private RecyclerView recyclerView;
    private WorkmatesAdapter workmatesAdapter;
    private boolean dataChanged = false;
    private List<EatingPlan> eatingPlans;
    private UserInformation userInformation;
    private static final String TAG = "RestaurantActivity";
    private List<UserInformation> usersList = new ArrayList<>();
    private static final int PHOTO_MAX_WIDTH = 1000;
    private static final int PHOTO_MAX_HEIGHT = 400;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        placesClient = Places.createClient(getBaseContext());
        restaurantInfo = getIntent().getParcelableExtra(RESTAURANT_INFO_BUNDLE_EXTRA);
        userInformation = getIntent().getParcelableExtra(USER_INFORMATION_EXTRA);
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
        setUserList();
    }

    private void checkPlan() {
        plansCollection = db.collection("plans");
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        plansCollection.document(year).collection(month).document(day)
                .collection("plan")
                .get().addOnSuccessListener(
                documentSnapshot -> {
                    List<DocumentSnapshot> documents = documentSnapshot.getDocuments();
                    eatingPlans = new ArrayList<>();
                    for (DocumentSnapshot document : documents) {
                        try {
                            EatingPlan eatingPlan = document.toObject(EatingPlan.class);
                            eatingPlans.add(eatingPlan);
                        } catch (Exception e) {
                        }
                    }
                    if (eatingPlans.size() > 0) {
                        boolean found = false;
                        for (EatingPlan eatingPlan : eatingPlans) {
                            try {
                                if (
                                        (eatingPlan.getRestaurantID().equals(restaurantInfo.getId())
                                                && (eatingPlan.getUserID()
                                                .equals(userInformation.getId()))
                                        )) {
                                    found = true;
                                }
                            } catch (NullPointerException e) {
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

    private void setUserList() {
        usersList.clear();
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot document : documents) {
                try {
                    UserInformation userInformation = document.toObject(UserInformation.class);
                    for (EatingPlan plan : eatingPlans) {
                        if ((plan.getUserID().equals(userInformation.getId()) &&
                                (plan.getRestaurantID().equals(restaurantInfo.getId()))
                        )) {
                            usersList.add(userInformation);
                        }
                    }
                } catch (Exception e) {
                }
            }
            workmatesAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e ->
                workmatesAdapter.notifyDataSetChanged());
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
        setRecyclerView();
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.activity_restaurant_recycler_view);
        workmatesAdapter = new WorkmatesAdapter(usersList
                , eatingPlans, false);
        recyclerView.setAdapter(workmatesAdapter);
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

        try {
            restaurantTitle.setText(restaurantInfo.getName());
            restaurantDescription.setText(restaurantInfo.getAdress());
        } catch (NullPointerException e) {
        }

        String photoRef = restaurantInfo.getPhotoRef();

        try {
            if (photoRef != null && !photoRef.isEmpty()) {
                setPhotoFromPhotoRef(photoRef);
            } else if (restaurantInfo.getPhotoMetadata() != null) {
                fetchPhotoFromAtributes(restaurantInfo.getPhotoMetadata());
            }
        } catch (NullPointerException e) {
        }
    }

    private void setPhotoFromPhotoRef(String photoRef) {
        String restaurantPhotoUrl = getRestaurantPhotoUrl(
                photoRef
                , getApplicationContext(),
                String.valueOf(PHOTO_MAX_WIDTH),
                String.valueOf(PHOTO_MAX_HEIGHT));
        putImageIntoImageView(restaurantImage, restaurantPhotoUrl);
    }

    private void fetchPhotoFromAtributes(PhotoMetadata photoMetadata) {
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(PHOTO_MAX_WIDTH)
                .setMaxHeight(PHOTO_MAX_HEIGHT)
                .build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            restaurantImage.setImageBitmap(bitmap);
        });
    }

    private void setPlanListeners() {
        goOptionContainer.setOnClickListener(v -> updatePlanStatus());
    }

    private void updatePlanStatus() {
        dataChanged = true;
        if (goingToThisRestaurant) {
            removeUserPlan();
            goingToThisRestaurant = false;
            displayPlan();
        } else {
            removeUserPlan();
            EatingPlan eatingPlan = new EatingPlan();
            eatingPlan.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());
            eatingPlan.setRestaurantID(restaurantInfo.getId());
            eatingPlan.setRestaurantName(restaurantInfo.getName());

            String pathName = getFormatedTodaysDate();

            plansCollection.document(pathName)
                    .collection("plan")
                    .document(eatingPlan.getUserID())
                    .set(eatingPlan)
                    .addOnSuccessListener(documentReference -> {
                                goingToThisRestaurant = true;
                                displayPlan();
                                usersList.add(userInformation);
                                workmatesAdapter.notifyDataSetChanged();
                            }
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(getBaseContext()
                                    , "It was not possible to login",
                                    Toast.LENGTH_SHORT).show()
                    );
        }
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
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        plansCollection.document(pathName)
                .collection("plan")
                .document(uid)
                .delete();

        try {
            usersList.remove(getUserInfo());
            workmatesAdapter.notifyDataSetChanged();
        } catch (NullPointerException e) {
        }

    }

    private UserInformation getUserInfo() {
        for (UserInformation item : usersList) {
            if (item.getId().equals(userInformation.getId())) return item;
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        if (dataChanged) {
            Intent intent = new Intent(RestaurantActivity.this
                    , NavigationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            super.onBackPressed();
        }
    }
}
