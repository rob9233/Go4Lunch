package robfernandes.xyz.go4lunch.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserInformation {
    private String id;
    private String email;
    private String name = "";
    private String photoUrl = "";
    @ServerTimestamp
    private Date timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        if (name.equals("")) {
            name = email;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
