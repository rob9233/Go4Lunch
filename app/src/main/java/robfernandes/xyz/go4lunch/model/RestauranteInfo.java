package robfernandes.xyz.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import robfernandes.xyz.go4lunch.model.placesResponse.OpeningHours;

public class RestauranteInfo implements Parcelable {
    private String name;
    private String adress;
    private OpeningHours openingHours;
    private String id;
    private double lat;
    private double lon;

    public RestauranteInfo() {
    }

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

    protected RestauranteInfo(Parcel in) {
        name = in.readString();
        adress = in.readString();
        id = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    public static final Creator<RestauranteInfo> CREATOR = new Creator<RestauranteInfo>() {
        @Override
        public RestauranteInfo createFromParcel(Parcel in) {
            return new RestauranteInfo(in);
        }

        @Override
        public RestauranteInfo[] newArray(int size) {
            return new RestauranteInfo[size];
        }
    };

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