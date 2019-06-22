package robfernandes.xyz.go4lunch.ui.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.adapters.WorkmatesAdapter;
import robfernandes.xyz.go4lunch.model.UserInformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends BaseFragment {

    private List<UserInformation> userList;
    private RecyclerView recyclerView;
    private WorkmatesAdapter workmatesAdapter;
    private View view;

    public WorkmatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
                        for (QueryDocumentSnapshot document : task.getResult()) {
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
        recyclerView = view.findViewById(R.id.fragment_work_mates_recycler_view);
        workmatesAdapter = new WorkmatesAdapter(userList, eatingPlanList);
        recyclerView.setAdapter(workmatesAdapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        workmatesAdapter.getFilter().filter(newText);
        return false;
    }
}
