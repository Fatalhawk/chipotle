package com.sensei.companion.display;

import android.support.v4.view.GestureDetectorCompat;
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
import com.sensei.companion.gestures.GestureManager;
import com.sensei.companion.gestures.*;

import android.util.Log;

import in.championswimmer.sfg.lib.SimpleFingerGestures;

public class AppLauncher extends AppCompatActivity {
    private static final String DEBUG_TAG = "appMonitor";
    public GestureDetectorCompat mDetector;

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
        setUpGestures(v);
        //setUpMoreGestures(v);
       // mDetector = new GestureDetectorCompat(this, new MyGestureListener());
    }

    public boolean onTouchEvent(MotionEvent event){
        //this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }






    public void setUpGestures(View v){
        GestureManager mySfg = new GestureManager(this);
        mySfg.setDebug(true);
        mySfg.setConsumeTouchEvents(true);
        mySfg.setOnFingerGestureListener(new GestureManager.OnFingerGestureListener() {
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
                    Log.i(DEBUG_TAG, "double tap with " + fingers + " fingers");
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

            /*public boolean onSingleTap(int fingers){
                Log.i(DEBUG_TAG, "Single tap with " + fingers + " fingers");
                return false;
            }*/

            public boolean onLongPress(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration){
                Log.i(DEBUG_TAG, "long press");
                return false;
            }

            public boolean onTap(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration){
                Log.i(DEBUG_TAG, "tap");
                return false;
            }
        });
        v.setOnTouchListener(mySfg);
    }

    public void setUpMoreGestures(View v){
        TouchTypeDetector.TouchTypListener touchTypListener=new TouchTypeDetector.TouchTypListener() {
            @Override public void onTwoFingerSingleTap() {
                // Two fingers single tap
                Log.i(DEBUG_TAG, "tap with " + 2 + " fingers");
            }

            @Override public void onThreeFingerSingleTap() {
                // Three fingers single tap
                //Log.i(DEBUG_TAG, "tap with " + 3 + " fingers");
            }

            @Override public void onDoubleTap() {
                // Double tap
                Log.i(DEBUG_TAG, "double tap");
            }

            @Override public void onScroll(int scrollDirection) {
                /*switch (scrollDirection) {
                    case TouchTypeDetector.SCROLL_DIR_UP:
                        // Scrolling Up
                        Log.i(DEBUG_TAG, "scroll up");
                        break;
                    case TouchTypeDetector.SCROLL_DIR_DOWN:
                        // Scrolling Down
                        Log.i(DEBUG_TAG, "scroll down");
                        break;
                    case TouchTypeDetector.SCROLL_DIR_LEFT:
                        // Scrolling Left
                        Log.i(DEBUG_TAG, "scroll left");
                        break;
                    case TouchTypeDetector.SCROLL_DIR_RIGHT:
                        // Scrolling Right
                        Log.i(DEBUG_TAG, "scroll right");
                        break;
                    default:
                        // Do nothing
                        Log.i(DEBUG_TAG, "nothing");
                        break;
                }*/
            }

            @Override public void onSingleTap() {
                // Single tap
                Log.i(DEBUG_TAG, "single tap");

            }

            @Override public void onSwipe(int swipeDirection) {
                switch (swipeDirection) {
                    case TouchTypeDetector.SWIPE_DIR_UP:
                        // Swipe Up
                        Log.i(DEBUG_TAG, "swipe up");
                        break;
                    case TouchTypeDetector.SWIPE_DIR_DOWN:
                        // Swipe Down
                        Log.i(DEBUG_TAG, "swipe down");
                        break;
                    case TouchTypeDetector.SWIPE_DIR_LEFT:
                        // Swipe Left
                        Log.i(DEBUG_TAG, "swipe left");
                        break;
                    case TouchTypeDetector.SWIPE_DIR_RIGHT:
                        // Swipe Right
                        Log.i(DEBUG_TAG, "swipe right");
                        break;
                    default:
                        //do nothing
                        Log.i(DEBUG_TAG, "nothing");
                        break;
                }
            }

            @Override public void onLongPress() {
                // Long press
                Log.i(DEBUG_TAG, "long press");
            }
        };
        Sensey.getInstance().startTouchTypeDetection(this,touchTypListener);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Sensey.getInstance().stopTouchTypeDetection();
        Sensey.getInstance().stop();
    }
}
