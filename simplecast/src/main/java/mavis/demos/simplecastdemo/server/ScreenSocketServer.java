package mavis.demos.simplecastdemo.server;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class ScreenSocketServer extends WebSocketServer {
    private final String TAG = ScreenSocketServer.class.getSimpleName();
    private WebSocket mWebSocket;

    public ScreenSocketServer(InetSocketAddress inetSocketAddress) {
        super(inetSocketAddress);
    }

    public String getIpAddress() {
        return getAddress().toString();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d(TAG, "onOpen");
        mWebSocket = conn;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d(TAG, "onClose:" + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {}

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.d(TAG, "onError:" + ex.toString());
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    public void sendData(byte[] bytes) {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            // 通过WebSocket 发送数据
            Log.d(TAG, "sendData:");
            mWebSocket.send(bytes);
        }
    }

    public void close() {
        mWebSocket.close();
    }
}