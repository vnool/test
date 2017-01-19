package com.example.yiyanrong.myapplication.Sphere;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yiyanrong on 17/1/13.
 */
public class OpenGLRenderer4 implements GLSurfaceView.Renderer,SensorEventListener {
    // 环境光
    private final float[] mat_ambient = { 1f, 1f, 1f, 1.0f };
    private FloatBuffer mat_ambient_buf;
    // 平行入射光
    private final float[] mat_diffuse = { 1f, 1f, 1f, 1.0f };
    private FloatBuffer mat_diffuse_buf;
    // 高亮区域
    private final float[] mat_specular = { 1f, 1f , 1f, 1.0f };
    private FloatBuffer mat_specular_buf;

    private Sphere mSphere = new Sphere();

    public volatile float mLightX = 10f;
    public volatile float mLightY = 10f;
    public volatile float mLightZ = 10f;
    //陀螺仪
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    // 将纳秒转化为秒
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float angle[] = new float[3];
    private float anglex=0.0f;
    private float angley=0.0f;
    private float anglez=0.0f;

    public OpenGLRenderer4(Context context) {
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener((SensorEventListener) this, gyroscopeSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清楚屏幕和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // 重置当前的模型观察矩阵
        gl.glLoadIdentity();

        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        // 设置视点
//       GLU.gluLookAt(gl,0.5f, 0.5f, 0.5f,anglex,angley,anglez, 1f,0,0);
        // 材质
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mat_ambient_buf);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mat_diffuse_buf);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mat_specular_buf);
        // 镜面指数 0~128 越小越粗糙
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 96.0f);

        //光源位置
        float[] light_position = {mLightX, mLightY, mLightZ, 0.0f};
        ByteBuffer mpbb = ByteBuffer.allocateDirect(light_position.length*4);
        mpbb.order(ByteOrder.nativeOrder());
        FloatBuffer mat_posiBuf = mpbb.asFloatBuffer();
        mat_posiBuf.put(light_position);
        mat_posiBuf.position(0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, mat_posiBuf);

        gl.glTranslatef(0.0f, 0.0f, 0.0f);
        mSphere.draw(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // 设置输出屏幕大小
        gl.glViewport(0, 0, width, height);

        // 设置投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // 重置投影矩阵
        gl.glLoadIdentity();
        // 设置视口大小
        // gl.glFrustumf(0, width, 0, height, 0.1f, 100.0f);

        GLU.gluPerspective(gl, 90.0f, (float) width / height, 0.1f, 50.0f);

        // 选择模型观察矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // 重置模型观察矩阵
        gl.glLoadIdentity();

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
        // 对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        // 背景：黑色
        gl.glClearColor(0, 0.0f, 0.0f, 0.0f);
        // 启动阴影平滑
        gl.glShadeModel(GL10.GL_SMOOTH);

        // 复位深度缓存
        gl.glClearDepthf(1.0f);
        // 启动深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // 所做深度测试的类型
        gl.glDepthFunc(GL10.GL_LEQUAL);

        initBuffers();
    }

    private void initBuffers() {
        ByteBuffer bufTemp = ByteBuffer.allocateDirect(mat_ambient.length * 4);
        bufTemp.order(ByteOrder.nativeOrder());
        mat_ambient_buf = bufTemp.asFloatBuffer();
        mat_ambient_buf.put(mat_ambient);
        mat_ambient_buf.position(0);

        bufTemp = ByteBuffer.allocateDirect(mat_diffuse.length * 4);
        bufTemp.order(ByteOrder.nativeOrder());
        mat_diffuse_buf = bufTemp.asFloatBuffer();
        mat_diffuse_buf.put(mat_diffuse);
        mat_diffuse_buf.position(0);

        bufTemp = ByteBuffer.allocateDirect(mat_specular.length * 4);
        bufTemp.order(ByteOrder.nativeOrder());
        mat_specular_buf = bufTemp.asFloatBuffer();
        mat_specular_buf.put(mat_specular);
        mat_specular_buf.position(0);
    }
    //坐标轴都是手机从左侧到右侧的水平方向为x轴正向，从手机下部到上部为y轴正向，垂直于手机屏幕向上为z轴正向
    @Override
    public void onSensorChanged(SensorEvent event) {

//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            // x,y,z分别存储坐标轴x,y,z上的加速度
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//            // 根据三个方向上的加速度值得到总的加速度值a
//            float a = (float) Math.sqrt(x * x + y * y + z * z);
//            System.out.println("a---------->" + a);
//            // 传感器从外界采集数据的时间间隔为10000微秒
//            System.out.println("magneticSensor.getMinDelay()-------->"
//                    + magneticSensor.getMinDelay());
//            // 加速度传感器的最大量程
//            System.out.println("event.sensor.getMaximumRange()-------->"
//                    + event.sensor.getMaximumRange());
//
//            System.out.println("x------------->" + x);
//            System.out.println("y------------->" + y);
//            System.out.println("z------------->" + z);
//
//            Log.d("TAG","x------------->" + x);
//            Log.d("TAG", "y------------>" + y);
//            Log.d("TAG", "z----------->" + z);
//
////			 showTextView.setText("x---------->" + x + "\ny-------------->" +
////			 y + "\nz----------->" + z);
//        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            // 三个坐标轴方向上的电磁强度，单位是微特拉斯(micro-Tesla)，用uT表示，也可以是高斯(Gauss),1Tesla=10000Gauss
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//            // 手机的磁场感应器从外部采集数据的时间间隔是10000微秒
//            System.out.println("magneticSensor.getMinDelay()-------->"
//                    + magneticSensor.getMinDelay());
//            // 磁场感应器的最大量程
//            System.out.println("event.sensor.getMaximumRange()----------->"
//                    + event.sensor.getMaximumRange());
//            System.out.println("x------------->" + x);
//            System.out.println("y------------->" + y);
//            System.out.println("z------------->" + z);
//            //
//            // Log.d("TAG","x------------->" + x);
//            // Log.d("TAG", "y------------>" + y);
//            // Log.d("TAG", "z----------->" + z);
//            //
//            // showTextView.setText("x---------->" + x + "\ny-------------->" +
//            // y + "\nz----------->" + z);
//        } else
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            if(timestamp != 0) {
                // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                final float dT = (event.timestamp - timestamp) * NS2S;
                // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                angle[0] += event.values[0] * dT;
                angle[1] += event.values[1] * dT;
                angle[2] += event.values[2] * dT;
                // 将弧度转化为角度
                anglex = (float) Math.toDegrees(angle[0]);
                angley = (float) Math.toDegrees(angle[1]);
                anglez = (float) Math.toDegrees(angle[2]);

            }
            //将当前时间赋值给timestamp
            timestamp = event.timestamp;

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }


}

