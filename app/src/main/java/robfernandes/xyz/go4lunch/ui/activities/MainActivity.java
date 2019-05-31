package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import robfernandes.xyz.go4lunch.R;

public class MainActivity extends AppCompatActivity {

    private Button logInWithEmailBtn;
    private Button logInWithFacebookBtn;
    private TextView dontHaveAccountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        setOnClickListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser!= null) {
            goToActivityWithClearTask(new Intent(MainActivity.this
                    , NavigationActivity.class));
        }
    }

    private void setViews() {
        logInWithEmailBtn = findViewById(R.id.activity_main_log_in_btn);
        dontHaveAccountTextView = findViewById(R.id.activity_main_dont_have_account_text_view);
        logInWithFacebookBtn = findViewById(R.id.activity_main_facebook_btn);
    }

    private void setOnClickListeners() {
        logInWithFacebookBtn.setOnClickListener(v ->
                goToActivityWithClearTask(new Intent(MainActivity.this
                        , NavigationActivity.class))
        );

        logInWithEmailBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this
                        , LoginInEmailActivity.class))
        );

        dontHaveAccountTextView.setOnClickListener(v -> {
            //    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
    }

    private void goToActivityWithClearTask(Intent intent) {
        //to not come back to this intent when pressing back button
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
