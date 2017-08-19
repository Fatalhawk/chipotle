package com.sensei.companion.connection;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sensei.companion.connection.messages.CMessage;
import com.sensei.companion.connection.messages.CommandMessage;
import com.sensei.companion.connection.messages.ProgramInfoMessage;
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
    private boolean isWaitingForReply;

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
                //send
                out.write(numBytesMessage);
                protoMessage.writeTo(out);

                //wait for reply
                try {
                    blockingQueue = new LinkedBlockingQueue<>(1);
                    isWaitingForReply = true;
                    String replyId = blockingQueue.take(); //wait for reply -- #bytes
                    if (!protoMessage.getMessageId().equals(replyId)) {
                        //TODO: WHAT TO DO IF PC SENDS BACK WRONG NUMBER
                    } else {
                        isWaitingForReply = false;
                        Log.i(AppLauncher.DEBUG_TAG, "[TCPClient] Successfully sent message: " + protoMessage.toString());
                    }
                } catch (InterruptedException e) {
                    Log.e (AppLauncher.DEBUG_TAG, "[TCPClient] blocking queue's take interrupted", e);
                    //TODO: error trap
                }

                out.flush ();
            } catch (IOException e) {
                Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] Error sending message", e);
                //TODO: error trap
            }
        }
        else {
            Log.d (AppLauncher.DEBUG_TAG, "[TCPClient] Error sending message: invalid message type");
            //TODO: error trap
        }
    }

    /*
    Checks whether the message is a reply or a newly issued command
     */
    private void messageGuider (byte [] messageBytes) {

        try {
            ProtoMessage.CommMessage message = ProtoMessage.CommMessage.parseFrom(messageBytes);
            if (message.getMessageType() == ProtoMessage.CommMessage.MessageType.REPLY) {
                if (isWaitingForReply) {
                    String replyId = message.getMessageId();
                    try {
                        blockingQueue.put(replyId);
                    } catch (InterruptedException e) {
                        Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] blocking queue's put interrupted", e);
                        //TODO: error trap
                    }
                } else {
                    Log.d (AppLauncher.DEBUG_TAG, "[TCPClient] Error - got reply when not waiting for reply");
                    //TODO: error trap
                }
            } else {
                if (messageListener != null) {
                    if (message.getMessageType() == ProtoMessage.CommMessage.MessageType.COMMAND) {
                        messageListener.callbackMessageReceiver(new CommandMessage(message));
                    } else if (message.getMessageType() == ProtoMessage.CommMessage.MessageType.PROGRAM_INFO) {
                        messageListener.callbackMessageReceiver(new ProgramInfoMessage(message));
                    }
                } else {
                    Log.d(AppLauncher.DEBUG_TAG, "[TCPClient] NULL MESSAGE LISTENER");
                    //TODO: error trap
                }
            }
        } catch (InvalidProtocolBufferException e) {
            Log.e (AppLauncher.DEBUG_TAG, "[TCPClient] Error parsing proto message", e);
        }
    }

    private void receiveMessage () {
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    in = socket.getInputStream();
                    while (connected) {
                        //receive message
                        byte[] numBytes = new byte [4];
                        in.read (numBytes);
                        int messageSize = ByteBuffer.wrap(numBytes).asIntBuffer().get();
                        byte[] message = new byte [messageSize];
                        in.read (message);
                        messageGuider (message);
                    }
                }
                catch (IOException e) {
                    Log.e (AppLauncher.DEBUG_TAG, "[TCPClient] S: Error w/ InputStream", e);
                    //TODO: error trap
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
        void callbackMessageReceiver (CommandMessage message);
        void callbackMessageReceiver (ProgramInfoMessage message);
        void restartConnection ();
    }
}
