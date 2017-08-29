package com.sensei.companion.display.testing;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sensei.companion.R;
import com.sensei.companion.communication.commands.CommandsData;
import com.sensei.companion.display.screen_selector.ScreenSelectorFragment;
import com.sensei.companion.display.activities.AppLauncher;

public class ScreenSelectorFragmentTest extends Fragment {

    private OnScreenSelectorInteractionListener mListener;

    public ScreenSelectorFragmentTest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_screen_selector_test, container, false);
        Button dummyDesktopButton = (Button)v.findViewById(R.id.button_desktop);
        Button dummyChromeButton = (Button)v.findViewById(R.id.button_chrome);
        Button dummyWordButton = (Button)v.findViewById(R.id.button_word);

        dummyDesktopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dummyDesktopClicked(v);
            }
        });

        dummyChromeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dummyChromeClicked(v);
            }
        });

        dummyWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dummyWordClicked(v);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ScreenSelectorFragment.OnScreenSelectorInteractionListener) {
            mListener = (ScreenSelectorFragmentTest.OnScreenSelectorInteractionListener) context;
        } else {
            Log.d (AppLauncher.DEBUG_TAG, "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void dummyDesktopClicked (View v) {
        if (mListener != null) {
            mListener.switchScreen(CommandsData.Program.WINDOWS);
        }
    }

    public void dummyChromeClicked (View v) {
        if (mListener != null) {
            mListener.switchScreen(CommandsData.Program.CHROME);
        }
    }

    public void dummyWordClicked (View v) {
        if (mListener != null) {
            mListener.switchScreen(CommandsData.Program.MICROSOFT_WORD);
        }
    }

    public interface OnScreenSelectorInteractionListener {
        // TODO: Update argument type and name
        void switchScreen (CommandsData.Program programKey);
    }
}
