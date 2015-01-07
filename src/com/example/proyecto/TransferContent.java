package com.example.proyecto;

import java.nio.charset.Charset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class TransferContent extends Activity {

	private NfcAdapter myNfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFiltersArray;
	private String[][] techListsArray;
	private CustomDialog dialog;
	private TextView writeResult;
	private NdefMessage content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_content);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		writeResult = (TextView)findViewById(R.id.writeResult);
		
		content = getIntent().getParcelableExtra("TAG_CONTENT");
		
		dialog = new CustomDialog(this);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.write_tag_dialog);
		
		dialog.show();
		
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		pendingIntent = PendingIntent.getActivity(
			    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	   
		try {
	       ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
	         //                             You should specify only the ones that you need. */
	       // ndef.addDataScheme("http");
	       //ndef.addAction(Intent.ACTION_VIEW);
	    }
	                                       
	    catch (MalformedMimeTypeException e) {
	        throw new RuntimeException("fail", e);
	    }
	    intentFiltersArray = new IntentFilter[] {ndef, };
	    
	    techListsArray = new String[][] { new String[] {  NfcA.class.getName() , 
	    		Ndef.class.getName()}, 
	    		{MifareUltralight.class.getName() } };
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transfer_content, menu);
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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		myNfcAdapter.disableForegroundDispatch(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		myNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
	}
	
	
	public void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		//super.onNewIntent(intent);
		
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		
		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef ndef = Ndef.get(detectedTag);
	    
	    //do something with tagFromIntent
	    byte[] uriField =  "dell.com".getBytes(Charset.forName("UTF-8"));
		byte[] payload = new byte[uriField.length + 1];
		payload[0] = 0x01; //Code for http://www.
		System.arraycopy(uriField, 0, payload, 1, uriField.length);
		NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
		NdefMessage newMessage = new NdefMessage(new NdefRecord[] {uriRecord});
		
		
		// WRITE DATA TO TAG
		Log.d("debug", "Starting writing process");
		boolean result = writeNdefMessageToTag(content, detectedTag );
		if (result) {
			writeResult.setText("Write succesful");
		} else {
			writeResult.setText("Write failure");
		} 
	}
	
	private boolean writeNdefMessageToTag(NdefMessage message, Tag detectedTag) {
		// TODO Auto-generated method stub
		
		int size = message.toByteArray().length;
		
		Log.d("debug", "Before TRY");
		try {
			Ndef ndef = Ndef.get(detectedTag);
			if (ndef != null) {
				ndef.connect();
				Log.d("debug", "After Connect");
				if (!ndef.isWritable()) {
					Toast.makeText(this, "Tag is read-only", Toast.LENGTH_SHORT).show();
					return false;
				}
				if (ndef.getMaxSize() < size) {
					Toast.makeText(this, "Tag data can't written to tag, Tag capacity is "+ ndef.getMaxSize() + "bytes, message is"
							+ size + " bytes."
				, Toast.LENGTH_SHORT).show();
					return false;
				}
				ndef.writeNdefMessage(message);
				ndef.close();
				Toast.makeText(this, "Message is written tag", Toast.LENGTH_SHORT).show();
				return true;
			} else {
				NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
				if (ndefFormat != null) {
					try {
						ndefFormat.connect();
						ndefFormat.format(message);
						ndefFormat.close();
						Toast.makeText(this, "The data is written to teh tag", Toast.LENGTH_SHORT).show();
						return true;
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(this, "Failed to format tag", Toast.LENGTH_SHORT).show();
						return false;
					}
					
				} else {
					Toast.makeText(this, "NDEF is not supported", Toast.LENGTH_SHORT).show();
					return false;
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("debug", "Exception: "+e.toString());
			Toast.makeText(this, "Write operation is failed", Toast.LENGTH_SHORT).show();
			return false;
		}
		
	}
}
