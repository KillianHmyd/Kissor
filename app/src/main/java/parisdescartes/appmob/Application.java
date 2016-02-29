package parisdescartes.appmob;

import parisdescartes.appmob.database.DatabaseHelper;

/**
 * Created by Killian on 29/02/2016.
 */
public class Application extends android.app.Application {
    private DatabaseHelper db = new DatabaseHelper(this);

    public DatabaseHelper getDb() {
        return db;
    }
}
