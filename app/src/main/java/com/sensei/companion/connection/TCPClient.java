package com.sensei.companion.connection;

import android.util.Log;

import com.sensei.companion.display.AppLauncher;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

class TCPClient {

    private String serverIpNumber;
    private MessageCallback messageListener;
    private boolean connected = false;
    private InputStream in;
    private OutputStream out;
    private Socket socket;

    TCPClient (String ipNumber, MessageCallback messageListener) {
        this.messageListener = messageListener;
        this.serverIpNumber = ipNumber;
    }

    /*
    Assuming the message is in the form:
    COMPANION_COMMAND
    [num bytes]
    [Program] [Command]
     */
    void sendMessage (int messageSubject, String messageContent) {

        if (out != null) {
            byte[] messageSubjectBytes = ByteBuffer.allocate(4).putInt(messageSubject).array();
            byte[] messageContentBytes = messageContent.getBytes();
            byte[] numBytesMessage = ByteBuffer.allocate(4).putInt(messageContentBytes.length).array();
            try {
                out.write(messageSubjectBytes);
                out.write(numBytesMessage);
                out.write(messageContentBytes);
                out.flush();
                Log.i(AppLauncher.DEBUG_TAG, "Sent message: " + messageContent);
            } catch (IOException e) {
                Log.e(AppLauncher.DEBUG_TAG, "Error sending message", e);
            }
        }
    }

    private void receiveMessage () {
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    in = socket.getInputStream();

                    while (connected) {
                        byte[] messageSubjectBytes = new byte[4];
                        byte[] messageSizeBytes = new byte[4];
                        in.read(messageSubjectBytes);
                        in.read(messageSizeBytes);
                        int subject = ByteBuffer.wrap(messageSubjectBytes).asIntBuffer().get();
                        int size = ByteBuffer.wrap(messageSizeBytes).asIntBuffer().get();

                        if (messageListener != null) {
                            if (subject == ConnectManager.COMPANION_COMMAND) {
                                byte[] messageContentBytes;
                                messageContentBytes = new byte[size];
                                in.read(messageContentBytes);
                                messageListener.callbackMessageReceiver(messageContentBytes);
                            } else if (subject == ConnectManager.NEW_PROGRAM_INFO) {
                                byte[] picSizeBytes = new byte[4];
                                byte[] programInfoBytes = new byte[size];
                                in.read(picSizeBytes);
                                int picSize = ByteBuffer.wrap(picSizeBytes).asIntBuffer().get();
                                in.read(programInfoBytes);
                                byte[] imageBytes = new byte[picSize];
                                in.read(imageBytes);
                                messageListener.callbackMessageReceiver(programInfoBytes, imageBytes);
                            } else {
                                //TODO: RECEIVE OTHER MESSAGE TYPES IF NECESSARY
                            }
                        } else {
                            Log.d(AppLauncher.DEBUG_TAG, "NULL MESSAGE LISTENER");
                        }
                    }

                }
                catch (IOException e) {
                    Log.e (AppLauncher.DEBUG_TAG, "S: Error w/ InputStream", e);
                }
            }
        });
        receiveThread.start ();
    }

    void run() {
        final int MY_PORT_NUMBER = 65000; //CHANGE LATER
        try {
            InetAddress serverAddress = InetAddress.getByName (serverIpNumber);
            Log.i (AppLauncher.DEBUG_TAG, "Connecting...");
            Socket socket = new Socket (serverAddress, MY_PORT_NUMBER);
            this.socket = socket;
            connected = true;
            try {
                out = socket.getOutputStream();
                receiveMessage();
                Log.i (AppLauncher.DEBUG_TAG, "In/Out created");
            }
            catch (IOException e) {
                Log.e (AppLauncher.DEBUG_TAG, "S: Error", e);
            }
            finally {
                out.flush();
                out.close();
                in.close();
                socket.close();
                Log.i(AppLauncher.DEBUG_TAG, "Socket Closed");
                messageListener.restartConnection ();
            }
        }
        catch (IOException e) {
            Log.e(AppLauncher.DEBUG_TAG, "C: Error", e);
            connected = false;
        }
    }

    boolean isRunning () {
        return connected;
    }

    void stopClient () {
        Log.i (AppLauncher.DEBUG_TAG, "Client stopped!");
        connected = false;
    }

    interface MessageCallback {
        void callbackMessageReceiver (byte [] messageContent);
        void callbackMessageReceiver (byte [] programInfo, byte[] imageBytes);
        void restartConnection ();
    }
}
