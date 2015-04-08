package com.example.proyecto;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.google.zxing.common.HybridBinarizer;

public class RestoreData extends Activity implements OnClickListener{

	private Bitmap bitmap;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restore_data);
		//getActionBar().setDisplayHomeAsUpEnabled(true);

		Button button1 = (Button) findViewById(R.id.restoreOp1);
		button1.setOnClickListener(this);
		Button button2 = (Button) findViewById(R.id.restoreOp2);
		button2.setOnClickListener(this);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.restore_data, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_restore_data,
					container, false);
			return rootView;
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.restoreOp1: // Take photo
			Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			//i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivityForResult(i,123);

			
			break;
		case R.id.restoreOp2: // Open photo
			
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
			Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/"+ getApplicationContext().getPackageName() + "/Files");
			intent.setDataAndType(uri, "image/*");
			//intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivityForResult(Intent.createChooser(intent, "Select image"),234); 
			
			break;

		default:
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String content;
		if (resultCode == RESULT_OK ) {
			if (requestCode == 123 ) {
				Bundle extras = data.getExtras();
				bitmap = (Bitmap) extras.get("data");
				//myImage.setImageBitmap(bitmap);
				content = decode();
				
				Intent intent = new Intent(this, RestoreResults.class);
				intent.putExtra("IMAGE_CONTENT", content);
				startActivity(intent);
				finish();
			}
			
			if (requestCode == 234) {
				Uri selectedImage = data.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	          try {
				bitmap = decodeUri(selectedImage);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	          content = decode();
				
				Intent intent = new Intent(this, RestoreResults.class);
				intent.putExtra("IMAGE_CONTENT", content);
				startActivity(intent);
				finish();
				
			}
			
		}
		/*if (resultCode == RESULT_CANCELED) {
			Intent intent = new Intent(this, ExtrasMain.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		}*/
		

	}
	
	private String decode() {
		String text = "No data";
		if (bitmap == null) {
			Log.i("tag", "wtf");
		}
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0,
				bitmap.getWidth(), bitmap.getHeight());

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
			// textv.setText(text);
			Log.v("debug tag", text);

		} catch (NotFoundException e) {

			e.printStackTrace();
		} catch (ChecksumException e) {

			e.printStackTrace();
		} catch (FormatException e) {

			e.printStackTrace();

		}
		Log.i("done", "done");
		if (text != null ){
			Log.d("toCode text", text);
			if (text.contains("NFCTag")) {
				return text;
			}
			else{
				return "The QR Code was not generated by this application.";
			}
				
		//Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
		}	
		else {
			return "Content not found";
			//Toast.makeText(getBaseContext(), "QQ", Toast.LENGTH_LONG).show();
		}
	}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
               || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this, ExtrasMain.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
		//this.finish();
	}
}
