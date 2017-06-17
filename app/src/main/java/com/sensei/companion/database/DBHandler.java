package com.sensei.companion.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sensei.companion.properties.Properties;

import java.lang.reflect.Type;

class DBHandler extends SQLiteOpenHelper {

    private final String DEBUG_TAG = "gestureMonitor";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "companionDB.db";
    private static final String TABLE_NAME = "user_interface";
    private static final String COLUMN_PROPERTIES = "properties";

    //We need to pass database information along to superclass
    DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                COLUMN_PROPERTIES + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void update(Properties properties){
        deleteProperties();
        addProperties(properties);
    }

    //Add a new row to the database
    void addProperties(Properties entry){
        Gson gson = new Gson();
        String entryS = gson.toJson(entry);

        ContentValues values = new ContentValues();
        values.put(COLUMN_PROPERTIES, entryS);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    //Delete a product from the database
    void deleteProperties(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE 1");
        db.close();
    }

    Properties getData(Context context){
        Properties props;
        try {
            SQLiteDatabase db = getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE 1";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            String dbString = c.getString(c.getColumnIndex(COLUMN_PROPERTIES));
            c.close();
            db.close();
            Gson gson = new Gson();
            Type type = new TypeToken<Properties>() {}.getType();
            props = gson.fromJson(dbString, type);
        }
        catch(Exception e){
            Log.i(DEBUG_TAG, "error getting data");
            props = new Properties(context);
        }
        return props;
    }

    boolean isEmpty(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE 1";
        Cursor mCursor = db.rawQuery(query, null);
        boolean ret = !(mCursor.moveToFirst());
        mCursor.close();
        db.close();
        return ret;
    }

}
