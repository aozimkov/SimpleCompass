package ru.a7logic.simplecompass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mCompass;
    private Sensor mAccelerometer;
    private float[] mMagnetic;
    private float[] mGravity;
    private float[] mOrientation = new float[3];
    float azimuth;



    TextView compassTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Sensor Manager and sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        compassTxt = (TextView) findViewById(R.id.direction);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // collect sensors data
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = sensorEvent.values;

        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mMagnetic = sensorEvent.values;

        if(mGravity != null && mMagnetic != null){
            float mR[] = new float[9];
            float mI[] = new float[9];
            float mReadyR[] = new float[9];

            if (SensorManager.getRotationMatrix(mR, mI, mGravity, mMagnetic)){
                //create a positioning matrix and prepare coordinate system
                SensorManager.remapCoordinateSystem(
                        mR, SensorManager.AXIS_X, SensorManager.AXIS_Y, mReadyR);
                SensorManager.getOrientation(mReadyR, mOrientation);
                /**
                 * values[0]: Azimuth, angle of rotation about the -z axis. This value represents
                 *  the angle between the device's y axis and the magnetic north pole. When facing
                 *  north, this angle is 0, when facing south, this angle is π. Likewise, when
                 *  facing east, this angle is π/2, and when facing west, this angle is -π/2.
                 *  The range of values is -π to π.
                 *
                 * values[1]: Pitch, angle of rotation about the x axis. This value represents the
                 *  angle between a plane parallel to the device's screen and a plane parallel to
                 *  the ground. Assuming that the bottom edge of the device faces the user and that
                 *  the screen is face-up, tilting the top edge of the device toward the ground
                 *  creates a positive pitch angle. The range of values is -π to π.
                 *
                 * values[2]: Roll, angle of rotation about the y axis. This value represents the
                 *  angle between a plane perpendicular to the device's screen and a plane
                 *  perpendicular to the ground. Assuming that the bottom edge of the device faces
                 *  the user and that the screen is face-up, tilting the left edge of the device
                 *  toward the ground creates a positive roll angle. The range of values is -π/2
                 *  to π/2.
                 */
                azimuth = mOrientation[0];

                float heading = (float)(Math.toDegrees(azimuth)+360)%360; //get heading float
                compassTxt.setText(getString(R.string.direction, (int)heading));
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mCompass, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}
