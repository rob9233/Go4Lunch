package robfernandes.com.go4lunch.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class EatingPlan {
    private String restaurantName;
    private String userID;
    private String restaurantID;
    @ServerTimestamp
    private Date timestamp;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
