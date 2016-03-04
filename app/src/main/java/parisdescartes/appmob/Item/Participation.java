package parisdescartes.appmob.Item;

/**
 * Created by Killian on 25/02/2016.
 */
public class Participation {
    private String eventid;
    private long userid;

    public Participation(String eventid, long userid) {
        this.eventid = eventid;
        this.userid = userid;
    }

    public String getEventid() {
        return eventid;
    }

    public long getUserid() {
        return userid;
    }
}
