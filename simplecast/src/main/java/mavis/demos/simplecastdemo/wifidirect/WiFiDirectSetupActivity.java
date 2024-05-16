package mavis.demos.simplecastdemo.wifidirect;import android.Manifest;import android.annotation.SuppressLint;import android.app.Activity;import android.content.BroadcastReceiver;import android.content.Context;import android.content.Intent;import android.content.IntentFilter;import android.content.pm.PackageManager;import android.net.wifi.WifiManager;import android.net.wifi.p2p.WifiP2pConfig;import android.net.wifi.p2p.WifiP2pDevice;import android.net.wifi.p2p.WifiP2pManager;import android.net.wifi.p2p.WifiP2pManager.ActionListener;import android.net.wifi.p2p.WifiP2pManager.Channel;import android.net.wifi.p2p.WifiP2pManager.ChannelListener;import android.os.Build;import android.os.Bundle;import android.provider.Settings;import android.util.Log;import android.view.Menu;import android.view.MenuInflater;import android.view.MenuItem;import android.widget.Toast;import java.net.Inet4Address;import java.net.InetAddress;import java.net.NetworkInterface;import java.net.SocketException;import java.util.Collections;import mavis.demos.simplecastdemo.IpUtil;import mavis.demos.simplecastdemo.R;public class WiFiDirectSetupActivity extends Activity implements ChannelListener,        DeviceListFragment.DeviceActionListener {    public static final String TAG = "wifidirectdemo";    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;    private static final int PERMISSIONS_REQUEST_CODE_NEARBY_WIFI_DEVICES = 1002;    private WifiP2pManager manager;    private boolean isWifiP2pEnabled = false;    private boolean retryChannel = false;    private final IntentFilter intentFilter = new IntentFilter();    private Channel channel;    private BroadcastReceiver receiver = null;    /**     * @param isWifiP2pEnabled the isWifiP2pEnabled to set     */    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {        this.isWifiP2pEnabled = isWifiP2pEnabled;    }    @Override    public void onRequestPermissionsResult(int requestCode, String[] permissions,                                           int[] grantResults) {        switch (requestCode) {            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION:                if  (grantResults[0] != PackageManager.PERMISSION_GRANTED) {                    Log.e(TAG, "Fine location permission is not granted!");                    finish();                }                break;        }    }    private boolean initP2p() {        // Device capability definition check        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {            Log.e(TAG, "Wi-Fi Direct is not supported by this device.");            return false;        }        // Hardware capability check        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);        if (wifiManager == null) {            Log.e(TAG, "Cannot get Wi-Fi system service.");            return false;        }        if (!wifiManager.isP2pSupported()) {            Log.e(TAG, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");            return false;        }        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);        if (manager == null) {            Log.e(TAG, "Cannot get Wi-Fi Direct system service.");            return false;        }        channel = manager.initialize(this, getMainLooper(), null);        if (channel == null) {            Log.e(TAG, "Cannot initialize Wi-Fi Direct.");            return false;        }        return true;    }    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_wifi_direct_setup);        // add necessary intent values to be matched.        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);        if (!initP2p()) {            finish();        }        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)                != PackageManager.PERMISSION_GRANTED) {            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},                    PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);        }        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU                && checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES)                != PackageManager.PERMISSION_GRANTED) {            requestPermissions(new String[]{Manifest.permission.NEARBY_WIFI_DEVICES},                    PERMISSIONS_REQUEST_CODE_NEARBY_WIFI_DEVICES);        }    }    /** register the BroadcastReceiver with the intent values to be matched */    @Override    public void onResume() {        super.onResume();        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);        registerReceiver(receiver, intentFilter);    }    @Override    public void onPause() {        super.onPause();        unregisterReceiver(receiver);    }    @Override    public void onBackPressed() {        Intent intent = getIntent();        String ipAddress = IpUtil.getP2pLinkIpAddress();        intent.putExtra("address",ipAddress);        setResult(RESULT_OK,intent);        finish();        super.onBackPressed();    }    /**     * Remove all peers and clear all fields. This is called on     * BroadcastReceiver receiving a state change event.     */    public void resetData() {        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()                .findFragmentById(R.id.frag_list);        if (fragmentList != null) {            fragmentList.clearPeers();        }    }    @Override    public boolean onCreateOptionsMenu(Menu menu) {        MenuInflater inflater = getMenuInflater();        inflater.inflate(R.menu.action_items, menu);        return true;    }    @SuppressLint("MissingPermission")    @Override    public boolean onOptionsItemSelected(MenuItem item) {        if (item.getItemId() == R.id.atn_direct_enable) {            if (manager != null && channel != null) {                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));            } else {                Log.e(TAG, "channel or manager is null");            }            return true;        } else if (item.getItemId() == R.id.atn_direct_discover) {            if (!isWifiP2pEnabled) {                Toast.makeText(WiFiDirectSetupActivity.this, R.string.p2p_off_warning,                        Toast.LENGTH_SHORT).show();                return true;            }            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()                    .findFragmentById(R.id.frag_list);            fragment.onInitiateDiscovery();            manager.discoverPeers(channel, new ActionListener() {                @Override                public void onSuccess() {                    Toast.makeText(WiFiDirectSetupActivity.this, "Discovery Initiated",                            Toast.LENGTH_SHORT).show();                }                @Override                public void onFailure(int reasonCode) {                    Toast.makeText(WiFiDirectSetupActivity.this, "Discovery Failed : " + reasonCode,                            Toast.LENGTH_SHORT).show();                }            });            return true;        } else {            return super.onOptionsItemSelected(item);        }    }    @SuppressLint("MissingPermission")    @Override    public void connect(WifiP2pConfig config) {        manager.connect(channel, config, new ActionListener() {            @Override            public void onSuccess() {                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.            }            @Override            public void onFailure(int reason) {                Toast.makeText(WiFiDirectSetupActivity.this, "Connect failed. Retry.",                        Toast.LENGTH_SHORT).show();            }        });    }    @Override    public void disconnect() {        manager.removeGroup(channel, new ActionListener() {            @Override            public void onFailure(int reasonCode) {                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);            }            @Override            public void onSuccess() {                Log.d(TAG, "Disconnect Success");            }        });    }    @Override    public void onChannelDisconnected() {        // we will try once more        if (manager != null && !retryChannel) {            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();            resetData();            retryChannel = true;            manager.initialize(this, getMainLooper(), this);        } else {            Toast.makeText(this,                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",                    Toast.LENGTH_LONG).show();        }    }    @Override    public void cancelDisconnect() {        if (manager != null) {            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()                    .findFragmentById(R.id.frag_list);            if (fragment.getDevice() == null                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {                disconnect();            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {                manager.cancelConnect(channel, new ActionListener() {                    @Override                    public void onSuccess() {                        Toast.makeText(WiFiDirectSetupActivity.this, "Aborting connection",                                Toast.LENGTH_SHORT).show();                    }                    @Override                    public void onFailure(int reasonCode) {                        Toast.makeText(WiFiDirectSetupActivity.this,                                "Connect abort request failed. Reason Code: " + reasonCode,                                Toast.LENGTH_SHORT).show();                    }                });            }        }    }}