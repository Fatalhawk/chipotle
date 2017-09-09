package com.sensei.companion.communication.messages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sensei.companion.proto.ProtoMessage;

public class ProgramInfoMessage extends CMessage {
    private int programId;
    private String programName;
    private Bitmap picture;
    private String programInfo;
    private String programType;

    public ProgramInfoMessage (ProtoMessage.CompRequest message) {
        super (message.getMessageId(), message.getMessageType());
        ProtoMessage.CompRequest.ProgramInfo programInfoMessage = message.getProgramMessage();
        this.programId = programInfoMessage.getProgramId();
        this.programName = programInfoMessage.getProgramName();
        this.programInfo = programInfoMessage.getProgramInfo();
        this.programType = programInfoMessage.getProgramType();
        byte [] picBytes = programInfoMessage.getPicture().toByteArray();
        this.picture = BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length);
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(String programInfo) {
        this.programInfo = programInfo;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }
}
