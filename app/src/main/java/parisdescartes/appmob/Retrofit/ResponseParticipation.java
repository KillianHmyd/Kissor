package parisdescartes.appmob.Retrofit;

import java.util.ArrayList;

import parisdescartes.appmob.Item.Participation;

/**
 * Created by Killian on 05/03/2016.
 */
public class ResponseParticipation {
    private int total;
    private ArrayList<Participation> participations;

    public ResponseParticipation(int total, ArrayList<Participation> participations) {
        this.total = total;
        this.participations = participations;
    }

    public int getTotal() {
        return total;
    }

    public ArrayList<Participation> getParticipations() {
        return participations;
    }
}
