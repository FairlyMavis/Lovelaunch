package ocean.playground.lovelaunch;

import android.content.ComponentName;
import android.content.Intent;

public class IntentHelper {

    public static Intent getMessengerDemoService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("ocean.playground.messengerdemoservice",
                "ocean.playground.messengerdemoservice.ServiceDemoService"));
        return intent;
    }

    public static Intent getMessengerDemoClient() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("ocean.playground.messengerdemoclient",
                "ocean.playground.messengerdemoclient.ClientActivity"));
        return intent;
    }

}
