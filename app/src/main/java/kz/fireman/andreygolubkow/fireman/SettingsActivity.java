package kz.fireman.andreygolubkow.fireman;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.Manifest.permission;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        //Uri.parse("package:" + getPackageName()));
        //startActivityForResult(appSettingsIntent, 1);
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        permission.INTERNET,
                        permission.WRITE_EXTERNAL_STORAGE,
                        permission.ACCESS_FINE_LOCATION,
                        permission.ACCESS_COARSE_LOCATION,
                        permission.ACCESS_WIFI_STATE,
                        permission.ACCESS_NETWORK_STATE,
                        permission.CAPTURE_AUDIO_OUTPUT,
                        permission.CAMERA,
                        permission.RECORD_AUDIO,
                        permission.WAKE_LOCK
                },
                1);

        Button saveButton = (Button) findViewById(R.id.saveSettingsButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SharedPreferences settings = getSharedPreferences(MainActivity.SETTINGS_FILE, MODE_PRIVATE);
                EditText carId = (EditText) findViewById(R.id.idCarText);
                EditText server = (EditText) findViewById(R.id.serverAdressText);
                EditText rtmp = (EditText) findViewById(R.id.rtmpServerAddressText);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(MainActivity.CAR_ID, carId.getText().toString());
                editor.putString(MainActivity.SERVER_ADDRESS, server.getText().toString());
                editor.putString(MainActivity.RTMP_ADDRESS, rtmp.getText().toString());
                editor.apply();
            }
        });
    }


}
