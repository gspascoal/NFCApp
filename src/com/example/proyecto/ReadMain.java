package com.example.proyecto;

import java.nio.charset.Charset;

import com.example.objetos.TagInfo;

import android.app.Activity;
import android.app.Dialog;
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
import android.provider.Settings.System;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReadMain extends Activity {

	private NfcAdapter myNfcAdapter;
	private TextView status;
	private String[][] techListsArray;
	private IntentFilter[] intentFiltersArray;
	private PendingIntent pendingIntent;
	private CustomDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		/*Setting up the dialog*/
		dialog = new CustomDialog(this);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.read_tag_dialog);
		
		
		dialog.show();
		
		
		status = (TextView) findViewById(R.id.status);
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (myNfcAdapter == null) {
			status.setText("NFC isn't available for the device");
		} else {
			status.setText("NFC is available for the device");
		}
		
		
		 // CHECK THIS METHOD
		 //FILTERING INTENT
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
		
		/*
		if (savedInstanceState == null) {
			/*getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
	}

	public void onPause() {
	    super.onPause();
	   myNfcAdapter.disableForegroundDispatch(this);
	}

	public void onResume() {
	    super.onResume();
	   myNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
	}

	public void onNewIntent(Intent intent)
	{
		//Check if the dialog is showing
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		
		
		
		//status.setText("");
		
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    Ndef ndef = Ndef.get(tagFromIntent);
	    
	    NdefMessage[] messages = getNdefMessages(intent);
	    
	    TagInfo tInfo = new TagInfo(tagFromIntent, intent);
	    Log.d("TagInfo", tInfo.getTagId());
	    Log.d("TagInfo", tInfo.getTagType());
	    Log.d("TagInfo", tInfo.getTagTechList());
	    Log.d("TagInfo", tInfo.getCanBeReadOnly() ? "Can be made RO" : "Can't be made RO");
	    Log.d("TagInfo", tInfo.getIsWritable() ? "Writable" : "No writable");
	    Log.d("TagInfo", String.valueOf(tInfo.getTagSize()) + " bytes");
	    Log.d("TagInfo", String.valueOf(tInfo.getInUse()) + " bytes");
	    Log.d("TagInfo", String.valueOf(tInfo.getMessages())+ " Message(s)");
	    Log.d("TagInfo", "First Record's TNF: "+tInfo.getTagRecords().get(0).getRecordTNF());
	    Log.d("TagInfo", "First Record's TNF description: "+tInfo.getTagRecords().get(0).getRecordTNFDesc());
	    Log.d("TagInfo", "First Record's Type: "+tInfo.getTagRecords().get(0).getRecordType());
	    Log.d("TagInfo", "First Record's Payload Type description: "+tInfo.getTagRecords().get(0).getRecordPayloadTypeDesc());
	    Log.d("TagInfo", "First Record's Payload: "+tInfo.getTagRecords().get(0).getRecordPayload());
	    Log.d("TagInfo", String.valueOf(tInfo.getMessages())+ " Message(s)");
	    Log.d("TagInfo", "Second Record's TNF: "+tInfo.getTagRecords().get(1).getRecordTNF());
	    Log.d("TagInfo", "Second Record's TNF description: "+tInfo.getTagRecords().get(1).getRecordTNFDesc());
	    Log.d("TagInfo", "Second Record's Type: "+tInfo.getTagRecords().get(1).getRecordType());
	    Log.d("TagInfo", "Second Record's Payload Type description: "+tInfo.getTagRecords().get(1).getRecordPayloadTypeDesc());
	    Log.d("TagInfo", "Second Record's Payload: "+tInfo.getTagRecords().get(1).getRecordPayload());
	    
	    
	    
	    
	    
	    //do something with tagFromIntent
	    Log.d("debug", "tag detected");
		 // GET NDEF MESSAGE IN THE TAG
		 
		status.append("\n");
		
		/*Checking if can be made Read-Only */
	    String cbmro= ndef.canMakeReadOnly() ? "Yes" : "No";
	    status.append("Can be made Read-Only?: "+  cbmro + "\n" );
	   
	    /*Checking Memory status */
	    status.append("Storage: \nTotal: " + ndef.getMaxSize() 
	    		+ "\nIn use: " +  this.inUse(messages)
	    		+ "\nFree: " + String.valueOf(ndef.getMaxSize() - this.inUse(messages)) + "\n");
	    
	    /*Checking if is writable */
	    String wrtbl = ndef.isWritable() ? "Yes" : "No";
	    status.append("Writtable?: "+  wrtbl + "\n" );
	    
	    /*Getting the Tag Type or Class*/
	    status.append("Type: "+ ndef.getType());
	    status.append("\n"); 
	    
	    /*Getting the Tag's ID or Serial Number */
	    status.append("ID or Serial Number: ");
	    byte[] tagId = tagFromIntent.getId();
	    for (int i = 0; i < tagId.length; i++) {
			status.append(Integer.toHexString(tagId[i]& 0xFF));
			if (i < tagId.length-1) {
				status.append(":");
			}
		}
	    status.append("\n");
	    
	    /*Getting the Tag's Technologies available List  */
	    String[] techList = tagFromIntent.getTechList();
	    status.append("Technologies available: ");
	    for (int i = 0; i < techList.length; i++) {
	    	status.append(techList[i].substring(17));
	    	if (i < techList.length-1) {
				status.append(",");
			}
		}
	    
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
				 		/*Getting the Well-Known NDEF Record type*/
				 		int inttype = record.getType()[0];
				 		char chartype = (char) inttype;
				 		status.append((j+1)+"th. Record type: "+ chartype+"\n");
				 		/*for (int k = 0; k < record.getType().length; k++) {
							java.lang.System.out.println(record.getType()[k]);
						}*/
				 		status.append((j+1)+"th. Record id: "+record.getId().toString()+"\n");
				 		//status.append((j+1)+"th. Record Contents: "+record.describeContents()+"\n");
				 		
				 		try {
				 			payload = new String(record.getPayload(), 1, record.getPayload().length-1, Charset.forName("UTF-8"));
					 		status.append((j+1)+"th. Record payload:  "+payload +"\n");
					 		payloadHeader = record.getPayload()[0];
					 		status.append((j+1)+"th. Record payload header:  "+payloadHeader +"\n");
						} catch (StringIndexOutOfBoundsException e) {
							// TODO: handle exception
							Toast.makeText(this, "Empty tag", Toast.LENGTH_SHORT).show();
							
						}
				 		
				 		
				 	}
				 }
		
			 /*
			  * Check if the content is a URL, then open a browser
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
				}*/
		 
		 }
		 else {
			Log.d("debug", "that shit is so empty");
		}
		 

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

	// UTILIATRIES METHODS
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

	/*Return amount of bytes in use*/
	public int inUse (NdefMessage[] mssgs){
		int total = 0;
		
		Log.d("debug", "# messages: " + mssgs.length);
		for (int i = 0; i < mssgs.length; i++) {
			total += mssgs[0].getByteArrayLength();
		}
		
		
		return total;
	} 
}