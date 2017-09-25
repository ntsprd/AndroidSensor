package accelerometer.client.activity;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {

    SharedPreferences preferences;

    public CityPreference(Activity activity){
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity(){
        return preferences.getString("city", "Gdansk, PL");
    }

    public void setCity(String city){
        preferences.edit().putString("city", city).commit();
    }

}