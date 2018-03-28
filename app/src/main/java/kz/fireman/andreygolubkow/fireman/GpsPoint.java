package kz.fireman.andreygolubkow.fireman;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andreygolubkow on 15.09.2017.
 */

public class GpsPoint {

    @SerializedName("id")
    @Expose
    public int Id;

    @SerializedName("lat")
    @Expose
    public double Lat;

    @SerializedName("lon")
    @Expose
    public double Lon;
}
