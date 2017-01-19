package com.example.yiyanrong.myapplication;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class



MainActivity extends Activity  {
    MyGLSurfaceView glSurfaceView;

    public static String VL = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_texCoord = a_texCoord;" +
            "gl_PointSize = 15.0;" +
            "}";
    public static String FL = "precision mediump float;" +
            "varying vec2 v_texCoord;" +
            "uniform sampler2D s_texture;" +
            "void main() {" +
            "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去标题栏

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,

                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        setContentView(new OpenGLView(this)/*new CubeSurfaceView(this)*//*new TriangleSurfaceView(this)*/);
        //glSurfaceView = new GLSurfaceView(this);
        setContentView(R.layout.activity_main);

        glSurfaceView = (MyGLSurfaceView)findViewById(R.id.mysurface);
       // glSurfaceView.renderInit();
        //glSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));

         glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new BallRender(this));
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }





}


