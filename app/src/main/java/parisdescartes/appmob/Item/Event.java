package parisdescartes.appmob.Item;

/**
 * Created by Killian on 25/02/2016.
 */
public class Event {
    private String _id;
    private long created_by;
    private double latitude;
    private double longitude;
    private String date;

    public Event(String eventid, long created_by, double latitude, double longitude, String date) {
        this._id = eventid;
        this.created_by = created_by;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getCreated_by() {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
