package com.sensei.companion.connection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

class TCPClient {

    private final String DEBUG_TAG = "appMonitor";
    private String serverIpNumber;
    private BufferedReader in;
    private PrintWriter out;
    private MessageCallback messageListener;
    private boolean connected = false;

    TCPClient (String ipNumber, MessageCallback messageListener) {
        this.messageListener = messageListener;
        this.serverIpNumber = ipNumber;
    }

    void sendMessage (String message) {
        if (out == null || out.checkError()) {
            Log.d (DEBUG_TAG, "error sending message");
        }
        else {
            out.println (message);
            out.flush();
            Log.i (DEBUG_TAG, "Sent message: " + message);
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
                out = new PrintWriter (new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.i (DEBUG_TAG, "In/Out created");
                this.sendMessage ("Yoooooo<EC>");
                while (connected) {
                    try {
                        String messageSubject = in.readLine ();
                        String messageContent = in.readLine ();
                        if (messageSubject != null && messageContent != null && messageListener != null) {
                            messageListener.callbackMessageReceiver(messageSubject, messageContent);
                        }
                    } catch (Exception e) {
                        Log.e (DEBUG_TAG, "Message Error", e);
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
        void callbackMessageReceiver (String messageSubject, String messageContent);
        void restartConnection ();
    }
}
