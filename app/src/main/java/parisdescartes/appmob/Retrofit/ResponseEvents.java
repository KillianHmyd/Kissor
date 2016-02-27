package parisdescartes.appmob.Retrofit;

import java.util.List;

import parisdescartes.appmob.Item.Event;

/**
 * Created by Killian on 25/02/2016.
 */
public class ResponseEvents {
    private int total;
    private List<Event> events;

    public ResponseEvents(int total, List<Event> events) {
        this.total = total;
        this.events = events;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
