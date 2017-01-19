package com.example.yiyanrong.myapplication.Sphere;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by yiyanrong on 17/1/13.
 */
public class OpenGLView extends GLSurfaceView  {
    private OpenGLRenderer4 mRenderer;
    private float mDownX = 0.0f;
    private float mDownY = 0.0f;
    private float mDownZ=0.0f;

    public OpenGLView(Context context) {
        super(context);
        mRenderer = new OpenGLRenderer4(context);
        this.setRenderer(mRenderer);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getActionMasked();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mDownX = event.getX();
//                mDownY = event.getY();
//                return true;
//            case MotionEvent.ACTION_UP:
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                float mX = event.getX();
//                float mY = event.getY();
//                mRenderer.mLightX += (mX-mDownX)/10;
//                mRenderer.mLightY -= (mY-mDownY)/10;
//                mDownX = mX;
//                mDownY = mY;
//                return true;
//            default:
//                return super.onTouchEvent(event);
//        }
//    }


}