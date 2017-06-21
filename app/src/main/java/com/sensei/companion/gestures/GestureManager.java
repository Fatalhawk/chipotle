package com.sensei.companion.gestures;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author championswimmer
 * @version 0.2
 * @since 0.1 12/04/14
 */
public class GestureManager implements View.OnTouchListener {
    //private final GestureDetectorCompat gDetect;
    private boolean debug = true;
    private boolean consumeTouchEvents = false;

    // Will see if these need to be used. For now just returning duration in milliS
    public static final long GESTURE_SPEED_SLOW = 1500;
    public static final long GESTURE_SPEED_MEDIUM = 1000;
    public static final long GESTURE_SPEED_FAST = 500;
    private static final String TAG = "SimpleFingerGestures";
    protected boolean tracking[] = {false, false, false, false, false};
    private Gesture ga;
    private OnFingerGestureListener onFingerGestureListener;


    /**
     * Constructor that creates an internal {@link in.championswimmer.sfg.lib.GestureAnalyser } object as well
     */
    public GestureManager(Context context) {
        ga = new Gesture();
        //gDetect = new GestureDetectorCompat(context, new MyGestureListener());
    }

    public GestureManager(Context context, int swipeSlopeIntolerance, int doubleTapMaxDelayMillis, int doubleTapMaxDownMillis) {
        ga = new Gesture(swipeSlopeIntolerance, doubleTapMaxDelayMillis, doubleTapMaxDownMillis);
        //gDetect = new GestureDetectorCompat(context, new MyGestureListener());
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

    private void doCallBack(Gesture.GestureType mGt) {
        switch (mGt.getGestureFlag()) {
            /*case Gesture.SINGLE_TAP_1:
                onFingerGestureListener.onSingleTap(1);
                break;*/
            /*case Gesture.LONG_PRESS_1:
                onFingerGestureListener.onLongPress();
                break;*/
            /*case Gesture.SWIPE_1_UP:
                onFingerGestureListener.onSwipeUp(1, mGt.getGestureDuration(), mGt.getGestureDistance());
                break;
            case Gesture.SWIPE_1_DOWN:
                onFingerGestureListener.onSwipeDown(1, mGt.getGestureDuration(), mGt.getGestureDistance());
                break;
            case Gesture.SWIPE_1_LEFT:
                onFingerGestureListener.onSwipeLeft(1, mGt.getGestureDuration(), mGt.getGestureDistance());
                break;
            case Gesture.SWIPE_1_RIGHT:
                onFingerGestureListener.onSwipeRight(1, mGt.getGestureDuration(), mGt.getGestureDistance());
                break;*/
            case Gesture.TAP_2:
                onFingerGestureListener.onTap(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());

            case Gesture.SWIPE_2_UP:
                onFingerGestureListener.onSwipeUp(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.SWIPE_2_DOWN:
                onFingerGestureListener.onSwipeDown(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.SWIPE_2_LEFT:
                onFingerGestureListener.onSwipeLeft(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.SWIPE_2_RIGHT:
                onFingerGestureListener.onSwipeRight(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.PINCH_2:
                onFingerGestureListener.onPinch(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.UNPINCH_2:
                onFingerGestureListener.onUnpinch(2, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;

            case Gesture.SWIPE_3_UP:
                onFingerGestureListener.onSwipeUp(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.SWIPE_3_DOWN:
                onFingerGestureListener.onSwipeDown(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.SWIPE_3_LEFT:
                onFingerGestureListener.onSwipeLeft(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.SWIPE_3_RIGHT:
                onFingerGestureListener.onSwipeRight(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.PINCH_3:
                onFingerGestureListener.onPinch(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.UNPINCH_3:
                onFingerGestureListener.onUnpinch(3, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;

            case Gesture.SWIPE_4_UP:
                onFingerGestureListener.onSwipeUp(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.SWIPE_4_DOWN:
                onFingerGestureListener.onSwipeDown(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.SWIPE_4_LEFT:
                onFingerGestureListener.onSwipeLeft(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.SWIPE_4_RIGHT:
                onFingerGestureListener.onSwipeRight(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.PINCH_4:
                onFingerGestureListener.onPinch(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
                break;
            case Gesture.UNPINCH_4:
                onFingerGestureListener.onUnpinch(4, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
            case Gesture.DOUBLE_TAP_1:
                onFingerGestureListener.onDoubleTap(1, ga.initialX, ga.initialY, ga.finalX, ga.finalY, mGt.getGestureDuration());
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


    /**
     * Interface definition for the callback to be invoked when 2-finger gestures are performed
     */
    public interface OnFingerGestureListener {

        /**
         * Called when user swipes <b>up</b> with two fingers
         *
         * @param fingers         number of fingers involved in this gesture
         * @param gestureDuration duration in milliSeconds
         * @return
         */
        public boolean onSwipeUp(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long duration);

        /**
         * Called when user swipes <b>down</b> with two fingers
         *
         * @param fingers         number of fingers involved in this gesture
         * @param gestureDuration duration in milliSeconds
         * @return
         */
        public boolean onSwipeDown(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long duration);

        /**
         * Called when user swipes <b>left</b> with two fingers
         *
         * @param fingers         number of fingers involved in this gesture
         * @param gestureDuration duration in milliSeconds
         * @return
         */
        public boolean onSwipeLeft(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long duration);

        /**
         * Called when user swipes <b>right</b> with two fingers
         *
         * @param fingers         number of fingers involved in this gesture
         * @param gestureDuration duration in milliSeconds
         * @return
         */
        public boolean onSwipeRight(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long duration);

        /**
         * Called when user <b>pinches</b> with two fingers (bring together)
         *
         * @param fingers         number of fingers involved in this gesture
         * @param gestureDuration duration in milliSeconds
         * @return
         */
        public boolean onPinch(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long duration);

        /**
         * Called when user <b>un-pinches</b> with two fingers (take apart)
         *
         * @param fingers         number of fingers involved in this gesture
         * @param gestureDuration duration in milliSeconds
         * @return
         */
        public boolean onUnpinch(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long duration);

        public boolean onDoubleTap(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long duration);


        public boolean onLongPress(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long duration);

        public boolean onTap(int fingers, double[] initialX, double[] initialY, double[] finalX, double[] finalY, long duration);
    }
}