package com.sensei.companion.connection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

public class ConnectService extends Service {

    private final String DEBUG_TAG = "appMonitor";
    private final IBinder connectBinder = new MyLocalBinder ();
    private TCPClient tcpClient;
    private String serverIpAddress;
    private InetAddress deviceIpAddress;
    private static Handler mHandler;
    private int networkPrefixLength;
    private static final int DISCOVERY_PORT = 4444;
    private static final int TIMEOUT_MS = 500;
    private String s = null;
    private TextView serverStatus;
    private Button connectButton;
    private int counter = 0;

    private String checkHosts (InetAddress deviceAddress){
        byte[] ip = deviceAddress.getAddress();
        String output = "";
        for (int i = 1; i <= 254; i++) {
            try {
                ip[3] = (byte) i;
                InetAddress address = InetAddress.getByAddress(ip);

                if (address.isReachable(100)) {
                    output = address.getHostAddress();
                    System.out.print(output + " is on the network");
                    return output;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * Listen on socket for responses, timing out after TIMEOUT_MS
     */
    private void listenForBroadcasts(DatagramSocket socket) {
        counter ++;
        Log.i (DEBUG_TAG, "Listening " + counter);
        byte[] buf = new byte[1024];
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                //s = new String(packet.getData(), 0, packet.getLength());
                s = "IP: " + packet.getAddress().getHostAddress();
                serverIpAddress = packet.getAddress().getHostAddress();
                Log.i (DEBUG_TAG, serverIpAddress);
                //Log.d(DEBUG_TAG, "Received response " + s + " from IP: " + socket.getInetAddress().getHostAddress());
            }
        } catch (SocketTimeoutException e) {
            Log.d(DEBUG_TAG, "Receive timed out");
        }
        catch (IOException e) {
            Log.e (DEBUG_TAG, "IOEXCEPTION", e);
        }
    }

    public void init (final Handler mHandler, TextView textView, Button button) {
        this.mHandler = mHandler;

        //serverIpAddress = "192.168.2.96";
        //s = "IP: " + serverIpAddress;

        /*
        deviceIpAddress = getLocalIpAddress();
        if (deviceIpAddress == null) {
            Log.d (DEBUG_TAG, "Could not find IP Address");
        }
        else {
            Log.i (DEBUG_TAG, "Local IP Address: " + deviceIpAddress.getHostAddress() + "/" + networkPrefixLength);
        }
        serverIpAddress = checkHosts(deviceIpAddress);
        Log.i (DEBUG_TAG, "Server address: " + serverIpAddress);
        //serverIpAddress = "100.64.188.96";
         */

        final TextView serverStatus = textView;
        final Button connectButton = button;

        Toast.makeText (this, "My Service Created", Toast.LENGTH_LONG).show();
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(DISCOVERY_PORT);
                    socket.setBroadcast(true);
                    socket.setSoTimeout(TIMEOUT_MS);
                }
                catch (IOException e) {
                    Log.e(DEBUG_TAG, "Could not send discovery request", e);
                }

                Log.i (DEBUG_TAG, "socket created");
                while (s == null) {
                    listenForBroadcasts(socket);
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Found PC: " + s);
                        connectButton.setOnClickListener(new Button.OnClickListener() {
                            public void onClick(View v) {
                                mHandler.sendEmptyMessageDelayed (ConnectManager.INIT_TOUCHBAR, 2000);
                                connectToPc();
                            }
                        });
                    }
                });
            }
        });
        networkThread.start ();
    }

    public void connectToPc () {
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                tcpClient = new TCPClient(serverIpAddress, new TCPClient.MessageCallback() {
                    @Override
                    public void callbackMessageReceiver(String message) {
                        Log.i (DEBUG_TAG, "Received message: " + message);
                        //TODO: filter and use received information
                        //example below:
                        //if (message == important event occurred)
                        //  mHandler.sendEmptyMessageDelayed (ConnectManager.EXAMPLE_MESSAGE, 2000);
                    }
                    @Override
                    public void restartConnection () {
                        connectToPc();
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
        else {
            Log.d (DEBUG_TAG, "Error sending");
        }
    }

    public void checkHosts(String subnet){
        int timeout=1000;
        for (int i=1;i<255;i++){
            String host = subnet + "." + i;
            try {
                if (InetAddress.getByName(host).isReachable(timeout)) {
                    Log.i (DEBUG_TAG, host + " is reachable");
                }
            }
            catch (Exception e) {
            }
        }
    }

    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private InetAddress getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        for(InterfaceAddress address: intf.getInterfaceAddresses()){
                            if(address.getNetworkPrefixLength() < 24){
                                networkPrefixLength = address.getNetworkPrefixLength();
                                return inetAddress;  //.getHostAddress for string version
                            }
                        }
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
