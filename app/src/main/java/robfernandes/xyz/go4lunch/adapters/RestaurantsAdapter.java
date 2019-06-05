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
import robfernandes.xyz.go4lunch.model.EatingPlan;
import robfernandes.xyz.go4lunch.model.RestaurantInfo;
import robfernandes.xyz.go4lunch.model.UserInformation;
import robfernandes.xyz.go4lunch.ui.activities.RestaurantActivity;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;
import static robfernandes.xyz.go4lunch.utils.Constants.RESTAURANT_INFO_BUNDLE_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Constants.USER_INFORMATION_EXTRA;
import static robfernandes.xyz.go4lunch.utils.Utils.formatNumberOfStars;
import static robfernandes.xyz.go4lunch.utils.Utils.getRestaurantPhotoUrl;
import static robfernandes.xyz.go4lunch.utils.Utils.putImageIntoImageView;

public class RestaurantsAdapter extends
        RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>
        implements Filterable {

    private List<RestaurantInfo> restaurantInfoList;
    private List<RestaurantInfo> fullRestaurantsList;
    private UserInformation userInformation;
    private LatLng userLatLng;
    private Context context;
    private List<EatingPlan> eatingPlanList;

    public RestaurantsAdapter(List<RestaurantInfo> restaurantInfoList, LatLng userLatLng
            , List<EatingPlan> eatingPlanList, UserInformation userInformation, Context context) {
        this.restaurantInfoList = restaurantInfoList;
        this.fullRestaurantsList = new ArrayList<>(restaurantInfoList);
        this.userLatLng = userLatLng;
        this.context = context;
        this.eatingPlanList = eatingPlanList;
        this.userInformation = userInformation;
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
        int numOfPlans = getNumberOfPlansOnRestaurant(restaurantInfo);
        if (numOfPlans>0) {
            viewHolder.plansContainer.setVisibility(View.VISIBLE);
            viewHolder.numPlansTextView.setText(String.format(String.valueOf(numOfPlans), "(%d)"));
        } else {
            viewHolder.plansContainer.setVisibility(View.INVISIBLE);
        }

        viewHolder.title.setText(restaurantInfo.getName());
        viewHolder.description.setText(restaurantInfo.getAdress());
        try {
            if (restaurantInfo.isOpen()) {
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
            String photoReference = restaurantInfo.getPhotoRef();
            String photoUrl = getRestaurantPhotoUrl(photoReference, context
                    , "100", "100");
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
        private TextView title, description, openHours, distance, numPlansTextView;
        private ImageView imageView, star1, star2, star3;
        private View starContainer;
        private View plansContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.restaurant_item_title);
            description = itemView.findViewById(R.id.restaurant_item_description);
            openHours = itemView.findViewById(R.id.restaurant_item_open_hours);
            distance = itemView.findViewById(R.id.restaurant_item_distance);
            imageView = itemView.findViewById(R.id.restaurant_item_image);
            star1 = itemView.findViewById(R.id.restaurant_item_rating_star_1);
            star2 = itemView.findViewById(R.id.restaurant_item_rating_star_2);
            star3 = itemView.findViewById(R.id.restaurant_item_rating_star_3);
            starContainer = itemView.findViewById(R.id.restaurant_item_rating_star_container);
            numPlansTextView = itemView.findViewById(R.id.restaurant_item_rating_plans_num);
            plansContainer = itemView.findViewById(R.id.restaurant_item_rating_plans_container);

            itemView.setOnClickListener(v ->
                    goToRestaurantActivity(restaurantInfoList.get(getAdapterPosition())));
        }
    }

    private void goToRestaurantActivity(RestaurantInfo restaurantInfo) {
        if (restaurantInfo != null) {
            Intent intent = new Intent(context, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_INFO_BUNDLE_EXTRA, restaurantInfo);
            intent.putExtra(USER_INFORMATION_EXTRA, userInformation);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "It is not possible to " +
                            "display info about this restaurant",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private int getNumberOfPlansOnRestaurant(RestaurantInfo restaurantInfo) {
        int numOfPlans = 0;

        try {
            for (EatingPlan eatingPlan : eatingPlanList) {
                if (restaurantInfo.getId().equals(eatingPlan.getRestaurantID())) {
                    numOfPlans++;
                }
            }
        } catch (Exception e) {
        }
        return numOfPlans;
    }
}
