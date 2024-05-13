package ocean.playground.messengerdemoclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class ClientActivity extends AppCompatActivity {
    public static final int SUM_CALCULATE = 0x001;

    private Messenger mClientMessenger;
    private Messenger mServiceMessenger;
    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceMessenger = new Messenger(service);
            mConnectStatusView.setText("Connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mConnectStatusView.setText("Disconnect!");
        }

        @Override
        public void onNullBinding(ComponentName name) {
            ServiceConnection.super.onNullBinding(name);
            mConnectStatusView.setText("onNullBinding!");
        }

        @Override
        public void onBindingDied(ComponentName name) {
            ServiceConnection.super.onBindingDied(name);
            mConnectStatusView.setText("onBindingDied!");
        }
    };
    private LinearLayout mResultDisplayLayout;
    private TextView mCurrentTextView;
    private TextView mConnectStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mConnectStatusView = findViewById(R.id.connect_status);
        mResultDisplayLayout = findViewById(R.id.results_display);

        //connect to service
        Intent intent = new Intent();
        intent.setClassName("ocean.playground.messengerdemoservice",
                "ocean.playground.messengerdemoservice.ServiceDemoService");
        bindService(intent,mConn,BIND_AUTO_CREATE);
//        startForegroundService(intent);
        mClientMessenger = new Messenger(new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msgFromServer) {
                super.handleMessage(msgFromServer);
                if (msgFromServer.what != SUM_CALCULATE) {
                    return;
                }
                int sum = msgFromServer.arg2;
                StringBuilder newText = new StringBuilder(mCurrentTextView.getText());
                newText.append(" = ").append(sum);
                mCurrentTextView.setText(newText);

                mResultDisplayLayout.addView(mCurrentTextView);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
    }

    public void calculateAddNumber(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int calculateTime = 10;
                do {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doOneCalculate();
                        }
                    });
                    calculateTime --;
                    SystemClock.sleep(100);
                } while (calculateTime > 0);
            }
        }).start();
    }

    private void doOneCalculate() {
        Random random = new Random();
        int firstInt = random.nextInt(100);
        int secondInt = random.nextInt(100);

        TextView oneResultView = new TextView(this);
        mCurrentTextView = oneResultView;
        StringBuilder oneLineText = new StringBuilder(firstInt + " + " + secondInt + " = ");
        oneResultView.setText(oneLineText);

        //transact to service
        oneLineText.append(" calculating...");
        oneResultView.setText(oneLineText);

        //archive and show result
        try {
            Message clientMsg = Message.obtain(null,SUM_CALCULATE,firstInt,secondInt);
            clientMsg.replyTo = mClientMessenger;
            mServiceMessenger.send(clientMsg);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
}