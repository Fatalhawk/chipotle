package com.sensei.companion.communication.connection;

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
import com.sensei.companion.communication.commands.CommandsData;
import com.sensei.companion.communication.messages.CMessage;
import com.sensei.companion.communication.messages.CommandMessage;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.proto.ProtoMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class ConnectService extends Service {
    private final IBinder connectBinder = new MyLocalBinder ();
    private TCPClient tcpClient;
    private String serverIpAddress;
    private static final int DISCOVERY_PORT = 4444; //CHANGE LATER
    private static final int TIMEOUT_MS = 500;
    private int counter = 0;
    private MessageHandler mHandler;

    public void init (final MessageHandler mHandler) {
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
                    Log.e(AppLauncher.DEBUG_TAG, "[ConnectService] Could not send discovery request", e);
                }
                Log.i (AppLauncher.DEBUG_TAG, "[ConnectService] UDP broadcast listener socket created");
                final int SEARCH_TIMEOUT = 5000;
                long searchStartTime = System.currentTimeMillis();
                while (serverIpAddress == null) {
                    Log.i(AppLauncher.DEBUG_TAG, "test1");
                    if (System.currentTimeMillis() - searchStartTime > SEARCH_TIMEOUT) {
                        break;
                    }
                    listenForBroadcasts(socket);
                }

                //Update search status on UI
                if (serverIpAddress == null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.getPopUpWindow().setOutsideTouchable(true);
                            mHandler.getPopUpWindow().update();
                            mHandler.getPopUpWindow().getContentView().findViewById(R.id.button_connect_pc).setVisibility(View.VISIBLE);
                            ((TextView)mHandler.getPopUpWindow().getContentView().findViewById(R.id.textview_search_status)).setText("Could not find a PC!");
                        }
                    });
                    //if (socket != null) {
                    //    socket.close();
                    //}
                    return;
                }

                Log.i(AppLauncher.DEBUG_TAG, "be4 null");

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.getPopUpWindow().setOutsideTouchable(true);
                        mHandler.getPopUpWindow().update();
                        ((TextView)mHandler.getPopUpWindow().getContentView().findViewById(R.id.textview_search_status)).setText("Found PC: " + serverIpAddress);
                        Button connectButton = (Button)mHandler.getPopUpWindow().getContentView().findViewById(R.id.button_connect_pc);
                        connectButton.setVisibility(View.VISIBLE);
                        connectButton.setText("Connect to PC");
                        connectButton.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mHandler.sendEmptyMessage (MessageHandler.INIT_TOUCHBAR);
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
                    public void callbackMessageReceiver(final ProtoMessage.CommMessage message, final String messageId) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                switch (message.getMessageType()) {
                                    case COMMAND:
                                        CommandsData.handleCommand(mHandler, new CommandMessage(message));
                                        break;
                                    case PROGRAM_INFO:
                                        Message myHandlerMessage = Message.obtain();
                                        Bundle myBundle = new Bundle ();
                                        myBundle.putByteArray (MessageHandler.PROGRAM_INFO_MESSAGE, message.toByteArray());
                                        myHandlerMessage.setData(myBundle);
                                        myHandlerMessage.what = MessageHandler.NEW_PROGRAM_INFO;
                                        if (!mHandler.sendMessage(myHandlerMessage)) {
                                            Log.d (AppLauncher.DEBUG_TAG, "[ConnectService] Handler message error: NEW_PROGRAM_INFO");
                                        }
                                        break;
                                    default:
                                        Log.d (AppLauncher.DEBUG_TAG, "[ConnectService] Unexpected message type received");
                                        break;
                                }
                                tcpClient.sendReplyMessage (messageId); //send reply back w/ same id
                            }
                        });
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

    /**
     * Listen on socket for responses, timing out after TIMEOUT_MS
     */
    private void listenForBroadcasts(DatagramSocket socket) {
        counter ++;
        Log.i (AppLauncher.DEBUG_TAG, "[ConnectService] Listening " + counter);
        byte[] buf = new byte[1024];
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                serverIpAddress = packet.getAddress().getHostAddress();
                break;
            }
        } catch (SocketTimeoutException e) {
        } catch (IOException e) {
            Log.e (AppLauncher.DEBUG_TAG, "[ConnectService] IOException occured when listening for udp broadcasts", e);
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
            Log.d (AppLauncher.DEBUG_TAG, "[ConnectService] Error sending message to PC");
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
