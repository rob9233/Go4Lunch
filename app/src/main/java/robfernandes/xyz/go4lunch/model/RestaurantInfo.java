package robfernandes.xyz.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.libraries.places.api.model.PhotoMetadata;

import java.util.List;

public class RestaurantInfo implements Parcelable {
    private String name;
    private String address;
    private boolean open;
    private String id;
    private double lat;
    private double lon;
    private String photoRef;
    private PhotoMetadata photoMetadata;
    private double rating;
    private String phone;
    private String website;
    private List<String> openSchedule;
    private boolean detailedInfo = false;

    public RestaurantInfo() {
    }

    protected RestaurantInfo(Parcel in) {
        name = in.readString();
        address = in.readString();
        open = in.readByte() != 0;
        id = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        photoRef = in.readString();
        photoMetadata = in.readParcelable(PhotoMetadata.class.getClassLoader());
        rating = in.readDouble();
        phone = in.readString();
        website = in.readString();
        openSchedule = in.createStringArrayList();
        detailedInfo = in.readByte() != 0;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public PhotoMetadata getPhotoMetadata() {
        return photoMetadata;
    }

    public void setPhotoMetadata(PhotoMetadata photoMetadata) {
        this.photoMetadata = photoMetadata;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getOpenSchedule() {
        return openSchedule;
    }

    public void setOpenSchedule(List<String> openSchedule) {
        this.openSchedule = openSchedule;
    }

    public boolean isDetailedInfo() {
        return detailedInfo;
    }

    public void setDetailedInfo(boolean detailedInfo) {
        this.detailedInfo = detailedInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeByte((byte) (open ? 1 : 0));
        dest.writeString(id);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(photoRef);
        dest.writeParcelable(photoMetadata, flags);
        dest.writeDouble(rating);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeStringList(openSchedule);
        dest.writeByte((byte) (detailedInfo ? 1 : 0));
    }
}