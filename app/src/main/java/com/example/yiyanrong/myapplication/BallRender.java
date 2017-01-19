package com.example.yiyanrong.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.example.yiyanrong.myapplication.MainActivity;
import com.example.yiyanrong.myapplication.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dingchengliang on 17/1/18.
 */
class BallRender implements GLSurfaceView.Renderer{
    public float mAngleX = 0;// 摄像机所在的x坐标
    public float mAngleY = 0;// 摄像机所在的y坐标
    public float mAngleZ = 3;// 摄像机所在的z坐标

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


    FloatBuffer verticalsBuffer;

    int CAP = 9;//绘制球体时，每次增加的角度
    float[] verticals = new float[(180 / CAP) * (360 / CAP) * 6 * 3];


    private final FloatBuffer mUvTexVertexBuffer;

    private final float[] UV_TEX_VERTEX = new float[(180 / CAP) * (360 / CAP) * 6 * 2];

    private int mProgram;
    private int mPositionHandle;
    private int mTexCoordHandle;
    private int mMatrixHandle;
    private int mTexSamplerHandle;
    int[] mTexNames;
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mCameraMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private int mWidth;
    private int mHeight;

    float[] arrayPoints = new float[(180 / CAP) * (360 / CAP) * 6 * 3 + 200];
    FloatBuffer pointsBuffer;

    Context C;

    public BallRender(Context c ) {
        C = c;


        float x = 0;
        float y = 0;
        float z = 0;

        float r = 3;//球体半径
        int index = 0;
        int index1 = 0;
        int pointStepX = 0;
        int pointStepY = 0;
        double d = CAP * Math.PI / 180;//每次递增的弧度
        for (int i = 0; i < 180; i += CAP) {
            double d1 = i * Math.PI / 180;
            pointStepY++;
            for (int j = 0; j < 360; j += CAP) {
                //获得球体上切分的超小片矩形的顶点坐标（两个三角形组成，所以有六点顶点）
                double d2 = j * Math.PI / 180;
                verticals[index++] = (float) (x + r * Math.sin(d1 + d) * Math.cos(d2 + d));
                verticals[index++] = (float) (y + r * Math.cos(d1 + d));
                verticals[index++] = (float) (z + r * Math.sin(d1 + d) * Math.sin(d2 + d));

                //获得球体上切分的超小片三角形的纹理坐标
                UV_TEX_VERTEX[index1++] = (j + CAP) * 1f / 360;
                UV_TEX_VERTEX[index1++] = (i + CAP) * 1f / 180;

                pointStepX++;
                if (pointStepX % 4 == 0 && pointStepY % 4 == 0) {
                    setPoints(verticals, index - 1);
                }

                verticals[index++] = (float) (x + r * Math.sin(d1) * Math.cos(d2));
                verticals[index++] = (float) (y + r * Math.cos(d1));
                verticals[index++] = (float) (z + r * Math.sin(d1) * Math.sin(d2));


                UV_TEX_VERTEX[index1++] = j * 1f / 360;
                UV_TEX_VERTEX[index1++] = i * 1f / 180;

                verticals[index++] = (float) (x + r * Math.sin(d1) * Math.cos(d2 + d));
                verticals[index++] = (float) (y + r * Math.cos(d1));
                verticals[index++] = (float) (z + r * Math.sin(d1) * Math.sin(d2 + d));

                UV_TEX_VERTEX[index1++] = (j + CAP) * 1f / 360;
                UV_TEX_VERTEX[index1++] = i * 1f / 180;

                verticals[index++] = (float) (x + r * Math.sin(d1 + d) * Math.cos(d2 + d));
                verticals[index++] = (float) (y + r * Math.cos(d1 + d));
                verticals[index++] = (float) (z + r * Math.sin(d1 + d) * Math.sin(d2 + d));
//setPoints(verticals,index-1);
                UV_TEX_VERTEX[index1++] = (j + CAP) * 1f / 360;
                UV_TEX_VERTEX[index1++] = (i + CAP) * 1f / 180;

                verticals[index++] = (float) (x + r * Math.sin(d1 + d) * Math.cos(d2));
                verticals[index++] = (float) (y + r * Math.cos(d1 + d));
                verticals[index++] = (float) (z + r * Math.sin(d1 + d) * Math.sin(d2));

                UV_TEX_VERTEX[index1++] = j * 1f / 360;
                UV_TEX_VERTEX[index1++] = (i + CAP) * 1f / 180;

                verticals[index++] = (float) (x + r * Math.sin(d1) * Math.cos(d2));
                verticals[index++] = (float) (y + r * Math.cos(d1));
                verticals[index++] = (float) (z + r * Math.sin(d1) * Math.sin(d2));
//setPoints(verticals,index-1);
                UV_TEX_VERTEX[index1++] = j * 1f / 360;
                UV_TEX_VERTEX[index1++] = i * 1f / 180;


            }
        }
        verticalsBuffer = ByteBuffer.allocateDirect(verticals.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(verticals);
        verticalsBuffer.position(0);


        mUvTexVertexBuffer = ByteBuffer.allocateDirect(UV_TEX_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(UV_TEX_VERTEX);
        mUvTexVertexBuffer.position(0);

        pointsBuffer = ByteBuffer.allocateDirect(arrayPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(arrayPoints);
        pointsBuffer.position(0);


    }


    int mypoints = 0;

    private void setPoints(float[] verticals, int index) {
        try {
            float Qx = verticals[index - 2];
            float Qy = verticals[index - 1];
            float Qz = verticals[index];
            arrayPoints[mypoints++] = Qx;
            arrayPoints[mypoints++] = Qy;
            arrayPoints[mypoints++] = Qz;
        } catch (Exception e) {
            String x = (index + "=== index");
        }

    }

    private int createShader(int type, String source) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        //checkError("glShaderSource");
        GLES20.glCompileShader(shader);
        //checkError("glCompileShader");
        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // DrawScene(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        mWidth = width;
        mHeight = height;
        mProgram = GLES20.glCreateProgram();

        GLES20.glEnable(GLES20.GL_ALIASED_POINT_SIZE_RANGE);


        int vertexShader = createShader(GLES20.GL_VERTEX_SHADER, VL);

        int fragmentShader = createShader(GLES20.GL_FRAGMENT_SHADER, FL);


        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");

        mTexNames = new int[1];
        GLES20.glGenTextures(1, mTexNames, 0);
        //这里的全景图需要长宽的比例使2：1，不然上下顶点会出现形变
        Bitmap bitmap = BitmapFactory.decodeResource(C.getResources(), R.drawable.ssec);
        // GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexNames[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        //GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        float ratio = (float) height / width;
        Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -ratio, ratio, 3, 7);


    }

    @Override
    public void onDrawFrame(GL10 gl) {


        //调整摄像机焦点位置，使画面滚动
        Matrix.setLookAtM(mCameraMatrix, 0, mAngleX, mAngleY, mAngleZ, 0, 0, 0, 0, 1, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mCameraMatrix, 0);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                12, verticalsBuffer);

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, (180 / CAP) * (360 / CAP) * 6);


        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                12, pointsBuffer);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, (180 / CAP) * (360 / CAP) * 6);

        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
                mUvTexVertexBuffer);

        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1i(mTexSamplerHandle, 0);
//
//

//

        GLES20.glDisableVertexAttribArray(mPositionHandle);


    }



}