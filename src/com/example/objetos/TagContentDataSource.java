package com.example.objetos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.example.proyecto.TagUIContent;
import com.example.proyecto.R.color;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class TagContentDataSource {

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_PAYLOAD, MySQLiteHelper.COLUMN_PLHEADER, MySQLiteHelper.COLUMN_PLTYPE};
  private FileInputStream fileInputStream;
  private FileOutputStream fileOutputStream;
  private Context context;

  public TagContentDataSource(Context context) {
	this.context = context;
    dbHelper = new MySQLiteHelper(context);
    
    Log.d("debug DB", "DB name: "+dbHelper.getDatabaseName());
    //Log.d("debug DB", "DB name: "+dbHelper.);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
    Log.d("debug", "DB path: "+database.getPath());
  }

  public void close() {
    dbHelper.close();
  }

  public TagContent createContent(String payload,String plHeader, String plType) {
    ContentValues values = new ContentValues();
    values.put(MySQLiteHelper.COLUMN_PAYLOAD, payload);
    values.put(MySQLiteHelper.COLUMN_PLHEADER, plHeader);
    values.put(MySQLiteHelper.COLUMN_PLTYPE, plType);
    long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
        values);
    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    TagContent newComment = cursorToComment(cursor);
    cursor.close();
    return newComment;
  }

  public void deleteComment(TagContent comment) {
    long id = comment.getId();
    System.out.println("Comment deleted with id: " + id);
    database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }

  public List<TagContent> getAllComments() {
    List<TagContent> comments = new ArrayList<TagContent>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      TagContent comment = cursorToComment(cursor);
      comments.add(comment);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return comments;
  }

  private TagContent cursorToComment(Cursor cursor) {
    TagContent comment = new TagContent();
    comment.setId(cursor.getLong(0));
    comment.setPayload(cursor.getString(1));
    comment.setPayloadHeader(cursor.getString(2));
    comment.setPayloadType(cursor.getString(3));
    return comment;
  }
  
  private void importDB() {
      try {
          File sd = Environment.getExternalStorageDirectory();
          File data = Environment.getDataDirectory();
              if (sd.canWrite()) {
              String currentDBPath = "//data//" + "com.example.proyecto"
                      + "//databases//" + "comments.db";
              String backupDBPath = "comments_copy.db"; // From SD directory.
              File backupDB = new File(data, currentDBPath);
              File currentDB = new File(sd, backupDBPath);

          FileChannel src = new FileInputStream(currentDB).getChannel();
          FileChannel dst = new FileOutputStream(backupDB).getChannel();
          dst.transferFrom(src, 0, src.size());
          src.close();
          dst.close();
          /*
          Toast.makeText(getApplicationContext(), "Import Successful!",
                  Toast.LENGTH_SHORT).show();*/

      }
  } catch (Exception e) {
  	/*
      Toast.makeText(getApplicationContext(), "Import Failed!", Toast.LENGTH_SHORT)
              .show();*/

  }
}

  public void exportDB() {
	  try {
	      File sd = Environment.getExternalStorageDirectory();
	      File data = Environment.getDataDirectory();
	      
	      Log.d("debug","Data directory "+ data.getPath());
	      Log.d("debug","SD directory "+ data.getPath());
	      
	      if (sd.canWrite()) {
	      	String currentDBPath = "//data//" + "com.example.proyecto"
	                  + "//databases//comments.db";
	          String backupDBPath = "comments_copy.db";
	          File currentDB = new File(data, currentDBPath);
	          File backupDB = new File(sd, backupDBPath);
	
	          fileInputStream = new FileInputStream(currentDB);
			FileChannel src = fileInputStream.getChannel();
	          fileOutputStream = new FileOutputStream(backupDB);
			FileChannel dst = fileOutputStream.getChannel();
	          dst.transferFrom(src, 0, src.size());
	          src.close();
	          dst.close();
	          Log.d("debug", "Backup successful!");
	          /*
	           
	          Toast.makeText(getApplicationContext(), "Backup Successful!",
	                  Toast.LENGTH_SHORT).show();*/
	
	      }
	  } catch (Exception e) {
		  Log.d("debug", "Backup failed!- Error: "+ e.toString());
	  	/*
	      Toast.makeText(getApplicationContext(), "Backup Failed!", Toast.LENGTH_SHORT)
	              .show();*/
	
	  }
	}

  public List<TagUIContent> getTagUIContents(){
	  
	  List<TagContent> tagContents = this.getAllComments();
	  List<TagUIContent> tagUIContents = new ArrayList<TagUIContent>();
	  
	  
	  for (TagContent tagContent : tagContents) {
		  TagUIContent nTagUIContent = new TagUIContent(context);
		  nTagUIContent.setPayload(tagContent.getPayloadHeader()+tagContent.getPayload());
		  nTagUIContent.setContentDesc(tagContent.getPayloadType());
		  nTagUIContent.setContentIcon(tagContent.getPayloadType());
		  tagUIContents.add(nTagUIContent);
	}
	  
	  return tagUIContents;
	  
	  
  }
} 
