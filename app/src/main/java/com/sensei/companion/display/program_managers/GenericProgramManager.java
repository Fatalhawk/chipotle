package com.sensei.companion.display.program_managers;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sensei.companion.R;
import com.sensei.companion.connection.ConnectManager;

public class GenericProgramManager extends TouchBarFragment {

    private final String DEBUG_TAG = "appMonitor";
    private int testCounter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_generic_program_manager, container, false);
        final Button button = (Button) v.findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTestMessage(v);
            }
        });
        return v;
    }

    public void sendTestMessage (View v) {
        super.mListener.sendMessage ("test");
    }
}
