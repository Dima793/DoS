package project.dos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static project.dos.BattlefieldLogic.battlefieldLogic;

public class DBController extends SQLiteOpenHelper implements EventsListener<Creature> {

    DBController (Context appContext) {
        super (appContext, "mySave.db", null, 1);
    }

    @Override
    public void onCreate (SQLiteDatabase database) {
        String query = "CREATE TABLE creatures (iD INTEGER, posX INTEGER, posY INTEGER, owner INTEGER, hp INTEGER, ap INTEGER, name TEXT, PRIMARY KEY(iD));";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade (SQLiteDatabase database, int old_ver, int new_ver) {
        //probably we should throw an exception here, but fuck it
        String query = "DROP TABLE IF EXISTS creatures";
        database.execSQL(query);
        onCreate(database);
    }
    public void insertOrEditCreature (Creature creature) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM creatures WHERE iD=" + creature.iD + ";";
        Cursor cursor = database.rawQuery(query, null);//close() should be called somewhere
        if (cursor.getCount() == 1) {
            removeCreature(creature);
            database = this.getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put("posX", creature.pos.x);
        values.put("posY", creature.pos.y);
        values.put("owner", creature.getOwner());
        values.put("hp", creature.hp);
        values.put("ap", creature.ap);
        values.put("name", creature.name);
        values.put("iD", creature.iD);
        database.insert("creatures", null, values);
        battlefieldLogic.toOut = printAll();
        database.close();
    }

    public void removeCreature (Creature creature) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DELETE FROM creatures WHERE iD=" + creature.iD + ";";
        database.execSQL(query);
        database.close();
    }

    public void removeAllCreatures () {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("creatures", null, null);
        database.close();
    }

    public Creature retrieve(Cursor c) {
        Creature creature = new Creature();
        creature.iD = c.getInt(0);
        creature.pos = new HexCoord(c.getInt(1), c.getInt(2));
        creature.owner = c.getInt(3);
        creature.hp = c.getInt(4);
        creature.ap = c.getInt(5);
        creature.name = c.getString(6);

        return creature;
    }

    public String printAll() {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM creatures";
        Cursor cursor = database.rawQuery(query, null);
        StringBuilder result = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                result.append(retrieve(cursor) + "\n");//String concatenation as argument to append()
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return result.toString();
    }

    @Override
    public void listenEvent(int eventCase, Creature creature) {//insert/edit or remove creature
        switch (eventCase) {
            case 0:
                insertOrEditCreature(creature);
                break;
            case 1:
                removeCreature(creature);
                break;
        }
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
