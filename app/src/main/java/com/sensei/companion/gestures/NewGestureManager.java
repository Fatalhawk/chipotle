package com.sensei.companion.gestures;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author championswimmer
 * @version 0.2
 * @since 0.1 12/04/14
 */
public class NewGestureManager implements View.OnTouchListener {

    private boolean debug = true;
    private boolean consumeTouchEvents = false;

    // Will see if these need to be used. For now just returning duration in milliS
    public static final long GESTURE_SPEED_SLOW = 1500;
    public static final long GESTURE_SPEED_MEDIUM = 1000;
    public static final long GESTURE_SPEED_FAST = 500;
    private static final String TAG = "SimpleFingerGestures";
    private boolean tracking[] = {false, false, false, false, false};
    private NewGesture ga;
    private OnFingerGestureListener onFingerGestureListener;


    /**
     * Constructor that creates an internal {@link in.championswimmer.sfg.lib.GestureAnalyser } object as well
     */
    public NewGestureManager() {
        ga = new NewGesture();
    }

    public NewGestureManager(int swipeSlopeIntolerance) {
        ga = new NewGesture(swipeSlopeIntolerance);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean getConsumeTouchEvents() {
        return consumeTouchEvents;
    }

    public void setConsumeTouchEvents(boolean consumeTouchEvents) {
        this.consumeTouchEvents = consumeTouchEvents;
    }

    /**
     * Register a callback to be invoked when multi-finger gestures take place
     * <p/>
     * <br></br>
     * <p>
     * For the callbacks implemented via this, check the interface {@link in.championswimmer.sfg.lib.SimpleFingerGestures.OnFingerGestureListener}
     * </p>
     *
     * @param omfgl The callback that will run
     */
    public void setOnFingerGestureListener(OnFingerGestureListener omfgl) {
        onFingerGestureListener = omfgl;
    }


    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        if (debug) Log.d(TAG, "onTouch");
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (debug) Log.d(TAG, "ACTION_DOWN");
                startTracking(0);
                ga.trackGesture(ev);
                return consumeTouchEvents;
            case MotionEvent.ACTION_UP:
                if (debug) Log.d(TAG, "ACTION_UP");
                if (tracking[0]) {
                    doCallBack(ga.getGesture(ev));
                }
                stopTracking(0);
                ga.untrackGesture();
                return consumeTouchEvents;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (debug) Log.d(TAG, "ACTION_POINTER_DOWN" + " " + "num" + ev.getPointerCount());
                startTracking(ev.getPointerCount() - 1);
                ga.trackGesture(ev);
                return consumeTouchEvents;
            case MotionEvent.ACTION_POINTER_UP:
                if (debug) Log.d(TAG, "ACTION_POINTER_UP" + " " + "num" + ev.getPointerCount());
                if (tracking[1]) {
                    doCallBack(ga.getGesture(ev));
                }
                stopTracking(ev.getPointerCount() - 1);
                ga.untrackGesture();
                return consumeTouchEvents;
            case MotionEvent.ACTION_CANCEL:
                if (debug) Log.d(TAG, "ACTION_CANCEL");
                return true;
            case MotionEvent.ACTION_MOVE:
                if (debug) Log.d(TAG, "ACTION_MOVE");
                return consumeTouchEvents;
        }
        return consumeTouchEvents;
    }

    private void doCallBack(NewGesture.GestureType mGt) {
        switch (mGt.getGestureFlag()) {
            case NewGesture.SWIPE_1_UP:
                onFingerGestureListener.onSwipeUp(1, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_1_DOWN:
                onFingerGestureListener.onSwipeDown(1, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_1_LEFT:
                onFingerGestureListener.onSwipeLeft(1, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_1_RIGHT:
                onFingerGestureListener.onSwipeRight(1, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;

            case NewGesture.SWIPE_2_UP:
                onFingerGestureListener.onSwipeUp(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_2_DOWN:
                onFingerGestureListener.onSwipeDown(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_2_LEFT:
                onFingerGestureListener.onSwipeLeft(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_2_RIGHT:
                onFingerGestureListener.onSwipeRight(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.PINCH_2:
                onFingerGestureListener.onPinch(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.UNPINCH_2:
                onFingerGestureListener.onUnpinch(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;

            case NewGesture.SWIPE_3_UP:
                onFingerGestureListener.onSwipeUp(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_3_DOWN:
                onFingerGestureListener.onSwipeDown(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_3_LEFT:
                onFingerGestureListener.onSwipeLeft(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_3_RIGHT:
                onFingerGestureListener.onSwipeRight(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.PINCH_3:
                onFingerGestureListener.onPinch(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.UNPINCH_3:
                onFingerGestureListener.onUnpinch(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;

            case NewGesture.SWIPE_4_UP:
                onFingerGestureListener.onSwipeUp(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_4_DOWN:
                onFingerGestureListener.onSwipeDown(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_4_LEFT:
                onFingerGestureListener.onSwipeLeft(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.SWIPE_4_RIGHT:
                onFingerGestureListener.onSwipeRight(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.PINCH_4:
                onFingerGestureListener.onPinch(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case NewGesture.UNPINCH_4:
                onFingerGestureListener.onUnpinch(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
            case NewGesture.TAP_2_FINGERS:
                onFingerGestureListener.onTwoFingerTap(ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
        }
    }

    private void startTracking(int nthPointer) {
        for (int i = 0; i <= nthPointer; i++) {
            tracking[i] = true;
        }
    }

    private void stopTracking(int nthPointer) {
        for (int i = nthPointer; i < tracking.length; i++) {
            tracking[i] = false;
        }
    }



    public interface OnFingerGestureListener {
        boolean onSwipeUp(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration);

        boolean onSwipeDown(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration);

        boolean onSwipeLeft(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration);

        boolean onSwipeRight(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration);

        boolean onPinch(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration);

        boolean onUnpinch(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration);

        boolean onTwoFingerTap(double[] initialX, double[] initialY, double[] finalX, double[] finalY, long gestureDuration);
    }
}