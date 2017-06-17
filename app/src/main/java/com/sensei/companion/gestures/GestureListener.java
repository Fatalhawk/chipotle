package com.sensei.companion.gestures;

import android.view.MotionEvent;
import android.view.View;

public abstract class GestureListener {

    View mView;

    public GestureListener (View v) {
        mView = v;
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchGesture (v, event);
                //return false; //true if the listener has consumed the event, false otherwise.
            }
        });
    }

    public abstract boolean onTouchGesture (View v, MotionEvent event);
}
