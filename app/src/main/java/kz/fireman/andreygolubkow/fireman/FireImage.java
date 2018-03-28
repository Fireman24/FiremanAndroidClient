package kz.fireman.andreygolubkow.fireman;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andreygolubkow on 12.09.2017.
 */

public class FireImage {

    @SerializedName("id")
    @Expose
    public int Id;

    @SerializedName("name")
    @Expose
    public String Name;

    @SerializedName("url")
    @Expose
    public String Url;
}
