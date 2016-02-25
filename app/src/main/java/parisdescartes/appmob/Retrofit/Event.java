package parisdescartes.appmob.Retrofit;

import java.sql.Date;

/**
 * Created by Killian on 25/02/2016.
 */
public class Event {
    private int eventid;
    private int created_by;
    private double latitude;
    private double longitude;
    private Date date;

    public Event(int eventid, int created_by, double latitude, double longitude, Date date) {
        this.eventid = eventid;
        this.created_by = created_by;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
