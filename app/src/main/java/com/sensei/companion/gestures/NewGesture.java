package com.sensei.companion.gestures;

import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Internal API class to analyse the recorded gestures.
 *
 * @author championswimmer
 * @version 0.2
 * @since 0.1 12/04/14
 */
class NewGesture {
    private static final String DEBUG_TAG = "appMonitor";
    public static final boolean DEBUG = true;
    // Finished gestures flags
    static final int SWIPE_1_UP = 11;
    static final int SWIPE_1_DOWN = 12;
    static final int SWIPE_1_LEFT = 13;
    static final int SWIPE_1_RIGHT = 14;
    static final int SWIPE_2_UP = 21;
    static final int SWIPE_2_DOWN = 22;
    static final int SWIPE_2_LEFT = 23;
    static final int SWIPE_2_RIGHT = 24;
    static final int SWIPE_3_UP = 31;
    static final int SWIPE_3_DOWN = 32;
    static final int SWIPE_3_LEFT = 33;
    static final int SWIPE_3_RIGHT = 34;
    static final int SWIPE_4_UP = 41;
    static final int SWIPE_4_DOWN = 42;
    static final int SWIPE_4_LEFT = 43;
    static final int SWIPE_4_RIGHT = 44;
    static final int PINCH_2 = 25;
    static final int UNPINCH_2 = 26;
    static final int PINCH_3 = 35;
    static final int UNPINCH_3 = 36;
    static final int PINCH_4 = 45;
    static final int UNPINCH_4 = 46;
    static final int SINGLE_TAP = 51;
    static final int LONG_PRESS = 52;
    static final int DOUBLE_TAP_1 = 107;
    static final int TAP_2_FINGERS = 53;
    static boolean doubleTapOccured = false;

    //Ongoing gesture flags
    public static final int SWIPING_1_UP = 101;
    public static final int SWIPING_1_DOWN = 102;
    public static final int SWIPING_1_LEFT = 103;
    public static final int SWIPING_1_RIGHT = 104;
    public static final int SWIPING_2_UP = 201;
    public static final int SWIPING_2_DOWN = 202;
    public static final int SWIPING_2_LEFT = 203;
    public static final int SWIPING_2_RIGHT = 204;
    public static final int PINCHING = 205;
    public static final int UNPINCHING = 206;
    private static final String TAG = "GestureAnalyser";
    double[] initialX = new double[5];
    double[] initialY = new double[5];
    double[] finalX = new double[5];
    double[] finalY = new double[5];
    private double[] currentX = new double[5];
    private double[] currentY = new double[5];
    private double[] delX = new double[5];
    private double[] delY = new double[5];

    private int numFingers = 0;
    private long initialT, finalT, currentT;

    private long prevInitialT, prevFinalT;

    private int swipeSlopeIntolerance = 3;

    private long doubleTapMaxDelayMillis;
    private long doubleTapMaxDownMillis;

    NewGesture() {
        this(3, 500, 125);
    }

    NewGesture(int swipeSlopeIntolerance, int doubleTapMaxDelayMillis, int doubleTapMaxDownMillis) {
        this.swipeSlopeIntolerance = swipeSlopeIntolerance;
        this.doubleTapMaxDownMillis = doubleTapMaxDownMillis;
        this.doubleTapMaxDelayMillis = doubleTapMaxDelayMillis;
    }

    void trackGesture(MotionEvent ev) {
        int n = ev.getPointerCount();
        for (int i = 0; i < n; i++) {
            initialX[i] = ev.getX(i);
            initialY[i] = ev.getY(i);
        }
        numFingers = n;
        initialT = SystemClock.uptimeMillis();
    }

    void untrackGesture() {
        numFingers = 0;
        prevFinalT = SystemClock.uptimeMillis();
        prevInitialT = initialT;
    }

    GestureType getGesture(MotionEvent ev) {
        double averageDistance = 0.0;
        for (int i = 0; i < numFingers; i++) {
            finalX[i] = ev.getX(i);
            finalY[i] = ev.getY(i);
            delX[i] = finalX[i] - initialX[i];
            delY[i] = finalY[i] - initialY[i];

            averageDistance += Math.sqrt(Math.pow(finalX[i] - initialX[i], 2) + Math.pow(finalY[i] - initialY[i], 2));
        }
        averageDistance /= numFingers;

        finalT = SystemClock.uptimeMillis();
        GestureType gt = new GestureType();
        gt.setGestureFlag(calcGesture());
        gt.setGestureDuration(finalT - initialT);
        gt.setGestureDistance(averageDistance);
        return gt;
    }

    public int getOngoingGesture(MotionEvent ev) {
        for (int i = 0; i < numFingers; i++) {
            currentX[i] = ev.getX(i);
            currentY[i] = ev.getY(i);
            delX[i] = finalX[i] - initialX[i];
            delY[i] = finalY[i] - initialY[i];
        }
        currentT = SystemClock.uptimeMillis();
        return calcGesture();
    }

    private int calcGesture() {
        if (isDoubleTap()) {
            return DOUBLE_TAP_1;
        }
        if (numFingers == 1) {
            if(Math.abs(delY[0]) < 25 && Math.abs(delX[0]) < 25 && (finalT-initialT) > 700){
                return LONG_PRESS;
            }
            if(Math.abs(delY[0]) < 25 && Math.abs(delX[0]) < 25){
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        if(doubleTapOccured == true){
                            Log.i(DEBUG_TAG, "dont display");
                        }
                        else{
                        }
                    }

                }, 500);

                return SINGLE_TAP;
            }
            else if ((-(delY[0])) > (swipeSlopeIntolerance * (Math.abs(delX[0])))) {
                return SWIPE_1_UP;
            }

            else if (((delY[0])) > (swipeSlopeIntolerance * (Math.abs(delX[0])))) {
                return SWIPE_1_DOWN;
            }

            else if ((-(delX[0])) > (swipeSlopeIntolerance * (Math.abs(delY[0])))) {
                return SWIPE_1_LEFT;
            }

            if (((delX[0])) > (swipeSlopeIntolerance * (Math.abs(delY[0])))) {
                return SWIPE_1_RIGHT;
            }
        }
        if (numFingers == 2) {
            if(Math.abs(delY[0]) < 25 && Math.abs(delX[0]) < 25 && Math.abs(delY[1]) < 25 && Math.abs(delX[1]) < 25){
                return TAP_2_FINGERS;
            }
            if (((-delY[0]) > (swipeSlopeIntolerance * Math.abs(delX[0]))) && ((-delY[1]) > (swipeSlopeIntolerance * Math.abs(delX[1])))) {
                return SWIPE_2_UP;
            }
            if (((delY[0]) > (swipeSlopeIntolerance * Math.abs(delX[0]))) && ((delY[1]) > (swipeSlopeIntolerance * Math.abs(delX[1])))) {
                return SWIPE_2_DOWN;
            }
            if (((-delX[0]) > (swipeSlopeIntolerance * Math.abs(delY[0]))) && ((-delX[1]) > (swipeSlopeIntolerance * Math.abs(delY[1])))) {
                return SWIPE_2_LEFT;
            }
            if (((delX[0]) > (swipeSlopeIntolerance * Math.abs(delY[0]))) && ((delX[1]) > (swipeSlopeIntolerance * Math.abs(delY[1])))) {
                return SWIPE_2_RIGHT;
            }
            if (finalFingDist(0, 1) > 2 * (initialFingDist(0, 1))) {
                return UNPINCH_2;
            }
            if (finalFingDist(0, 1) < 0.5 * (initialFingDist(0, 1))) {
                return PINCH_2;
            }
        }
        if (numFingers == 3) {
            if (((-delY[0]) > (swipeSlopeIntolerance * Math.abs(delX[0])))
                    && ((-delY[1]) > (swipeSlopeIntolerance * Math.abs(delX[1])))
                    && ((-delY[2]) > (swipeSlopeIntolerance * Math.abs(delX[2])))) {
                return SWIPE_3_UP;
            }
            if (((delY[0]) > (swipeSlopeIntolerance * Math.abs(delX[0])))
                    && ((delY[1]) > (swipeSlopeIntolerance * Math.abs(delX[1])))
                    && ((delY[2]) > (swipeSlopeIntolerance * Math.abs(delX[2])))) {
                return SWIPE_3_DOWN;
            }
            if (((-delX[0]) > (swipeSlopeIntolerance * Math.abs(delY[0])))
                    && ((-delX[1]) > (swipeSlopeIntolerance * Math.abs(delY[1])))
                    && ((-delX[2]) > (swipeSlopeIntolerance * Math.abs(delY[2])))) {
                return SWIPE_3_LEFT;
            }
            if (((delX[0]) > (swipeSlopeIntolerance * Math.abs(delY[0])))
                    && ((delX[1]) > (swipeSlopeIntolerance * Math.abs(delY[1])))
                    && ((delX[2]) > (swipeSlopeIntolerance * Math.abs(delY[2])))) {
                return SWIPE_3_RIGHT;
            }

            if ((finalFingDist(0,1) > 1.75*(initialFingDist(0,1)))
                    && (finalFingDist(1,2) > 1.75*(initialFingDist(1,2)))
                    && (finalFingDist(2,0) > 1.75*(initialFingDist(2,0))) ) {
                return UNPINCH_3;
            }
            if ((finalFingDist(0,1) < 0.66*(initialFingDist(0,1)))
                    && (finalFingDist(1,2) < 0.66*(initialFingDist(1,2)))
                    && (finalFingDist(2,0) < 0.66*(initialFingDist(2,0))) ) {
                return PINCH_3;
            }

        }
        if (numFingers == 4) {
            if (((-delY[0]) > (swipeSlopeIntolerance * Math.abs(delX[0])))
                    && ((-delY[1]) > (swipeSlopeIntolerance * Math.abs(delX[1])))
                    && ((-delY[2]) > (swipeSlopeIntolerance * Math.abs(delX[2])))
                    && ((-delY[3]) > (swipeSlopeIntolerance * Math.abs(delX[3])))) {
                return SWIPE_4_UP;
            }
            if (((delY[0]) > (swipeSlopeIntolerance * Math.abs(delX[0])))
                    && ((delY[1]) > (swipeSlopeIntolerance * Math.abs(delX[1])))
                    && ((delY[2]) > (swipeSlopeIntolerance * Math.abs(delX[2])))
                    && ((delY[3]) > (swipeSlopeIntolerance * Math.abs(delX[3])))) {
                return SWIPE_4_DOWN;
            }
            if (((-delX[0]) > (swipeSlopeIntolerance * Math.abs(delY[0])))
                    && ((-delX[1]) > (swipeSlopeIntolerance * Math.abs(delY[1])))
                    && ((-delX[2]) > (swipeSlopeIntolerance * Math.abs(delY[2])))
                    && ((-delX[3]) > (swipeSlopeIntolerance * Math.abs(delY[3])))) {
                return SWIPE_4_LEFT;
            }
            if (((delX[0]) > (swipeSlopeIntolerance * Math.abs(delY[0])))
                    && ((delX[1]) > (swipeSlopeIntolerance * Math.abs(delY[1])))
                    && ((delX[2]) > (swipeSlopeIntolerance * Math.abs(delY[2])))
                    && ((delX[3]) > (swipeSlopeIntolerance * Math.abs(delY[3])))) {
                return SWIPE_4_RIGHT;
            }
            if ((finalFingDist(0,1) > 1.5*(initialFingDist(0,1)))
                    && (finalFingDist(1,2) > 1.5*(initialFingDist(1,2)))
                    && (finalFingDist(2,3) > 1.5*(initialFingDist(2,3)))
                    && (finalFingDist(3,0) > 1.5*(initialFingDist(3,0))) ) {
                return UNPINCH_4;
            }
            if ((finalFingDist(0,1) < 0.8*(initialFingDist(0,1)))
                    && (finalFingDist(1,2) < 0.8*(initialFingDist(1,2)))
                    && (finalFingDist(2,3) < 0.8*(initialFingDist(2,3)))
                    && (finalFingDist(3,0) < 0.8*(initialFingDist(3,0))) ) {
                return PINCH_4;
            }
        }
        return 0;
    }

    private double initialFingDist(int fingNum1, int fingNum2) {

        return Math.sqrt(Math.pow((initialX[fingNum1] - initialX[fingNum2]), 2)
                + Math.pow((initialY[fingNum1] - initialY[fingNum2]), 2));
    }

    private double finalFingDist(int fingNum1, int fingNum2) {

        return Math.sqrt(Math.pow((finalX[fingNum1] - finalX[fingNum2]), 2)
                + Math.pow((finalY[fingNum1] - finalY[fingNum2]), 2));
    }

    private boolean isDoubleTap() {
        return initialT - prevFinalT < doubleTapMaxDelayMillis && initialT - prevFinalT > 25 && finalT - initialT < doubleTapMaxDownMillis && prevFinalT - prevInitialT < doubleTapMaxDownMillis;
    }

    class GestureType {
        private int gestureFlag;
        private long gestureDuration;

        double gestureDistance;

        long getGestureDuration() {
            return gestureDuration;
        }

        void setGestureDuration(long gestureDuration) {
            this.gestureDuration = gestureDuration;
        }


        int getGestureFlag() {
            return gestureFlag;
        }

        void setGestureFlag(int gestureFlag) {
            this.gestureFlag = gestureFlag;
        }


        public double getGestureDistance() {
            return gestureDistance;
        }

        void setGestureDistance(double gestureDistance) {
            this.gestureDistance = gestureDistance;
        }
    }


}