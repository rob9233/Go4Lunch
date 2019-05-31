package robfernandes.xyz.go4lunch.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import robfernandes.xyz.go4lunch.R;
import robfernandes.xyz.go4lunch.adapters.RestaurantsAdapter;
import robfernandes.xyz.go4lunch.model.NearByPlaces;

import static robfernandes.xyz.go4lunch.utils.Constants.NEARBY_PLACES;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantListFragment extends Fragment {
    private NearByPlaces nearByPlaces;
    private View view;
    private RecyclerView recyclerView;
    private RestaurantsAdapter restaurantsAdapter;


    public RestaurantListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        nearByPlaces = getArguments().getParcelable(NEARBY_PLACES);
        setRecyclerVIew();
        return view;
    }

    private void setRecyclerVIew() {
        recyclerView = view.findViewById(R.id.fragment_restaurants_recycler_view);
        restaurantsAdapter = new RestaurantsAdapter(nearByPlaces.getRestauranteInfoList());
        recyclerView.setAdapter(restaurantsAdapter);
    }

}
