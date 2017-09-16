/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dji.FPVDemo.opengl;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;


/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    //    protected final MyGLRenderer mRenderer;
    public final MyGLRenderer mRenderer;
    public static Resources res;
    Double headingangle;

    public MyGLSurfaceView(Context context) {
        super(context);
        res = context.getResources();

        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    private float mPreviousAngle;

    public void setHeadingAngle(Float headingangle) {
//        float x = mPreviousAngle;
        if (headingangle != mPreviousAngle) {
            mPreviousAngle = headingangle;
            mRenderer.setAngle(headingangle);
            requestRender();
        }

    }

    private float mPreviousPitch;
    private float mPreviousRoll;

    public void setAttitude(Float pitch, Float roll) {
        if (pitch != mPreviousPitch || roll != mPreviousRoll) {
            mPreviousPitch = pitch;
            mPreviousRoll = roll;
            mRenderer.setPitch(pitch);
            mRenderer.setRoll(roll);
        }
    }

//    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
//    private float mPreviousY;

//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        // MotionEvent reports input details from the touch screen
//        // and other input controls. In this case, you are only
//        // interested in events where the touch position changed.
//
//        float x = e.getX();
//        float y = e.getY();
//
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//
//                float dx = x - mPreviousX;
//                float dy = y - mPreviousY;
//
//                // reverse direction of rotation above the mid-line
//                if (y > getHeight() / 2) {
//                    dx = dx * -1 ;
//                }
//
//                // reverse direction of rotation to left of the mid-line
//                if (x < getWidth() / 2) {
//                    dy = dy * -1 ;
//                }
//
//                mRenderer.setAngle(
//                        mRenderer.getAngle() +
//                        ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
//                requestRender();
//
//        }
//
//        mPreviousX = x;
//        mPreviousY = y;
//        return true;
//    }

}
