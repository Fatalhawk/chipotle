package com.sensei.companion.display;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sensei.companion.R;

public class TouchBarActivity extends AppCompatActivity implements TouchBarFragment.OnTouchbarInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_bar);
    }


}
