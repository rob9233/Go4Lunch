package robfernandes.xyz.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;
import robfernandes.xyz.go4lunch.ui.activities.RestaurantActivity;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;
import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Utils.formatNumberOfStars;
import static robfernandes.xyz.go4lunch.utils.Utils.putImageIntoImageView;

public class RestaurantsAdapter extends
        RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>
        implements Filterable {

    private List<RestaurantInfo> restaurantInfoList;
    private List<RestaurantInfo> fullRestaurantsList;
    private LatLng userLatLng;
    private Context context;
    private static final String TAG = "RestaurantsAdapter";

    public RestaurantsAdapter(List<RestaurantInfo> restaurantInfoList, LatLng userLatLng, Context context) {
        this.restaurantInfoList = restaurantInfoList;
        this.fullRestaurantsList = new ArrayList<>(restaurantInfoList);
        this.userLatLng = userLatLng;
        this.context = context;
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
        RestaurantInfo restaurantInfo = restaurantInfoList.get(i);
        viewHolder.title.setText(restaurantInfo.getName());
        viewHolder.description.setText(restaurantInfo.getAdress());
        try {
            if (restaurantInfo.getOpeningHours().getOpenNow()) {
                viewHolder.openHours.setText("Open now");
                viewHolder.openHours.setTextColor(Color.GREEN);
            } else {
                viewHolder.openHours.setText("Closed");
                viewHolder.openHours.setTextColor(Color.RED);
            }
        } catch (NullPointerException e) {
        }

        LatLng restaurantLatLng = new LatLng(restaurantInfo.getLat(), restaurantInfo.getLon());

        String distanceString = ((int) computeDistanceBetween(userLatLng, restaurantLatLng)) + "m";

        viewHolder.distance.setText(distanceString);
        try {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
            sb.append("?maxwidth=100");
            sb.append("&maxheight=100");
            sb.append("&photoreference=");
            sb.append(restaurantInfo.getPhotos().get(0).getPhotoReference());
            sb.append("&key=");
            sb.append(context.getString(R.string.google_maps_api_key));

            String photoUrl = sb.toString();
            putImageIntoImageView(viewHolder.imageView, photoUrl);
        } catch (Exception e) {
        }

        try {
            int starsNum = formatNumberOfStars(restaurantInfo.getRating());
            if (starsNum < 1) {
                viewHolder.star1.setVisibility(View.INVISIBLE);
            }
            if (starsNum < 2) {
                viewHolder.star2.setVisibility(View.INVISIBLE);
            }
            if (starsNum < 3) {
                viewHolder.star3.setVisibility(View.INVISIBLE);
            }
            if (starsNum > 0) {
                viewHolder.star0.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {
            viewHolder.starContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return restaurantInfoList.size();
    }

    @Override
    public Filter getFilter() {
        return restaurantFilter;
    }

    private Filter restaurantFilter = new Filter() {
        //this runs asycn
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RestaurantInfo> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullRestaurantsList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (RestaurantInfo restaurantInfo : fullRestaurantsList) {
                    if (restaurantInfo.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(restaurantInfo);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            //returns to pubishResults
            return results;
        }

        //this runs sync when performFiltering is over
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            restaurantInfoList.clear();
            restaurantInfoList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, description, openHours, distance;
        private ImageView imageView, star1, star2, star3, star0;
        private View starContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.restaurant_item_title);
            description = itemView.findViewById(R.id.restaurant_item_description);
            openHours = itemView.findViewById(R.id.restaurant_item_open_hours);
            distance = itemView.findViewById(R.id.restaurant_item_distance);
            imageView = itemView.findViewById(R.id.restaurant_item_image);
            star0 = itemView.findViewById(R.id.restaurant_item_rating_star_0);
            star1 = itemView.findViewById(R.id.restaurant_item_rating_star_1);
            star2 = itemView.findViewById(R.id.restaurant_item_rating_star_2);
            star3 = itemView.findViewById(R.id.restaurant_item_rating_star_3);
            starContainer = itemView.findViewById(R.id.restaurant_item_rating_star_container);

            itemView.setOnClickListener(v ->
                    goToRestaurantActivity(restaurantInfoList.get(getAdapterPosition())));
        }
    }

    private void goToRestaurantActivity(RestaurantInfo restaurantInfo) {
        if (restaurantInfo != null) {
            Intent intent = new Intent(context, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_INFO_BUNDLE_EXTRA, restaurantInfo);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "It is not possible to " +
                            "display info about this restaurant",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
