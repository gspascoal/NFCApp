package com.example.proyecto;

import com.example.proyecto.R;

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
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class EraseTag extends Activity {

	private CustomDialog dialog;
	private NfcAdapter myNfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFiltersArray;
	private String[][] techListsArray;
	private String writeMessage;
	private TextView eraseMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_erase_tag);
		dialog = new CustomDialog(this);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.erase_tag_dialog);
		
		
		eraseMessage = (TextView)findViewById(R.id.eraseResult);
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		dialog.show();
		
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
		getMenuInflater().inflate(R.menu.erase_tag, menu);
		return true;
	}

	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.eraseDone:
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
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
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		//super.onNewIntent(intent);
	
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		
		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef ndef = Ndef.get(detectedTag);
		
		NdefMessage msgUNK = new NdefMessage(new NdefRecord[]{
				new NdefRecord(NdefRecord.TNF_UNKNOWN, null, null, new byte[ndef.getMaxSize() - 6])});
		NdefMessage msgEMP = new NdefMessage(new NdefRecord[]{
				new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)});
		
		writeNdefMessageToTag(msgUNK, detectedTag);
		writeNdefMessageToTag(msgEMP, detectedTag);
		
		eraseMessage.setText(writeMessage);
		
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
					Toast.makeText(this, "Tag is read-only", Toast.LENGTH_SHORT)
							.show();
					writeMessage = "Tag is read-only";
					return false;
				}
				if (ndef.getMaxSize() < size) {
					Toast.makeText(
							this,
							"Tag data can't written to tag, Tag capacity is "
									+ ndef.getMaxSize() + "bytes, message is"
									+ size + " bytes.", Toast.LENGTH_SHORT)
							.show();
					Log.d("debug erase", "Tag data can't written to tag, Tag capacity is "
									+ ndef.getMaxSize() + "bytes, message is"
									+ size + " bytes.");
					writeMessage = "Tag data can't written to tag, Tag capacity is "
							+ ndef.getMaxSize()
							+ "bytes, message is"
							+ size
							+ " bytes.";
					return false;
				}
				ndef.writeNdefMessage(message);
				ndef.close();
				Toast.makeText(this, "Message is written tag",
						Toast.LENGTH_SHORT).show();
				writeMessage = "Message is written tag";
				return true;
			} else {
				NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
				if (ndefFormat != null) {
					try {
						ndefFormat.connect();
						ndefFormat.format(message);
						ndefFormat.close();
						Toast.makeText(this, "The data is written to the tag",
								Toast.LENGTH_SHORT).show();
						writeMessage = "The data is written to the tag";
						return true;
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(this, "Failed to format tag",
								Toast.LENGTH_SHORT).show();
						writeMessage = "Failed to format tag";
						return false;
					}

				} else {
					Toast.makeText(this, "NDEF is not supported",
							Toast.LENGTH_SHORT).show();
					writeMessage = "NDEF is not supported";
					return false;
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("debug", "Exception: " + e.toString());
			Toast.makeText(this, "Write operation is failed",
					Toast.LENGTH_SHORT).show();
			return false;
		}

	}


}
