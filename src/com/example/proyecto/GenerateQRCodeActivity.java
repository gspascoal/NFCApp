package com.example.proyecto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.QR.Contents;
import com.example.QR.QRCodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;




public class GenerateQRCodeActivity extends Activity implements OnClickListener {
	
	private static final int SCANNER_REQUEST_CODE = 123;

	ImageView myImage;
	
	private String LOG_TAG = "GenerateQRCode";

	private Bitmap bitmap;

	private String text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_qrcode);

		  Button button1 = (Button) findViewById(R.id.button1);
		  button1.setOnClickListener(this);
		  Button button2 = (Button) findViewById(R.id.button2);
		  button2.setOnClickListener(this);
		  myImage = (ImageView) findViewById(R.id.imageView1);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.generate_qrcode, menu);
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

	 public void onClick(View v) {
		 Log.v("debug tag", v.getId()+"");
		  switch (v.getId()) {
		  case R.id.button1:
		   EditText qrInput = (EditText) findViewById(R.id.qrInput);
		   String qrInputText = qrInput.getText().toString();
		   Log.v(LOG_TAG, qrInputText);
		 
		   //Find screen size
		   WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		   Display display = manager.getDefaultDisplay();
		   Point point = new Point();
		   display.getSize(point);
		   int width = point.x;
		   int height = point.y;
		   int smallerDimension = width < height ? width : height;
		   smallerDimension = smallerDimension * 3/4;
		 
		   //Encode with a QR Code image
		   QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText, 
		             null, 
		             Contents.Type.TEXT,  
		             BarcodeFormat.QR_CODE.toString(), 
		             smallerDimension);
		   try {
		   bitmap = qrCodeEncoder.encodeAsBitmap();
		    
		    myImage.setImageBitmap(bitmap);
		 
		   } catch (WriterException e) {
		    e.printStackTrace();
		   }
		 
		 
		   break;
		  case R.id.button2:
			  /*
			  storeImage(bitmap);
			  Log.v("debug tag", "on click button");
			  */
			  
			  /*
			  Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	            intent.putExtra("SCAN_MODE", "SCAN_MODE");
	            startActivityForResult(intent, SCANNER_REQUEST_CODE);
	
			  */
			  
			  Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		        startActivityForResult(i, 0);
			  
			  break;
		   // More buttons go here (if any) ...
		 
		  }
		 }
	 
	
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		 super.onActivityResult(requestCode, resultCode, data);
	        if (resultCode == RESULT_OK) {
	            Bundle extras = data.getExtras();
	            bitmap = (Bitmap) extras.get("data");
	            myImage.setImageBitmap(bitmap);
	            decode();
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
	 
	 private  File getOutputMediaFile(){
		    // To be safe, you should check that the SDCard is mounted
		    // using Environment.getExternalStorageState() before doing this. 
		    File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
		    		+"/"+getApplicationContext().getPackageName()
		    		+"/Files"); 
		   Log.d(LOG_TAG, "Storing path: " + Environment.getExternalStorageDirectory()
				   +"/");
		   
		   	//getBaseContext().getExternalFilesDir(null)
		   
		   
		    // This location works best if you want the created images to be shared
		    // between applications and persist after your app has been uninstalled.

		    // Create the storage directory if it does not exist
		    if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            return null;
		        }
		    } 
		    // Create a media file name
		    String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
		    File mediaFile;
		        String mImageName="MI_"+ timeStamp +".jpg";
		        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);  
		    return mediaFile;
		} 

	   private void decode() {


	        if (bitmap == null) {
	            Log.i("tag", "wtf");
	        }
	        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

	        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
	        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
	                bitmap.getHeight());

	        LuminanceSource source = new com.google.zxing.RGBLuminanceSource(
	                bitmap.getWidth(), bitmap.getHeight(), intArray);
	        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	        Reader reader = new MultiFormatReader();
	        try {
	            Result result = reader.decode(bitmap);

	            text = result.getText();
	            byte[] rawBytes = result.getRawBytes();
	            BarcodeFormat format = result.getBarcodeFormat();
	            ResultPoint[] points = result.getResultPoints();
	            //textv.setText(text);
	            Log.v("debug tag", text);

	        } catch (NotFoundException e) {

	            e.printStackTrace();
	        } catch (ChecksumException e) {

	            e.printStackTrace();
	        } catch (FormatException e) {

	            e.printStackTrace();

	        }
	        Log.i("done", "done");
	        if(text!=null)
	        Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
	        else{
	            Toast.makeText(getBaseContext(), "QQ", Toast.LENGTH_LONG).show();
	        }
	    }
	 
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_generate_qrcode,
					container, false);
			return rootView;
		}
	}

}
