package parisdescartes.appmob.Item;

import java.util.List;

/**
 * Created by Killian on 25/02/2016.
 */
public class User {
    private long userid;
    private String first_name;
    private String last_name;
    private String photo_url;

    public User(long userid, String first_name, String last_name, String photo_url) {
        this.userid = userid;
        this.first_name = first_name;
        this.last_name = last_name;
        this.photo_url = photo_url;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
}
