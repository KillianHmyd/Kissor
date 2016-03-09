package parisdescartes.appmob.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import parisdescartes.appmob.Item.Event;
import parisdescartes.appmob.Item.Participation;
import parisdescartes.appmob.Item.User;

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
                +COL_REF+ " TEXT PRIMARY KEY,"
                +COL_CREATOR+" INTEGER NOT NULL,"
                +COL_LATITUDE+" REAL NOT NULL,"
                +COL_LONGITUDE+" REAL NOT NULL,"
                +COL_DATE+" TEXT NOT NULL)");

        db.execSQL("create table " + TABLE_USER + " ("
                +COL_ID+ " INTEGER PRIMARY KEY,"
                +COL_FIRST_NAME+" TEXT NOT NULL,"
                +COL_LAST_NAME+" TEXT NOT NULL,"
                +COL_PICTURE+" TEXT NOT NULL)");

        db.execSQL("create table " + TABLE_PARTICIPATION + " ("
                + COL_EVENT + " INTEGER NOT NULL,"
                + COL_USER + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + COL_EVENT + ") REFERENCES " + TABLE_EVENT + "(" + COL_REF + "),"
                + "FOREIGN KEY(" + COL_USER + ") REFERENCES " + TABLE_USER + "(" + COL_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPATION);
        onCreate(db);
    }

    /*** *** *** *** *** INSERTION *** *** *** *** ***/
    public boolean insertUser_data(long user_id, String first_name, String last_name, String photo_url){
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

    public boolean insertEvent_data(String ref, long creator, double latitude, double longitude, String date){
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

    public boolean insertParticipation_data(String ref_event, long id_user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EVENT, ref_event);
        contentValues.put(COL_USER, id_user);
        contentValues.put(COL_EVENT, ref_event);
        contentValues.put(COL_USER, id_user);

        long result = db.insert(TABLE_PARTICIPATION, null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public void insertParticipation(Participation participation){
        this.insertParticipation_data(participation.getEventid(), participation.getUserid());
    }

    /*** *** *** *** *** MODIFICATION *** *** *** *** ***/
    public boolean updateUser_data(long id_user, String first_name, String last_name, String photo_url){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id_user);
        contentValues.put(COL_FIRST_NAME, first_name);
        contentValues.put(COL_LAST_NAME, last_name);
        contentValues.put(COL_PICTURE, photo_url);

        long result = db.update(TABLE_USER, contentValues, "ID = ?", new String[]{id_user + ""});
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean updateEvent_data(String ref_event, long creator, double latitude, double longitude, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_REF, ref_event);
        contentValues.put(COL_CREATOR, creator);
        contentValues.put(COL_LATITUDE, latitude);
        contentValues.put(COL_LONGITUDE, longitude);
        contentValues.put(COL_DATE, date);

        long result = db.update(TABLE_EVENT, contentValues, "REF = ?", new String[]{ref_event + ""});
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    /*** *** *** *** *** SELECTION *** *** *** *** ***/
    /* Renvoie tous les events */
    public Cursor getAllEvents_data(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_EVENT, null);
        return res;
    }

    /* Renvoie tous les users */
    public Cursor getAllUsers_data(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_USER, null);
        return res;
    }

    /* Renvoie un event */
    public Cursor getEvent_data(String ref_event){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_EVENT + " WHERE " + COL_REF + " = ?",  new String[] {ref_event + ""});
        return res;
    }

    /* Renvoie un user */
    public Cursor getUser_data(long id_user){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_USER + " WHERE " + COL_ID + " = ?",  new String[] {id_user + ""});
        return res;
    }

    /* Renvoie les events auquel un user participe */
    public Cursor getEventsByUser_data(long id_user){
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select * from " + TABLE_EVENT+ ", "+ TABLE_PARTICIPATION + " WHERE " + id_user + "=" + COL_USER , null);
        Cursor res = db.rawQuery("select * from " + TABLE_EVENT+ ", "+ TABLE_PARTICIPATION + " WHERE " + COL_USER  + " = ?", new String[] {id_user + ""});
        return res;
    }

    /*Renvoie les participants � un event donn�*/
    public Cursor getUsersByEvent_data(String ref_event){
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select * from " + TABLE_USER + ", "+ TABLE_PARTICIPATION + " WHERE " + ref_event + "=" + COL_EVENT , null);
        Cursor res = db.rawQuery("select * from " + TABLE_USER + ", " + TABLE_PARTICIPATION + " WHERE " + COL_EVENT + " = ?", new String[]{ref_event + ""});
        return res;
    }

    /*** *** *** *** *** SUPPRESSION *** *** *** *** ***/
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
    public Integer deleteEvent(String ref_event){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENT, COL_REF + " = ?", new String[] {ref_event +""});
    }

    /*Supression d'un user en particulier*/
    public Integer deleteUser(long id_user){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USER, COL_ID + " = ?", new String[] {id_user +""});
    }

    /*** *** *** *** *** ACCESSEURS OBJET *** *** *** *** ***/
    public User getUser (long id_user){
        Cursor res = this.getUser_data(id_user);
        if(res.getCount() == 0){
            //show message "AUCUN USER CORREPONDANT A CET ID
            return null;
        }
        res.moveToFirst();
        User user = new User(
                res.getLong(0),
                res.getString(1),
                res.getString(2),
                res.getString(3)
        );
        return user;
    }

    public Event getEvent (String ref_event){
        Cursor res = this.getEvent_data(ref_event);
        if(res.getCount() == 0){
            //show message "AUCUN EVENT CORREPONDANT A CET REF
            return null;
        }
        res.moveToFirst();
        Event event = new Event(
                res.getString(0),
                res.getLong(1),
                res.getDouble(2),
                res.getDouble(3),
                res.getString(4)
        );
        return event;
    }

    public Cursor getParticipations_data(long idUser){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_PARTICIPATION + " WHERE " + COL_USER + " = ?",  new String[] {idUser + ""});
        return res;
    }

    public ArrayList<Participation> getParticipations(long userid) {
        Cursor res = getParticipations_data(userid);
        ArrayList<Participation> participations = new ArrayList<Participation>();
        res.moveToFirst();
        int i = 1;
        while(res.moveToNext()){
            System.out.println(i++);
            participations.add(new Participation(res.getString(0), res.getLong(1)));
        }
        return participations;
    }

    public boolean participe(String idevent){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_PARTICIPATION + " WHERE " + COL_EVENT + " = ?",  new String[] {idevent + ""});
        if(res.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean participer(String idevent, long iduser){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EVENT, idevent);
        contentValues.put(COL_USER, iduser);

        long result = db.insert(TABLE_PARTICIPATION, null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    /*** *** *** *** *** AJOUT ET MISE A JOUR - USER OBJET *** *** *** *** ***/
    public void insertUser(User user){
        this.insertUser_data(user.getUserid(), user.getFirst_name(), user.getLast_name(), user.getPhoto_url());
    }

    public void updateUser(User user){
        this.updateUser_data(user.getUserid(), user.getFirst_name(), user.getLast_name(), user.getPhoto_url());
    }

    /**
     * Ajoute un user s'il n'est pas pr�sent dans la base de donn�es
     * sinon, il met � jour l'utlisateur d�j� pr�sent dans la base de donn�es
     * @param user : l'utilisateur � ajouter ou � mettre � jour
     */
    public void addUser (User user){
        Cursor res = this.getUser_data(user.getUserid());
        if(res.getCount() == 0){
            //ALORS INSERTION
            this.insertUser(user);
        }else{
            //SINON MISE A JOUR
            this.updateUser(user);
        }
    }

    /*** *** *** *** *** AJOUT ET MISE A JOUR - EVENT OBJET *** *** *** *** ***/
    public void insertEvent(Event event){
        this.insertEvent_data(event.get_id(), event.getCreated_by(), event.getLatitude(), event.getLongitude(), event.getDate());
    }

    public void updateEvent(Event event){
        this.updateEvent_data(event.get_id(), event.getCreated_by(), event.getLatitude(), event.getLongitude(), event.getDate());
    }

    /**
     * Ajoute un event s'il n'est pas pr�sent dans la base de donn�es
     * sinon, il met � jour l'event d�j� pr�sent dans la base de donn�es
     * @param event : l'event � ajouter ou � mettre � jour
     */
    public void addEvent (Event event){
        Cursor res = this.getEvent_data(event.get_id());
        if(res.getCount() == 0){
            //ALORS INSERTION
            this.insertEvent(event);
        }else{
            //SINON MISE A JOUR
            this.updateEvent(event);
        }
    }
}
