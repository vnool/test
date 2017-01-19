package com.example.yiyanrong.myapplication;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class



MainActivity extends Activity implements OnCaptureCallback {
    int CAP = 30;//绘制球体时，每次增加的角度
    float[] verticals = new float[(180/CAP) * (360/CAP) * 6 * 3];
    private final float[] UV_TEX_VERTEX = new float[(180/CAP) * (360/CAP) * 6 * 2];
    private MaskSurfaceView surfaceview;
    private ImageView imageView;
    //	拍照
    private Button btn_capture;
    //	重拍
    private Button btn_recapture;
    //	取消
    private Button btn_cancel;
    //	确认
    private Button btn_ok;

    //	拍照后得到的保存的文件路径
    private String filepath;
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
        setContentView(R.layout.activity_main);
        glSurfaceView = (MyGLSurfaceView)findViewById(R.id.surface);
//        setContentView(new OpenGLView(this)/*new CubeSurfaceView(this)*//*new TriangleSurfaceView(this)*/);
        //glSurfaceView = new GLSurfaceView(this);
       // glSurfaceView.renderInit();
        //glSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.setZOrderOnTop(true);
        glSurfaceView.setRenderer(new BallRender(this));
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        this.surfaceview = (MaskSurfaceView) findViewById(R.id.surface_view);
        this.imageView = (ImageView) findViewById(R.id.image_view);
        btn_capture = (Button) findViewById(R.id.btn_capture);
        btn_recapture = (Button) findViewById(R.id.btn_recapture);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);


//		设置矩形区域大小
        this.surfaceview.setMaskSize(1560, 800);

//		拍照
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_capture.setEnabled(false);
                btn_ok.setEnabled(true);
                btn_recapture.setEnabled(true);
                CameraHelper.getInstance().tackPicture(MainActivity.this);
            }
        });

//		重拍
        btn_recapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_capture.setEnabled(true);
                btn_ok.setEnabled(false);
                btn_recapture.setEnabled(false);
                imageView.setVisibility(View.GONE);
                surfaceview.setVisibility(View.VISIBLE);
                deleteFile();
                CameraHelper.getInstance().startPreview();
            }
        });

//		确认
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

//		取消
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                deleteFile();
                MainActivity.this.finish();
            }
        });
    }
    /**
     * 删除图片文件呢
     */
    private void deleteFile(){
        if(this.filepath==null || this.filepath.equals("")){
            return;
        }
        File f = new File(this.filepath);
        if(f.exists()){
            f.delete();
        }
    }

    @Override
    public void onCapture(boolean success, String filepath) {
        this.filepath = filepath;
        String message = "拍照成功";
        if(!success){
            message = "拍照失败";
            CameraHelper.getInstance().startPreview();
            this.imageView.setVisibility(View.GONE);
            this.surfaceview.setVisibility(View.VISIBLE);
        }else{
            this.imageView.setVisibility(View.VISIBLE);
            this.surfaceview.setVisibility(View.GONE);
            this.imageView.setImageBitmap(BitmapFactory.decodeFile(filepath));
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }









}


