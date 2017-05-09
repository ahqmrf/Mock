package apps.ahqmrf.mock.model;

import apps.ahqmrf.mock.Message;

/**
 * Created by Lenovo on 5/9/2017.
 */
public class MessageBuilder {
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

    public MessageBuilder() {

    }

    public MessageBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public MessageBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public MessageBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public MessageBuilder setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public MessageBuilder setDay(String day) {
        this.day = day;
        return this;
    }

    public MessageBuilder setTime(String time) {
        this.time = time;
        return this;
    }

    public MessageBuilder setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public MessageBuilder setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public MessageBuilder setSeen(boolean seen) {
        this.seen = seen;
        return this;
    }

    public MessageBuilder setClicked(boolean clicked) {
        this.clicked = clicked;
        return this;
    }

    public MessageBuilder setLast(boolean last) {
        this.last = last;
        return this;
    }

    public MessageBuilder setSeenDay(String seenDay) {
        this.seenDay = seenDay;
        return this;
    }

    public MessageBuilder setSeenTime(String seenTime) {
        this.seenTime = seenTime;
        return this;
    }

    public Message build() {
        return new Message(id, type, text, imageUrl, day, time, sender, receiver, seen, seenDay, seenTime, clicked, last);
    }
}

