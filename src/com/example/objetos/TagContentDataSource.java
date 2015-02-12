package com.example.objetos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.SyncStateContract.Columns;
import android.util.Log;

import com.example.proyecto.TagUIContent;

public class TagContentDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] contentColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_PAYLOAD, MySQLiteHelper.COLUMN_PLHEADER,
			MySQLiteHelper.COLUMN_PLTYPE, MySQLiteHelper.COLUMN_CREATED_AT };
	private String[] tagColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME };
	private FileInputStream fileInputStream;
	private FileOutputStream fileOutputStream;
	private Context context;

	public TagContentDataSource(Context context) {
		this.context = context;
		dbHelper = new MySQLiteHelper(context);

		Log.d("debug DB", "DB name: " + dbHelper.getDatabaseName());
		// Log.d("debug DB", "DB name: "+dbHelper.);
	}

	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	// DATABASE methods
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		Log.d("debug", "DB path: " + database.getPath());
	}

	public void close() {
		dbHelper.close();
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
				 * Toast.makeText(getApplicationContext(), "Import Successful!",
				 * Toast.LENGTH_SHORT).show();
				 */

			}
		} catch (Exception e) {
			/*
			 * Toast.makeText(getApplicationContext(), "Import Failed!",
			 * Toast.LENGTH_SHORT) .show();
			 */

		}
	}

	public void exportDB() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			Log.d("debug", "Data directory " + data.getPath());
			Log.d("debug", "SD directory " + data.getPath());

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
				 * 
				 * Toast.makeText(getApplicationContext(), "Backup Successful!",
				 * Toast.LENGTH_SHORT).show();
				 */

			}
		} catch (Exception e) {
			Log.d("debug", "Backup failed!- Error: " + e.toString());
			/*
			 * Toast.makeText(getApplicationContext(), "Backup Failed!",
			 * Toast.LENGTH_SHORT) .show();
			 */

		}
	}

	// TAG_CONTENT table methods
	public TagContent createContent(String payload, String plHeader,
			String plType) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PAYLOAD, payload);
		values.put(MySQLiteHelper.COLUMN_PLHEADER, plHeader);
		values.put(MySQLiteHelper.COLUMN_PLTYPE, plType);
		values.put(MySQLiteHelper.COLUMN_CREATED_AT, getDateTime());
		long insertId = database.insert(MySQLiteHelper.TABLE_CONTENT, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTENT,
				contentColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		TagContent newComment = cursorToComment(cursor);
		cursor.close();
		return newComment;
	}

	public void deleteContent(Long id) {
		// long id = comment.getId();
		System.out.println("Content deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_CONTENT, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<TagContent> getAllComments() {
		List<TagContent> comments = new ArrayList<TagContent>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTENT,
				contentColumns, null, null, null, null, null);

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
		comment.setCreatedAt(cursor.getString(4));
		return comment;
	}

	public List<TagUIContent> getTagUIContents() {

		List<TagContent> tagContents = this.getAllComments();
		List<TagUIContent> tagUIContents = new ArrayList<TagUIContent>();

		for (TagContent tagContent : tagContents) {
			TagUIContent nTagUIContent = new TagUIContent(context);
			nTagUIContent.setPayload(tagContent.getPayloadHeader()
					+ tagContent.getPayload());
			nTagUIContent.setContentDesc(tagContent.getPayloadType());
			nTagUIContent.setContentIcon(tagContent.getPayloadType());
			nTagUIContent.setContentId(String.valueOf(tagContent.getId()));
			tagUIContents.add(nTagUIContent);
		}

		return tagUIContents;

	}

	public TagContent getContentById(String id) {
		TagContent nTagContent = null;
		String[] argumentsString = { String.valueOf(id) };
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTENT,
				contentColumns, "_id=?", argumentsString, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			nTagContent = cursorToComment(cursor);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		return nTagContent;
	}

	public int updateContent(String id, String payload, String header,
			String type) {

		String whereClause = MySQLiteHelper.COLUMN_ID + "=?";
		String[] whereArgs = { id };

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PAYLOAD, payload);
		values.put(MySQLiteHelper.COLUMN_PLHEADER, header);
		values.put(MySQLiteHelper.COLUMN_PLTYPE, type);
		int result = database.update(MySQLiteHelper.TABLE_CONTENT, values,
				whereClause, whereArgs);

		return result;

	}

	public List<TagUIContent> getContentFiltered(String filters) {

		String condition = "";

		if (filters != "") {
			condition = "WHERE payload_type IN (" + filters + ")";
		}

		String sqlSentence = "SELECT * FROM tag_content " + condition;
		Log.d("debug query", sqlSentence);
		List<TagContent> tagContents = new ArrayList<TagContent>();
		List<TagUIContent> tagUIContents = new ArrayList<TagUIContent>();

		Cursor cursor = database.rawQuery(sqlSentence, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TagContent comment = cursorToComment(cursor);
			tagContents.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		for (TagContent tagContent : tagContents) {
			TagUIContent nTagUIContent = new TagUIContent(context);
			nTagUIContent.setPayload(tagContent.getPayloadHeader()
					+ tagContent.getPayload());
			nTagUIContent.setContentDesc(tagContent.getPayloadType());
			nTagUIContent.setContentIcon(tagContent.getPayloadType());
			nTagUIContent.setContentId(String.valueOf(tagContent.getId()));
			tagUIContents.add(nTagUIContent);
		}

		return tagUIContents;

	}

	public List<TagUIContent> getContentbySearch(String query) {

		String condition = "";

		if (query != "") {
			condition = "WHERE payload LIKE '%" + query + "%'"
					+ " OR payload_type LIKE '%" + query + "%'";
		}

		String sqlSentence = "SELECT * FROM tag_content " + condition;
		Log.d("debug query", sqlSentence);
		List<TagContent> tagContents = new ArrayList<TagContent>();
		List<TagUIContent> tagUIContents = new ArrayList<TagUIContent>();

		Cursor cursor = database.rawQuery(sqlSentence, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TagContent comment = cursorToComment(cursor);
			tagContents.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		for (TagContent tagContent : tagContents) {
			TagUIContent nTagUIContent = new TagUIContent(context);
			nTagUIContent.setPayload(tagContent.getPayloadHeader()
					+ tagContent.getPayload());
			nTagUIContent.setContentDesc(tagContent.getPayloadType());
			nTagUIContent.setContentIcon(tagContent.getPayloadType());
			nTagUIContent.setContentId(String.valueOf(tagContent.getId()));
			tagUIContents.add(nTagUIContent);
		}

		return tagUIContents;

	}

	// CONTENT_TAG table methods
	public ContentTag createTag(String name) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		long insertId = database.insert(MySQLiteHelper.TABLE_TAG, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAG,
				tagColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		ContentTag newComment = cursorToTag(cursor);
		cursor.close();
		return newComment;
	}

	public void deleteTag(Long id, boolean deleteAll) {
		// long id = comment.getId();
		if (deleteAll) {
			List<TagContent> contentsByTag = new ArrayList<TagContent>();

			ContentTag nContentTag = getTagById(String.valueOf(id));
			contentsByTag = getContentByTag(nContentTag.getName());

			for (TagContent tagContent : contentsByTag) {
				deleteContent(tagContent.getId());
			}
		}

		System.out.println("Tag deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_TAG, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	private ContentTag cursorToTag(Cursor cursor) {
		ContentTag comment = new ContentTag();
		comment.setId(cursor.getLong(0));
		comment.setName(cursor.getString(1));
		return comment;
	}

	public int updateTag(String id, String name) {

		String whereClause = MySQLiteHelper.COLUMN_ID + "=?";
		String[] whereArgs = { id };

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, name);
		int result = database.update(MySQLiteHelper.TABLE_TAG, values,
				whereClause, whereArgs);

		return result;

	}

	public List<ContentTag> getAllTags() {
		List<ContentTag> tags = new ArrayList<ContentTag>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_TAG, tagColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ContentTag tag = cursorToTag(cursor);
			tags.add(tag);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return tags;
	}

	public ContentTag getTagById(String id) {
		ContentTag nTagContent = null;
		String[] argumentsString = { String.valueOf(id) };
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTENT,
				tagColumns, "_id=?", argumentsString, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			nTagContent = cursorToTag(cursor);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		return nTagContent;
	}

	// CONTENT_TAGS table methods
	public long assignTag(long cId, long tId) {

		ContentValues values = new ContentValues();

		values.put(MySQLiteHelper.COLUMN_CONTENT_ID, cId);
		values.put(MySQLiteHelper.COLUMN_TAG_ID, tId);

		long id = database.insert(MySQLiteHelper.TABLE_CONTENT_TAG, null,
				values);

		return id;
	}

	public List<TagContent> getContentByTag(String tagName) {
		List<TagContent> contentsByTag = new ArrayList<TagContent>();
		TagContent nTagContent = null;
		String contentsByTags = "SELECT * " + "FROM "
				+ MySQLiteHelper.TABLE_CONTENT + " c, "
				+ MySQLiteHelper.TABLE_TAG + " t, "
				+ MySQLiteHelper.TABLE_CONTENT_TAG + " ct " + "WHERE t."
				+ MySQLiteHelper.COLUMN_NAME + " = " + tagName + " AND c."
				+ MySQLiteHelper.COLUMN_ID + " = ct."
				+ MySQLiteHelper.COLUMN_CONTENT_ID + " AND t."
				+ MySQLiteHelper.COLUMN_ID + " = ct."
				+ MySQLiteHelper.COLUMN_TAG_ID;

		Log.d("debug getting by Tag", contentsByTags);
		Cursor cursor = database.rawQuery(contentsByTags, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			nTagContent = cursorToComment(cursor);
			contentsByTag.add(nTagContent);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		return contentsByTag;
	}

	public int updateContentTag(long id, long tag_id) {

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_TAG_ID, tag_id);

		// updating row
		return database.update(MySQLiteHelper.TABLE_CONTENT_TAG, values,
				MySQLiteHelper.COLUMN_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public void deleteContentTag(long id) {

		database.delete(MySQLiteHelper.TABLE_CONTENT, MySQLiteHelper.COLUMN_ID
				+ " = ?", new String[] { String.valueOf(id) });
	}

	public List<ContentTag> getTagsOfContent(String content_id) {
		List<ContentTag> contentsByTag = new ArrayList<ContentTag>();
		ContentTag nContentTag = null;
		String contentsByTags = "SELECT * " + "FROM "
				+ MySQLiteHelper.TABLE_CONTENT + " c, "
				+ MySQLiteHelper.TABLE_TAG + " t, "
				+ MySQLiteHelper.TABLE_CONTENT_TAG + " ct " + "WHERE c."
				+ MySQLiteHelper.COLUMN_ID + " = " + content_id + " AND c."
				+ MySQLiteHelper.COLUMN_ID + " = ct."
				+ MySQLiteHelper.COLUMN_CONTENT_ID + " AND t."
				+ MySQLiteHelper.COLUMN_ID + " = ct."
				+ MySQLiteHelper.COLUMN_TAG_ID;

		Log.d("debug getting Tags", contentsByTags);
		Cursor cursor = database.rawQuery(contentsByTags, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			nContentTag = cursorToTag(cursor);
			contentsByTag.add(nContentTag);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		return contentsByTag;
	}
	
	public void describeTable(){
		
		String sqlString = "PRAGMA table_info("+MySQLiteHelper.TABLE_CONTENT_TAG+");";
		Cursor cursor = database.rawQuery(sqlString, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Log.d("debug describe",cursor.getString(1));
			cursor.moveToNext();
		}
		cursor.close();
	}
}
