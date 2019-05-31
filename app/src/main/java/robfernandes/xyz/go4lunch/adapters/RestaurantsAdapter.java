package robfernandes.xyz.go4lunch.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.RestauranteInfo;

public class RestaurantsAdapter extends
        RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

    List<RestauranteInfo> restauranteInfoList;

    public RestaurantsAdapter(List<RestauranteInfo> restauranteInfoList) {
        this.restauranteInfoList = restauranteInfoList;
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
        viewHolder.title.setText(restauranteInfoList.get(i).getName());
        //TODO
        viewHolder.description.setText(restauranteInfoList.get(i).getLat() + "");
        viewHolder.openHours.setText(restauranteInfoList.get(i).getId());
    }

    @Override
    public int getItemCount() {
        return restauranteInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, description, openHours;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.restaurant_item_title);
            description = itemView.findViewById(R.id.restaurant_item_description);
            openHours = itemView.findViewById(R.id.restaurant_item_open_hours);
        }
    }
}
