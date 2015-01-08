package com.example.proyecto;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.Cursor;
import android.net.Uri;
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
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.objetos.TagContentDataSource;


public class CreateTagContent extends Activity implements OnItemSelectedListener{

	
	private Spinner kindSelector;
	private RelativeLayout formContainer;
	private CustomDialog dialog;
	private TagContentDataSource datasource;
	private Form form;
	private Tag detectedTag;
	private NfcAdapter myNfcAdapter;
	private String[][] techListsArray;
	private IntentFilter[] intentFiltersArray;
	private PendingIntent pendingIntent;
	private final static int PICK_CONTACT = 1;
	public Map<String, String> PLH =  new LinkedHashMap<String,String>();
	public Map<String, Integer> LNI =  new LinkedHashMap<String,Integer>();
	private String currentSelection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_tag_content);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		PLH.put("1", "http://www."); 
		PLH.put("2", "https://www."); 
		PLH.put("3", "http://"); 
		PLH.put("4", "https://");
		PLH.put("5", "tel:"); 
		PLH.put("6", "mailto:");
		PLH.put("66", "Bussiness card");
		PLH.put("99", "App launcher");
		
		LNI.put("Link", R.layout.form_link);
		LNI.put("Telephone Number", R.layout.form_telf);
		LNI.put("Email", R.layout.form_mail);
		LNI.put("SMS", R.layout.form_sms);
		LNI.put("Geo Location", R.layout.form_geo);
		LNI.put("Plain Text", R.layout.form_text);
		
		formContainer = (RelativeLayout)findViewById(R.id.formContainer);
		
		kindSelector = (Spinner)findViewById(R.id.kindSelector);
		kindSelector.setOnItemSelectedListener(this);
		
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
	   
	    techListsArray = new String[][] { new String[] { NfcA.class.getName() , 
	    		Ndef.class.getName(), 
	    		MifareUltralight.class.getName() } };
		datasource = new TagContentDataSource(this);
	    datasource.open();
	    
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> Selectoradapter = ArrayAdapter.createFromResource(this,
		        R.array.kinds_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		Selectoradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		kindSelector.setAdapter(Selectoradapter);
		
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_tag_content, menu);
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
			View rootView = inflater.inflate(
					R.layout.fragment_create_tag_content, container, false);
			return rootView;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		// TODO Auto-generated method stub
		String kind = parent.getItemAtPosition(position).toString();
		currentSelection = kind;
		Log.d("debug", "item selected: "+ kind);
		int layoutId = LNI.get(kind);
		form =  new Form(this, layoutId);
		formContainer.removeAllViews();
		formContainer.addView(form);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.contactButton:
			intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_CONTACT);
			break;
		case R.id.wsaveButton:

			break;	
		case R.id.wsaveWriteButton:
			intent = new Intent(this, TransferContent.class);
		    String kind = kindSelector.getSelectedItem().toString();
			intent.putExtra("TAG_CONTENT", createTagContent(kind));
			startActivity(intent);
			break;	
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data){
		super.onActivityResult(reqCode, resultCode, data);
		
		ListView numberListView;
		final EditText fieldPhone = (EditText)findViewById(R.id.fieldPhone);
		final ArrayList<String> numbers = new ArrayList<String>();
		
		if(reqCode == PICK_CONTACT){
			if(resultCode == RESULT_OK){
				Uri contactdata =  data.getData();
				Cursor c = getContentResolver().query(contactdata, null, null, null, null);
				
				if (c.moveToFirst()) {
					String contactname = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
					String contactId = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
					Cursor phones = getContentResolver().query(Phone.CONTENT_URI, null,Phone.CONTACT_ID + " = " + contactId, null, null);
					    while (phones.moveToNext()) {
					        String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
					        numbers.add(number);
					    }
					    phones.close();
					    //int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
				        
				        numberListView = new ListView(this);				        
				        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				                android.R.layout.simple_list_item_1, numbers);

				        numberListView.setAdapter(adapter);
				        numberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				            @Override
				            public void onItemClick(AdapterView<?> parent, final View view,
				                int position, long id) {
				              final String item = (String) parent.getItemAtPosition(position);
				              fieldPhone.setText(item);
				              /*
				              switch (currentSelection) {
							case "Telephone Number":
								
								break;
							case "SMS":
								fieldR.setText(item);
								break;	
							default:
								break;
							}*/
				              
				              dialog.dismiss();
				            }

				          });
				        
				        dialog = new CustomDialog(this);
						dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(numberListView);
						dialog.show();
						
						Log.d("debug","Name: "+contactname);
						Toast.makeText(this, "You've picked "+ contactname,Toast.LENGTH_LONG).show(); 
				}
				else{Toast.makeText(this, "Empty",Toast.LENGTH_LONG).show(); }
			
				c.close();
			}
			
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//myNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
		datasource.open();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//myNfcAdapter.disableForegroundDispatch(this);
		datasource.open();
	}
	
	public NdefMessage createTagContent(String kind){

		NdefMessage newMessage =  null;
		byte[] uriField;
		byte[] payload;
		NdefRecord uriRecord; 
		String content;
	    switch (kind) {
		case "Telephone Number":
			EditText fieldPhone =  (EditText)findViewById(R.id.fieldPhone);
			String telNumber = fieldPhone.getText().toString();
			uriField =  telNumber.getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = 0x05; //Code for tel:
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
			newMessage = new NdefMessage(new NdefRecord[] {uriRecord});
			break;
		case "Email":
			EditText fieldTo =  (EditText)findViewById(R.id.fieldTo);
			EditText fieldSubject =  (EditText)findViewById(R.id.fieldSubject);
			EditText fieldBody =  (EditText)findViewById(R.id.fieldBody);
			content = fieldTo.getText().toString() + "?subject="+fieldSubject.getText().toString()+"&body="+fieldBody.getText().toString();
			uriField =  content.getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = 0x06; // Code for mailto:
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
			newMessage = new NdefMessage(new NdefRecord[] {uriRecord});
			break;
		case "SMS":
			EditText fieldReceiver =  (EditText)findViewById(R.id.fieldPhone);
			EditText fieldMessage =  (EditText)findViewById(R.id.fieldMessage);
			content = "sms:"+fieldReceiver.getText().toString() + "?body="+fieldMessage.getText().toString();
			uriField =  content.getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = 0x00; // Code for sms:
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
			newMessage = new NdefMessage(new NdefRecord[] {uriRecord});
			break;
		default:
			break;
		}
	    /*
	    //do something with tagFromIntent
	    byte[] uriField =  "dell.com".getBytes(Charset.forName("UTF-8"));
		byte[] payload = new byte[uriField.length + 1];
		payload[0] = 0x05; //Code for http://www.
		System.arraycopy(uriField, 0, payload, 1, uriField.length);
		NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
		NdefMessage newMessage = new NdefMessage(new NdefRecord[] {uriRecord});
		
		*/
		// WRITE DATA TO TAG
		Log.d("debug", "Starting writing process");
		//writeNdefMessageToTag(newMessage );
		return newMessage; 
		
	}
	
	/*
	private class 
 extends ArrayAdapter<String> {

		    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		    public StableArrayAdapter(Context context, int textViewResourceId,
		        List<String> objects) {
		      super(context, textViewResourceId, objects);
		      for (int i = 0; i < objects.size(); ++i) {
		        mIdMap.put(objects.get(i), i);
		      }
		    }

		    @Override
		    public long getItemId(int position) {
		      String item = getItem(position);
		      return mIdMap.get(item);
		    }

		    @Override
		    public boolean hasStableIds() {
		      return true;
		    }

		  }*/
}
