package com.sensei.companion.display.program_managers;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;


import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import com.sensei.companion.R;
import com.sensei.companion.communication.messages.CommandMessage;
import com.sensei.companion.proto.ProtoMessage;

public class WordManager extends TouchBarFragment {

    ColorPicker cp;
    private RadioRealButtonGroup group2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_word_manager, container, false);
        group2 = (RadioRealButtonGroup) v.findViewById(R.id.radioRealButtonGroup);
        group2.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                // do stuff
            }
        });

        // color stuff first argument is view

        cp = new ColorPicker(getActivity(), 255, 255, 255);
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                // Do whatever you want
                // Examples
                Log.d("Alpha", Integer.toString(Color.alpha(color)));
                Log.d("Red", Integer.toString(Color.red(color)));
                Log.d("Green", Integer.toString(Color.green(color)));
                Log.d("Blue", Integer.toString(Color.blue(color)));

                Log.d("Pure Hex", Integer.toHexString(color));
                Log.d("#Hex no alpha", String.format("#%06X", (0xFFFFFF & color)));
                Log.d("#Hex with alpha", String.format("#%08X", (0xFFFFFFFF & color)));
                // send color message

                cp.dismiss();
            }
        });
        return v;
    }


    public void color_pressed(View view) {
        cp.show();
    }

    public void bold_pressed(View view) {
        if (super.mListener != null) {
            super.mListener.sendMessage(new CommandMessage(ProtoMessage.CompRequest.CommandInfo.Target.PROGRAM, "BOLD", ""));
        }
    }
}
