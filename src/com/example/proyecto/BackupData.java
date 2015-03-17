package com.example.proyecto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.QR.Contents;
import com.example.QR.QRCodeEncoder;
import com.example.objetos.TagContent;
import com.example.objetos.TagContentDataSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class BackupData extends Activity implements OnClickListener {

	private ImageView myImage;
	private TagContentDataSource datasource;
	public Map<String, String> DBR = new LinkedHashMap<String, String>(); //DataBaseResource
	private String LOG_TAG = "GenerateQRCode";
	private Bitmap bitmap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup_data);
	

		DBR.put("0",getResources().getString(R.string.link));
		DBR.put("1",getResources().getString(R.string.mail));
		DBR.put("2",getResources().getString(R.string.sms));
		DBR.put("3",getResources().getString(R.string.tel));
		DBR.put("4",getResources().getString(R.string.geoLoc));
		DBR.put("5",getResources().getString(R.string.plainText));
		DBR.put("6",getResources().getString(R.string.thesis));
		DBR.put("7",getResources().getString(R.string.report));
		
		

		datasource = new TagContentDataSource(this);

		Button button1 = (Button) findViewById(R.id.backupSave);
		button1.setOnClickListener(this);
		Button button2 = (Button) findViewById(R.id.backupShare);
		button2.setOnClickListener(this);
		myImage = (ImageView) findViewById(R.id.backupQRImage);
	
		datasource.open();
		List<TagContent> contents = datasource.getAllComments();
		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss")
		.format(new Date());
		String toCode = "Saved at "+ timeStamp+ "\n";

		for (TagContent tagContent : contents) {
			toCode += DBR.get(tagContent.getPayloadType())+" - "+ tagContent.getPayloadHeader()+tagContent.getPayload()+"\n";
		}
		datasource.close();
		// Find screen size
		WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		int width = point.x;
		int height = point.y;
		int smallerDimension = width < height ? width : height;
		smallerDimension = smallerDimension * 3 / 4;

		// Encode with a QR Code image
		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(toCode, null,
				Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
				smallerDimension);
		try {
			bitmap = qrCodeEncoder.encodeAsBitmap();

			myImage.setImageBitmap(bitmap);

		} catch (WriterException e) {
			e.printStackTrace();
		}

	
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.backup_data, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backupSave:
			storeImage(bitmap);
			break;
		case R.id.backupShare:

			break;
		default:
			break;
		}
	}

	private void storeImage(Bitmap image) {
		File pictureFile = getOutputMediaFile();
		Log.d(LOG_TAG, "After get file");
		if (pictureFile == null) {
			Log.d(LOG_TAG,
					"Error creating media file, check storage permissions: ");// e.getMessage());
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(LOG_TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(LOG_TAG, "Error accessing file: " + e.getMessage());
		}
	}

	private File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory() + "/"
						+ getApplicationContext().getPackageName() + "/Files");
		Log.d(LOG_TAG,
				"Storing path: " + Environment.getExternalStorageDirectory()
						+ "/");

		// getBaseContext().getExternalFilesDir(null)

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss")
				.format(new Date());
		File mediaFile;
		String mImageName = "MI_" + timeStamp + ".jpg";
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ mImageName);
		return mediaFile;
	}

}
