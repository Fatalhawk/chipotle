package com.sensei.companion.display.user_interface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sensei.companion.R;

public class UIAppLauncher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchbar_main);
    }
}
