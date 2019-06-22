package robfernandes.xyz.go4lunch.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Switch;

import robfernandes.xyz.go4lunch.R;

import static robfernandes.xyz.go4lunch.utils.Utils.getNotificationStatusFromPrefs;
import static robfernandes.xyz.go4lunch.utils.Utils.setNotificationStatusInPrefs;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbar();
        setNotificationSwitch();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.activity_settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    private void setNotificationSwitch() {
        Switch notificationsSwitch = findViewById(R.id.activity_settings_notifications_switch);
        notificationsSwitch.setChecked(getNotificationStatusFromPrefs(getBaseContext()));
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked)
                -> setNotificationStatusInPrefs(getApplicationContext(), isChecked));
    }
}
