package com.example.proyecto;

import java.nio.charset.Charset;

import com.example.objetos.TagContentDataSource;
import com.example.objetos.TagInfo;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.net.VpnService;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
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
	private TagInfo tInfo;
	private RelativeLayout pContent;
	private RelativeLayout cContent;
	private RelativeLayout rContainer;
	private RelativeLayout container;
	private TagContentDataSource datasource; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_content);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		writeResult = (TextView)findViewById(R.id.writeResult);
		pContent = (RelativeLayout)findViewById(R.id.pContent_container);
		cContent = (RelativeLayout)findViewById(R.id.cContent_container);
		rContainer = (RelativeLayout)findViewById(R.id.resultContainer);
		container = (RelativeLayout)findViewById(R.id.main_container);
		
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
	    
	    datasource = new TagContentDataSource(this);
	    datasource.open();
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
		datasource.open();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		myNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
		datasource.open();
	}
	
	public void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		//super.onNewIntent(intent);
		
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		
		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef ndef = Ndef.get(detectedTag);
	    
		
	    
	    tInfo = new TagInfo(detectedTag, intent, this);
	    NdefMessage[] messages = getNdefMessages(intent);
		
	    container.setVisibility(View.VISIBLE);
	    rContainer.setVisibility(View.VISIBLE);
		
		// WRITE DATA TO TAG
		Log.d("debug", "Starting writing process");
		boolean result = writeNdefMessageToTag(content, detectedTag );
		if (result) {
			writeResult.setText("Write succesful");
			
			cContent.addView(datasource.getTagUIContents().get(datasource.getTagUIContents().size()-1));			
			if (messages != null) {
				TagUIContent nTagUIContent = new TagUIContent(this);
				  nTagUIContent.setPayload(tInfo.getTagRecords().get(0).getRecordPayload());
				  nTagUIContent.setContentDesc(tInfo.getTagRecords().get(0).getRecordPayloadTypeDesc());
				  nTagUIContent.setContentIcon(tInfo.getTagRecords().get(0).getRecordPayloadTypeDesc());
				  nTagUIContent.setContentId(String.valueOf(-1));
				pContent.addView(nTagUIContent);
			}
		} else {
			writeResult.setText("Write failure");
		} 
	}
	
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.doneButton:
			Intent intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			break;

		default:
			break;
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
	
	private NdefMessage[] getNdefMessages(Intent intent) {
		// TODO Auto-generated method stub
		NdefMessage[] message = null;
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Log.d("debug", "I found some shit.");
			Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMessages != null) {
				message = new NdefMessage[rawMessages.length];
				for (int i = 0; i < rawMessages.length; i++) {
					message[i] = (NdefMessage) rawMessages[i];
				}
			} else {
				Log.d("debug", "0 Ndef Messages.");
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
				message = new NdefMessage[] {msg};
			}
		}
		else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Log.d("debug", "NDEF intent.");
			Log.d("debug", "I found some shit.");
			Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMessages != null) {
				message = new NdefMessage[rawMessages.length];
				for (int i = 0; i < rawMessages.length; i++) {
					message[i] = (NdefMessage) rawMessages[i];
				}
			} else {
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
				message = new NdefMessage[] {msg};
			}
		}
		else {
			Log.d("debug", "Unknow intent.");
			finish();
		}
		return message;
	}
}
