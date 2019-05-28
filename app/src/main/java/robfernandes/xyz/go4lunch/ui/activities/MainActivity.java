package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.placesResponse.PlacesResponse;
import robfernandes.xyz.go4lunch.services.network.NearbyRestaurantsService;

public class MainActivity extends AppCompatActivity {

    private Button logInBtn;
    private TextView dontHaveAccountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        setOnClickListeners();
    }

    private void setViews() {
        logInBtn = findViewById(R.id.activity_main_log_in_btn);
        dontHaveAccountTextView = findViewById(R.id.activity_main_dont_have_account_text_view);
    }

    private void setOnClickListeners() {
        logInBtn.setOnClickListener(v -> goToActivityWithClearTask(new Intent(MainActivity.this
                , NavigationActivity.class)));

        dontHaveAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void goToActivityWithClearTask(Intent intent) {
        //to not come back to this intent when pressing back button
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
