package com.sensei.companion.display.program_managers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sensei.companion.R;

public class GenericProgramManager extends TouchBarFragment {

    private final String DEBUG_TAG = "appMonitor";
    private int testCounter = 0;

    /*
    Every time an action is done on the touchbar fragment, call super.mListener.sendMessage (String message)
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.touch_bar_fragment, container, false);
        final ImageButton imageButton7 =  (ImageButton) view.findViewById(R.id.imageButton7);
        imageButton7.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //once I set the play.png and pause.png properly I'm good for the touchbar fragment
                if(imageButton7.getTag() != null && imageButton7.getTag().toString().equals("pause.png")){
                    imageButton7.setImageResource(R.drawable.play);
                    imageButton7.setTag("play.png");
                } else {
                    imageButton7.setImageResource(R.drawable.pause);
                    imageButton7.setTag("pause.png");
                }
            }
        });
        return view;
    }

}
