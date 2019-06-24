package robfernandes.xyz.go4lunch.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import java.util.Locale;
import java.util.UUID;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.ui.activities.MainActivity;

import static robfernandes.xyz.go4lunch.utils.Constants.LANG_KEY;
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
        } catch (Exception ignored) {
        }
    }

    public static void putImageIntoImageView(ImageView imageView, String url) {
        try {
            Picasso.get().load(url).into(imageView);
        } catch (Exception ignored) {
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
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" +
                maxWidth +
                "&maxheight=" +
                maxHeight +
                "&photoreference=" +
                photoReference +
                "&key=" +
                context.getString(R.string.google_maps_api_key);
    }

    public static String generateRandomFileName() {
        return UUID.randomUUID().toString();
    }

    public static String getFormatedTodaysDate() {
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat")
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
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int googleApiIndex;
        switch (day) {
            case Calendar.MONDAY:
                googleApiIndex = 0;
                break;
            case Calendar.TUESDAY:
                googleApiIndex = 1;
                break;
            case Calendar.WEDNESDAY:
                googleApiIndex = 2;
                break;
            case Calendar.THURSDAY:
                googleApiIndex = 3;
                break;
            case Calendar.FRIDAY:
                googleApiIndex = 4;
                break;
            case Calendar.SATURDAY:
                googleApiIndex = 5;
                break;
            case Calendar.SUNDAY:
                googleApiIndex = 6;
                break;
            default:
                googleApiIndex = 0;
                break;
        }
        return googleApiIndex;
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

    public static void changeLanguage(Activity activity, String lang) {
        Configuration config = activity.getResources().getConfiguration();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        config.locale = locale;
        activity.getResources().updateConfiguration(config,
                activity.getResources().getDisplayMetrics());
        setLangFromPrefs(activity, lang);
        restartApp(activity);
    }

    public static String getLangFromPrefs(Context context) {
        return getSharedPreference(context).getString(LANG_KEY, "en");
    }

    private static void setLangFromPrefs(Context context, String value) {
        getSharedPreference(context).edit().putString(LANG_KEY, value).apply();
    }
}
