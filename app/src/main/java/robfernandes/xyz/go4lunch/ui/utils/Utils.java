package robfernandes.xyz.go4lunch.ui.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

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
}
