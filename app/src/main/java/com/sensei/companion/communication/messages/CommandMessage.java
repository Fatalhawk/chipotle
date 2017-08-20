package com.sensei.companion.communication.messages;

import com.sensei.companion.proto.ProtoMessage;

public class CommandMessage extends CMessage {
    private String command;
    private String extraInfo;
    private ProtoMessage.CommMessage.Command.CommandEnvironment environment;

    public CommandMessage (ProtoMessage.CommMessage message) {
        super (message.getMessageId(), message.getMessageType());
        ProtoMessage.CommMessage.Command commandMessage = message.getCommandMessage();
        this.command = commandMessage.getCommand();
        this.extraInfo = commandMessage.getExtraInfo();
        this.environment = commandMessage.getEnvironment();
    }

    public CommandMessage (ProtoMessage.CommMessage.Command.CommandEnvironment environment, String command, String extra_info) {
        super (ProtoMessage.CommMessage.MessageType.COMMAND);
        this.environment = environment;
        this.command = command;
        this.extraInfo = extra_info;
    }

    public ProtoMessage.CommMessage getProtoMessage () {
        ProtoMessage.CommMessage.Command.Builder commandMessage = ProtoMessage.CommMessage.Command.newBuilder();
        commandMessage.setCommand (command);
        commandMessage.setExtraInfo (extraInfo);
        commandMessage.setEnvironment(environment);

        ProtoMessage.CommMessage.Builder message = ProtoMessage.CommMessage.newBuilder();
        message.setMessageId(getMessageID());
        message.setMessageType (ProtoMessage.CommMessage.MessageType.COMMAND);
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

    public ProtoMessage.CommMessage.Command.CommandEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(ProtoMessage.CommMessage.Command.CommandEnvironment environment) {
        this.environment = environment;
    }
}
