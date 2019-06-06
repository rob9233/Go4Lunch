package robfernandes.xyz.go4lunch.services.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import robfernandes.xyz.go4lunch.model.placesDetailsResponse.PlacesDetailsResponse;
import robfernandes.xyz.go4lunch.model.placesResponse.PlacesResponse;

public interface NearbyRestaurantsService {
    @GET("search/json?radius=1000&sensor=false&types=restaurant")
    Call<PlacesResponse> getNearbyRestaurants(@Query("location") String location,
                                              @Query("key") String apiKey);

    @GET("details/json?fields=opening_hours,formatted_phone_number")
    Call<PlacesDetailsResponse> getPlaceDetails(@Query("placeid") String placeid,
                                                @Query("key") String apiKey);
}
