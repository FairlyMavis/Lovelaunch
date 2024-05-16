package mavis.demos.simplecastdemo.server;

import android.media.projection.MediaProjection;

import java.net.InetSocketAddress;

public class SocketManager {

    private static final String TAG = SocketManager.class.getSimpleName();
    private static final int SOCKET_PORT = 50000;
    private final ScreenSocketServer mScreenSocketServer;
    private ScreenEncoder mScreenEncoder;

    public SocketManager(String ipAddress) {
        mScreenSocketServer = new ScreenSocketServer(new InetSocketAddress(ipAddress,SOCKET_PORT));
    }

    public String getIpAddress() {
        return mScreenSocketServer.getIpAddress();
    }

    public void start(MediaProjection mediaProjection) {
        mScreenSocketServer.start();
        mScreenEncoder = new ScreenEncoder(this, mediaProjection);
        mScreenEncoder.startEncode();
    }

    public void close() {
        try {
            mScreenSocketServer.stop();
            mScreenSocketServer.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mScreenEncoder != null) {
            mScreenEncoder.stopEncode();
        }
    }

    public void sendData(byte[] bytes) {
        mScreenSocketServer.sendData(bytes);
    }
}