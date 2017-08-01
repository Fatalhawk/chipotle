package com.sensei.companion.display;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.sensei.companion.R;
import com.sensei.companion.gestures.MyGestureListener;
import com.sensei.companion.gestures.NewGestureManager;

public class GestureActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "appMonitor";
    public GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchbar_main);
        View v = findViewById(android.R.id.content);
        setUpGestures(v);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
    }

    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void setUpGestures(View v){
        NewGestureManager mySfg = new NewGestureManager();
        mySfg.setDebug(false);
        mySfg.setConsumeTouchEvents(true);
        mySfg.setOnFingerGestureListener(new NewGestureManager.OnFingerGestureListener() {
            @Override
            public boolean onSwipeUp(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration) {
                if(fingers == 1){
                    Log.i(DEBUG_TAG, "swipe up with " + fingers + " fingers");
                    //action
                }
                if(fingers == 2){
                    Log.i(DEBUG_TAG, "swipe up with " + fingers + " fingers");
                    //action
                }
                else if(fingers == 3){
                    Log.i(DEBUG_TAG, "swipe up with " + fingers + " fingers");
                    //action
                }
                else if(fingers == 4){
                    Log.i(DEBUG_TAG, "swipe up with " + fingers + " fingers");
                }
                return false;
            }

            @Override
            public boolean onSwipeDown(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration) {
                if(fingers == 1){
                    Log.i(DEBUG_TAG, "swipe down with " + fingers + " fingers");
                    //action
                }
                if(fingers == 2){
                    Log.i(DEBUG_TAG, "swipe down with " + fingers + " fingers");
                    //action
                }
                else if(fingers == 3){
                    Log.i(DEBUG_TAG, "swipe down with " + fingers + " fingers");
                    //action
                }
                else if(fingers == 4){
                    Log.i(DEBUG_TAG, "swipe down with " + fingers + " fingers");
                }
                return false;
            }

            @Override
            public boolean onSwipeLeft(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration) {
                if(fingers == 1){
                    Log.i(DEBUG_TAG, "swipe left with " + fingers + " fingers");
                    //action
                }
                if(fingers == 2){
                    //action
                    Log.i(DEBUG_TAG, "swipe left with " + fingers + " fingers");
                }
                else if(fingers == 3){
                    //action
                    Log.i(DEBUG_TAG, "swipe left with " + fingers + " fingers");
                }
                else if(fingers == 4){
                    Log.i(DEBUG_TAG, "swipe left with " + fingers + " fingers");
                }
                return false;
            }

            @Override
            public boolean onSwipeRight(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration) {
                if(fingers == 1){
                    Log.i(DEBUG_TAG, "swipe right with " + fingers + " fingers");
                    //action
                }
                if(fingers == 2){
                    //action
                    Log.i(DEBUG_TAG, "swipe right with " + fingers + " fingers");
                }
                else if(fingers == 3){
                    //action
                    Log.i(DEBUG_TAG, "swipe right with " + fingers + " fingers");
                }
                else if(fingers == 4){
                    Log.i(DEBUG_TAG, "swipe right with " + fingers + " fingers");
                }
                return false;
            }

            @Override
            public boolean onPinch(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration) {
                if(fingers == 2){
                    //action
                    Log.i(DEBUG_TAG, "pinch in with " + fingers + " fingers");
                }
                else if(fingers == 3){
                    //action
                    Log.i(DEBUG_TAG, "pinch in with " + fingers + " fingers");
                }
                else if(fingers == 4){
                    Log.i(DEBUG_TAG, "pinch in with " + fingers + " fingers");
                }
                return false;
            }

            @Override
            public boolean onUnpinch(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration) {
                if(fingers == 2){
                    //action
                    Log.i(DEBUG_TAG, "pinch out with " + fingers + " fingers");
                }
                else if(fingers == 3){
                    //action
                    Log.i(DEBUG_TAG, "pinch out with " + fingers + " fingers");
                }
                else if(fingers == 4){
                    Log.i(DEBUG_TAG, "pinch out with " + fingers + " fingers");
                }
                return false;
            }
            public boolean onTwoFingerTap(double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration){
                Log.i(DEBUG_TAG, "two finger tap");
                return false;
            }
        });
        v.setOnTouchListener(mySfg);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
