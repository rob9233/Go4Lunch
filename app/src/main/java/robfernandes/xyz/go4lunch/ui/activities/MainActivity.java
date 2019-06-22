package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.UserInformation;

public class MainActivity extends BaseRegisterActivity {

    private Button logInWithEmailBtn, logInWithFacebookBtn;
    private SignInButton getLogInWithGmailBtn;
    private TextView dontHaveAccountTextView;
    private EditText email, password;
    private GoogleSignInOptions gso;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MainActivityc";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        setViews();
        setOnClickListeners();
        setSignInOptions();
    }

    private void setSignInOptions() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: ");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Google sign in failed"
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        UserInformation userInformation = new UserInformation();
                        userInformation.setEmail(acct.getEmail());
                        userInformation.setName(acct.getDisplayName());
                        userInformation.setId(firebaseAuth.getUid());
                        try {
                            userInformation.setPhotoUrl(acct.getPhotoUrl().toString());
                        } catch (NullPointerException e) {
                            userInformation.setPhotoUrl(getString(R.string.logo_url));
                        }

                        saveUserInfoAndLogIn(userInformation);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void setViews() {
        getLogInWithGmailBtn = findViewById(R.id.activity_main_gmail_btn);
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
                        Toast.makeText(getApplicationContext(), "please insert a password",
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

        getLogInWithGmailBtn.setOnClickListener(v -> googleSignIn());
    }

    private void logInWithEmail() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString()
                , password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        logInUser();
                    } else {
                        Toast.makeText(getBaseContext(), "login failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
