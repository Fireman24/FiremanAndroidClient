package kz.fireman.andreygolubkow.fireman;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andreygolubkow on 26.09.2017.
 */

public class GeoobjectModel {
    @SerializedName("popupText")
    @Expose
    private String popupText;
    @SerializedName("marker")
    @Expose
    private String marker;
    @SerializedName("gpsPoint")
    @Expose
    private GpsPoint gpsPoint;

    public String getPopupText() {
        return popupText;
    }

    public void setPopupText(String popupText) {
        this.popupText = popupText;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public GpsPoint getGpsPoint() {
        return gpsPoint;
    }

    public void setGpsPoint(GpsPoint gpsPoint) {
        this.gpsPoint = gpsPoint;
    }
}
