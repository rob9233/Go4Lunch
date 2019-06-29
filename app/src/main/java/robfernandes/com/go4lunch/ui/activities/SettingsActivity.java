package robfernandes.com.go4lunch.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import robfernandes.com.go4lunch.R;

import static robfernandes.com.go4lunch.utils.Utils.changeLanguage;
import static robfernandes.com.go4lunch.utils.Utils.getNotificationStatusFromPrefs;
import static robfernandes.com.go4lunch.utils.Utils.setNotificationStatusInPrefs;

public class SettingsActivity extends AppCompatActivity {

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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    private void setLangSpinner() {
        Spinner spinner = findViewById(R.id.activity_settings_lang_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lang_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        String currentLang = Locale.getDefault().getLanguage();
        int spinnerIndex;

        switch (currentLang) {
            case "en":
                spinnerIndex = 0;
                break;
            case "es":
                spinnerIndex = 1;
                break;
            default:
                spinnerIndex = 0;
                break;
        }

        spinner.setSelection(spinnerIndex);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectionLangName = parent.getItemAtPosition(position).toString();
                String selectionLang;
                switch (selectionLangName) {
                    case "English":
                        selectionLang = "en";
                        break;
                    case "Español":
                        selectionLang = "es";
                        break;
                    default:
                        selectionLang = "en";
                        break;
                }

                if (!selectionLang.equals(currentLang)) {
                    displayChangeLangDialog(selectionLang, spinner, spinnerIndex);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void displayChangeLangDialog(String lang, Spinner spinner, int spinnerIndex) {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(getBaseContext().getString(R.string.change_language))
                .setMessage(getBaseContext().getString(R.string.change_lang_restart_app))
                .setPositiveButton(android.R.string.yes, (dialog, which)
                        -> changeLanguage(SettingsActivity.this, lang, true))
                .setNegativeButton(android.R.string.no, (dialog, which)
                        -> spinner.setSelection(spinnerIndex))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setNotificationSwitch() {
        Switch notificationsSwitch = findViewById(R.id.activity_settings_notifications_switch);
        notificationsSwitch.setChecked(getNotificationStatusFromPrefs(getBaseContext()));
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked)
                -> setNotificationStatusInPrefs(getApplicationContext(), isChecked));
    }
}
