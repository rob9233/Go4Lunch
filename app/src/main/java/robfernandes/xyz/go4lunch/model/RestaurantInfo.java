package robfernandes.xyz.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RestaurantInfo implements Parcelable {
    private String name;
    private String adress;
    private boolean open;
    private String id;
    private double lat;
    private double lon;
    private String photoRef;
    private double rating;

    public RestaurantInfo() {
    }

    protected RestaurantInfo(Parcel in) {
        name = in.readString();
        adress = in.readString();
        open = in.readByte() != 0;
        id = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        photoRef = in.readString();
        rating = in.readDouble();
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
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

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(adress);
        dest.writeByte((byte) (open ? 1 : 0));
        dest.writeString(id);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(photoRef);
        dest.writeDouble(rating);
    }
}