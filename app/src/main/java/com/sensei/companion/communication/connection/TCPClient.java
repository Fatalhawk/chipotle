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
import java.util.concurrent.TimeUnit;

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
        ProtoMessage.CompRequest protoMessage = null;
        if (message.getType() == ProtoMessage.CompRequest.MessageType.COMMAND) {
            protoMessage = ((CommandMessage)message).getProtoMessage();
        }

        if (protoMessage != null) {
            int numBytes = protoMessage.toByteArray().length;
            byte [] numBytesMessage = ByteBuffer.allocate(4).putInt(numBytes).array();

            try {
                //send
                out.write(numBytesMessage);
                protoMessage.writeTo(out);
                Log.i (AppLauncher.DEBUG_TAG, "Sent msg ["+numBytes+" bytes]: " + ((CommandMessage)message).getProtoMessage().toString());

                //wait for reply
                try {
                    blockingQueue = new LinkedBlockingQueue<>(1);
                    isWaitingForReply = true;

                    String replyId = blockingQueue.poll(1, TimeUnit.SECONDS); //wait for reply -- #bytes
                    if (replyId == null) {
                        Log.d(AppLauncher.DEBUG_TAG, "[TCPClient] Timed out waiting for a reply");
                        stopClient();
                    }
                    else {
                        if (!protoMessage.getMessageId().equals(replyId)) {
                            //TODO: WHAT TO DO IF PC SENDS BACK WRONG NUMBER
                        } else {
                            isWaitingForReply = false;
                            Log.i(AppLauncher.DEBUG_TAG, "[TCPClient] Successfully sent message and got reply!");
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e (AppLauncher.DEBUG_TAG, "[TCPClient] blocking queue's take interrupted", e);
                }

                out.flush ();
            } catch (IOException e) {
                Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] Error sending message", e);
                stopClient();
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
    void sendReplyMessage (String messageId) {
        ProtoMessage.CompRequest replyMessage = new ReplyMessage(messageId).getProtoMessage();
        int numBytes = replyMessage.toByteArray().length;
        //Log.i(AppLauncher.DEBUG_TAG, "numbytes reply" + numBytes);
        byte[] numBytesMessage = ByteBuffer.allocate(4).putInt(numBytes).array();
        try {
            out.write (numBytesMessage);
            replyMessage.writeTo (out);
            out.flush();
            Log.i(AppLauncher.DEBUG_TAG, "[TCPClient] Sent reply message[" + numBytes + "]: " + replyMessage.getMessageId());
        } catch (IOException e) {
            Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] Error sending reply message", e);
            stopClient();
        }
    }

    /*
    Checks whether the message is a reply or a newly issued command and proceeds appropriately
     */
    private void messageGuider (byte[] messageBytes) {
        try {
            ProtoMessage.CompRequest message = ProtoMessage.CompRequest.parseFrom(messageBytes);
            Log.i (AppLauncher.DEBUG_TAG, "[TCPClient] Received message: " + message.toString());
            if (message.getMessageType() == ProtoMessage.CompRequest.MessageType.REPLY) {
                if (isWaitingForReply) {
                    String replyId = message.getMessageId();
                    try {
                        blockingQueue.put(replyId);
                    } catch (InterruptedException e) {
                        Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] blocking queue's put interrupted", e);
                    }
                } else {
                    Log.d (AppLauncher.DEBUG_TAG, "[TCPClient] Error - got reply when not waiting for reply");
                }
            } else {
                if (messageListener != null) {
                    messageListener.callbackMessageReceiver(message, message.getMessageId());
                } else {
                    Log.d(AppLauncher.DEBUG_TAG, "[TCPClient] NULL MESSAGE LISTENER");
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
                        byte[] numBytes = new byte[4];
                        in.read(numBytes);
                        for (int i = 0; i < numBytes.length / 2; i++) {
                            byte temp = numBytes[i];
                            numBytes[i] = numBytes[numBytes.length - 1 - i];
                            numBytes[numBytes.length - 1 - i] = temp;
                        }
                        int messageSize = ByteBuffer.wrap(numBytes).asIntBuffer().get();
                        //Log.i(AppLauncher.DEBUG_TAG, "message size: " + messageSize);
                        byte[] message = new byte[messageSize];
                        if (messageSize == 0)
                            break;
                        in.read(message);
                        //ProtoMessage.CommandInfo message = ProtoMessage.CommandInfo.parseFrom(in);
                        messageGuider(message);
                    }
                } catch (IOException e) {
                    Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] S: Error w/ InputStream", e);
                    connected = false;
                } finally {
                    try {
                        out.flush();
                        out.close();
                        in.close();
                        socket.close();
                        Log.i(AppLauncher.DEBUG_TAG, "[TCPClient] Socket closed");
                    } catch (IOException e) {
                        Log.e (AppLauncher.DEBUG_TAG, "[TCPClient] IOException when stopping client", e);
                    }
                    messageListener.restartConnection();
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
                stopClient();
            }
        }
        catch (IOException e) {
            Log.e(AppLauncher.DEBUG_TAG, "[TCPClient] C: Error", e);
            stopClient();
        }
    }

    boolean isRunning () {
        return connected;
    }

    void stopClient () {
        connected = false;
        Log.i (AppLauncher.DEBUG_TAG, "Stopped client!");
    }

    interface MessageCallback {
        void callbackMessageReceiver (ProtoMessage.CompRequest message, String messageId);
        void restartConnection ();
    }
}
