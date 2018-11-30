package com.example.pradiptaagus.app_project4.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pradiptaagus.app_project4.Model.MemoItemResponse;

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

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MEMO);
        db.execSQL(CREATE_TABLE_FRIEND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS memos");
        onCreate(db);
    }
}
