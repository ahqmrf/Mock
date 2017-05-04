package apps.ahqmrf.mock;

/**
 * Created by Lenovo on 5/4/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class Time implements Parcelable {
    private String timeStamp;
    private String date;

    public static int compare(Time x, Time y) {
        String tokenX[] = x.getDate().split("/");
        String tokenY[] = y.getDate().split("/");
        int xYear = Integer.parseInt(tokenX[0]);
        int yYear = Integer.parseInt(tokenY[0]);
        if (xYear > yYear) return 1;
        int xMonth = Integer.parseInt(tokenX[1]);
        int yMonth = Integer.parseInt(tokenY[1]);
        if (xMonth > yMonth) return 1;
        int xDay = Integer.parseInt(tokenX[2]);
        int yDay = Integer.parseInt(tokenY[2]);
        if (xDay > yDay) return 1;
        else if (xDay == yDay) return 0;
        return -1;
    }

    public static int compareTimestamp(Time x, Time y) {
        String tokenX[] = x.getTimeStamp().split(":");
        String tokenY[] = y.getTimeStamp().split(":");
        int hx = Integer.parseInt(tokenX[0]), mx = Integer.parseInt(tokenX[1]), sx = Integer.parseInt(tokenX[2]);
        int hy = Integer.parseInt(tokenY[0]), my = Integer.parseInt(tokenY[1]), sy = Integer.parseInt(tokenY[2]);
        if (hx == hy) {
            if (mx > my) return 1;
            if (mx < my) return -1;
            else {
                if (sx > sy) return 1;
                if (sx < sy) return -1;
                return 0;
            }
        } else {
            if (hx < hy) return -1;
            return 1;
        }
    }

    public Time(String timeStamp, String date) {
        this.timeStamp = timeStamp;
        this.date = date;
    }

    protected Time(Parcel in) {
        timeStamp = in.readString();
        date = in.readString();
    }

    public static final Creator<Time> CREATOR = new Creator<Time>() {
        @Override
        public Time createFromParcel(Parcel in) {
            return new Time(in);
        }

        @Override
        public Time[] newArray(int size) {
            return new Time[size];
        }
    };

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timeStamp);
        dest.writeString(date);
    }

    public int getDay() {
        String tokens[] = date.split("/");
        return Integer.parseInt(tokens[2]);
    }
}
