package com.sensei.companion.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Database {

    private DBHandler dbHandler;

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this.dbHandler = new DBHandler(context, name, factory, version);
    }

    public void add(Properties input) {
        dbHandler.addProperties(input);
    }

    public void delete(){
        dbHandler.deleteProperties();
    }

    public void update(Properties input){
        dbHandler.update(input);
    }

    public Properties getData(Context context){return dbHandler.getData(context);}
    public boolean isEmpty(){return dbHandler.isEmpty();}

}
