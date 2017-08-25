package com.sensei.companion.communication.messages;

import com.sensei.companion.proto.ProtoMessage;

public class ReplyMessage extends CMessage {
    public ReplyMessage (String messageId) {
        super (messageId, ProtoMessage.CommMessage.MessageType.REPLY);
    }

    public ProtoMessage.CommMessage getProtoMessage () {
        ProtoMessage.CommMessage.Builder message = ProtoMessage.CommMessage.newBuilder();
        message.setMessageId(getMessageID());
        message.setMessageType(ProtoMessage.CommMessage.MessageType.REPLY);
        return message.build ();
    }
}
