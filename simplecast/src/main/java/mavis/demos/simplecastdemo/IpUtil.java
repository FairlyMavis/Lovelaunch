package mavis.demos.simplecastdemo;

import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

public class IpUtil {
    private static final String TAG ="IpUtil";
    public static String getP2pLinkIpAddress() {
        String ipAddress = "";
        //get ip address of p2p iface
        try {
            for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (iface.getName().startsWith("p2p")) {
                    for (InetAddress address : Collections.list(iface.getInetAddresses())) {
                        if (address instanceof Inet4Address) {
                            ipAddress = address.getHostAddress();
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "Unable to get IP address of p2p interface");
        }
        return ipAddress;
    }
}