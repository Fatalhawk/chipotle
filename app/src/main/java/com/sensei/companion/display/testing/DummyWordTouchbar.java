package com.sensei.companion.display.testing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensei.companion.R;
import com.sensei.companion.display.program_managers.TouchBarFragment;

public class DummyWordTouchbar extends TouchBarFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dummy_word_touchbar, container, false);
    }
}
