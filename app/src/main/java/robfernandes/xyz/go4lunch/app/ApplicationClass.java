package robfernandes.xyz.go4lunch.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.multidex.MultiDexApplication;
import robfernandes.xyz.go4lunch.R;

public class ApplicationClass extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    "Show plans",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Go4Lunch plans notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
