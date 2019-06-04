package robfernandes.xyz.go4lunch.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import robfernandes.xyz.go4lunch.R;

public class Utils {
    public static LatLngBounds getBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        //  in SphericalUtil 225 and 45 are heading values to calculate a Spherical LatLng
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    public static void putImageIntoImageView(ImageView imageView, String url, Drawable errorDrawable) {
        try {
            Picasso.get().load(url).error(errorDrawable).into(imageView);
        } catch (Exception e) {
        }
    }

    public static void putImageIntoImageView(ImageView imageView, String url) {
        Picasso.get().load(url).into(imageView);
    }

    public static int formatNumberOfStars(double rating) {
        return (int) (rating * 3d / 5d + .5d);
    }

    public static String getRestaurantPhotoUrl(String photoReference, Context context
            , String maxWidth, String maxHeight) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        sb.append("?maxwidth=");
        sb.append(maxWidth);
        sb.append("&maxheight=");
        sb.append(maxHeight);
        sb.append("&photoreference=");
        sb.append(photoReference);
        sb.append("&key=");
        sb.append(context.getString(R.string.google_maps_api_key));
        return sb.toString();
    }

    public static String generateRandomFileName() {
        return UUID.randomUUID().toString();
    }

    public static String getFormatedTodaysDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("yyyy/M/d");
        return df.format(date);
    }
}
