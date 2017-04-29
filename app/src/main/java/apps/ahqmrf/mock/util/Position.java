package apps.ahqmrf.mock.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bsse0 on 4/29/2017.
 */

public class Position implements Parcelable{
    double latitude;
    double longitude;
    String state;

    public Position() {}

    public Position(double latitude, double longitude, String state) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
    }

    protected Position(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        state = in.readString();
    }

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(state);
    }
}
