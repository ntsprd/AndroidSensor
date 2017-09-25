package accelerometer.client.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import accelerometer.client.R;
import accelerometer.client.api.RestApi;
import accelerometer.client.model.Acceleration;
import accelerometer.client.model.ActivityEnum;
import accelerometer.client.model.TrainingAcceleration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;

public class CollectingActivity extends AppCompatActivity implements SensorEventListener{

    private String urlString;
    private String userID;
    private String selectedActivityString;
    private Timer timer;
    private TimerTask startTimerTask;
    private TimerTask stopTimerTask;

    private TextView accelerationTextView;
    private Spinner activitySpinner;
    private ToneGenerator toneGenerator;
    private Button startButton;
    private Button stopButton;

    private RestApi restApi;

    private SensorManager sm;
    private Sensor accelerometer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_data);
        accelerationTextView = (TextView) findViewById(R.id.acceleration);
        userID = ((EditText) findViewById(R.id.userID)).getText().toString();
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        initActivitySpinner();
        initRestApi();
        initTimerTasksWithAlertSound();
        initActionButtons();
        Button myBackButton = (Button) findViewById(R.id.button_collect_exit);
        myBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSensor();
                startTimerTask.cancel();
                stopTimerTask.cancel();
                timer.cancel();
                finish();
            }
        });
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
        Acceleration capturedAcceleration = getAccelerationFromSensor(event);
        updateTextView(capturedAcceleration);
        new SendAccelerationAsyncTask().execute(capturedAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void initActionButtons() {
        startButton = (Button) findViewById(R.id.button_start_training);
        stopButton = (Button) findViewById(R.id.button_stop_training);

        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = ((EditText) findViewById(R.id.userID)).getText().toString();
                selectedActivityString = (String) activitySpinner.getSelectedItem();
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                timer.schedule(startTimerTask, 3000);
                timer.schedule(stopTimerTask, 20000);
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSensor();
                startTimerTask.cancel();
                stopTimerTask.cancel();
                timer.cancel();
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200);
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                finish();
            }
        });
    }

    private void initTimerTasksWithAlertSound() {
        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        timer = new Timer();

        startTimerTask = new TimerTask() {
            @Override
            public void run() {
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                startSensor();
            }
        };

        stopTimerTask = new TimerTask() {
            @Override
            public void run() {
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                startTimerTask.cancel();
                stopSensor();

                //startButton.setVisibility(View.VISIBLE);
                //stopButton.setVisibility(View.GONE);
            }
        };
    }

    private void initRestApi() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            urlString = extras.getString(MainActivity.URL);
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(urlString)
                .build();

        restApi = restAdapter.create(RestApi.class);
    }

    private void initActivitySpinner() {
        activitySpinner = (Spinner) findViewById(R.id.spinner_activity);

        final List activityList = new ArrayList();
        for(ActivityEnum activityType : ActivityEnum.values()){
            activityList.add(activityType.getLabel());
        }

        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                activityList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(adapter);

        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedActivityString = activitySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedActivityString = activityList.get(0).toString();
            }

        });
    }

    private void startSensor() {
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensor() {
        sm.unregisterListener(this);
    }


    private void updateTextView(Acceleration capturedAcceleration) {
        accelerationTextView.setText("X:" + capturedAcceleration.getX() +
                "\nY:" + capturedAcceleration.getY() +
                "\nZ:" + capturedAcceleration.getZ() +
                "\nTimestamp:" + capturedAcceleration.getTimestamp());
    }

    private Acceleration getAccelerationFromSensor(SensorEvent event) {
        long timestamp = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
        return new Acceleration(event.values[0], event.values[1], event.values[2], timestamp);
    }

    private class SendAccelerationAsyncTask extends AsyncTask<Acceleration, Void, Void> {

        @Override
        protected Void doInBackground(Acceleration... params) {

            try {
                TrainingAcceleration training = new TrainingAcceleration();
                training.setAcceleration(params[0]);
                training.setUserID(userID);
                training.setActivity(selectedActivityString);

                restApi.sendTrainingValues(training);

            } catch(Exception ex) {
                System.err.print(ex.getMessage());
            }
            return null;
        }
    }
}
