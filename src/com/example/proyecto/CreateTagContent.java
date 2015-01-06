package com.example.proyecto;

import java.nio.charset.Charset;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
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
	private FormTel f_Telephone;
	private Tag detectedTag;
	private NfcAdapter myNfcAdapter;
	private String[][] techListsArray;
	private IntentFilter[] intentFiltersArray;
	private PendingIntent pendingIntent;
	private final static int PICK_CONTACT = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_tag_content);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		formContainer = (RelativeLayout)findViewById(R.id.formContainer);
		f_Telephone =  new FormTel(this);
		kindSelector = (Spinner)findViewById(R.id.kindSelector);
		kindSelector.setOnItemSelectedListener(this);
		
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
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
		Log.d("debug", "item selected: "+ kind);
		switch (kind) {
		case "Telephone Number":
			//formFragment = (RelativeLayout)findViewById(R.layout.form_telf);
			//f_Telephone =  new FormTel(this);
			formContainer.addView(f_Telephone);
			break;

		default:
			break;
		}
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
			String cntnt = f_Telephone.getFieldPhone().getText().toString();
			createTagContent(cntnt);
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
		myNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
		datasource.open();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		myNfcAdapter.disableForegroundDispatch(this);
		datasource.open();
	}
	
	public void createTagContent(String cntnt){
		
		
	    
	    //do something with tagFromIntent
	    byte[] uriField =  "dell.com".getBytes(Charset.forName("UTF-8"));
		byte[] payload = new byte[uriField.length + 1];
		payload[0] = 0x05; //Code for http://www.
		System.arraycopy(uriField, 0, payload, 1, uriField.length);
		NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
		NdefMessage newMessage = new NdefMessage(new NdefRecord[] {uriRecord});
		
		
		// WRITE DATA TO TAG
		Log.d("debug", "Starting writing process");
		writeNdefMessageToTag(newMessage );
		
		
	}
	
	private boolean writeNdefMessageToTag(NdefMessage message) {
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
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
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
