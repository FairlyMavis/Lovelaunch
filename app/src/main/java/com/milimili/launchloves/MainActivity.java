package com.milimili.launchloves;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoveLayout loveLayout = findViewById(R.id.loveLayout);
        findViewById(R.id.btnLaunchLove).setOnClickListener(v -> {
            int[] lovePictures = new int[]{R.drawable.pl_blue,R.drawable.pl_red,R.drawable.pl_yellow,};
            loveLayout.initLovePicture(lovePictures)
                .launchLove();
        });
    }
}