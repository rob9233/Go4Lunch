package robfernandes.xyz.go4lunch.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

public class ApplicationClass extends MultiDexApplication {
    public static final String CHANNEL_PLANS_ID = "CHANNEL_PLANS";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_PLANS_ID,
                    "Show plans",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Go4Lunch plans notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
