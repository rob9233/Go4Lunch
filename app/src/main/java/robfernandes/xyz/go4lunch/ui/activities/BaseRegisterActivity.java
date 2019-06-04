package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import robfernandes.xyz.go4lunch.model.UserInformation;

public class BaseRegisterActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    void logInUser() {
        goToActivityWithClearTask(new Intent(getApplicationContext()
                , NavigationActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            logInUser();
        }
    }

    void goToActivityWithClearTask(Intent intent) {
        //to not come back to this intent when pressing back button
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    void saveUserInfo(UserInformation userInformation) {
        CollectionReference users = db.collection("users");
        users.document(userInformation.getId()).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //user exists the the db
                            logInUser();
                        } else {
                            //user is not the the db
                            createUserInDB(userInformation, users);
                        }
                    } else {
                        createUserInDB(userInformation, users);
                    }
                }
        );
    }

    private void createUserInDB(UserInformation userInformation, CollectionReference users) {
        users.document(userInformation.getId())
                .set(userInformation)
                .addOnSuccessListener(documentReference ->
                        logInUser()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getBaseContext()
                                , "It was not possible to login",
                                Toast.LENGTH_SHORT).show()
                );
    }
}
