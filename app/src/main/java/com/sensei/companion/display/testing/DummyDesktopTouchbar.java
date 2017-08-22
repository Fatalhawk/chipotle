package com.sensei.companion.display.testing;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sensei.companion.R;
import com.sensei.companion.communication.messages.CMessage;
import com.sensei.companion.communication.messages.CommandMessage;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.display.program_managers.TouchBarFragment;
import com.sensei.companion.proto.ProtoMessage;

public class DummyDesktopTouchbar extends TouchBarFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dummy_desktop_touchbar, container, false);
        Button sendTestMsgButton = (Button)v.findViewById(R.id.button_send_message);
        sendTestMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMessageButtonPressed(v);
            }
        });
        return v;
    }

    public void testMessageButtonPressed (View v) {
        super.mListener.sendMessage(new CommandMessage(ProtoMessage.CommMessage.Command.CommandEnvironment.PROGRAM, "testmsg", ""));
    }
}
