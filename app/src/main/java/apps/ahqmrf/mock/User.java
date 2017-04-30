package apps.ahqmrf.mock;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bsse0 on 4/28/2017.
 */

public class User implements Parcelable{
    String email;
    String username;
    String fullName;
    String imageUrl;


    public User() {}

    public User(String email, String username, String fullName, String imageUrl) {
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
    }

    protected User(Parcel in) {
        email = in.readString();
        username = in.readString();
        fullName = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeString(imageUrl);
    }
}
