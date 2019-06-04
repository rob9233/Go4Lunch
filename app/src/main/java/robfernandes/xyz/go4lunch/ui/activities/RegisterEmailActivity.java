package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.UserInformation;

import static robfernandes.xyz.go4lunch.utils.Utils.generateRandomFileName;

public class RegisterEmailActivity extends BaseRegisterActivity {

    private EditText emailEditText, passwordEditText, nameEditText;
    private Button registerBtn;
    private FirebaseAuth firebaseAuth;
    private Uri profileImageUri;
    private ImageView profileImage;
    private static final int PICK_IMAGE_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        setViews();
        firebaseAuth = FirebaseAuth.getInstance();
        setListeners();
    }

    private void setViews() {
        emailEditText = findViewById(R.id.activity_register_email_email);
        passwordEditText = findViewById(R.id.activity_register_email_password);
        registerBtn = findViewById(R.id.activity_register_email_register_btn);
        nameEditText = findViewById(R.id.activity_register_email_name);
        profileImage = findViewById(R.id.activity_register_email_profile_image);
    }

    private void setListeners() {
        registerBtn.setOnClickListener(v -> {
            if (emailEditText.getText().toString().isEmpty()) {
                Toast.makeText(getBaseContext(), "Please insert an email",
                        Toast.LENGTH_SHORT).show();
            } else if (passwordEditText.getText().toString().isEmpty()) {
                Toast.makeText(getBaseContext(), "Please insert a password",
                        Toast.LENGTH_SHORT).show();
            } else if (nameEditText.getText().toString().isEmpty()) {
                Toast.makeText(getBaseContext(), "Please insert the name",
                        Toast.LENGTH_SHORT).show();
            } else {
                registerUser();
            }
        });

        profileImage.setOnClickListener(v -> {
            selectImage();
        });
    }

    private void selectImage() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
        pickImageIntent.setType("image/*");
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE) {
            profileImageUri = data.getData();
            profileImage.setImageURI(profileImageUri);
            findViewById(R.id.activity_register_email_profile_text_view).setVisibility(View.GONE);
        }
    }

    private void registerUser() {
        String filename = generateRandomFileName();

        final StorageReference storageReference = FirebaseStorage
                .getInstance()
                .getReference()
                .child("images/" + filename);

        storageReference.putFile(profileImageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String profileImageUrl;
                            profileImageUrl = uri.toString();
                            createUser(profileImageUrl);
                        }))
                .addOnFailureListener(exception -> {
                   Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void createUser(String profileImageUrl) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        UserInformation userInformation = new UserInformation();
                        userInformation.setEmail(emailEditText.getText().toString());
                        userInformation.setName(nameEditText.getText().toString());
                        userInformation.setId(firebaseAuth.getUid());
                        userInformation.setPhotoUrl(profileImageUrl);
                        saveUserInfoAndLogIn(userInformation);
                    } else {
                        Toast.makeText(getBaseContext(), "Failed"
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
