package com.example.proyecto;

import java.nio.charset.Charset;

import com.example.objetos.TagContent;
import com.example.objetos.TagContentDataSource;
import com.example.objetos.TagInfo;

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
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ReadTEG extends Activity {

	private NfcAdapter myNfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFiltersArray;
	private String[][] techListsArray;
	private TextView title;
	private TextView author;
	private TextView reference;
	private TextView url;
	private TagContentDataSource datasource;
	private TagInfo tInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_teg);
		
		datasource = new TagContentDataSource(this);
		
		title = (TextView)findViewById(R.id.rfieldTitle);
		author = (TextView)findViewById(R.id.rfieldAuthor);
		reference = (TextView)findViewById(R.id.rfieldRef);
		url = (TextView)findViewById(R.id.rFieldUrl);
		
		
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (myNfcAdapter == null) {
			//status.setText("NFC isn't available for the device");
		} else {
			//status.setText("NFC is available for the device");
		}
		
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Log.d("debug","NDEF Discovered");
			Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			
			// GET NDEF MESSAGE IN THE TAG
			NdefMessage[] messages = getNdefMessages(getIntent());
			
			// PROCESS NDEF MESSAGE
			String payload = null;
			byte payloadHeader ;
			
			for (int i = 0; i < messages.length; i++) {
				//status.append("Message "+(i+1)+" \n");
				for (int j = 0; j < messages[0].getRecords().length; j++) {
					NdefRecord record = messages[i].getRecords()[j];
					//status.append((j+1)+"th. Record Tnf: "+record.getTnf()+"\n");
					//status.append((j+1)+"th. Record type: "+record.getType()+"\n");
					//status.append((j+1)+"th. Record id: "+record.getId()+"\n");
					
					payload = new String(record.getPayload(), 1, record.getPayload().length-1, Charset.forName("UTF-8"));
					//status.append((j+1)+"th. Record payload:  "+payload +"\n");
					payloadHeader = record.getPayload()[0];
					//status.append((j+1)+"th. Record payload header:  "+payloadHeader +"\n");
				}
			}
			// DO WHATEVER WITH THE DATA
			
			tInfo = new TagInfo(detectedTag, getIntent(), this);
			
			Log.d("debug ReadTEG", "Payload: "+payload);
			
			title.setText(payload.substring(payload.indexOf(":") + 1,
					payload.indexOf("?")));
			author.setText(payload.substring(payload.indexOf("a=") + 2,
					payload.indexOf("&")));
			reference.setText(payload.substring(payload.indexOf("r=") + 2,
					payload.lastIndexOf("&")));
			url.setText(payload.substring(payload.indexOf("u=") + 2,
					payload.length()));
			
		}
		else{
			Log.d("debug", "Nothing detected");
		}
		
		
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
		getMenuInflater().inflate(R.menu.read_teg, menu);
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

	public void onClick(View v){
		
		switch (v.getId()) {
		case R.id.shareButton:
			String pLoad= "";
			pLoad += title.getText().toString() + ". By: " + author.getText().toString() + " Ref: " + reference.getText().toString() + ". - "+ url.getText().toString() ;
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, pLoad);
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			break;
		case R.id.rsaveButton:
			 TagContent content = null;
			 datasource.open();

		      // save the new tag_content to the database
		    String payloadHeaderDesc = tInfo.getTagRecords().get(0).isWOP() ? " " : tInfo.getTagRecords().get(0).getRecordPayloadHeaderDesc();
		    Log.d("debug TEG",tInfo.getTagRecords().get(0).getRecordPayload());
		    Log.d("debug TEG",tInfo.getTagRecords().get(0).isWOP()+"");
		    Log.d("debug TEG",tInfo.getTagRecords().get(0).getRecordPayloadheader()+"");
		    Log.d("debug TEG",tInfo.getTagRecords().get(0).getRecordPayloadHeaderDesc());
		    //Log.d("debug TEG",tInfo.getTagRecords().get(0).getRecordPayloadTypeDesc());
		      content = datasource.createContent(tInfo.getTagRecords().get(0).getRecordPayload(),
		    		  payloadHeaderDesc,
		    		  tInfo.getTagRecords().get(0).getRecordPayloadTypeDesc());
		      
		      	
		      if (content != null) {
		    	    Toast.makeText(this, "Tag content saved!", Toast.LENGTH_SHORT).show();

			      	Intent intent = new Intent(this, SaveResult.class);
					intent.putExtra("CONTENT_ID", content.getId());
					intent.putExtra("CONTENT_EDIT", "NEW");
					startActivity(intent);
			}else{
				 Toast.makeText(this, "Tag content already saved!", Toast.LENGTH_SHORT).show();
			}
		      
		      
			break;

		default:
			break;
		}
		
		
	}
}
