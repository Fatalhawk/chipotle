package com.sensei.companion.display;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
import com.github.nisrulz.sensey.TouchTypeDetector;
import com.sensei.companion.R;
import android.util.Log;

import in.championswimmer.sfg.lib.SimpleFingerGestures;

public class AppLauncher extends AppCompatActivity {
    private static final String DEBUG_TAG = "appMonitor";

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Setup onTouchEvent for detecting type of touch gesture
        Sensey.getInstance().setupDispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);
        View v = findViewById(android.R.id.content);
        SimpleFingerGestures mySfg = new SimpleFingerGestures();
        mySfg.setDebug(true);
        mySfg.setConsumeTouchEvents(true);
        mySfg.setOnFingerGestureListener(new SimpleFingerGestures.OnFingerGestureListener() {
            @Override
            public boolean onSwipeUp(int fingers, long gestureDuration, double gestureDistance) {
                if(fingers == 2){
                    //action
                }
                else if(fingers == 3){
                    //action
                }
                return false;
            }

            @Override
            public boolean onSwipeDown(int fingers, long gestureDuration, double gestureDistance) {
                if(fingers == 2){
                    //action
                }
                else if(fingers == 3){
                    //action
                }
                return false;
            }

            @Override
            public boolean onSwipeLeft(int fingers, long gestureDuration, double gestureDistance) {
                if(fingers == 2){
                    //action
                }
                else if(fingers == 3){
                    //action
                }
                return false;
            }

            @Override
            public boolean onSwipeRight(int fingers, long gestureDuration, double gestureDistance) {
                if(fingers == 2){
                    //action
                }
                else if(fingers == 3){
                    //action
                }
                return false;
            }

            @Override
            public boolean onPinch(int fingers, long gestureDuration, double gestureDistance) {
                if(fingers == 2){
                    //action
                }
                return false;
            }

            @Override
            public boolean onUnpinch(int fingers, long gestureDuration, double gestureDistance) {
                if(fingers == 2){
                    //action
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(int fingers) {
                if(fingers == 1){

                }
                //doubleTapStuff(fingers);
                return false;
            }
        });
        v.setOnTouchListener(mySfg);
        /*Sensey.getInstance().init(this);
        TouchTypeDetector.TouchTypListener touchTypListener=new TouchTypeDetector.TouchTypListener() {
            @Override public void onTwoFingerSingleTap() {
                // Two fingers single tap
                //Log.i(DEBUG_TAG, "sensey single tap");
            }
            @Override public void onThreeFingerSingleTap() {
                // Three fingers single tap
            }

            @Override public void onDoubleTap() {
                // Double tap
                //Log.i(DEBUG_TAG, "sensey single tap");
            }

            @Override public void onScroll(int scrollDirection) {
                switch (scrollDirection) {
                    case TouchTypeDetector.SCROLL_DIR_UP:
                        //Log.i(DEBUG_TAG, "sensey scroll up");
                        // Scrolling Up
                        break;
                    case TouchTypeDetector.SCROLL_DIR_DOWN:
                        //Log.i(DEBUG_TAG, "sensey scroll down");
                        // Scrolling Down
                        break;
                    case TouchTypeDetector.SCROLL_DIR_LEFT:
                        // Scrolling Left
                        //Log.i(DEBUG_TAG, "sensey scroll left");
                        break;
                    case TouchTypeDetector.SCROLL_DIR_RIGHT:
                        // Scrolling Right
                        //Log.i(DEBUG_TAG, "sensey scroll right");
                        break;
                    default:
                        // Do nothing
                        //Log.i(DEBUG_TAG, "sensey default scroll");
                        break;
                }
            }

            @Override public void onSingleTap() {
                // Single tap
                Log.i(DEBUG_TAG, "sensey single tap");
            }

            /*@Override public void onSwipe(int swipeDirection) {
                switch (swipeDirection) {
                    case TouchTypeDetector.SWIPE_DIR_UP:
                        // Swipe Up
                        break;
                    case TouchTypeDetector.SWIPE_DIR_DOWN:
                        // Swipe Down
                        break;
                    case TouchTypeDetector.SWIPE_DIR_LEFT:
                        // Swipe Left
                        break;
                    case TouchTypeDetector.SWIPE_DIR_RIGHT:
                        // Swipe Right
                        break;
                    default:
                        //do nothing
                        break;
                }
            }*/

            /*@Override public void onLongPress() {
                // Long press
                Log.i(DEBUG_TAG, "long press");
            }
        };
        Sensey.getInstance().startTouchTypeDetection(this, touchTypListener);*/

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        Sensey.getInstance().stopTouchTypeDetection();
        Sensey.getInstance().stop();
    }
}
