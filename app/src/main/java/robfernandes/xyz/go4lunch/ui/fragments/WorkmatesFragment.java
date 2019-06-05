package robfernandes.xyz.go4lunch.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.adapters.WorkmatesAdapter;
import robfernandes.xyz.go4lunch.model.UserInformation;

import static com.android.volley.VolleyLog.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends BaseFragment {

    private List<UserInformation> userList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                        displayUsers();
                    }
                });
    }

    private void displayUsers() {
        recyclerView = view.findViewById(R.id.fragment_work_mates_recycler_view);
        workmatesAdapter = new WorkmatesAdapter(userList);
        recyclerView.setAdapter(workmatesAdapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        workmatesAdapter.getFilter().filter(newText);
        return false;
    }
}
