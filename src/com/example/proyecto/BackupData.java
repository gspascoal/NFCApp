package com.example.proyecto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.QR.Contents;
import com.example.QR.QRCodeEncoder;
import com.example.objetos.AddTagLayout;
import com.example.objetos.ContentTag;
import com.example.objetos.ShareAllLayout;
import com.example.objetos.TagContent;
import com.example.objetos.TagContentDataSource;
import com.example.proyecto.R.id;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class BackupData extends Activity implements OnClickListener {

	private ImageView myImage;
	private TagContentDataSource datasource;
	public Map<String, String> DBR = new LinkedHashMap<String, String>(); // DataBaseResource
	public Map<String, Integer> PLTI = new LinkedHashMap<String, Integer>();
	private String LOG_TAG = "GenerateQRCode";
	private Bitmap bitmap;
	private String toCode;
	private String contenId;
	private TextView cDescription;
	private TextView cPayload;
	private ImageView cIcon;
	private TextView cId;
	private TagContent nTagContent;
	private TextView bkQRST;
	private TextView bkQRT;
	private LinearLayout savedQRDataLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup_data);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		DBR.put("0", getResources().getString(R.string.link));
		DBR.put("1", getResources().getString(R.string.mail));
		DBR.put("2", getResources().getString(R.string.sms));
		DBR.put("3", getResources().getString(R.string.tel));
		DBR.put("4", getResources().getString(R.string.geoLoc));
		DBR.put("5", getResources().getString(R.string.plainText));
		DBR.put("6", getResources().getString(R.string.thesis));
		DBR.put("7", getResources().getString(R.string.report));
		
		
		PLTI.put(getResources().getString(R.string.nA), R.drawable.default64);
		PLTI.put(getResources().getString(R.string.link), R.drawable.link64);
		PLTI.put(getResources().getString(R.string.link), R.drawable.link64);
		PLTI.put(getResources().getString(R.string.tel), R.drawable.tel64);
		PLTI.put(getResources().getString(R.string.mail), R.drawable.mail64);
		PLTI.put(getResources().getString(R.string.sms), R.drawable.sms64);
		PLTI.put(getResources().getString(R.string.geoLoc), R.drawable.geo64);
		PLTI.put(getResources().getString(R.string.bussinesCard), R.drawable.business_cardb24);
		PLTI.put(getResources().getString(R.string.plainText), R.drawable.text64);
		PLTI.put(getResources().getString(R.string.thesis), R.drawable.thesis64);
		PLTI.put(getResources().getString(R.string.report), R.drawable.default64);

		datasource = new TagContentDataSource(this);
		
		toCode = "NFCTag (c) 2015 \n";
				
		bkQRT = (TextView) findViewById(R.id.backupQRT);
		bkQRST = (TextView) findViewById(R.id.backupQRST);
		Button button1 = (Button) findViewById(R.id.backupSave);
		button1.setOnClickListener(this);
		Button button2 = (Button) findViewById(R.id.backupShare);
		button2.setOnClickListener(this);
		
		savedQRDataLayout = (LinearLayout)findViewById(R.id.savedQRContent);
		
		myImage = (ImageView) findViewById(R.id.backupQRImage);
		cDescription = (TextView) findViewById(R.id.contentDescription);
		cPayload = (TextView) findViewById(R.id.contentPayload);
		cIcon = (ImageView) findViewById(R.id.contentIcon);
		cId = (TextView) findViewById(R.id.contentId);
		
		
		if (getIntent().getStringExtra("CONTENT_ID") != null) {
			bkQRT.setVisibility(View.VISIBLE);
			savedQRDataLayout.setVisibility(View.VISIBLE);
			
			datasource.open();
			contenId = getIntent().getStringExtra("CONTENT_ID");
			nTagContent = datasource.getContentById(contenId);
			
			cIcon.setBackgroundResource(PLTI.get(DBR.get(nTagContent.getPayloadType())));
			cDescription.setText(DBR.get(nTagContent.getPayloadType()));
			cPayload.setText(nTagContent.getPayloadHeader()+nTagContent.getPayload());
			cId.setText(String.valueOf(nTagContent.getId()));
			datasource.close();
			
			bkQRST.setText(getResources().getString(R.string.bu_title3));
			toCode += nTagContent.getPayloadType() + " Type: " +DBR.get(nTagContent.getPayloadType()) 
					 + " Header: " + nTagContent.getPayloadHeader() + " Content: " + nTagContent.getPayload();
			
		}
		else{
			datasource.open();
			List<TagContent> contents = datasource.getAllComments();
			String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss")
					.format(new Date());
			toCode += "Saved at " + timeStamp + "\n";

			for (TagContent tagContent : contents) {
				toCode +=  "Item: "+ tagContent.getPayloadType() + " Type: "+DBR.get(tagContent.getPayloadType()) + " Header: "
						+ tagContent.getPayloadHeader() + " Content: " + tagContent.getPayload()
						+ "\n";
			}
			datasource.close();
			
			
		}
		
		
		
		// Find screen size
		WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		int width = point.x;
		int height = point.y;
		int smallerDimension = width < height ? width : height;
		smallerDimension = smallerDimension * 3 / 4;

		Log.d("toCode text", toCode);
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
		//getMenuInflater().inflate(R.menu.backup_data, menu);
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
			final ShareAllLayout shareAllLayout = new ShareAllLayout(this);
			final CustomDialog dialogShare = new CustomDialog(this);
			
			shareAllLayout.getRadioGroup().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
			shareAllLayout.getNegative().setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO
							// Auto-generated method stub

							dialogShare.dismiss();
						}
					});

			shareAllLayout.getPositive().setOnClickListener(
					new View.OnClickListener() {

						private int selectedCount;

						@Override
						public void onClick(View v) {
							// TODO
							// Auto-generated method stub
							
							if (shareAllLayout.getAsText().isChecked()) {
								Intent sendIntent = new Intent();
								sendIntent.setAction(Intent.ACTION_SEND);
								sendIntent.putExtra(Intent.EXTRA_TEXT,toCode);
								sendIntent.setType("text/plain");
								startActivity(sendIntent);
							}
							if (shareAllLayout.getAsImage().isChecked()) {
								
								 Intent share = new Intent(Intent.ACTION_SEND);
								 share.setType("image/jpeg");
								 
								 ContentValues values = new ContentValues();
								 values.put(Images.Media.TITLE, "title");
								 values.put(Images.Media.MIME_TYPE, "image/jpeg"); Uri uri =
								 getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
								 
								 OutputStream outstream; try { outstream =
								 getContentResolver().openOutputStream(uri);
								 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
								 outstream.close(); } catch (Exception e) {
								 System.err.println(e.toString()); }
								  
								 share.putExtra(Intent.EXTRA_STREAM, uri);
								 startActivity(Intent.createChooser(share, getResources().getString(R.string.bu_shareChooser))); 
								 
							}
							


							dialogShare.dismiss();

						}
					});
			
			dialogShare.setTitle(getResources().getString(R.string.bu_dialogTitle));
			dialogShare.setContentView(shareAllLayout);
			dialogShare.show();
			
			
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
			Toast.makeText(this, getResources().getString(R.string.bu_savePath) + pictureFile.getPath(),
					Toast.LENGTH_LONG).show();
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
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"
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
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this, ExtrasMain.class);
		startActivity(intent);
		//this.finish();
	}
}
