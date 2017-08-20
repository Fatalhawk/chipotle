package com.sensei.companion.communication.connection;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sensei.companion.communication.messages.CMessage;
import com.sensei.companion.communication.messages.CommandMessage;
import com.sensei.companion.communication.messages.ReplyMessage;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.proto.ProtoMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    Sends #bytes, sends message, waits for reply consisting of that message's unique id.
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
    Send back a reply message to sender to ensure them that their message has been received.
    Reply message consists of empty message with same id as that received.
     */
    private void sendReplyMessage (String messageId) {
        ProtoMessage.CommMessage replyMessage = new ReplyMessage(messageId).getProtoMessage();
        int numBytes = replyMessage.toByteArray().length;
        byte[] numBytesMessage = ByteBuffer.allocate(4).putInt(numBytes).array();
        try {
            out.write (numBytesMessage);
            replyMessage.writeTo (out);
            out.flush();
            Log.i(AppLauncher.DEBUG_TAG, "[TCPClient] Sent reply message: " + replyMessage.toString());
        } catch (IOException e) {
            Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] Error sending reply message", e);
            //TODO: error trap
        }
    }

    /*
    Checks whether the message is a reply or a newly issued command and proceeds appropriately
     */
    private void messageGuider (byte [] messageBytes) {
        try {
            ProtoMessage.CommMessage message = ProtoMessage.CommMessage.parseFrom(messageBytes);
            Log.i (AppLauncher.DEBUG_TAG, "[TCPClient] Received message: " + message.toString());
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
                sendReplyMessage (message.getMessageId()); //send reply back w/ same id
                if (messageListener != null) {
                    messageListener.callbackMessageReceiver(message);
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
        void callbackMessageReceiver (ProtoMessage.CommMessage message);
        void restartConnection ();
    }
}
