package com.sensei.companion.display;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.sensei.companion.R;
import com.sensei.companion.communication.commands.CommandsData;

public class ScreenSelectorFragment extends Fragment {

    private final String DEBUG_TAG = "appMonitor";
    private OnScreenSelectorInteractionListener mListener;

    //can only have 10 desktops maximum, need to discuss with the team later or make array dynamic
    private ImageButton[] imageButtons = new ImageButton[10];
    private int image_tracker = 1;  //only 1 image here


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /** at the beginning all the programs will be loaded into dtop_management, load all pictures and names
         during program, every time one of the imagebuttons, it should switch the fragment of the touch bar (Salar has this done)
         I can assume the pictures and names will be given
         For desktops, similar story **/
        //need swipe up to delete desktops


        View view1 = inflater.inflate(R.layout.dtop_management, container, false);


        RecyclerView recyclerView = (RecyclerView) view1.findViewById(R.id.desktopList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);



        //need to do everything programmatically now

        final ImageButton addDesktopButton =  (ImageButton) view1.findViewById(R.id.addDesktopButton);
        addDesktopButton.setTag(Integer.toString(image_tracker));
        addDesktopButton.setId(View.generateViewId());
        addDesktopButton.setImageResource(R.drawable.ic_add_black_24dp);

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        //addview not supported in AdapterView, what to do??

        ImageButton first_desktop = new ImageButton(getActivity());
        first_desktop.setId(View.generateViewId());
        first_desktop.setImageResource(R.drawable.ic_desktop_windows_black_24dp);




        //just put onclick listener here makes your life easier

        float scale = getResources().getDisplayMetrics().density;
        final int ib_padding = (int) (28*scale + 0.5f); //56 is the size of a floating action button



        addDesktopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                ImageButton nImageButton = new ImageButton(getActivity());
                nImageButton.setLayoutParams(params);
                addDesktopButton.setId(View.generateViewId());
                //need to set ImageButton's image
                nImageButton.setImageResource(R.drawable.ic_desktop_windows_black_24dp);
                nImageButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.RedSmooth_3));
                nImageButton.setPadding( 0, 0, ib_padding, 0);



                //need to add these buttons to the recyclerview somehow?


                imageButtons[image_tracker] = nImageButton;

                image_tracker++;
                System.out.println(Integer.toString(image_tracker));
                addDesktopButton.setTag(Integer.toString(image_tracker));

            }
        });


        return view1;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScreenSelectorInteractionListener) {
            mListener = (OnScreenSelectorInteractionListener) context;
        } else {
            Log.d (DEBUG_TAG, "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnScreenSelectorInteractionListener {
        // TODO: Update argument type and name
        void switchScreen (CommandsData.Program programKey);
    }
}
