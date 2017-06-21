package com.sensei.companion.connection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ConnectService extends Service {

    private final String DEBUG_TAG = "appMonitor";
    private final IBinder connectBinder = new MyLocalBinder ();
    private TCPClient tcpClient;
    private String serverIpAddress;
    private Handler mHandler;

    public ConnectService() {
    }

    public void init (final Handler mHandler) {
        this.mHandler = mHandler;

        serverIpAddress = getLocalIpAddress();
        if (serverIpAddress == null) {
            Log.d (DEBUG_TAG, "Could not find IP Address");
        }

        Toast.makeText (this, "My Service Created", Toast.LENGTH_LONG).show();
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                tcpClient = new TCPClient(serverIpAddress, new TCPClient.MessageCallback() {
                    @Override
                    public void callbackMessageReceiver(String message) {
                        //TODO: filter and use received information
                        //example below:
                        //if (message == important event occurred)
                        //  mHandler.sendEmptyMessageDelayed (MainActivity.SHUTDOWN, 2000);
                    }
                });

                tcpClient.run ();
            }
        });
        networkThread.start ();
    }

    public void terminateConnection () {
        if (tcpClient != null && tcpClient.isRunning()) {
            tcpClient.stopClient ();
        }
    }

    public void sendMessageToPC (String message) {
        if (tcpClient != null && tcpClient.isRunning()) {
            tcpClient.sendMessage (message);
        }
    }

    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(DEBUG_TAG, ex.toString());
        }
        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return connectBinder;
    }

    class MyLocalBinder extends Binder {
        ConnectService getService() {
            return ConnectService.this;
        }
    }
}
