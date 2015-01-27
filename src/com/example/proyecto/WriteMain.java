package com.example.proyecto;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.objetos.CustomAdapater;
import com.example.objetos.TagContent;
import com.example.objetos.TagContentDataSource;

public class WriteMain extends Activity {
	
	private NfcAdapter myNfcAdapter;
	private TextView status;
	private ListView contentList;
	private String[][] techListsArray;
	private IntentFilter[] intentFiltersArray;
	private PendingIntent pendingIntent;
	private TagContentDataSource datasource;
	private TextView emptyDB;
	private Button moreButton;
	private LinearLayout writeFooter;
	private CustomAdapater adapterAdapater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_write_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//status = (TextView) findViewById(R.id.status);
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		contentList = (ListView)findViewById(R.id.recentList);
		
		/*
		if (myNfcAdapter == null) {
			status.setText("NFC isn't available for the device");
		} else {
			status.setText("NFC is available for the device");
		}*/
		
		/*
		
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Log.d("debug","NDEF Discovered");
			Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			//PREPARE THE NDEF MESSAGE
			byte[] uriField =  "amazon.com".getBytes(Charset.forName("US-ACII"));
			byte[] payload = new byte[uriField.length + 1];
			payload[0] = 0x01; //Code for http://www.
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
			NdefMessage newMessage = new NdefMessage(new NdefRecord[] {uriRecord});
			
			
			// WRITE DATA TO TAG
			writeNdefMessageToTag(newMessage, detectedTag );
			
		}*/
		
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
	    
	    List<TagContent> test = datasource.getAllComments();
	      //Toast.makeText(this, "Tag content saved!", Toast.LENGTH_SHORT).show();
	      for (int i = test.size()-1; i >= 0; i--) {
			Log.d("List element", "tag_content: " + test.get(i));
		}
	    
	    
	    List<TagUIContent> tagUIContents = datasource.getTagUIContents();
	    Log.d("debug list write", ""+tagUIContents.size()+"");
	    Collections.reverse(tagUIContents);
	    
	    if (tagUIContents.size() >= 10) {
	    	tagUIContents = tagUIContents.subList(0, 10);
	    	moreButton = (Button)findViewById(R.id.moreButton);
	    	writeFooter = (LinearLayout)findViewById(R.id.writeFooter);
	    	writeFooter.setVisibility(View.VISIBLE);
		}
	    
	    adapterAdapater = new CustomAdapater(this,tagUIContents);
	    if (adapterAdapater == null) {
	    	Log.d("debug list write", "adapter is null");
		}
	    contentList.setAdapter(adapterAdapater);
	    
	    /*
	    Log.d("debug", "Content length "+tagUIContents.size() );
	    if (contentList.getChildCount() > 0) {
			contentList.removeAllViews();
		}
	  
	    if (tagUIContents.size() > 0) {
	    	for (int i = tagUIContents.size()-1; i >= 0; i--) {
		    	contentList.addView(tagUIContents.get(i));
			}
		} else {
			emptyDB = new TextView(this);
			emptyDB.setText("No recent tag content found!");
			emptyDB.setGravity(1);
			contentList.addView(emptyDB);
		}*/
	    
		
	    //datasource.close();
	}
	
	public void onPause() {
	    super.onPause();
	   myNfcAdapter.disableForegroundDispatch(this);
	   datasource.open();
	}

	public void onResume() {
	    super.onResume();
	   myNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
	   datasource.open();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.write_main, menu);
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

	public void onNewIntent(Intent intent)
	{
		
		/*
		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    
	    //do something with tagFromIntent
	    byte[] uriField =  "dell.com".getBytes(Charset.forName("UTF-8"));
		byte[] payload = new byte[uriField.length + 1];
		payload[0] = 0x01; //Code for http://www.
		System.arraycopy(uriField, 0, payload, 1, uriField.length);
		NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
		NdefMessage newMessage = new NdefMessage(new NdefRecord[] {uriRecord});
		
		
		// WRITE DATA TO TAG
		Log.d("debug", "Starting writing process");
		writeNdefMessageToTag(newMessage, detectedTag );*/
	  
	}
	
	public void	onClick(View view) {
		Intent  intent;
		switch (view.getId()) {
		case R.id.newContentButton:
			intent = new Intent(this, CreateTagContent.class);
			startActivity(intent);
			break;
		case R.id.moreButton:
			intent = new Intent(this, TagsMain.class);
			startActivity(intent);
			break;
		default:
			break;
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
			View rootView = inflater.inflate(R.layout.fragment_write_main,
					container, false);
			return rootView;
		}
	}

}
