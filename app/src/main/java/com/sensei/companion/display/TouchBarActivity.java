package com.sensei.companion.display;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sensei.companion.R;
import com.sensei.companion.connection.ConnectManager;

public class TouchBarActivity extends AppCompatActivity{

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_bar);

        ConnectManager.MessageHandler.setActivityReferenceToTouchBar(this);
    }

    public void sendMessage (View v) {
        counter ++;
        ConnectManager.sendMessageToPC("YOOOOO<EC> #" + counter);
    }
}
