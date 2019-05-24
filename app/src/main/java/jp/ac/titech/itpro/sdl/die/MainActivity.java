package jp.ac.titech.itpro.sdl.die;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, SensorEventListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private GLSurfaceView glView;
    private SimpleRenderer renderer;

    private Cube cube;
    private Pyramid pyramid;

    private SensorManager manager;
    private Sensor gyroscope;

    private SeekBar seekBarX;
    private SeekBar seekBarY;
    private SeekBar seekBarZ;

    double sumX = 1800;
    double sumY = 1800;
    double sumZ = 1800;

    double from = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        glView = findViewById(R.id.gl_view);
        seekBarX = findViewById(R.id.seekbar_x);
        seekBarY = findViewById(R.id.seekbar_y);
        seekBarZ = findViewById(R.id.seekbar_z);
        seekBarX.setMax(3600);
        seekBarY.setMax(3600);
        seekBarZ.setMax(3600);
        seekBarX.setOnSeekBarChangeListener(this);
        seekBarY.setOnSeekBarChangeListener(this);
        seekBarZ.setOnSeekBarChangeListener(this);

        renderer = new SimpleRenderer();
        cube = new Cube();
        pyramid = new Pyramid();
        renderer.setObj(cube);
        glView.setRenderer(renderer);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager == null) {
            Toast.makeText(this, R.string.toast_no_sensor_manager, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope == null) {
            Toast.makeText(this, R.string.toast_no_gyroscope, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        manager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        glView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
        case R.id.menu_cube:
            renderer.setObj(cube);
            break;
        case R.id.menu_pyramid:
            renderer.setObj(pyramid);
            break;
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
        case R.id.seekbar_x:
            renderer.rotateObjX(progress);
            break;
        case R.id.seekbar_y:
            renderer.rotateObjY(progress);
            break;
        case R.id.seekbar_z:
            renderer.rotateObjZ(progress);
            break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float omegaX = event.values[0];
        float omegaY = event.values[1];
        float omegaZ = event.values[2];

        if(from == -1) {
            from = event.timestamp;
        }

        sumX += Math.toDegrees(omegaX * (event.timestamp - from) * 0.00000000001);
        sumY += Math.toDegrees(omegaY * (event.timestamp - from) * 0.00000000001);
        sumZ += Math.toDegrees(omegaZ * (event.timestamp - from) * 0.00000000001);

        seekBarX.setProgress((int)sumX);
        seekBarY.setProgress((int)sumY);
        seekBarZ.setProgress((int)sumZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: accuracy=" + accuracy);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
