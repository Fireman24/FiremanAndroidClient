package kz.fireman.andreygolubkow.fireman;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FiremanModel {

    @SerializedName("fireAvailable")
    @Expose
    private Boolean fireAvailable;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("taskDateTime")
    @Expose
    private String taskDateTime;
    @SerializedName("logList")
    @Expose
    private String[] logList = null;
    @SerializedName("imagesList")
    @Expose
    private List<FireImage> imagesList = null;

    public Boolean getFireAvailable() {
        return fireAvailable;
    }

    public void setFireAvailable(Boolean fireAvailable) {
        this.fireAvailable = fireAvailable;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskDateTime() {
        return taskDateTime;
    }

    public void setTaskDateTime(String taskDateTime) {
        this.taskDateTime = taskDateTime;
    }

    public String[] getLogList() {
        return logList;
    }

    public void setLogList(String[] logList) {
        this.logList = logList;
    }

    public List<FireImage> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<FireImage> imagesList) {
        this.imagesList = imagesList;
    }

}