package com.sensei.companion.display;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import com.sensei.companion.gestures.*;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;
import com.sensei.companion.R;
import com.sensei.companion.gestures.*;

import android.util.Log;

public class AppLauncher extends AppCompatActivity {
    private static final String DEBUG_TAG = "appMonitor";
    public GestureDetectorCompat mDetector;
    private TapGesture tapDetection;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Setup onTouchEvent for detecting type of touch gesture
       // Sensey.getInstance().setupDispatchTouchEvent(ev);
        if (tapDetection != null) {
            tapDetection.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);
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
        mySfg.setDebug(true);
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

            @Override
            public boolean onDoubleTap(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration) {
                if(fingers == 1){
                 // Log.i(DEBUG_TAG, "double tap with " + fingers + " fingers");
                }
                else if(fingers == 2){
                    Log.i(DEBUG_TAG, "double tap with " + fingers + " fingers");
                }
                else if(fingers == 3){
                    Log.i(DEBUG_TAG, "double tap with " + fingers + " fingers");
                }
                //doubleTapStuff(fingers);
                return false;
            }

            public boolean onSingleTap(double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration){
               // Log.i(DEBUG_TAG, "single tap");
                return false;
            }

            public boolean onLongPress(double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration){
                //Log.i(DEBUG_TAG, "long press");
                return false;
            }

            public boolean onTwoFingerTap(double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration){
                Log.i(DEBUG_TAG, "two finger tap");
                return false;
            }
        });
        v.setOnTouchListener(mySfg);
        TapGesture.TouchTypListener touchTypListener=new TapGesture.TouchTypListener() {
            @Override public void onDoubleTap() {
                // Double tap
                Log.i(DEBUG_TAG, "sensey double tap");
            }

            @Override public void onSingleTap() {
                // Single tap
                Log.i(DEBUG_TAG, "sensey single tap");

            }

            @Override public void onLongPress(){
                // Long press
                Log.i(DEBUG_TAG, "sensey long press");
            }
        };
        tapDetection = new TapGesture(this, touchTypListener);
        //Sensey.getInstance().startTouchTypeDetection(this, touchTypListener);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //tapDetection = null;
        Sensey.getInstance().stopTouchTypeDetection();
        Sensey.getInstance().stop();
    }
}
