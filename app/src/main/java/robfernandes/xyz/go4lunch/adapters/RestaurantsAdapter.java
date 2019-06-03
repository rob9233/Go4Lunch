package robfernandes.xyz.go4lunch.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.RestauranteInfo;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class RestaurantsAdapter extends
        RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

    private List<RestauranteInfo> restaurantInfoList;
    private LatLng userLatLng ;

    public RestaurantsAdapter(List<RestauranteInfo> restaurantInfoList,
                              LatLng userLatLng) {
        this.restaurantInfoList = restaurantInfoList;
        this.userLatLng = userLatLng;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.restaurant_item, viewGroup, false);
        return new RestaurantsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        RestauranteInfo restauranteInfo = restaurantInfoList.get(i);
        viewHolder.title.setText(restauranteInfo.getName());
        viewHolder.description.setText(restauranteInfo.getAdress());
        try {
            if (restauranteInfo.getOpeningHours().getOpenNow()) {
                viewHolder.openHours.setText("Open now");
                viewHolder.openHours.setTextColor(Color.GREEN);
            } else {
                viewHolder.openHours.setText("Closed");
                viewHolder.openHours.setTextColor(Color.RED);
            }
        } catch (NullPointerException e) {
        }

        LatLng restaurantLatLng = new LatLng(restauranteInfo.getLat(), restauranteInfo.getLon());

        String distanceString = ((int) computeDistanceBetween(userLatLng, restaurantLatLng)) + "m";

        viewHolder.distance.setText(distanceString);
    }

    @Override
    public int getItemCount() {
        return restaurantInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, description, openHours, distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.restaurant_item_title);
            description = itemView.findViewById(R.id.restaurant_item_description);
            openHours = itemView.findViewById(R.id.restaurant_item_open_hours);
            distance = itemView.findViewById(R.id.restaurant_item_distance);
        }
    }
}
