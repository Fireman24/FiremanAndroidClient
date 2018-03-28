package kz.fireman.andreygolubkow.fireman;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andreygolubkow on 17.11.2017.
 */

public class MapsModel {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }

}
