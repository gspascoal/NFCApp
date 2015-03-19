package com.example.proyecto;

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

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class RestoreData extends Activity implements OnClickListener{

	private Bitmap bitmap;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restore_data);
		

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
		getMenuInflater().inflate(R.menu.restore_data, menu);
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
			Intent i = new Intent(
		    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, 0);

			
			break;
		case R.id.restoreOp2: // Open photo
			
			
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
		if (resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			bitmap = (Bitmap) extras.get("data");
			//myImage.setImageBitmap(bitmap);
			content = decode();
			
			Intent intent = new Intent(this, RestoreResults.class);
			intent.putExtra("IMAGE_CONTENT", content);
			startActivity(intent);
		}

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
		if (text != null)
			return text;
			//Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
		else {
			return "Content not found";
			//Toast.makeText(getBaseContext(), "QQ", Toast.LENGTH_LONG).show();
		}
	}

	
}
