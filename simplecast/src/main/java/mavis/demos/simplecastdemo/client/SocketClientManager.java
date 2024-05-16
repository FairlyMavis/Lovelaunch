package mavis.demos.simplecastdemo.client;

import android.view.Surface;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketClientManager implements ScreenSocketClient.SocketCallback {


    private ScreenDecoder mScreenDecoder;
    private ScreenSocketClient mSocketClient;
    private final String mWsSocket;

    // Constructor
    public SocketClientManager(String wsSocket) {
        mWsSocket = wsSocket;
    }

    public void start(Surface surface) {
        mScreenDecoder = new ScreenDecoder();
        mScreenDecoder.startDecode(surface);
        try {
            // 需要修改为服务端的IP地址与端口
//      URI uri = new URI("ws://192.168.1.122:" + SOCKET_PORT);
            URI uri = new URI(mWsSocket);
            mSocketClient = new ScreenSocketClient(this, uri);
            mSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mSocketClient != null) {
            mSocketClient.close();
        }
        if (mScreenDecoder != null) {
            mScreenDecoder.stopDecode();
        }
    }

    @Override
    public void onReceiveData(byte[] data) {
        if (mScreenDecoder != null) {
            mScreenDecoder.decodeData(data);
        }
    }
}