package apps.ahqmrf.mock;

/**
 * Created by Lenovo on 5/9/2017.
 */

public class Message {
    private int     id;
    private String  type;
    private String  text;
    private String  imageUrl;
    private String  day;
    private String  time;
    private String  sender;
    private String  receiver;
    private boolean seen;
    private String  seenDay;
    private String  seenTime;
    private boolean clicked;
    private boolean last;

    public Message() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public void setSeenDay(String seenDay) {
        this.seenDay = seenDay;
    }

    public void setSeenTime(String seenTime) {
        this.seenTime = seenTime;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean isSeen() {
        return seen;
    }

    public boolean isClicked() {
        return clicked;
    }

    public boolean isLast() {
        return last;
    }

    public String getSeenDay() {
        return seenDay;
    }

    public String getSeenTime() {
        return seenTime;
    }

    public Message(int id, String type, String text, String imageUrl, String day, String time, String sender, String receiver, boolean seen, String seenDay, String seenTime, boolean clicked, boolean last) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.imageUrl = imageUrl;
        this.day = day;
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
        this.seen = seen;
        this.seenDay = seenDay;
        this.seenTime = seenTime;
        this.clicked = clicked;
        this.last = last;
    }
}
