package robfernandes.xyz.go4lunch.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.EatingPlan;
import robfernandes.xyz.go4lunch.model.UserInformation;

import static android.graphics.Typeface.BOLD;

public class WorkmatesAdapter extends
        RecyclerView.Adapter<WorkmatesAdapter.ViewHolder>
        implements Filterable {

    private List<UserInformation> userList;
    private List<UserInformation> fullUserList;
    private List<EatingPlan> eatingPlanList;

    public WorkmatesAdapter(List<UserInformation> userList, List<EatingPlan> eatingPlanList) {
        this.userList = userList;
        this.fullUserList = new ArrayList<>(userList);
        this.eatingPlanList = eatingPlanList;
    }

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.workmate_item, viewGroup, false);
        return new WorkmatesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder viewHolder, int i) {
        UserInformation userInformation = userList.get(i);
        String planedRestaurantName = getPlanedRestaurant(userInformation);
        String text = userInformation.getName();
        if (planedRestaurantName != null && !planedRestaurantName.isEmpty()) {
            text += " is eating at " + planedRestaurantName;
            viewHolder.description.setTextColor(Color.BLACK);
        } else {
            text += " hasn't decided yet";
        }

        viewHolder.description.setText(text);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.workmates_item_description);
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

            //returns to pubishResults
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
        } catch (Exception e) {
        }

        return null;
    }
}
