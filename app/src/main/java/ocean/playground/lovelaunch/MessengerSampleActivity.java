package ocean.playground.lovelaunch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MessengerSampleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_sample);
    }

    public void openServiceComponent(View v) {
        startActivity(IntentHelper.getMessengerDemoService());
    }

    public void openClientComponent(View v) {
        startActivity(IntentHelper.getMessengerDemoClient());
    }
}
