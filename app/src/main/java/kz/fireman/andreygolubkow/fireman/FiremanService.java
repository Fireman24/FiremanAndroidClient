package kz.fireman.andreygolubkow.fireman;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by andreygolubkow on 12.09.2017.
 */

public interface FiremanService {

    @GET("firecar/{carId}/update")
    Call<FiremanModel> Update(@Path("carId") String carId, @Query("lat") double lat, @Query("lon") double lon);

    @GET("firecar/{carId}/geoobjects")
    Call<List<GeoobjectModel>> GetGeoObjects(@Path("carId") String carId);

    @GET("firecar/{carId}/images")
    Call<List<FireImage>> GetImages(@Path("carId") String carId);

    @Multipart
    @POST("firecar/{carId}/camera")
    Call<ResponseBody> upload(@Path("carId") String carId,
                              @Part MultipartBody.Part uploadedFile);


}
