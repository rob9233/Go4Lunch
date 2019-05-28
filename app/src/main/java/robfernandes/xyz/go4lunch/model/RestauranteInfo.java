package robfernandes.xyz.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RestauranteInfo implements Parcelable {
    private String name;
    private String id;
    private double lat;
    private double lon;

    public RestauranteInfo() {
    }


    protected RestauranteInfo(Parcel in) {
        name = in.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }
}
