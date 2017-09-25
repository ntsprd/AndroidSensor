package accelerometer.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import accelerometer.client.R;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "url";
    private EditText urlTextEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        urlTextEdit = (EditText) findViewById(R.id.editURL);

        final Button startButton = (Button) findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AccelerometerActivity.class);
                intent.putExtra(URL, urlTextEdit.getText().toString());
                startActivity(intent);
            }
        });

        Button collectButton = (Button) findViewById(R.id.button_collect);
        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CollectingActivity.class);
                intent.putExtra(URL, urlTextEdit.getText().toString());
                startActivity(intent);
            }
        });

        Button weatherButton = (Button) findViewById(R.id.weather_button);
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });

    }
}
