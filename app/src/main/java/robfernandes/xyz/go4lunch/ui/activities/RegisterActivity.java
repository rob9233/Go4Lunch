package robfernandes.xyz.go4lunch.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jgabrielfreitas.core.BlurImageView;

import robfernandes.xyz.go4lunch.R;

import static robfernandes.xyz.go4lunch.utils.Constants.BLUR_RADIOUS;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setViews();
        blurBackground();
        setOnClickListeners();
    }

    private void blurBackground() {
        BlurImageView blurImageView = findViewById(R.id.activity_register_background_blur_image);
        blurImageView.setBlur(BLUR_RADIOUS);
    }

    private void setViews() {

    }

    private void setOnClickListeners() {

    }
}
