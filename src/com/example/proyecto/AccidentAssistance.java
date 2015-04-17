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
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.objetos.TagInfo;

public class AccidentAssistance extends Activity {

	private TextView footer;
	private NfcAdapter myNfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFiltersArray;
	private String[][] techListsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accident_assistance);
		
		footer = (TextView)findViewById(R.id.assistanceFooter);
		
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
			//NdefMessage[] messages = getNdefMessages(getIntent());
			
			// PROCESS NDEF MESSAGE
			String payload = null;
			byte payloadHeader ;
			/*
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
			tutor.setText(payload.substring(payload.indexOf("t=") + 2,
					payload.indexOf("&s")));
			reference.setText(payload.substring(payload.indexOf("s=") + 2,
					payload.indexOf("&y")));
			year.setText(payload.substring(payload.indexOf("y=") + 2,
					payload.lastIndexOf("&")));
			url.setText(payload.substring(payload.indexOf("u=") + 2,
					payload.length()));
			*/
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
		getMenuInflater().inflate(R.menu.accident_assistance, menu);
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
	
	public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	        // Enable vibrate
	    	footer.setVisibility(View.VISIBLE);
	    } else {
	        // Disable vibrate
	    	footer.setVisibility(View.INVISIBLE);
	    }
	}
}
