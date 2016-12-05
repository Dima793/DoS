package project.dos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.HashSet;

public class DBController extends SQLiteOpenHelper {
    public static  DBController dataBaseController;

    DBController (Context appContext) {
        super (appContext, "mySave.db", null, 1);
    }

    public static void initialize(Context context) {
        if (dataBaseController == null) {
            dataBaseController = new DBController(context);
        }
    }

    @Override
    public void onCreate (SQLiteDatabase database) {
        String query = "CREATE TABLE creatures (posX INTEGER, posY INTEGER, owner INTEGER, hp INTEGER, ap INTEGER, name TEXT, PRIMARY KEY (posX, posY));";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade (SQLiteDatabase database, int old_ver, int new_ver) {
        //probably we should throw an exeption here, but fuck it
        String query = "DROP TABLE IF EXISTS creatures";
        database.execSQL(query);
        onCreate(database);
    }
    public void insertOrEditCreature (Creature creature) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM creatures WHERE posX=" + creature.pos.first.toString() + " AND posY=" + creature.pos.second.toString() + ";";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount() == 1) {
            removeCreature(creature);
        }
        ContentValues values = new ContentValues();
        values.put("posX", creature.pos.first);
        values.put("posY", creature.pos.second);
        values.put("owner", creature.getOwner());
        values.put("hp", creature.hp);
        values.put("ap", creature.ap);
        values.put("name", creature.name);
        database.insert("creatures", null, values);
        database.close();
    }
    public void removeCreature (Creature creature) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DELETE FROM creatures WHERE posX= " + creature.pos.first.toString() + " AND posY=" + creature.pos.second.toString() + ";";
        database.execSQL(query);
        database.close();
    }
    public void removeAllCreatures () {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("creatures", null, null);
        database.close();
    }
    /*
    @NonNull
    public HashSet<String> searchBy (String fieldName, String value) {
        try {
            SQLiteDatabase database = this.getReadableDatabase();
            String query = "SELECT * FROM creatures WHERE " + fieldName + "= '" + value + "'";
            Cursor cursor = database.rawQuery(query, null);
            HashSet<String> result = new HashSet<String>();
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> bookData = new HashMap<String, String>();
                    for (int i = 1; i < cursor.getColumnCount(); i++) {
                        bookData.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                    result.add(bookData.toString());
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();
            return result;
        } catch (Exception e) {
            HashSet<String> res = new HashSet<String>();
            return res;
        }
    }
    */
}
