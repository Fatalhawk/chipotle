package com.sensei.companion.connection;

import android.util.Log;

import com.sensei.companion.connection.messages.CMessage;
import com.sensei.companion.connection.messages.CommandMessage;
import com.sensei.companion.display.AppLauncher;
import com.sensei.companion.proto.ProtoMessage;

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
import java.util.concurrent.LinkedBlockingQueue;

class TCPClient {

    private String serverIpNumber;
    private MessageCallback messageListener;
    private boolean connected = false;
    private InputStream in;
    private OutputStream out;
    private Socket socket;
    private LinkedBlockingQueue<String> blockingQueue;

    TCPClient (String ipNumber, MessageCallback messageListener) {
        this.messageListener = messageListener;
        this.serverIpNumber = ipNumber;
    }

    /*
    Sends #bytes, waits for a reply consisting of that #bytes again, sends message, waits for reply
    consisting of that message's unique id.
     */
    void sendMessage (CMessage message) {
        ProtoMessage.CommMessage protoMessage = null;
        if (message.getType() == ProtoMessage.CommMessage.MessageType.COMMAND) {
            protoMessage = ((CommandMessage)message).getProtoMessage();
        }

        if (protoMessage != null) {

            int numBytes = protoMessage.toByteArray().length;
            byte [] numBytesMessage = ByteBuffer.allocate(4).putInt(numBytes).array();

            try {
                blockingQueue = new LinkedBlockingQueue<>(2);

                try {
                    out.write(numBytesMessage);
                    String reply1 = blockingQueue.take(); //wait for reply -- #bytes
                    if (Integer.parseInt(reply1) != numBytes) {
                        //TODO: WHAT TO DO IF PC SENDS BACK WRONG NUMBER
                    }
                    else {
                        protoMessage.writeTo(out);
                        String reply2 = blockingQueue.take(); //wait for reply -- message id -- 36 bytes in string form
                        if (!protoMessage.getMessageId().equals(reply2)) {
                            //TODO: WHAT TO DO IF PC SENDS BACK WRONG ID
                        }
                        else {
                            Log.i(AppLauncher.DEBUG_TAG, "[TCPClient] Successfully sent message: " + protoMessage.toString());
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e (AppLauncher.DEBUG_TAG, "[TCPClient] blocking queue's take interrupted", e);
                }

                out.flush ();
            } catch (IOException e) {
                Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] Error sending message", e);
            }
        }
        else {
            Log.d (AppLauncher.DEBUG_TAG, "[TCPClient] Error sending message: invalid message type");
        }
    }

    private void receiveMessage () {
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    in = socket.getInputStream();

                    while (connected) {

                        /*
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
                            Log.d(AppLauncher.DEBUG_TAG, "[TCPClient] NULL MESSAGE LISTENER");
                        }
                        */

                    }

                }
                catch (IOException e) {
                    Log.e (AppLauncher.DEBUG_TAG, "[TCPClient] S: Error w/ InputStream", e);
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
                Log.i (AppLauncher.DEBUG_TAG, "[TCPClient] In/Out created");
            }
            catch (IOException e) {
                Log.e (AppLauncher.DEBUG_TAG, "[TCPClient] S: Error", e);
            }
            finally {
                out.flush();
                out.close();
                in.close();
                socket.close();
                Log.i(AppLauncher.DEBUG_TAG, "[TCPClient] Socket Closed");
                messageListener.restartConnection ();
            }
        }
        catch (IOException e) {
            Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] C: Error", e);
            connected = false;
        }
    }

    boolean isRunning () {
        return connected;
    }

    void stopClient () {
        Log.i (AppLauncher.DEBUG_TAG, "[TCPClient] Client stopped!");
        connected = false;
    }

    interface MessageCallback {
        void callbackMessageReceiver (byte [] messageContent);
        void callbackMessageReceiver (byte [] programInfo, byte[] imageBytes);
        void restartConnection ();
    }
}
