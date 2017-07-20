package com.sensei.companion.display;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sensei.companion.R;

public class ScreenSelectorFragment extends Fragment {

    private final String DEBUG_TAG = "appMonitor";
    private OnScreenSelectorInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_screen_selector, container, false);
        final Button desktopButton = (Button) v.findViewById(R.id.desktop_button);
        final Button wordButton = (Button) v.findViewById(R.id.word_button);

        desktopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.switchScreen(TouchBarActivity.DESKTOP);
            }
        });
        wordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.switchScreen(TouchBarActivity.WORD);
            }
        });

        return v;
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
}
