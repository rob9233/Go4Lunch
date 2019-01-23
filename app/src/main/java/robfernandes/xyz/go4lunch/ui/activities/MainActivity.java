package robfernandes.xyz.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import robfernandes.xyz.go4lunch.R;

public class MainActivity extends AppCompatActivity {

    private Button logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        setOnClickListeners();
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
                startActivity(intent);
            }
        });
    }
}
