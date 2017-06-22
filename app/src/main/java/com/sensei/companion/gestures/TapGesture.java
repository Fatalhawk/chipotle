package com.sensei.companion.gestures;

/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class TapGesture {

    /**
     * The Gesture listener.
     */
    final GestureListener gestureListener; // it's needed for TouchTypeDetectorTest, don't remove
    //gesture detector
    private final GestureDetectorCompat gDetect;
    private final TouchTypListener touchTypListener;

    /**
     * Instantiates a new Touch type detector.
     *
     * @param context
     *     the context
     * @param touchTypListener
     *     the touch typ listener
     */
    public TapGesture(Context context, TouchTypListener touchTypListener) {
        gestureListener = new GestureListener();
        gDetect = new GestureDetectorCompat(context, gestureListener);
        this.touchTypListener = touchTypListener;
    }

    /**
     * On touch event boolean.
     *
     * @param event
     *     the event
     * @return the boolean
     */
    public boolean onTouchEvent(MotionEvent event) {
        return gDetect.onTouchEvent(event);
    }

    /**
     * The interface Touch typ listener.
     */
    public interface TouchTypListener {

        /**
         * On double tap.
         */
        void onDoubleTap();

        /**
         * On single tap.
         */
        void onSingleTap();


        void onLongPress();
    }

    /**
     * The type Gesture listener.
     */
    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            touchTypListener.onLongPress();
            super.onLongPress(e);
        }


        @Override
        public boolean onDoubleTap(MotionEvent e) {
            touchTypListener.onDoubleTap();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            touchTypListener.onSingleTap();
            return super.onSingleTapConfirmed(e);
        }
    }
}
