package com.example.pradiptaagus.app_project4.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION= 1;
    private static final String DATABASE_NAME = "db_memo";

    //query
    private static final String CREATE_TABLE_MEMO = "CREATE TABLE memos(" +
        "id INTEGER PRIMARY KEY," +
        "title TEXT," +
        "detail TEXT," +
        "user_id INTEGER," +
        "created_at TEXT," +
        "updated_at TEXT" +
    ")";

    private static final String CREATE_TABLE_FRIEND = "CREATE TABLE friends(" +
        "id INTEGER PRIMARY KEY, " +
        "name TEXT," +
        "email TEXT" +
    ")";

    private static final String CREATE_TABLE_SHARED_MEMO = "CREATE TABLE shared_memos(" +
            "id INTEGER PRIMARY KEY," +
            "title TEXT," +
            "detail TEXT," +
            "user_id INTEGER," +
            "created_at TEXT," +
            "updated_at TEXT" +
    ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEMO);
        db.execSQL(CREATE_TABLE_FRIEND);
        db.execSQL(CREATE_TABLE_SHARED_MEMO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS memos");
        onCreate(db);
    }
}
