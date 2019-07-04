package robfernandes.com.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Locale;
import java.util.Objects;

import robfernandes.com.go4lunch.R;
import robfernandes.com.go4lunch.model.UserInformation;
import robfernandes.com.go4lunch.utils.Utils;

import static robfernandes.com.go4lunch.utils.Utils.changeLanguage;

public class MainActivity extends UserLoggedOutBaseRegisterActivity {

    private Button logInWithEmailBtn;
    private SignInButton getLogInWithGmailBtn;
    private TextView dontHaveAccountTextView;
    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String language = Locale.getDefault().getLanguage();
        String langFromPrefs = Utils.getLangFromPrefs(getBaseContext());

        if (!language.equals(langFromPrefs)) {
            changeLanguage(MainActivity.this, langFromPrefs, false);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        setViews();
        setOnClickListeners();
        setSignInOptions();
        setLogInWithFacebook();
    }

    private void setSignInOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(Objects.requireNonNull(account));
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.google_sign_in_failed)
                        , Toast.LENGTH_SHORT).show();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                            userInformation.setPhotoUrl(Objects.requireNonNull(acct.getPhotoUrl()).
                                    toString());
                        } catch (NullPointerException e) {
                            userInformation.setPhotoUrl(getString(R.string.logo_url));
                        }

                        saveUserInfoAndLogIn(userInformation);
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.authentication_failed)
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setViews() {
        getLogInWithGmailBtn = findViewById(R.id.activity_main_gmail_btn);
        email = findViewById(R.id.activity_main_email_edit_text);
        password = findViewById(R.id.activity_main_password_edit_text);
        logInWithEmailBtn = findViewById(R.id.activity_main_log_in_btn);
        dontHaveAccountTextView = findViewById(R.id.activity_main_dont_have_account_text_view);
    }

    private void setOnClickListeners() {
        logInWithEmailBtn.setOnClickListener(v -> {
                    if (email.getText().toString().isEmpty()) {
                        Toast.makeText(getBaseContext(), getString(R.string.please_unsert_an_email),
                                Toast.LENGTH_SHORT).show();
                    } else if (password.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.please_insert_a_password),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        logInWithEmail();
                    }
                }
        );

        dontHaveAccountTextView.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this
                        , RegisterEmailActivity.class)));

        getLogInWithGmailBtn.setOnClickListener(v -> googleSignIn());
    }

    @SuppressWarnings("deprecation")
    private void setLogInWithFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        UserInformation userInformation = new UserInformation();
                        FirebaseAuth fAuth = FirebaseAuth.getInstance();
                        userInformation.setEmail(fAuth.getCurrentUser().getEmail());
                        userInformation.setName(fAuth.getCurrentUser().getDisplayName());
                        userInformation.setId(firebaseAuth.getUid());
                        userInformation.setPhotoUrl(getString(R.string.logo_url));

                        saveUserInfoAndLogIn(userInformation);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this
                                , getString(R.string.authentication_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
