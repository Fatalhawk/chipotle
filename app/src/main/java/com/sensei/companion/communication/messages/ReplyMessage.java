package com.sensei.companion.communication.messages;

import com.sensei.companion.proto.ProtoMessage;

public class ReplyMessage extends CMessage {
    public ReplyMessage (String messageId) {
        super (messageId, ProtoMessage.CompRequest.MessageType.REPLY);
    }

    public ProtoMessage.CompRequest getProtoMessage () {
        ProtoMessage.CompRequest.Builder message = ProtoMessage.CompRequest.newBuilder();
        message.setMessageId(getMessageID());
        message.setMessageType(ProtoMessage.CompRequest.MessageType.REPLY);
        return message.build ();
    }
}
