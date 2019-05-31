package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import robfernandes.xyz.go4lunch.R;

public class RegisterEmailActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        setViews();
        mAuth = FirebaseAuth.getInstance();
        setListeners();
    }

    private void setViews() {
        emailEditText = findViewById(R.id.activity_register_email_email);
        passwordEditText = findViewById(R.id.activity_register_email_password);
        registerBtn = findViewById(R.id.activity_register_email_register_btn);
    }

    private void setListeners() {
        registerBtn.setOnClickListener(v -> {
            if (emailEditText.getText().toString().isEmpty()) {
                Toast.makeText(getBaseContext(), "Please insert an email",
                        Toast.LENGTH_SHORT).show();
            } else if (passwordEditText.getText().toString().isEmpty()) {
                Toast.makeText(getBaseContext(), "Please insert a password",
                        Toast.LENGTH_SHORT).show();
            } else {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Sign up successful"
                                , Toast.LENGTH_SHORT).show();
                        goToNavigationActivity();
                    } else {
                        Toast.makeText(getBaseContext(), "Failed"
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToNavigationActivity() {
        Intent intent = new Intent(RegisterEmailActivity.this
                , NavigationActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
