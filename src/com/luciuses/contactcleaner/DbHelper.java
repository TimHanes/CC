package com.luciuses.contactcleaner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
 
public class DbHelper extends SQLiteOpenHelper implements BaseColumns{
 
 
    public final static String DATABASE_TABLE = "UriDublicatesOfContact";
 
    public static final String CONTACT_URI = "contactUri";
    public static final String URI_DUBLICATES_BY_NAME = "uriDublicatesByName";
    public static final String URI_DUBLICATES_BY_PHONE = "uriDublicatesByPhone";
 
    private static final String DATABASE_NAME = "BdUriDublicatesOfContacts";
    private static final int DATABASE_VERSION = 1;
 
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + CONTACT_URI
            + " text not null, " + URI_DUBLICATES_BY_NAME + " text, " + URI_DUBLICATES_BY_PHONE
            + " text);";
    
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
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        // Создаём новую таблицу
        onCreate(db);
    }
}