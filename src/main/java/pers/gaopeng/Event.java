package pers.gaopeng;

import java.sql.Timestamp;

public class Event {
    public String user;
    public String url;
    public Integer id;
    public long timestamp;

    public Event() {
    }

    public Event(String user, String url, Integer id) {
        this.user = user;
        this.url = url;
        this.id = id;
    }

    public Event(String user, String url, Integer id, long timestamp) {
        this.user = user;
        this.url = url;
        this.id = id;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "user='" + user + '\'' +
                ", url='" + url + '\'' +
                ", id=" + id +
                ", timestamp=" + timestamp +
                '}';
    }
}
