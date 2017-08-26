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
import java.util.List;
import java.util.ArrayList;

import com.sensei.companion.R;

public class ScreenSelectorFragment extends Fragment {

    private final String DEBUG_TAG = "appMonitor";
    private OnScreenSelectorInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /** at the beginning all the programs will be loaded into dtop_management, load all pictures and names
         during program, every time one of the imagebuttons, it should switch the fragment of the touch bar (Salar has this done)
         I can assume the pictures and names will be given
         For desktops, similar story **/
        //need swipe up to delete desktops
        super.onCreate(savedInstanceState);

        View view1 = inflater.inflate(R.layout.dtop_management, container, false);


        RecyclerView recyclerView = (RecyclerView) view1.findViewById(R.id.screenList);

        List<Screen> screens = createList(3);

        MyAdapter adapter = new MyAdapter(getActivity(), screens);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //need to do everything programmatically now

        final ImageButton addDesktopButton =  (ImageButton) view1.findViewById(R.id.addDesktopButton);
        addDesktopButton.setId(View.generateViewId());
        addDesktopButton.setImageResource(R.drawable.ic_add_black_24dp);





        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        float scale = getResources().getDisplayMetrics().density;
        final int ib_padding = (int) (28*scale + 0.5f); //56 is the size of a floating action button




        /**
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
            **/

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

    interface OnScreenSelectorInteractionListener {
        // TODO: Update argument type and name
        void switchScreen (int screenKey);
    }


    private List<Screen> createList(int size) {

        List<Screen> result = new ArrayList<Screen>();
        for (int i=1; i <= size; i++) {
            Screen ci = new Screen(Screen.DESKTOP_NAME_PREFIX + i, new Button(getActivity()));
            ci.getButton().setId(View.generateViewId());

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ci.getButton().setLayoutParams(params);
            System.out.println("YOLO" + ci.getButton().getId());
            // ci.imageB.setImageResource(R.drawable.ic_desktop_windows_black_24dp);
            // ci.imageB.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.RedSmooth_3));
            result.add(ci);

        }

        return result;
    }
}
