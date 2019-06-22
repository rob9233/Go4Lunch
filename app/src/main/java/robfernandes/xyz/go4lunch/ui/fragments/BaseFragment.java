package robfernandes.xyz.go4lunch.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.model.EatingPlan;

public abstract class BaseFragment extends Fragment
        implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    SearchView searchView;
    List<EatingPlan> eatingPlanList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    void getEatingPlans() {
        eatingPlanList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        CollectionReference collection = db.collection("plans")
                .document(year).collection(month).document(day).collection("plan");

        collection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

            for (DocumentSnapshot document : documents) {
                eatingPlanList.add(document.toObject(EatingPlan.class));
            }
            displayEatingPlans();
        });
    }

    protected abstract void displayEatingPlans();
}
