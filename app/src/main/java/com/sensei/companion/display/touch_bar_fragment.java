package com.sensei.companion.display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class touch_bar_fragment extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.touch_bar_fragment, container, false);
    }
}
