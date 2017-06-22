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

    private String ipNumber;
    private BufferedReader in;
    private PrintWriter out;
    private MessageCallback messageListener;
    private boolean connected = false;
    private int portNumber = 65000; //CHANGE LATER

    TCPClient (String ipNumber, MessageCallback messageListener) {
        this.messageListener = messageListener;
        this.ipNumber = ipNumber;
    }

    void sendMessage (String message) {
        if (out == null || out.checkError()) {
            Log.d (DEBUG_TAG, "error sending message");
        }
        else {
            out.println (message);
            out.flush();
            Log.i (DEBUG_TAG, "Sent Message: " + message);
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
        try {
            InetAddress serverAddress = InetAddress.getByName (ipNumber);
            Log.i (DEBUG_TAG, "Connecting...");
            Socket socket = new Socket (serverAddress, portNumber);
            connected = true;
            try {
                out = new PrintWriter (new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.i (DEBUG_TAG, "In/Out created");
                this.sendMessage ("Yoooooo<EC>");
                while (connected) {
                    String incomingMessage = in.readLine ();
                    if (incomingMessage != null && messageListener != null) {
                        //Log.i(DEBUG_TAG, "Received Message: " + incomingMessage);
                        messageListener.callbackMessageReceiver (incomingMessage);
                    }
                    //else {
                    //    Log.d (DEBUG_TAG, "Null error");
                    //}
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
            }
        }
        catch (IOException e) {
            Log.e(DEBUG_TAG, "C: Error", e);
            connected = false;
        }
    }

    interface MessageCallback {
        void callbackMessageReceiver (String message);
    }
}
