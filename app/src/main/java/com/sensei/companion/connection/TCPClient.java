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

public class TCPClient {

    private final String DEBUG_TAG = "appMonitor";

    private String ipNumber, incomingMessage;
    private BufferedReader in;
    private PrintWriter out;
    private MessageCallback messageListener = null;
    private boolean connected = false;
    private int portNumber = 4444; //CHANGE LATER

    public TCPClient (String ipNumber, MessageCallback messageListener) {
        this.messageListener = messageListener;
        this.ipNumber = ipNumber;
    }

    public void sendMessage (String message) {
        if (out == null || out.checkError()) {
            Log.d (DEBUG_TAG, "error sending message");
        }
        else {
            out.println (message);
            out.flush();
            Log.i (DEBUG_TAG, "Sent Message: " + message);
        }
    }

    public void stopClient () {
        Log.i (DEBUG_TAG, "Client stopped!");
        connected = false;
    }

    public void run() {
        connected = true;
        try {
            InetAddress serverAddress = InetAddress.getByName (ipNumber);
            Log.i (DEBUG_TAG, "Connecting...");
            Socket socket = new Socket (serverAddress, portNumber);

            try {
                out = new PrintWriter (new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.i (DEBUG_TAG, "In/Out created");

                //this.sendMessage (...)

                while (connected) {
                    incomingMessage = in.readLine ();
                    if (incomingMessage != null && messageListener != null) {
                        Log.i(DEBUG_TAG, "Received Message: " + incomingMessage);
                        messageListener.callbackMessageReceiver (incomingMessage);
                    }
                    else {
                        Log.d (DEBUG_TAG, "Null error");
                    }
                    incomingMessage = null;
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

    public interface MessageCallback {
        public void callbackMessageReceiver (String message);
    }
}
