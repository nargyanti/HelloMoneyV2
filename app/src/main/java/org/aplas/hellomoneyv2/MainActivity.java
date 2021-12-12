package org.aplas.hellomoneyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpenCamera = findViewById(R.id.btn_open_camera);
        Button btnOpenGuide = findViewById(R.id.btn_open_guide);

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToOpenCamera = new Intent(MainActivity.this, ClassifierActivity.class);
                startActivity(moveToOpenCamera);
            }
        });

        btnOpenGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToGuideActivity = new Intent(MainActivity.this, GuideActivity.class);
                startActivity(moveToGuideActivity);
            }
        });
    }
}