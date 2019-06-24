package robfernandes.xyz.go4lunch.services.firebase;

import android.app.Notification;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.EatingPlan;

import static robfernandes.xyz.go4lunch.utils.Utils.getNotificationStatusFromPrefs;

/**
 * Created by Roberto Fernandes on 22/06/2019.
 */
public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        userHasPlans();
    }

    private void userHasPlans() {
        try {
            if (getNotificationStatusFromPrefs(this)) {
                String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                        .getUid();
                Calendar calendar = Calendar.getInstance();
                String year = String.valueOf(calendar.get(Calendar.YEAR));
                String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

                FirebaseFirestore
                        .getInstance()
                        .collection("plans")
                        .document(year)
                        .collection(month)
                        .document(day)
                        .collection("plan")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                            List<EatingPlan> otherUsersEatingPlans = new ArrayList<>();
                            EatingPlan userEatingPlan = null;
                            for (DocumentSnapshot doc : documents) {
                                EatingPlan tempEatingPlan = doc.toObject(EatingPlan.class);
                                if (tempEatingPlan != null) {
                                    if (tempEatingPlan.getUserID().equals(uid)) {
                                        userEatingPlan = tempEatingPlan;
                                    } else {
                                        otherUsersEatingPlans.add(tempEatingPlan);
                                    }
                                }
                            }
                            if (userEatingPlan != null) {
                                getNumberUsersGoingToTheSameRestaurant(userEatingPlan
                                        , otherUsersEatingPlans);
                            }
                        }).addOnFailureListener(e -> {
                });
            }
        } catch (Exception ignored) {
        }
    }

    private void getNumberUsersGoingToTheSameRestaurant(EatingPlan userEatingPlan
            , List<EatingPlan> otherUsersEatingPlans) {
        int num = 0;
        for (EatingPlan eatingPlan : otherUsersEatingPlans) {
            if (eatingPlan.getRestaurantID().equals(userEatingPlan.getRestaurantID())) {
                num++;
            }
        }

        displayNotification(userEatingPlan, num);
    }

    private void displayNotification(EatingPlan userEatingPlan, int num) {
        String restaurantName = userEatingPlan.getRestaurantName();
        String title = "Lunch at " + restaurantName;
        String message = "Don't forget your lunch at " + restaurantName;

        if (num == 1) {
            message += " " + num + " friend is join you";
        } else if (num > 1) {
            message += " " + num + " friends are join you";
        }

        Notification notification = new NotificationCompat.Builder(this,
                getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_restaurant_orange_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);
    }
}
