package apps.ahqmrf.mock;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lenovo on 5/7/2017.
 */

public class LastMessage implements Parcelable {
    String email;
    String username;
    String fullName;
    String imageUrl;
    private String sender;
    private String receiver;
    private String day;
    private String time;
    private String text;

    public LastMessage() {}

    public LastMessage(String email, String username, String fullName, String imageUrl, String sender, String receiver, String day, String time, String text) {
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
        this.sender = sender;
        this.receiver = receiver;
        this.day = day;
        this.time = time;
        this.text = text;
    }

    protected LastMessage(Parcel in) {
        email = in.readString();
        username = in.readString();
        fullName = in.readString();
        imageUrl = in.readString();
        sender = in.readString();
        receiver = in.readString();
        day = in.readString();
        time = in.readString();
        text = in.readString();
    }

    public static final Creator<LastMessage> CREATOR = new Creator<LastMessage>() {
        @Override
        public LastMessage createFromParcel(Parcel in) {
            return new LastMessage(in);
        }

        @Override
        public LastMessage[] newArray(int size) {
            return new LastMessage[size];
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        dest.writeString(sender);
        dest.writeString(receiver);
        dest.writeString(day);
        dest.writeString(time);
        dest.writeString(text);
    }
}
