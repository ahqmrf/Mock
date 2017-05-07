package apps.ahqmrf.mock;

/**
 * Created by Lenovo on 5/4/2017.
 */

public class Message {
    private String sender;
    private String receiver;
    private String day;
    private String time;
    private String text;
    private boolean isLast;

    public Message() {

    }

    public Message(String sender, String receiver, String day, String time, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.day = day;
        this.time = time;
        this.text = text;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
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
}