package robfernandes.xyz.go4lunch.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.UserInformation;

public class WorkmatesAdapter extends
        RecyclerView.Adapter<WorkmatesAdapter.ViewHolder> {

    private List<UserInformation> userList;

    public WorkmatesAdapter(List<UserInformation> userList) {
        this.userList = userList;
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
        viewHolder.name.setText(userList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.workmates_item_name);
        }
    }
}
