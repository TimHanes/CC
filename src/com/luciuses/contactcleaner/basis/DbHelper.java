package com.luciuses.contactcleaner.basis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
 
public class DbHelper extends SQLiteOpenHelper implements BaseColumns{
 
 
    public final static String DATABASE_TABLE = "Dublicates";
 
    public static final String SOURSE_TYPE = "sourseType";
    public static final String SOURSE = "sourse";
    public static final String REGION = "region";
    public static final String ID_DUBLICATES = "iDDublicates";
 
    private static final String DATABASE_NAME = "DbDublicates";
    private static final int DATABASE_VERSION = 1;
 
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + SOURSE_TYPE
            + " integer not null, " + SOURSE
            + " text not null, "+ REGION
            + " text not null, " + ID_DUBLICATES + " text not null);";
    
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);              
    }  

    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);  
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);       
        onCreate(db);
    }
}