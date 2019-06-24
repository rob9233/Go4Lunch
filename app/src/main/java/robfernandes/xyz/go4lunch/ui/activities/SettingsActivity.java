package robfernandes.xyz.go4lunch.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import robfernandes.xyz.go4lunch.R;

import static robfernandes.xyz.go4lunch.utils.Utils.getNotificationStatusFromPrefs;
import static robfernandes.xyz.go4lunch.utils.Utils.setNotificationStatusInPrefs;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivityx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbar();
        setLangSpinner();
        setNotificationSwitch();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.activity_settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    private void setLangSpinner() {
        Spinner spinner = findViewById(R.id.activity_settings_lang_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lang_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setNotificationSwitch() {
        Switch notificationsSwitch = findViewById(R.id.activity_settings_notifications_switch);
        notificationsSwitch.setChecked(getNotificationStatusFromPrefs(getBaseContext()));
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked)
                -> setNotificationStatusInPrefs(getApplicationContext(), isChecked));
    }
}
