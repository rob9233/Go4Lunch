package robfernandes.xyz.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RestauranteInfo implements Parcelable {
    private String name;

    public RestauranteInfo() {
    }

    protected RestauranteInfo(Parcel in) {
        name = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
