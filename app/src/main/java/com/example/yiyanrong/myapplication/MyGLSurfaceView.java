package com.example.yiyanrong.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yiyanrong on 2017/1/17.
 */

public class MyGLSurfaceView extends GLSurfaceView implements SensorEventListener {


    private BallRender myRender;


    float startRawX;
    float startRawY;

    double xFlingAngle;
    double xFlingAngleTemp;

    double yFlingAngle;
    double yFlingAngleTemp;

    Context C;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        C = context;
        registerSensor();
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
        this.myRender = (BallRender) renderer;
    }

    public void updateLookAt(float X, float Y, float Z) {
        myRender.mAngleX = X;
        myRender.mAngleY = Y;
        myRender.mAngleZ = Z;
        this.requestRender();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        float mAngleX = 0;// 摄像机所在的x坐标
        float mAngleY = 0;// 摄像机所在的y坐标
        float mAngleZ = 3;// 摄像机所在的z坐标


        //处理手指滑动事件，我这里的处理是判断手指在横向和竖向滑动的距离
        //这个距离隐射到球体上经度和纬度的距离，根据这个距离计算三维空间的两个
        //夹角，根据这个夹角调整摄像机所在位置
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            startRawX = me.getRawX();
            startRawY = me.getRawY();
        } else if (me.getAction() == MotionEvent.ACTION_MOVE) {

            float distanceX = startRawX - me.getRawX();
            float distanceY = startRawY - me.getRawY();
            //这里的0.1f是为了不上摄像机移动的过快
            distanceY = 0.1f * (distanceY) / getHeight();

            yFlingAngleTemp = distanceY * 180 / (Math.PI * 3);

            if (yFlingAngleTemp + yFlingAngle > Math.PI / 2) {
                yFlingAngleTemp = Math.PI / 2 - yFlingAngle;
            }
            if (yFlingAngleTemp + yFlingAngle < -Math.PI / 2) {
                yFlingAngleTemp = -Math.PI / 2 - yFlingAngle;
            }
            //这里的0.1f是为了不上摄像机移动的过快
            distanceX = 0.1f * (-distanceX) / getWidth();
            xFlingAngleTemp = distanceX * 180 / (Math.PI * 3);


            mAngleX = (float) (3 * Math.cos(yFlingAngle + yFlingAngleTemp) * Math.sin(xFlingAngle + xFlingAngleTemp));

            mAngleY = (float) (3 * Math.sin(yFlingAngle + yFlingAngleTemp));


            mAngleZ = (float) (3 * Math.cos(yFlingAngle + yFlingAngleTemp) * Math.cos(xFlingAngle + xFlingAngleTemp));

            System.out.println("camera x------------->" + mAngleX);
            System.out.println("camera y------------->" + mAngleY);
            System.out.println("camera z------------->" + mAngleZ);

            updateLookAt(mAngleX, mAngleY, mAngleZ);

        } else if (me.getAction() == MotionEvent.ACTION_UP) {
            xFlingAngle += xFlingAngleTemp;
            yFlingAngle += yFlingAngleTemp;
        }
        return true;
    }


    // 将纳秒转化为秒
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float angle[] = new float[3];
    private float anglex = 0.0f;
    private float angley = 0.0f;
    private float anglez = 0.0f;
    //陀螺仪
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;

    void registerSensor() {
        sensorManager = (SensorManager) C.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(
                //Sensor.TYPE_ORIENTATION
                //Sensor.TYPE_GYROSCOPE
                //Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR
                //Sensor.TYPE_MAGNETIC_FIELD
                Sensor.TYPE_ROTATION_VECTOR
        );
        sensorManager.registerListener((SensorEventListener) this, gyroscopeSensor,
                SensorManager.SENSOR_DELAY_UI
                // SensorManager.SENSOR_DELAY_NORMAL
        );


//        gyroscopeSensor = sensorManager
//                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
//
//        StringBuffer sb = new StringBuffer();
//        //获取手机全部的传感器
//        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
//        //迭代输出获得上的传感器
//        for (Sensor sensor : sensors) {
//        //System.out.println(sensor.getName().toString());
//            sb.append(sensor.getName().toString());
//            sb.append("\n");
//            Log.i("Sensor", sensor.getName().toString());
//        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int curType = event.sensor.getType();

        if (curType == Sensor.TYPE_ACCELEROMETER) {
            // x,y,z分别存储坐标轴x,y,z上的加速度
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // 根据三个方向上的加速度值得到总的加速度值a
            float a = (float) Math.sqrt(x * x + y * y + z * z);
            System.out.println("a---------->" + a);
            // 传感器从外界采集数据的时间间隔为10000微秒
//            System.out.println("magneticSensor.getMinDelay()-------->"
//                    + magneticSensor.getMinDelay());
//
            // 加速度传感器的最大量程
            System.out.println("event.sensor.getMaximumRange()-------->"
                    + event.sensor.getMaximumRange());

            System.out.println("x------------->" + x);
            System.out.println("y------------->" + y);
            System.out.println("z------------->" + z);

            Log.d("TAG", "x------------->" + x);
            Log.d("TAG", "y------------>" + y);
            Log.d("TAG", "z----------->" + z);

//			 showTextView.setText("x---------->" + x + "\ny-------------->" +
//			 y + "\nz----------->" + z);
        } else if (curType == Sensor.TYPE_MAGNETIC_FIELD) {
            if ((int) (event.timestamp / 10000) % 5 != 0) return;


            // 三个坐标轴方向上的电磁强度，单位是微特拉斯(micro-Tesla)，用uT表示，也可以是高斯(Gauss),1Tesla=10000Gauss
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // 手机的磁场感应器从外部采集数据的时间间隔是10000微秒
//            System.out.println("magneticSensor.getMinDelay()-------->"
//                    + magneticSensor.getMinDelay());
            // 磁场感应器的最大量程
            System.out.println("event.sensor.getMaximumRange()----------->"
                    + event.sensor.getMaximumRange());
            System.out.println("x------------->" + x);
            System.out.println("y------------->" + y);
            System.out.println("z------------->" + z);
            //
            // Log.d("TAG","x------------->" + x);
            // Log.d("TAG", "y------------>" + y);
            // Log.d("TAG", "z----------->" + z);
            //
            // showTextView.setText("x---------->" + x + "\ny-------------->" +
            // y + "\nz----------->" + z);
        } else if (curType == Sensor.TYPE_GYROSCOPE) {
            //从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            if (timestamp == 0) {
                timestamp = event.timestamp;
                return;
            }

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

            //Log.e("SENSOR", anglex + ", " + angley + ", " + anglez);

            float X = 3 * (float) Math.cos(angle[1]);
            float Z = 3 * (float) Math.sin(angle[1]);

            this.updateLookAt(X, myRender.mAngleY, Z);

            //将当前时间赋值给timestamp
            timestamp = event.timestamp;

        } else  if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
//            SensorManager.getRotationMatrixFromVector(
//                    mRotationMatrix , event.values);
        } else {

            if ((int) (event.timestamp / 10000) % 5 != 0) return;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
//            System.out.println("magneticSensor.getMinDelay()-------->"
//                    + magneticSensor.getMinDelay());

            System.out.println("event.sensor.getMaximumRange()----------->"
                    + event.sensor.getMaximumRange());
            System.out.println("x------------->" + x);
            System.out.println("y------------->" + y);
            System.out.println("z------------->" + z);


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
