package robfernandes.xyz.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class NearByPlaces implements Parcelable {
    private List<RestaurantInfo> restaurantInfoList;

    public NearByPlaces() {
    }

    protected NearByPlaces(Parcel in) {
        restaurantInfoList = in.createTypedArrayList(RestaurantInfo.CREATOR);
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

    public List<RestaurantInfo> getRestaurantInfoList() {
        return restaurantInfoList;
    }

    public void setRestaurantInfoList(List<RestaurantInfo> restaurantInfoList) {
        this.restaurantInfoList = restaurantInfoList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(restaurantInfoList);
    }
}
