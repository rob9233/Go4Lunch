package robfernandes.com.go4lunch.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import robfernandes.com.go4lunch.R;
import robfernandes.com.go4lunch.adapters.WorkmatesAdapter;
import robfernandes.com.go4lunch.model.UserInformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends BaseFragment {

    private List<UserInformation> userList;
    private WorkmatesAdapter workmatesAdapter;
    private View view;

    public WorkmatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_work_mates, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserInfo();
    }

    private void getUserInfo() {
        userList = new ArrayList<>();
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document :
                                Objects.requireNonNull(task.getResult())) {
                            UserInformation userInformation = document
                                    .toObject(UserInformation.class);
                            userList.add(userInformation);
                        }
                        getEatingPlans();
                    }
                });
    }

    @Override
    protected void displayEatingPlans() {
        displayUsers();
    }

    private void displayUsers() {
        RecyclerView recyclerView = view.findViewById(R.id.fragment_work_mates_recycler_view);
        workmatesAdapter = new WorkmatesAdapter(getContext(), userList, eatingPlanList);
        recyclerView.setAdapter(workmatesAdapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        workmatesAdapter.getFilter().filter(newText);
        return false;
    }
}
