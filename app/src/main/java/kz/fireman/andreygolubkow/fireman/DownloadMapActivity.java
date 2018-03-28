package kz.fireman.andreygolubkow.fireman;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.layout.simple_list_item_1;
import static kz.fireman.andreygolubkow.fireman.MainActivity.SERVER_ADDRESS;
import static kz.fireman.andreygolubkow.fireman.MainActivity.SETTINGS_FILE;

public class DownloadMapActivity extends AppCompatActivity {

    private MapsModel[] maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_map);

        //Грузим настройки
        SharedPreferences settings = getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE);
        String serverAddress = settings.getString(SERVER_ADDRESS, "localhost");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverAddress)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MapsService mapsService = retrofit.create(MapsService.class);
        mapsService.GetMaps().enqueue(new Callback<MapsModel[]>() {

            @Override
            public void onFailure(Call<MapsModel[]> call, Throwable t) {

            }

            @Override
            public void onResponse(Call<MapsModel[]> call, Response<MapsModel[]> response) {

                MapsModel[] mapModel = response.body();
                maps = mapModel;
                if (mapModel!=null)
                {
                    // получаем экземпляр элемента ListView
                    ListView listView = (ListView) findViewById(R.id.mapsList);

                    MapListAdapter adapter = new MapListAdapter(getBaseContext(),mapModel);
                    listView.setAdapter(adapter);
                }
            }
        });

        ListView listView = (ListView) findViewById(R.id.mapsList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                downloadFile(maps[i].getUrl()+"/get");
            }
        });

    }

    private void downloadFile(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        new AsyncTask<String, Integer, File>() {
            private Exception m_error = null;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Downloading ...");
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                progressDialog.show();
            }

            @Override
            protected File doInBackground(String... params) {
                URL url;
                HttpURLConnection urlConnection;
                InputStream inputStream;
                int totalSize;
                int downloadedSize;
                byte[] buffer;
                int bufferLength;

                File file = null;
                FileOutputStream fos = null;

                try {
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    file = File.createTempFile("Mustachify", "download");
                    fos = new FileOutputStream(file);
                    inputStream = urlConnection.getInputStream();

                    totalSize = urlConnection.getContentLength();
                    downloadedSize = 0;

                    buffer = new byte[1024];
                    bufferLength = 0;

                    // читаем со входа и пишем в выход,
                    // с каждой итерацией публикуем прогресс
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        publishProgress(downloadedSize, totalSize);
                    }

                    fos.close();
                    inputStream.close();

                    return file;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    m_error = e;
                } catch (IOException e) {
                    e.printStackTrace();
                    m_error = e;
                }

                return null;
            }

            // обновляем progressDialog
            protected void onProgressUpdate(Integer... values) {
                progressDialog
                        .setProgress((int) ((values[0] / (float) values[1]) * 100));
            };

            @Override
            protected void onPostExecute(File file) {
                // отображаем сообщение, если возникла ошибка
                if (m_error != null) {
                    m_error.printStackTrace();
                    return;
                }
                // закрываем прогресс и удаляем временный файл
                progressDialog.hide();
                file.delete();
            }
        }.execute(url);
    }
}
