package robfernandes.xyz.go4lunch.model;

public class UserInformation {
    private String id;
    private String email;
    private String name = "";

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
}
