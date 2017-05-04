package apps.ahqmrf.mock;

/**
 * Created by Lenovo on 5/4/2017.
 */

public class Confirmation {
    String email;
    String username;
    String fullName;
    String imageUrl;

    public Confirmation(String email, String username, String fullName, String imageUrl) {
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
