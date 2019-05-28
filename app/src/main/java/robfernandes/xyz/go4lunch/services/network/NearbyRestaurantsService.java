package robfernandes.xyz.go4lunch.services.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import robfernandes.xyz.go4lunch.model.placesResponse.PlacesResponse;

public interface NearbyRestaurantsService {
    @GET("json?radius=1000&sensor=false&types=restaurant")
    Call<PlacesResponse> getNearbyRestaurants(@Query("location") String location,
                                              @Query("key") String apiKey);
}
