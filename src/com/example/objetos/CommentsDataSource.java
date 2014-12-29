package com.example.objetos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class CommentsDataSource {

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
      MySQLiteHelper.COLUMN_COMMENT };
private FileInputStream fileInputStream;
private FileOutputStream fileOutputStream;

  public CommentsDataSource(Context context) {
    dbHelper = new MySQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public Comment createComment(String comment) {
    ContentValues values = new ContentValues();
    values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
    long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
        values);
    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Comment newComment = cursorToComment(cursor);
    cursor.close();
    return newComment;
  }

  public void deleteComment(Comment comment) {
    long id = comment.getId();
    System.out.println("Comment deleted with id: " + id);
    database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }

  public List<Comment> getAllComments() {
    List<Comment> comments = new ArrayList<Comment>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Comment comment = cursorToComment(cursor);
      comments.add(comment);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return comments;
  }

  private Comment cursorToComment(Cursor cursor) {
    Comment comment = new Comment();
    comment.setId(cursor.getLong(0));
    comment.setComment(cursor.getString(1));
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

      
      if (sd.canWrite()) {
      	String currentDBPath = "//data//" + "com.example.proyecto"
                  + "//databases//";
          String backupDBPath = "comments_copy.db";
          File currentDB = new File(data, currentDBPath);
          currentDB.mkdir();
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
} 
