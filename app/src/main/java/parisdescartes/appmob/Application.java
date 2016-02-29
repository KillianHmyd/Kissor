package parisdescartes.appmob;

import parisdescartes.appmob.database.DatabaseHelper;

public class Application extends android.app.Application {
    private DatabaseHelper db = new DatabaseHelper(this);

    public DatabaseHelper getDb() {
        return db;
    }
}
