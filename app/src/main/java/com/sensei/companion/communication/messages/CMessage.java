package com.sensei.companion.communication.messages;

import com.sensei.companion.proto.ProtoMessage;

import java.util.UUID;

public class CMessage {
    private String messageId;
    private ProtoMessage.CommMessage.MessageType type;

    //ProgramInfoMessage && ReplyMessage uses this constructor
    CMessage (String messageId, ProtoMessage.CommMessage.MessageType type) {
        this.messageId = messageId;
        this.type = type;
    }

    //CommandMessage uses this constructor
    CMessage (ProtoMessage.CommMessage.MessageType type) {
        this.type = type;
        messageId = UUID.randomUUID().toString();
    }

    public String getMessageID() {
        return messageId;
    }

    public void setMessageID(String messageId) {
        this.messageId = messageId;
    }

    public ProtoMessage.CommMessage.MessageType getType() {
        return type;
    }

    public void setType(ProtoMessage.CommMessage.MessageType type) {
        this.type = type;
    }
}
