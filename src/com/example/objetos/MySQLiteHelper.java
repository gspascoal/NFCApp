package com.example.objetos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class MySQLiteHelper extends SQLiteOpenHelper {

	  public static final String TABLE_COMMENTS = "tag_content";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_PAYLOAD = "payload";
	  public static final String COLUMN_PLHEADER = "payload_header";
	  public static final String COLUMN_PLTYPE = "payload_type";
	  public static final String COLUMN_TAGS = "content_tags";

	  private static final String DATABASE_NAME = "tag_content.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_COMMENTS + " (" + COLUMN_ID
	      + " integer primary key autoincrement, " 
	      + COLUMN_PAYLOAD  +  " text not null, " 
	      + COLUMN_PLHEADER + " text not null, "+ COLUMN_PLTYPE +" text not null);";

	  public MySQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
	    onCreate(db);
	  }
	  

	}