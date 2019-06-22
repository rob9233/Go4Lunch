package robfernandes.xyz.go4lunch.services.firebase;

import android.app.Notification;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import robfernandes.xyz.go4lunch.R;

/**
 * Created by Roberto Fernandes on 22/06/2019.
 */
public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification firebaseNotification = remoteMessage.getNotification();
        Notification notification = new NotificationCompat.Builder(this,
                getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_restaurant_orange_24dp)
                .setContentTitle(firebaseNotification != null ? firebaseNotification.getTitle() : null)
                .setContentText(Objects.requireNonNull(firebaseNotification).getBody())
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification);

    }
}
