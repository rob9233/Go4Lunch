package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import robfernandes.xyz.go4lunch.R;

public class MainActivity extends AppCompatActivity {

    private Button logInWithEmailBtn;
    private Button logInWithFacebookBtn;
    private TextView dontHaveAccountTextView;
    private EditText email, password;

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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            goToActivityWithClearTask(new Intent(MainActivity.this
                    , NavigationActivity.class));
        }
    }

    private void setViews() {
        email = findViewById(R.id.activity_main_email_edit_text);
        password = findViewById(R.id.activity_main_password_edit_text);
        logInWithEmailBtn = findViewById(R.id.activity_main_log_in_btn);
        dontHaveAccountTextView = findViewById(R.id.activity_main_dont_have_account_text_view);
        logInWithFacebookBtn = findViewById(R.id.activity_main_facebook_btn);
    }

    private void setOnClickListeners() {
        logInWithFacebookBtn.setOnClickListener(v ->
                goToActivityWithClearTask(new Intent(MainActivity.this
                        , NavigationActivity.class))
        );

        logInWithEmailBtn.setOnClickListener(v -> {
                    if (email.getText().toString().isEmpty()) {
                        Toast.makeText(getBaseContext(), "please insert an email",
                                Toast.LENGTH_SHORT).show();
                    } else if (password.getText().toString().isEmpty()) {
                        Toast.makeText(getBaseContext(), "please insert a password",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        logInWithEmail();
                    }
                }
        );

        dontHaveAccountTextView.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this
                    , RegisterEmailActivity.class));
        });
    }

    private void logInWithEmail() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString()
                , password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        goToActivityWithClearTask(new Intent(MainActivity.this
                                , NavigationActivity.class));
                    } else {
                        Toast.makeText(getBaseContext(), "login failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToActivityWithClearTask(Intent intent) {
        //to not come back to this intent when pressing back button
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
