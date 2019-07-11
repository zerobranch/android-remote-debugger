package com.sarproj.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Random;

public class DBHelper extends SQLiteOpenHelper {
    private final static int THIRD_VERSION = 20127;
    SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, "newPagings_1.db", null, THIRD_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
//        String user = "CREATE TABLE User (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address TEXT);";
//        String flavors = "CREATE TABLE Flavor (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, age INTEGER);";
//        db.execSQL(user);
//        db.execSQL(flavors);
//        insertFlavor();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println();
    }

    public void insertUser() {
        SQLiteDatabase database = getWritableDatabase();

        String user = "CREATE TABLE User (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address INTEGER, sun real);";
        getWritableDatabase().execSQL(user);
        Random rnd = new Random();
        int rdnNumber = rnd.nextInt(10000);
        ContentValues cv = new ContentValues();
        cv.put("name", "name_" + rdnNumber);
        cv.put("address", rdnNumber);
        database.insert("User", null, cv);
//        database.close();
    }

    public void insertFlavor() {
        SQLiteDatabase database = getWritableDatabase();
        String flavors = "CREATE TABLE Flavor (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, age INTEGER);";
        database.execSQL(flavors);

        Random rnd = new Random();
        int rdnNumber = rnd.nextInt(10000);
        ContentValues cv = new ContentValues();
        cv.put("name", "name_" + rdnNumber);
        cv.put("description", "description_" + rdnNumber);
        cv.put("age", rdnNumber);
        database.insert("Flavor", null, cv);
//        database.close();
    }
}
