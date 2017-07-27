package com.sensei.companion.display;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.support.design.widget.FloatingActionButton;


import com.sensei.companion.R;



public class dtop_management extends Fragment  {


   /** private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.addDesktopButton:
                    //need a way to pass in how many desktops to be made
                    imagetracker++;
                    imageButtons[imagetracker] = new ImageButton(this);
                    imageButtons[imagetracker].setLayoutParams(new ViewGroup.LayoutParams("126dp", ViewGroup.LayoutParams.MATCH_PARENT));

                    break;




            }
        }
    } **/

    //can only have 10 desktops maximum, need to discuss with the team later or make array dynamic
    private ImageButton[] imageButtons = new ImageButton[10];
    private int image_tracker = 1;  //only 1 image here





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        /** at the beginning all the programs will be loaded into dtop_management, load all pictures and names
        during program, every time one of the imagebuttons, it should switch the fragment of the touch bar (Salar has this done)
         I can assume the pictures and names will be given
         For desktops, similar story **/
        //swipe up to delete desktops


        View view1 = inflater.inflate(R.layout.dtop_management, container, false);
        HorizontalScrollView scrollView = (HorizontalScrollView) view1.findViewById(R.id.h_scroll);
        final FloatingActionButton addDesktopButton =  (FloatingActionButton) view1.findViewById(R.id.addDesktopButton);
        addDesktopButton.setTag(Integer.toString(image_tracker));
        ImageButton first_desktop = (ImageButton) view1.findViewById(R.id.first_desktop);
        final LinearLayout desktops_layout = (LinearLayout) view1.findViewById(R.id.desktops_layout);
        //just put onclick listener here makes your life easier
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        float scale = getResources().getDisplayMetrics().density;
        final int ib_padding = (int) (56*scale + 0.5f); //56 is the size of a floating action button

        addDesktopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                ImageButton nImageButton = new ImageButton(getActivity());
                nImageButton.setLayoutParams(params);
                nImageButton.setId(image_tracker + 1);
                //need to set ImageButton's image
                nImageButton.setImageResource(R.drawable.ic_desktop_windows_black_24dp);
                nImageButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.RedSmooth_3));
                nImageButton.setPadding( 0, 0, ib_padding, 0);

                desktops_layout.addView(nImageButton);


                imageButtons[image_tracker] = nImageButton;

                image_tracker++;
                System.out.println(Integer.toString(image_tracker));
                addDesktopButton.setTag(Integer.toString(image_tracker));

            }
        });


        return view1;
    }
}
