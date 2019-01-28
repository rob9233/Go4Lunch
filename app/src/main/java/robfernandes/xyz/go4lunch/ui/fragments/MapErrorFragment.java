package robfernandes.xyz.go4lunch.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import robfernandes.xyz.go4lunch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapErrorFragment extends Fragment {


    public MapErrorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_error, container, false);
    }

}
