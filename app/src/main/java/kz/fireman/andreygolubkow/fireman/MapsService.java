package kz.fireman.andreygolubkow.fireman;

import java.lang.reflect.Array;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by andreygolubkow on 17.11.2017.
 */

public interface MapsService {

    @GET("maps")
    Call<MapsModel[]> GetMaps();

}
