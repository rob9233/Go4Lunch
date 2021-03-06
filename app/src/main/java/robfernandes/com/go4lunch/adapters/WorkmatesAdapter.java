package robfernandes.com.go4lunch.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import robfernandes.com.go4lunch.R;
import robfernandes.com.go4lunch.model.EatingPlan;
import robfernandes.com.go4lunch.model.UserInformation;

import static robfernandes.com.go4lunch.utils.Utils.putImageIntoImageView;

public class WorkmatesAdapter extends
        RecyclerView.Adapter<WorkmatesAdapter.ViewHolder>
        implements Filterable {

    private List<UserInformation> userList;
    private List<UserInformation> fullUserList;
    private List<EatingPlan> eatingPlanList;
    private Context context;
    private boolean showFullDescription;

    public WorkmatesAdapter(
            Context context,
            List<UserInformation> userList, List<EatingPlan> eatingPlanList,
            boolean showFullDescription
    ) {
        this.context = context;
        this.showFullDescription = showFullDescription;
        this.userList = userList;
        this.fullUserList = new ArrayList<>(userList);
        this.eatingPlanList = eatingPlanList;
    }

    public WorkmatesAdapter(Context context, List<UserInformation> userList, List<EatingPlan> eatingPlanList
    ) {
        this(context, userList, eatingPlanList, true);
    }

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.workmate_item, viewGroup, false);
        return new WorkmatesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder viewHolder, int i) {
        UserInformation userInformation = userList.get(i);
        String descriptionText;

        if (showFullDescription) {
            String planedRestaurantName = getPlanedRestaurant(userInformation);
            descriptionText = userInformation.getName();
            if (planedRestaurantName != null && !planedRestaurantName.isEmpty()) {
                descriptionText += " " +
                        context.getString(R.string.is_eating_at) + " " + planedRestaurantName;
                viewHolder.description.setTextColor(Color.BLACK);
            } else {
                descriptionText += " " + context.getString(R.string.hasnt_decided_yet);
            }
        } else {
            viewHolder.description.setTextColor(Color.BLACK);
            descriptionText = userInformation.getName();
        }

        viewHolder.description.setText(descriptionText);

        putImageIntoImageView(viewHolder.porfilePic, userInformation.getPhotoUrl());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private ImageView porfilePic;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.workmates_item_description);
            porfilePic = itemView.findViewById(R.id.workmates_item_image);
        }
    }

    @Override
    public Filter getFilter() {
        return workmatesFilter;
    }

    private Filter workmatesFilter = new Filter() {
        //this runs asycn
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<UserInformation> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullUserList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (UserInformation user : fullUserList) {
                    if (user.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            //returns to publishResults
            return results;
        }

        //this runs sync when performFiltering is over
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    private String getPlanedRestaurant(UserInformation userInformation) {
        try {
            for (EatingPlan eatingPlan : eatingPlanList) {
                if (eatingPlan.getUserID().equals(userInformation.getId())) {
                    return eatingPlan.getRestaurantName();
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}
