package com.sensei.companion.display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.graphics.drawable.Drawable;
import android.

public class touch_bar_fragment extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.touch_bar_fragment, container, false);
        final ImageButton imageButton7 =  (ImageButton) view.findViewById(R.id.imageButton7);
        imageButton7.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //once I set the play.png and pause.png properly I'm good for the touchbar fragment
                if(imageButton7.getTag() != null && imageButton7.getTag().toString().equals("pause.png")){
                    imageButton7.setImageResource(R.drawable.play.png);
                    imageButton7.setTag("play.png");
                } else {
                    imageButton7.setImageResource(R.drawable.pause.png);
                    imageButton7.setTag("pause.png");
                }
            }



        });

    }
}
