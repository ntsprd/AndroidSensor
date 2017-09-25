package accelerometer.client.activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class FetchJSON {

    public static JSONObject getJSON(Context context, String city) {
        String OPEN_WEATHER_MAP_API =
                "http://api.openweathermap.org/data/2.5/weather?q="+city+",uk&appid=93408b7a457d511ea46bf8b341d43038";
        BufferedReader reader = null;
        try {
            URL url = new URL(OPEN_WEATHER_MAP_API);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024*8];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
            final JSONObject obj = new JSONObject(buffer.toString());

            return obj;
        } catch(Exception e){
            return null;
        }
    }

}