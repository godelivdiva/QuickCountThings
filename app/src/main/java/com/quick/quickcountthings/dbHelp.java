package com.quick.quickcountthings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbHelp extends SQLiteOpenHelper {

    static final String table_material = "tb_material";
    public final String _id = "_id";
    public final String obj_type = "obj_type";
    public final String xcenter = "xcenter";
    public final String ycenter = "ycenter";
    public final String width = "width";
    public final String height = "height";
    public final String idx = "idx";
    public final String flag = "flag";

    String mQuery;

    public dbHelp(Context context) {
        super(context, "db_material", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mQuery = "CREATE TABLE " + table_material + " (" +
                _id + " INTEGER PRIMARY KEY," +
                obj_type + " TEXT," +
                xcenter + " TEXT," +
                ycenter + " TEXT," +
                width + " TEXT," +
                height + " TEXT," +
                idx + " TEXT," +
                flag + " TEXT" + ")";
        db.execSQL(mQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("NEW",""+newVersion);
        Log.d("OLD",""+oldVersion);
        if(newVersion>oldVersion){
            db.execSQL("DROP TABLE "+table_material);
            onCreate(db);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //    insert data
    public void insert(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("insert", "" + values.toString());
        db.insert("tb_material", null, values);
        db.close();
    }
    //    delete data all
    public void delete() {
        SQLiteDatabase db = this.getWritableDatabase();
        mQuery = "DELETE FROM tb_material";
        db.execSQL(mQuery);
        db.close();
    }
    //    select data all
    public Cursor select() {
        SQLiteDatabase db = this.getWritableDatabase();
        mQuery = "SELECT * FROM tb_material";
        Cursor c = db.rawQuery(mQuery, null);
        return c;
    }

    public Cursor selectSqlite() {
        SQLiteDatabase db = getWritableDatabase();
        System.out.println("Data : " + mQuery);
        mQuery = "SELECT xcenter, ycenter, width, height " +
                "FROM tb_material";
        return db.rawQuery(mQuery, null);
    }
}
