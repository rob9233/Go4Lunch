package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jgabrielfreitas.core.BlurImageView;

import robfernandes.xyz.go4lunch.R;

import static robfernandes.xyz.go4lunch.utils.Constants.BLUR_RADIOUS;

public class MainActivity extends AppCompatActivity {

    private Button logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        blurBackground();
        setOnClickListeners();
    }

    private void blurBackground() {
        BlurImageView blurImageView = findViewById(R.id.activity_main_background_blur_image);
        blurImageView.setBlur(BLUR_RADIOUS);
    }

    private void setViews() {
        logInBtn = findViewById(R.id.activity_main_log_in_btn);
    }

    private void setOnClickListeners() {
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this
                        , NavigationActivity.class);
                //don't come back to this intent when pressing back button
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
