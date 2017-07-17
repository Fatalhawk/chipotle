package com.sensei.companion.display.program_managers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensei.companion.R;

public class TouchBarFragment extends Fragment {

    private final String DEBUG_TAG = "appMonitor";
    OnTouchbarInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTouchbarInteractionListener) {
            mListener = (OnTouchbarInteractionListener) context;
        } else {
            Log.d (DEBUG_TAG, "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTouchbarInteractionListener {
        // TODO: Update argument type and name
        void sendMessage (String msg);
    }
}
