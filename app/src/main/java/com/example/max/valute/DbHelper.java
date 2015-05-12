package com.example.max.valute;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 1 on 12.05.2015.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "DB";
    public static final String TABLE_NAME ="valute_table";
    public static final String KEY_ID ="id";
    public static final String DATE ="date";
    public static final String VALUTE ="valute";
    public static final String VALUE ="value";
    private static final String DATABASE_NAME ="myDB";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(LOG_TAG, "--onCreate database");
        db.execSQL("create table "+ TABLE_NAME + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + DATE + " text,"
                + VALUTE + " text," + VALUE + " text" + ");");
    }
    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVesion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        this.onCreate(db);
    }
}
