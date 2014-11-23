package com.example.proyecto;

import java.nio.charset.Charset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReadMain extends Activity {

	private NfcAdapter myNfcAdapter;
	private TextView status;
	private String[][] techListsArray;
	private IntentFilter[] intentFiltersArray;
	private PendingIntent pendingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_main);
		status = (TextView) findViewById(R.id.status);
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (myNfcAdapter == null) {
			status.setText("NFC isn't available for the device");
		} else {
			status.setText("NFC is available for the device");
		}
		
		/*
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Log.d("debug","NDEF Discovered");
			Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			
			// GET NDEF MESSAGE IN THE TAG
			NdefMessage[] messages = getNdefMessages(getIntent());
			
			// PROCESS NDEF MESSAGE
			String payload = null;
			byte payloadHeader ;
			
			for (int i = 0; i < messages.length; i++) {
				status.append("Message "+(i+1)+" \n");
				for (int j = 0; j < messages[0].getRecords().length; j++) {
					NdefRecord record = messages[i].getRecords()[j];
					status.append((j+1)+"th. Record Tnf: "+record.getTnf()+"\n");
					status.append((j+1)+"th. Record type: "+record.getType()+"\n");
					status.append((j+1)+"th. Record id: "+record.getId()+"\n");
					
					payload = new String(record.getPayload(), 1, record.getPayload().length-1, Charset.forName("UTF-8"));
					status.append((j+1)+"th. Record payload:  "+payload +"\n");
					payloadHeader = record.getPayload()[0];
					status.append((j+1)+"th. Record payload header:  "+payloadHeader +"\n");
				}
			}
			// DO WHATEVER WITH THE DATA
			
			
		}
		else{
			Log.d("debug", "Nothing detected");
		}
		*/
		
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
	   
	    techListsArray = new String[][] { new String[] { NfcA.class.getName() , 
	    		Ndef.class.getName(), 
	    		MifareUltralight.class.getName() } };
		
		/*
		if (savedInstanceState == null) {
			/*getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
	}

	private NdefMessage[] getNdefMessages(Intent intent) {
		// TODO Auto-generated method stub
		NdefMessage[] message = null;
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Log.d("", "I found some shit.");
			Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMessages != null) {
				message = new NdefMessage[rawMessages.length];
				for (int i = 0; i < rawMessages.length; i++) {
					message[i] = (NdefMessage) rawMessages[i];
				}
			} else {
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
				NdefMessage msgMessage = new NdefMessage(new NdefRecord[] {record});
			}
		}
		else {
			Log.d("", "Unknow intent.");
			finish();
		}
		return message;
	}

	
	public void onPause() {
	    super.onPause();
	   myNfcAdapter.disableForegroundDispatch(this);
	}

	public void onResume() {
	    super.onResume();
	   myNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
	}

	public void onNewIntent(Intent intent) {
	    Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    //do something with tagFromIntent
	    Log.d("debug", "tag detected");
		 // GET NDEF MESSAGE IN THE TAG
		 NdefMessage[] messages = getNdefMessages(intent);
		 
		 // PROCESS NDEF MESSAGE
		 String payload = null;
		 byte payloadHeader = 0x00;
		 if (messages != null) {
			 for (int i = 0; i < messages.length; i++) {
				 status.append("\n");
				 	status.append("Message "+(i+1)+" \n");
				 	for (int j = 0; j < messages[0].getRecords().length; j++) {
				 		NdefRecord record = messages[i].getRecords()[j];
				 		status.append((j+1)+"th. Record Tnf: "+record.getTnf() +"\n");
				 		status.append((j+1)+"th. Record type: "+record.getType()+"\n");
				 		status.append((j+1)+"th. Record id: "+record.getId()+"\n");
				 		status.append((j+1)+"th. Record Contents: "+record.describeContents()+"\n");
				 		
				 		
				 		payload = new String(record.getPayload(), 1, record.getPayload().length-1, Charset.forName("UTF-8"));
				 		status.append((j+1)+"th. Record payload:  "+payload +"\n");
				 		payloadHeader = record.getPayload()[0];
				 		status.append((j+1)+"th. Record payload header:  "+payloadHeader +"\n");
				 	}
				 }
		
			 if (payloadHeader == 0x01) {
					Intent dataIntent = new Intent();
					dataIntent.setAction(Intent.ACTION_VIEW);
					dataIntent.setData(Uri.parse("http://"+payload));
					try {
						Log.d("debug", "Yes, I did it");
						startActivity(dataIntent);
						
					} catch (ActivityNotFoundException e) {
						// TODO: handle exception
						
						return;
					}
				}
		 
		 }
		 else {
			Log.d("debug", "that shit is so empty");
		}
		 
	    /*
		 if (payloadHeader == 0x01) {
			Intent dataIntent = new Intent();
			dataIntent.setAction(Intent.ACTION_VIEW);
			dataIntent.setData(Uri.parse("http://"+payload));
			try {
				startActivity(dataIntent);
				Log.d("debug", "Yes, I did it");
			} catch (ActivityNotFoundException e) {
				// TODO: handle exception
				return;
			}
		}*/
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.read_main, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_read_main,
					container, false);
			return rootView;
		}
	}

}
