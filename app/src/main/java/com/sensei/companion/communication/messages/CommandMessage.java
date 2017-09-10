package com.sensei.companion.communication.messages;

import com.google.protobuf.ByteString;
import com.sensei.companion.proto.ProtoMessage;

public class CommandMessage extends CMessage {
    private String command;
    private String extraInfo;
    private ProtoMessage.CompRequest.CommandInfo.Target environment;

    public CommandMessage (ProtoMessage.CompRequest message) {
        super (message.getMessageId(), message.getMessageType());
        ProtoMessage.CompRequest.CommandInfo commandMessage = message.getCommandMessage();
        this.command = commandMessage.getCommand();
        this.extraInfo = new String(commandMessage.getExtraInfo().toByteArray());
        this.environment = commandMessage.getCommandTarget();
    }

    public CommandMessage (ProtoMessage.CompRequest.CommandInfo.Target environment, String command, String extra_info) {
        super (ProtoMessage.CompRequest.MessageType.COMMAND);
        this.environment = environment;
        this.command = command;
        this.extraInfo = extra_info;
    }

    public ProtoMessage.CompRequest getProtoMessage () {
        ProtoMessage.CompRequest.CommandInfo.Builder commandMessage = ProtoMessage.CompRequest.CommandInfo.newBuilder();
        commandMessage.setCommand (command);
        commandMessage.setExtraInfo (ByteString.copyFrom(extraInfo.getBytes()));
        commandMessage.setCommandTarget(environment);

        ProtoMessage.CompRequest.Builder message = ProtoMessage.CompRequest.newBuilder();
        message.setMessageId(getMessageID());
        message.setMessageType (ProtoMessage.CompRequest.MessageType.COMMAND);
        message.setCommandMessage(commandMessage);

        return message.build();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public ProtoMessage.CompRequest.CommandInfo.Target getEnvironment() {
        return environment;
    }

    public void setEnvironment(ProtoMessage.CompRequest.CommandInfo.Target environment) {
        this.environment = environment;
    }
}
