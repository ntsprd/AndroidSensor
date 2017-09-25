package accelerometer.client.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import accelerometer.client.R;
import accelerometer.client.api.RestApi;
import accelerometer.client.model.Acceleration;

import java.util.Date;

import retrofit.RestAdapter;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener{

    private String restURL;
    private TextView accelerationTextView;
    private Button startButton;
    private Button stopButton;
    private RestApi restApi;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        accelerationTextView = (TextView) findViewById(R.id.acceleration);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        initRestApi();
        initActionButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accelerometer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Acceleration capturedAcceleration = getAccFromSensor(event);
        updateTextView(capturedAcceleration);
        new SendAccelerationAsyncTask().execute(capturedAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void initRestApi() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            restURL = extras.getString(MainActivity.URL);
        }

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(restURL).build();
        restApi = restAdapter.create(RestApi.class);
    }

    private void initActionButtons() {
        startButton = (Button) findViewById(R.id.button_start);
        stopButton = (Button) findViewById(R.id.button_stop);

        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSensor();
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSensor();
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                finish();
            }
        });
    }

    private void startSensor() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensor() {
        sensorManager.unregisterListener(this);
    }

    private void updateTextView(Acceleration acc) {
        accelerationTextView.setText("x:" + acc.getX() + "\n" +
                "y: " + acc.getY() + "\n" +
                "z: " + acc.getZ() + "\n" +
                "czas: " + acc.getTimestamp());
    }

    private Acceleration getAccFromSensor(SensorEvent event) {
        long timestamp = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
        return new Acceleration(event.values[0], event.values[1], event.values[2], timestamp);
    }

    private class SendAccelerationAsyncTask extends AsyncTask<Acceleration, Void, Void> {

        @Override
        protected Void doInBackground(Acceleration... params) {
            try {
                restApi.sendAccelerationValues(params[0]);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
