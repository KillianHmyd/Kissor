package parisdescartes.appmob.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yassin on 25/02/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DataBase.db";

    /*CREATION TABLE EVENT*/
    public static final String TABLE_EVENT = "EVENT";
    public static final String COL_REF = "REF";
    public static final String COL_CREATOR = "CREATOR";
    public static final String COL_LATITUDE = "LATITUDE";
    public static final String COL_LONGITUDE = "LONGITUDE";
    public static final String COL_DATE = "DATE";

    /*CREATION TABLE USER*/
    public static final String TABLE_USER = "USER";
    public static final String COL_ID = "ID";
    public static final String COL_FIRST_NAME = "FIRST_NAME";
    public static final String COL_LAST_NAME = "LAST_NAME";
    public static final String COL_PICTURE = "PICTURE";

    /*CREATION TABLE PARTICIPATION*/
    public static final String TABLE_PARTICIPATION = "PARTICIPATION";
    public static final String COL_EVENT = "REF_EVENT";
    public static final String COL_USER = "ID_USER";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);//version 1 par exemple
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_EVENT + " ("
                +COL_REF+ " INTEGER PRIMARY KEY,"
                +COL_CREATOR+" INTEGER NOT NULL,"
                +COL_LATITUDE+" REAL NOT NULL,"
                +COL_LONGITUDE+" REAL NOT NULL,"
                +COL_DATE+" TEXT NOT NULL)");

        db.execSQL("create table " + TABLE_USER + " ("
                +COL_ID+ " INTEGER PRIMARY KEY,"
                +COL_FIRST_NAME+" TEXT NOT NULL,"
                +COL_LAST_NAME+" TEXT NOT NULL,"
                +COL_PICTURE+" TEXT NOT NULL");

        db.execSQL("create table " + TABLE_PARTICIPATION + " ("
                + COL_EVENT + " INTEGER NOT NULL,"
                + COL_USER + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + COL_EVENT + ") REFERENCES " + TABLE_EVENT + "(" + COL_REF + "),"
                + "FOREIGN KEY(" + COL_USER + ") REFERENCES " + TABLE_USER + "(" + COL_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPATION);
        onCreate(db);
    }

    public boolean insertUser(int user_id, String first_name, String last_name, String photo_url){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, user_id);
        contentValues.put(COL_FIRST_NAME, first_name);
        contentValues.put(COL_LAST_NAME, last_name);
        contentValues.put(COL_PICTURE, photo_url);

        long result = db.insert(TABLE_USER, null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertEvent(int ref, int creator, float latitude, float longitude, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_REF, ref);
        contentValues.put(COL_CREATOR, creator);
        contentValues.put(COL_LATITUDE, latitude);
        contentValues.put(COL_LONGITUDE, longitude);
        contentValues.put(COL_DATE, date);

        long result = db.insert(TABLE_EVENT, null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertParticipation(int ref_event, int id_user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EVENT, ref_event);
        contentValues.put(COL_USER, id_user);

        long result = db.insert(TABLE_PARTICIPATION, null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    /*public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }*/

    /* Renvoie tous les events */
    public Cursor getAllEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_EVENT, null);
        return res;
    }

    /* Renvoie tous les users */
    public Cursor getAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_USER, null);
        return res;
    }

    /* Renvoie les events auquel un user participe */
    public Cursor getEventsByUser(int id_user){
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select * from " + TABLE_EVENT+ ", "+ TABLE_PARTICIPATION + " WHERE " + id_user + "=" + COL_USER , null);
        Cursor res = db.rawQuery("select * from " + TABLE_EVENT+ ", "+ TABLE_PARTICIPATION + " WHERE " + COL_USER  + " = ?", new String[] {id_user + ""});
        return res;
    }

    /*Renvoie les participants � un event donn�*/
    public Cursor getUsersByEvent(int ref_event){
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select * from " + TABLE_USER + ", "+ TABLE_PARTICIPATION + " WHERE " + ref_event + "=" + COL_EVENT , null);
        Cursor res = db.rawQuery("select * from " + TABLE_USER + ", "+ TABLE_PARTICIPATION + " WHERE " + COL_EVENT + " = ?", new String[] {ref_event + ""});
        return res;
    }

    /*Supression de tous les events de la table event*/
    public Integer deleteAllEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENT, "1", null);
    }

    /*Supression de toutes les participations*/
    public Integer deleteAllParticipations(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PARTICIPATION, "1", null);
    }

    /*Supression de tous les Users*/
    public Integer deleteAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USER, "1", null);
    }

    /*Supression d'un event en particulier*/
    public Integer deleteEvent(int ref_event){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENT, COL_REF + " = ?", new String[] {ref_event +""});
    }

    /*Supression d'un user en particulier*/
    public Integer deleteUser(int id_user){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USER, COL_ID + " = ?", new String[] {id_user +""});
    }

}
