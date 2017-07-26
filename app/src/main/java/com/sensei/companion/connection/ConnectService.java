package com.sensei.companion.connection;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sensei.companion.connection.commands.CommandsData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class ConnectService extends Service {

    private final String DEBUG_TAG = "appMonitor";
    private final IBinder connectBinder = new MyLocalBinder ();
    private TCPClient tcpClient;
    private String serverIpAddress;
    private static final int DISCOVERY_PORT = 4444; //CHANGE LATER
    private static final int TIMEOUT_MS = 500;
    private String s = null;
    private int counter = 0;
    private Handler mHandler;

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
        } catch (IOException e) {
            Log.e (DEBUG_TAG, "IOEXCEPTION", e);
        }
    }

    public void init (final Handler mHandler, TextView textView, Button button) {
        final TextView serverStatus = textView;
        final Button connectButton = button;

        this.mHandler = mHandler;

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
                                mHandler.sendEmptyMessage (ConnectManager.INIT_TOUCHBAR);
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
                    /*
                    For COMPANION_COMMAND messages
                     */
                    @Override
                    public void callbackMessageReceiver(byte[] messageContent) {
                        Log.i (DEBUG_TAG, "Received message: " + new String (messageContent));
                        CommandsData.handleCommand(mHandler, messageContent);
                    }
                    /*
                    For NEW_PROGRAM_INFO messages in the form "[Program]"
                     */
                    @Override
                    public void callbackMessageReceiver(byte[] programInfoBytes, byte[] imageBytes) {
                        Log.i (DEBUG_TAG, "Received program info & pic: " + new String (programInfoBytes));

                        Message myHandlerMessage = Message.obtain();
                        Bundle myBundle = new Bundle ();
                        myBundle.putByteArray (ConnectManager.PROGRAM_INFO, programInfoBytes);
                        myBundle.putByteArray (ConnectManager.IMAGE_BYTES, imageBytes);
                        myHandlerMessage.setData (myBundle);
                        myHandlerMessage.what = ConnectManager.NEW_PROGRAM_INFO;

                        boolean handlerSuccess = mHandler.sendMessage (myHandlerMessage);
                        if (!handlerSuccess) {
                            Log.d (DEBUG_TAG, "Handler message error: NEW_PROGRAM_INFO");
                        }
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

    public void sendMessageToPC (int messageSubject, String messageContent) {
        if (tcpClient != null && tcpClient.isRunning()) {
            tcpClient.sendMessage (messageSubject, messageContent);
        }
        else {
            Log.d (DEBUG_TAG, "Error sending");
        }
    }

    //////////////////////////////////////// BINDER STUFF ////////////////////////////////////////

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
