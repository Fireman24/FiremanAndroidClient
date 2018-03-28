package kz.fireman.andreygolubkow.fireman;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImagesActivity extends AppCompatActivity {

    private String _serverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        String idCar = (String) getIntent().getStringExtra("idcar");
        _serverAddress = (String) getIntent().getStringExtra("serverAddress");

        FiremanService api = getImages();
        api.GetImages(idCar).enqueue(new Callback<List<FireImage>>() {
            @Override
            public void onResponse(Call<List<FireImage>> call, Response<List<FireImage>> response) {

                Toast.makeText(getApplicationContext(), "Всего вложений " + String.valueOf(response.body().size()),
                        Toast.LENGTH_SHORT).show();

                ListView imagesList = (ListView) findViewById(R.id.imagesList);
                // создаем адаптер
                ImageAdapter imagesAdapter = new ImageAdapter(ImagesActivity.this, R.layout.list_item, response.body());

                imagesList.setAdapter(imagesAdapter);

                AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                        // получаем выбранный пункт
                        FireImage selectedState = (FireImage) parent.getItemAtPosition(position);
                        //-1 т. к. последним симповолом у сервера стоит слеш
                        String sAddr = _serverAddress.substring(0, _serverAddress.length() - 1);
                        Uri address = Uri.parse(sAddr + selectedState.Url + "/get");
                        Intent openlinkIntent = new Intent(Intent.ACTION_VIEW, address);
                        startActivity(openlinkIntent);

                    }
                };
                imagesList.setOnItemClickListener(itemListener);
            }

            @Override
            public void onFailure(Call<List<FireImage>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ошибка сети.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public FiremanService getImages() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(_serverAddress)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        FiremanService umoriliApi = retrofit.create(FiremanService.class);
        return umoriliApi;
    }


}
