package ocean.playground.messengerdemoservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;

import androidx.annotation.NonNull;

public class ServiceDemoService extends Service {

    public static final int SUM_CALCULATE = 0x001;
    private  Messenger mServiceMessenger;


    @Override
    public void onCreate() {
        super.onCreate();
//        createNotificationChannel();
        SumCalculateThread sumCalculateThread = new SumCalculateThread("calculate", Process.THREAD_PRIORITY_DEFAULT);
        sumCalculateThread.start();
        mServiceMessenger = new Messenger(new Handler(sumCalculateThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message fromClientMsg) {
                super.handleMessage(fromClientMsg);
                if (fromClientMsg.what != SUM_CALCULATE) {
                    return;
                }
                try {
                    Message replayMsg = Message.obtain(fromClientMsg);
                    replayMsg.arg2 = fromClientMsg.arg1 + fromClientMsg.arg2;
                    fromClientMsg.replyTo.send(replayMsg);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mServiceMessenger.getBinder();
    }
}