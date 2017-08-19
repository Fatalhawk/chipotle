package com.sensei.companion.connection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sensei.companion.R;
import com.sensei.companion.connection.commands.CommandsData;
import com.sensei.companion.connection.messages.CMessage;
import com.sensei.companion.connection.messages.CommandMessage;
import com.sensei.companion.connection.messages.ProgramInfoMessage;
import com.sensei.companion.display.AppLauncher;

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
    private int counter = 0;
    private ConnectManager.MessageHandler mHandler;

    public void init (final ConnectManager.MessageHandler mHandler) {
        this.mHandler = mHandler;
        Log.i (AppLauncher.DEBUG_TAG, "My Service Created");

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //init broadcast receiver which will hear PCs trying to connect
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(DISCOVERY_PORT);
                    socket.setBroadcast(true);
                    socket.setSoTimeout(TIMEOUT_MS);
                }
                catch (IOException e) {
                    Log.e(DEBUG_TAG, "Could not send discovery request", e);
                }
                Log.i (DEBUG_TAG, "UDP broadcast listener socket created");
                final int SEARCH_TIMEOUT = 3000;
                long searchStartTime = System.currentTimeMillis();
                while (serverIpAddress == null) {
                    if (System.currentTimeMillis() - searchStartTime > SEARCH_TIMEOUT) {
                        break;
                    }
                    listenForBroadcasts(socket);
                }

                //Update search status on UI
                if (serverIpAddress == null) {
                    Log.i (AppLauncher.DEBUG_TAG, "what");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)mHandler.getPopUpWindow().getContentView().findViewById(R.id.textview_search_status)).setText("Could not find a PC!");
                        }
                    });
                    if (socket != null) {
                        socket.close();
                    }
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)mHandler.getPopUpWindow().getContentView().findViewById(R.id.textview_search_status)).setText("Found PC: " + serverIpAddress);
                    }
                });

                //Connect to the IP of the PC found
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        (mHandler.getPopUpWindow().getContentView().findViewById(R.id.button_connect_pc)).setOnClickListener(new Button.OnClickListener() {
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

                    @Override
                    public void callbackMessageReceiver(CommandMessage message) {

                    }

                    @Override
                    public void callbackMessageReceiver(ProgramInfoMessage message) {

                    }

                    /*
                    //FOR COMPANION_COMMAND messages
                    @Override
                    public void callbackMessageReceiver(byte[] messageContent) {
                        Log.i (DEBUG_TAG, "Received message: " + new String (messageContent));
                        CommandsData.handleCommand(mHandler, messageContent);
                    }

                    //For NEW_PROGRAM_INFO messages in the form "[Program]"
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
                    */

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

    public void terminateConnection () {
        if (tcpClient != null && tcpClient.isRunning()) {
            tcpClient.stopClient ();
        }
    }

    public void sendMessageToPC (CMessage message) {
        if (tcpClient != null && tcpClient.isRunning()) {
            tcpClient.sendMessage (message);
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
