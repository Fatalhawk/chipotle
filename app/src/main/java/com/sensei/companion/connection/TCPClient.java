package com.sensei.companion.connection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
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

    private final String DEBUG_TAG = "appMonitor";
    private String serverIpNumber;
    //private BufferedReader in;
    //private PrintWriter out;
    private MessageCallback messageListener;
    private boolean connected = false;
    private InputStream in;
    private OutputStream out;

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
                Log.i(DEBUG_TAG, "Sent message: " + messageContent);
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Error sending message", e);
            }
        }
    }

    boolean isRunning () {
        return connected;
    }

    void stopClient () {
        Log.i (DEBUG_TAG, "Client stopped!");
        connected = false;
    }

    void run() {
        final int PORT_NUMBER = 65000; //CHANGE LATER

        try {
            InetAddress serverAddress = InetAddress.getByName (serverIpNumber);
            Log.i (DEBUG_TAG, "Connecting...");
            Socket socket = new Socket (serverAddress, PORT_NUMBER);
            connected = true;
            try {
                out = socket.getOutputStream();
                in = socket.getInputStream();
                Log.i (DEBUG_TAG, "In/Out created");
                while (connected) {
                    byte [] messageSubjectBytes = new byte [4];
                    byte [] messageSizeBytes = new byte [4];
                    in.read (messageSubjectBytes);
                    in.read (messageSizeBytes);
                    int subject = ByteBuffer.wrap(messageSubjectBytes).asIntBuffer().get();
                    int size = ByteBuffer.wrap(messageSizeBytes).asIntBuffer().get();

                    if (messageListener != null) {
                        if (subject == ConnectManager.COMPANION_COMMAND) {
                            byte [] messageContentBytes;
                            messageContentBytes = new byte [size];
                            in.read (messageContentBytes);
                            messageListener.callbackMessageReceiver(messageContentBytes);
                        } else if (subject == ConnectManager.NEW_PROGRAM_INFO) {
                            byte [] picSizeBytes = new byte [4];
                            byte [] programInfoBytes = new byte [size];
                            in.read (picSizeBytes);
                            int picSize = ByteBuffer.wrap (picSizeBytes).asIntBuffer().get();
                            in.read (programInfoBytes);
                            byte [] imageBytes = new byte [picSize];
                            in.read (imageBytes);
                            messageListener.callbackMessageReceiver(programInfoBytes, imageBytes);
                        } else {
                            //TODO: RECEIVE OTHER MESSAGE TYPES IF NECESSARY
                        }
                    } else {
                        Log.d (DEBUG_TAG, "NULL MESSAGE LISTENER");
                    }
                }

            }
            catch (IOException e) {
                Log.e (DEBUG_TAG, "S: Error", e);
            }
            finally {
                out.flush();
                out.close();
                in.close();
                socket.close();
                Log.i(DEBUG_TAG, "Socket Closed");
                messageListener.restartConnection ();
            }
        }
        catch (IOException e) {
            Log.e(DEBUG_TAG, "C: Error", e);
            connected = false;
        }
    }

    interface MessageCallback {
        void callbackMessageReceiver (byte [] messageContent);
        void callbackMessageReceiver (byte [] programInfo, byte[] imageBytes);
        void restartConnection ();
    }
}
