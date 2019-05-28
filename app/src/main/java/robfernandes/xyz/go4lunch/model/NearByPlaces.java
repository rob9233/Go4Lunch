package robfernandes.xyz.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class NearByPlaces implements Parcelable {
    private List<RestauranteInfo> restauranteInfoList;

    public NearByPlaces() {
    }

    protected NearByPlaces(Parcel in) {
        restauranteInfoList = in.createTypedArrayList(RestauranteInfo.CREATOR);
    }

    public static final Creator<NearByPlaces> CREATOR = new Creator<NearByPlaces>() {
        @Override
        public NearByPlaces createFromParcel(Parcel in) {
            return new NearByPlaces(in);
        }

        @Override
        public NearByPlaces[] newArray(int size) {
            return new NearByPlaces[size];
        }
    };

    public List<RestauranteInfo> getRestauranteInfoList() {
        return restauranteInfoList;
    }

    public void setRestauranteInfoList(List<RestauranteInfo> restauranteInfoList) {
        this.restauranteInfoList = restauranteInfoList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(restauranteInfoList);
    }
}
