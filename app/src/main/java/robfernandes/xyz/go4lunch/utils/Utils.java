package robfernandes.xyz.go4lunch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.ui.activities.MainActivity;

import static robfernandes.xyz.go4lunch.utils.Constants.NOTIFICATIONS_KEY;
import static robfernandes.xyz.go4lunch.utils.Constants.PREFS_KEY;

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
        try {
            Picasso.get().load(url).into(imageView);
        } catch (Exception e) {
        }
    }

    public static void restartApp(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
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

    public static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static int getTodaysWeekDay() {
        //-1 to start at monday
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;
    }

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
    }

    public static boolean getNotificationStatusFromPrefs(Context context) {
        return getSharedPreference(context).getBoolean(NOTIFICATIONS_KEY, true);
    }

    public static void setNotificationStatusInPrefs(Context context, boolean value) {
        getSharedPreference(context).edit().putBoolean(NOTIFICATIONS_KEY, value).apply();
    }
}
