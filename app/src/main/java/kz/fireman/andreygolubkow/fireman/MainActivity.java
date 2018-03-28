package kz.fireman.andreygolubkow.fireman;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

import org.joda.time.DateTime;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.layout.simple_list_item_1;


public class MainActivity extends AppCompatActivity {

    public static final String SETTINGS_FILE = "Fireman";
    public static final String CAR_ID = "Car";
    public static final String SERVER_ADDRESS = "Server";
    public static final String RTMP_ADDRESS = "Rtmp";

    public static SurfaceView mSurfaceView;
    public static String RtmpAddres;

    private String _idCar = "-1";
    private String _serverAddress = "";

    private double _lat = 0;
    private double _lon = 0;
    private boolean _firstLocate = false;

    private LocationManager _manager;

    private boolean _locateFlag = false;
    private double _navToLat = 0;
    private double _navToLon = 0;
    private String _descr = "";

    private double _navFireLat = 0;
    private double _navFireLon = 0;


    private List<FireImage> _images = null;



    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        //Грузим настройки
        SharedPreferences settings = getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE);

        if (!settings.contains(CAR_ID) ) {
            ShowSettings();
            return;
        }
      //  initLayout();
      //  initRecorder();
        //Карта
        listener = CreateLocationListener();


            _idCar = settings.getString(CAR_ID, "");
            _serverAddress = settings.getString(SERVER_ADDRESS, "localhost");
            RtmpAddres = settings.getString(RTMP_ADDRESS, "localhost");

            _manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Проверка наличия разрешений
                // Если нет разрешения на использование соответсвующих разркешений выполняем какие-то действия
                return;
            }
            _manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);

            Timer myTimer = new Timer(); // Создаем таймер
            final Handler uiHandler = new Handler();

            myTimer.schedule(new TimerTask() { // Определяем задачу
                @Override
                public void run() {

                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            UpdateData();
                        }
                    });
                }
            }, 1000L, 5L * 1000); // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.



        final MapView map = (MapView) findViewById(R.id.mapView);
        Configuration.getInstance().setTileFileSystemCacheMaxBytes(1073741824);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        Context ctx = getApplicationContext();
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        Bitmap car = BitmapFactory.decodeResource(getResources(), R.drawable.firecar);
        mLocationOverlay.setPersonIcon(car);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        Button goMap = (Button) findViewById(R.id.goNavigator);
        goMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNavigatorFromMap(view);
            }
        });

        Button showFire = (Button) findViewById(R.id.showFire);
        goMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_navFireLat == 0)
                    return;
                IMapController mapController = map.getController();
                mapController.setCenter(new GeoPoint(_navFireLat,_navFireLon));
            }
        });

        Button startBroadcast = (Button) findViewById(R.id.startStreaming);
        startBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowBroadcastRunner();
            }
        });
    }

    @Override
    public void onDestroy() {

        //stopService(new Intent(MainActivity.this, RecorderService.class));
        super.onDestroy();
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    public void imagesButtonClick(View view) {
        Intent intent = new Intent(this, ImagesActivity.class);
        intent.putExtra("idcar", _idCar);
        intent.putExtra("serverAddress", _serverAddress);

        startActivity(intent);
    }

    //Открываем навигацию на выделенный объект
    void goNavigatorFromMap(View view) {
        if (_navToLat == 0 || _navToLon == 0) {
            Toast.makeText(MainActivity.this, "Нет координат для навигации.", Toast.LENGTH_SHORT).show();
        } else {
            Uri uri = Uri.parse("yandexnavi://show_point_on_map?lat=" + String.valueOf(_navToLat) + "&lon=" + String.valueOf(_navToLon) + "&zoom=12&no-balloon=0");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("ru.yandex.yandexnavi");

// Проверяет, установлено ли приложение.
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
            boolean isIntentSafe = activities.size() > 0;
            if (isIntentSafe) {

//Запускает Яндекс.Навигатор.
                startActivity(intent);
            } else {

// Открывает страницу Яндекс.Навигатора в Google Play.
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
                startActivity(intent);
            }
        }
    }

    public FiremanService getApi() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(_serverAddress)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FiremanService firemanService = retrofit.create(FiremanService.class);
        return firemanService;
    }

    private void UpdateData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        _manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);

        FiremanService api = getApi();

        //Обновление данных о пожаре
        api.Update(_idCar, _lat, _lon).enqueue(new Callback<FiremanModel>() {
            @Override
            public void onResponse(Call<FiremanModel> call, Response<FiremanModel> response) {
                TextView isConnectedText = (TextView) findViewById(R.id.isConnectedTextView);
                TextView addressText = (TextView) findViewById(R.id.fireAddress);
                TextView startFireTime = (TextView) findViewById(R.id.startFireTime);
                TextView fireDesription = (TextView) findViewById(R.id.fireDesription);

                isConnectedText.setText("Подключено");

                FiremanModel model = response.body();
                if (model != null) {
                    if (model.getFireAvailable() == true) {
                        addressText.setText(model.getAddress());
                        fireDesription.setText(model.getDescription());

                        String dateTimeString = model.getTaskDateTime();
                        DateTime dateTime = DateTime.parse(dateTimeString);
                        startFireTime.setText(dateTime.toString("HH:mm"));
                        _images = model.getImagesList();

                        // получаем экземпляр элемента ListView
                        ListView listView = (ListView) findViewById(R.id.logList);

                        // используем адаптер данных
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                                simple_list_item_1, model.getLogList());

                        listView.setAdapter(adapter);

                    } else {
                        addressText.setText("");
                        startFireTime.setText("");
                        fireDesription.setText("");
                        _navFireLat = 0;
                        _navFireLon = 0;
                    }

                }

            }

            @Override
            public void onFailure(Call<FiremanModel> call, Throwable t) {
                //Toast.makeText(MainActivity.this, "Ошибка сети.", Toast.LENGTH_SHORT).show();
                TextView isConnectedText = (TextView) findViewById(R.id.isConnectedTextView);
                isConnectedText.setText("Нет сети");
            }
        });

        //Получение гео объектов
        api.GetGeoObjects(_idCar).enqueue(new Callback<List<GeoobjectModel>>() {
            @Override
            public void onResponse(Call<List<GeoobjectModel>> call, Response<List<GeoobjectModel>> response) {
                MapView map = (MapView) findViewById(R.id.mapView);

                List<GeoobjectModel> list = response.body();
                if (list == null) {
                    Button images = (Button) findViewById(R.id.showAttachments);
                    images.setEnabled(false);
                    return;
                }
                Button images = (Button) findViewById(R.id.showAttachments);
                images.setEnabled(false);
                ArrayList<Overlay> markers = new ArrayList<Overlay>();

                for (GeoobjectModel geo : list) {

                    Marker marker = new Marker(map);
                    marker.setPosition(new GeoPoint(geo.getGpsPoint().Lat, geo.getGpsPoint().Lon));
                    //marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                    Switch showHydrants = (Switch) findViewById(R.id.showHydarants);

                    if (Objects.equals(geo.getMarker(), "hydrant") && showHydrants.isChecked()) {
                        marker.setIcon(getResources().getDrawable(R.drawable.hydrant));
                    } else if (Objects.equals(geo.getMarker(), "fire")) {
                        marker.setIcon(getResources().getDrawable(R.drawable.fire));
                        _navFireLat = geo.getGpsPoint().Lat;
                        _navFireLon = geo.getGpsPoint().Lon;

                    } else {
                        continue;
                    }
                    marker.setTitle(geo.getPopupText());

                    marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                            _navToLat = marker.getPosition().getLatitude();
                            _navToLon = marker.getPosition().getLongitude();

                            marker.showInfoWindow();
                            return true;
                        }
                    });

                    markers.add(marker);
                }

                List<Overlay> overlays = map.getOverlays();

                for (Overlay o : overlays) {
                    if (o.getClass() == Marker.class) {
                        overlays.remove(o);
                    }
                }

                map.getOverlays().addAll(markers);

            }

            @Override
            public void onFailure(Call<List<GeoobjectModel>> call, Throwable t) {
                //Toast.makeText(MainActivity.this, "Ошибка сети.", Toast.LENGTH_SHORT).show();
                TextView isConnectedText = (TextView) findViewById(R.id.isConnectedTextView);
                isConnectedText.setText("Нет сети");
            }
        });

        //Слежение за машиной
        Switch needLocate = (Switch) findViewById(R.id.locateSwitch);
        if (_locateFlag == false || needLocate.isChecked()) {
            MapView map = (MapView) findViewById(R.id.mapView);
            IMapController mapController = map.getController();
            GeoPoint startPoint = new GeoPoint(_lat, _lon);
            mapController.setCenter(startPoint);
            _locateFlag = true;
        }

        if (_firstLocate == false)
        {
            MapView map = (MapView) findViewById(R.id.mapView);
            IMapController mapController = map.getController();
            mapController.setZoom(12);
            GeoPoint point = new GeoPoint(51.1450,71.42692);
            mapController.setCenter(point);
            //.animateTo(point);

            _firstLocate = true;

        }
    }


    @Override
    protected void onResume() {

        super.onResume();
        //StartTranslate();
    }

    private LocationListener CreateLocationListener()
    {
        return new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                _lat = location.getLatitude();
                _lon = location.getLongitude();

            } else {
                Toast.makeText(MainActivity.this, "Ошибка при получении gps координат.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    }

    private void ShowSettings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void ShowBroadcastRunner()
    {
       /////////////////////////////////////////
    }


}
