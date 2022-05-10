package pers.gaopeng;

import java.sql.Timestamp;

public class Event {
    public String user;
    public String url;
    public Integer id;

    public Event() {
    }

    public Event(String user, String url, Integer id) {
        this.user = user;
        this.url = url;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "user='" + user + '\'' +
                ", url='" + url + '\'' +
                ", id=" + id +
                '}';
    }
}
