package robfernandes.xyz.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import robfernandes.xyz.go4lunch.model.placesResponse.OpeningHours;
import robfernandes.xyz.go4lunch.model.placesResponse.Photo;

public class RestaurantInfo implements Parcelable {
    private String name;
    private String adress;
    private OpeningHours openingHours;
    private String id;
    private double lat;
    private double lon;
    private List<Photo> photos;

    public RestaurantInfo() {
    }

    protected RestaurantInfo(Parcel in) {
        name = in.readString();
        adress = in.readString();
        id = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    public static final Creator<RestaurantInfo> CREATOR = new Creator<RestaurantInfo>() {
        @Override
        public RestaurantInfo createFromParcel(Parcel in) {
            return new RestaurantInfo(in);
        }

        @Override
        public RestaurantInfo[] newArray(int size) {
            return new RestaurantInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(adress);
        dest.writeString(id);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }
}