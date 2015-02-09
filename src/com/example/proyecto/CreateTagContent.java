package com.example.proyecto;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
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
import android.widget.AutoCompleteTextView.Validator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.objetos.GPSTracker;
import com.example.objetos.TagContent;
import com.example.objetos.TagContentDataSource;

public class CreateTagContent extends Activity implements
		OnItemSelectedListener {

	private Spinner kindSelector;
	private Spinner protocolSelector;
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
	public Map<String, String> PLH = new LinkedHashMap<String, String>();
	public Map<String, Integer> LNI = new LinkedHashMap<String, Integer>();
	public Map<String, Byte> UP = new LinkedHashMap<String, Byte>();
	private String currentSelection;
	private String currentPSelection;
	private String extraPayload;
	private TextView latitudeText;
	private TextView longitudeText;
	private Button wsaveButton;
	private Button wsaveWriteButton;
	private GPSTracker gps;
	private String editMode = "NEW";
	private String contenId;

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

		UP.put("http://www.", (byte) 0x01);
		UP.put("https://www.", (byte) 0x02);
		UP.put("http://", (byte) 0x03);
		UP.put("https://", (byte) 0x04);
		UP.put("ftp://", (byte) 0x0D);
		UP.put("sftp://", (byte) 0x0A);
		UP.put("file://", (byte) 0x1D);
		UP.put("telnet://", (byte) 0x10);

		
		formContainer = (RelativeLayout) findViewById(R.id.formContainer);
		wsaveButton = (Button) findViewById(R.id.wsaveButton);
		wsaveWriteButton = (Button) findViewById(R.id.wsaveWriteButton);

		if (getIntent().getStringExtra("CONTENT_ID") != null) {
			contenId = getIntent().getStringExtra("CONTENT_ID");
		}

		if (getIntent().getStringExtra("CONTENT_EDIT") != null) {
			
			editMode =  getIntent().getStringExtra("CONTENT_EDIT");
			Log.d("debug edit",editMode);
			wsaveButton.setText("Update");
			wsaveWriteButton.setText("Write");
		}
		
		kindSelector = (Spinner) findViewById(R.id.kindSelector);
		kindSelector.setOnItemSelectedListener(this);

		/*
		 * protocolSelector = (Spinner)findViewById(R.id.prtclSelector);
		 * protocolSelector.setOnItemSelectedListener(this);
		 */

		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

		try {
			ndef.addDataType("*/*"); /*
									 * Handles all MIME based dispatches. // You
									 * should specify only the ones that you
									 * need.
									 */
			// ndef.addDataScheme("http");
			// ndef.addAction(Intent.ACTION_VIEW);
		}

		catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		intentFiltersArray = new IntentFilter[] { ndef, };

		techListsArray = new String[][] { new String[] { NfcA.class.getName(),
				Ndef.class.getName(), MifareUltralight.class.getName() } };
		datasource = new TagContentDataSource(this);
		datasource.open();

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> kSelectoradapter = ArrayAdapter
				.createFromResource(this, R.array.kinds_array,
						android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		kSelectoradapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		kindSelector.setAdapter(kSelectoradapter);

		// Log.d("debug extra",getIntent().getStringExtra("CONTENT_PAYLOAD").toString());
		if (getIntent().getStringExtra("CONTENT_KIND") != null
				&& getIntent().getStringExtra("CONTENT_PAYLOAD") != null) {
			String kind = getIntent().getStringExtra("CONTENT_KIND");
			extraPayload = getIntent().getStringExtra("CONTENT_PAYLOAD");
			String[] kindsArray = getResources().getStringArray(
					R.array.kinds_array);
			int selected = 0;
			for (int i = 0; i < kindsArray.length; i++) {
				if (kindsArray[i].equalsIgnoreCase(getIntent().getStringExtra(
						"CONTENT_KIND"))) {
					selected = i;
					break;
				}
			}
			kindSelector.setSelection(selected);
			kindSelector.setEnabled(false);
			Log.d("debug extra", kind + " - " + extraPayload);
			// Log.d("debug extra",getIntent().getStringExtra("CONTENT_PAYLAOD")/);

		}

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	private void fillFields(String kind, String payload) {
		// TODO Auto-generated method stub
		switch (kind) {
		case "Plain Text":
			EditText fieldText = (EditText) findViewById(R.id.fieldText);
			String text = payload.substring(payload.indexOf(")") + 1,
					payload.length());
			fieldText.setText(text);
			break;
		case "Geo Location":
			EditText fieldLatitude = (EditText) findViewById(R.id.fieldLatitude);
			EditText fieldLongitude = (EditText) findViewById(R.id.fieldLongitude);
			String latitude = payload.substring(payload.indexOf(":") + 1,
					payload.indexOf(","));
			String longitude = payload.substring(payload.indexOf(",") + 1,
					payload.length());
			fieldLatitude.setText(latitude);
			fieldLongitude.setText(longitude);
			break;
		case "Email":
			EditText fieldTo = (EditText) findViewById(R.id.fieldTo);
			EditText fieldSubject = (EditText) findViewById(R.id.fieldSubject);
			EditText fieldBody = (EditText) findViewById(R.id.fieldBody);
			String to = payload.substring(payload.indexOf(":") + 1,
					payload.indexOf("?"));
			String subject = payload.substring(payload.indexOf("t=") + 2,
					payload.indexOf("&"));
			String body = payload.substring(payload.indexOf("y=") + 2,
					payload.length());
			fieldTo.setText(to);
			fieldSubject.setText(subject);
			fieldBody.setText(body);
			break;
		case "SMS":
			EditText fieldReceiver = (EditText) findViewById(R.id.fieldReceiver);
			EditText fieldMessage = (EditText) findViewById(R.id.fieldMessage);
			String receiver = payload.substring(payload.indexOf(":") + 1,
					payload.indexOf("?"));
			String message = payload.substring(payload.indexOf("y=") + 2,
					payload.length());
			fieldReceiver.setText(receiver);
			fieldMessage.setText(message);
			break;
		case "Telephone Number":
			EditText fieldPhone = (EditText) findViewById(R.id.fieldPhone);
			String phone = payload.substring(payload.indexOf(":") + 1,
					payload.length());
			fieldPhone.setText(phone);
			break;
		case "Link":
			EditText fieldLink = (EditText) findViewById(R.id.fieldLink);
			if (getIntent().getStringExtra("CONTENT_ID") != null) {
				Log.d("debug bd", getIntent().getStringExtra("CONTENT_ID"));
				Log.d("debug bd",
						datasource.getContentById(
								getIntent().getStringExtra("CONTENT_ID"))
								.toString());

				String link = datasource.getContentById(
						getIntent().getStringExtra("CONTENT_ID")).getPayload();
				String prtcl = datasource.getContentById(
						getIntent().getStringExtra("CONTENT_ID"))
						.getPayloadHeader();

				String[] protocolsArray = getResources().getStringArray(
						R.array.protocols_array);
				int selected = 0;
				for (int i = 0; i < protocolsArray.length; i++) {
					if (protocolsArray[i].equalsIgnoreCase(prtcl)) {
						selected = i;
						break;
					}
				}
				protocolSelector.setSelection(selected);
				fieldLink.setText(link);
			}
			break;
		default:
			break;
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
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		String kind = parent.getItemAtPosition(position).toString();
		currentSelection = kind;
		Log.d("debug", "item selected: " + kind);
		int layoutId = LNI.get(kind);
		form = new Form(this, layoutId);
		formContainer.removeAllViews();
		formContainer.addView(form);
		if (layoutId == R.layout.form_link) {
			protocolSelector = (Spinner) findViewById(R.id.prtclSelector);
			protocolSelector
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							String protocol = parent
									.getItemAtPosition(position).toString();
							currentPSelection = protocol;
							Log.d("debug", "protocol selected: " + protocol);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}
					});
			// Create an ArrayAdapter using the string array and a default
			// spinner layout
			ArrayAdapter<CharSequence> pSelectoradapter = ArrayAdapter
					.createFromResource(this, R.array.protocols_array,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			pSelectoradapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			protocolSelector.setAdapter(pSelectoradapter);

		}
		if (getIntent().getStringExtra("CONTENT_KIND") != null
				&& getIntent().getStringExtra("CONTENT_PAYLOAD") != null) {
			fillFields(kind, extraPayload);
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
			intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_CONTACT);
			break;
		case R.id.wsaveButton:

			if (saveContent(currentSelection)) {
				/* OPEN A NEW ACTIVITY */
				intent = new Intent(this, SaveResult.class);
				intent.putExtra("CONTENT_ID", contenId);
				intent.putExtra("CONTENT_EDIT", editMode);
				startActivity(intent);
			}

			break;
		case R.id.wsaveWriteButton:
			boolean s = saveContent(currentSelection);
			if (s) {
				intent = new Intent(this, TransferContent.class);
				String kind = kindSelector.getSelectedItem().toString();
				intent.putExtra("TAG_CONTENT", createTagContent(kind));
				intent.putExtra("CONTENT_ID", contenId);
				intent.putExtra("CONTENT_EDIT", editMode);
				startActivity(intent);
			}

			break;
		case R.id.locationButton:
			Log.d("debug", "GCL clicked");
			latitudeText = (TextView) findViewById(R.id.fieldLatitude);
			longitudeText = (TextView) findViewById(R.id.fieldLongitude);

			gps = new GPSTracker(CreateTagContent.this);

			if (gps.canGetLocation()) {
				latitudeText.setText(Double.toString(gps.getLatitude()));
				longitudeText.setText(Double.toString(gps.getLongitude()));
			} else {
				gps.showSettingsAlert();
			}

			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		ListView numberListView;
		final EditText fieldPhone = (EditText) findViewById(R.id.fieldPhone);
		final EditText fieldReceiver = (EditText) findViewById(R.id.fieldReceiver);
		final ArrayList<String> numbers = new ArrayList<String>();

		if (reqCode == PICK_CONTACT) {
			if (resultCode == RESULT_OK) {
				Uri contactdata = data.getData();
				Cursor c = getContentResolver().query(contactdata, null, null,
						null, null);

				if (c.moveToFirst()) {
					String contactname = c
							.getString(c
									.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
					String contactId = c
							.getString(c
									.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
					Cursor phones = getContentResolver().query(
							Phone.CONTENT_URI, null,
							Phone.CONTACT_ID + " = " + contactId, null, null);
					while (phones.moveToNext()) {
						String number = phones.getString(phones
								.getColumnIndex(Phone.NUMBER));
						numbers.add(number);
					}
					phones.close();
					// int type =
					// phones.getInt(phones.getColumnIndex(Phone.TYPE));

					numberListView = new ListView(this);
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							this, android.R.layout.simple_list_item_1, numbers);

					numberListView.setAdapter(adapter);
					numberListView
							.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										final View view, int position, long id) {
									final String item = (String) parent
											.getItemAtPosition(position);
									//

									switch (currentSelection) {
									case "Telephone Number":
										fieldPhone.setText(item);
										break;
									case "SMS":
										fieldReceiver.setText(item);
										break;
									default:
										break;
									}

									dialog.dismiss();
								}

							});

					dialog = new CustomDialog(this);
					dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(numberListView);
					dialog.show();

					Log.d("debug", "Name: " + contactname);
					Toast.makeText(this, "You've picked " + contactname,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show();
				}

				c.close();
			}

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// myNfcAdapter.enableForegroundDispatch(this, pendingIntent,
		// intentFiltersArray, techListsArray);
		datasource.open();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// myNfcAdapter.disableForegroundDispatch(this);
		datasource.open();
	}

	public NdefMessage createTagContent(String kind) {

		NdefMessage newMessage = null;
		byte[] uriField;
		byte[] payload;
		NdefRecord uriRecord;
		String content;
		switch (kind) {
		case "Telephone Number":
			EditText fieldPhone = (EditText) findViewById(R.id.fieldPhone);
			String telNumber = fieldPhone.getText().toString();
			uriField = telNumber.getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = 0x05; // Code for tel:
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
					NdefRecord.RTD_URI, new byte[0], payload);
			newMessage = new NdefMessage(new NdefRecord[] { uriRecord });
			break;
		case "Email":
			EditText fieldTo = (EditText) findViewById(R.id.fieldTo);
			EditText fieldSubject = (EditText) findViewById(R.id.fieldSubject);
			EditText fieldBody = (EditText) findViewById(R.id.fieldBody);
			content = fieldTo.getText().toString() + "?subject="
					+ fieldSubject.getText().toString() + "&body="
					+ fieldBody.getText().toString();
			uriField = content.getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = 0x06; // Code for mailto:
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
					NdefRecord.RTD_URI, new byte[0], payload);
			newMessage = new NdefMessage(new NdefRecord[] { uriRecord });
			break;
		case "SMS":
			EditText fieldReceiver = (EditText) findViewById(R.id.fieldReceiver);
			EditText fieldMessage = (EditText) findViewById(R.id.fieldMessage);
			content = "sms:" + fieldReceiver.getText().toString() + "?body="
					+ fieldMessage.getText().toString();
			uriField = content.getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = 0x00; // Code for sms:
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
					NdefRecord.RTD_URI, new byte[0], payload);
			newMessage = new NdefMessage(new NdefRecord[] { uriRecord });
			break;
		case "Geo Location":
			EditText fieldLatitude = (EditText) findViewById(R.id.fieldLatitude);
			EditText fieldLongitude = (EditText) findViewById(R.id.fieldLongitude);
			content = "geo:" + fieldLatitude.getText().toString() + ","
					+ fieldLongitude.getText().toString();
			uriField = content.getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = 0x00; // Code for sms:
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
					NdefRecord.RTD_URI, new byte[0], payload);
			newMessage = new NdefMessage(new NdefRecord[] { uriRecord });
			break;
		case "Plain Text":
			EditText fieldText = (EditText) findViewById(R.id.fieldText);
			// Locale locale= new Locale("en","US");
			Locale locale = Locale.getDefault();
			byte[] langBytes = locale.getLanguage().getBytes(
					Charset.forName("UTF-8"));
			boolean encodeInUtf8 = true;
			Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8")
					: Charset.forName("UTF-16");
			int utfBit = encodeInUtf8 ? 0 : (1 << 7);
			char status = (char) (utfBit + langBytes.length);
			String RTD_TEXT = fieldText.getText().toString();
			byte[] textBytes = RTD_TEXT.getBytes(utfEncoding);
			byte[] data = new byte[1 + langBytes.length + textBytes.length];
			data[0] = (byte) status;
			System.arraycopy(langBytes, 0, data, 1, langBytes.length);
			System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
					textBytes.length);
			NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
					NdefRecord.RTD_TEXT, new byte[0], data);
			newMessage = new NdefMessage(new NdefRecord[] { textRecord });
			break;
		case "Link":
			EditText fieldLink = (EditText) findViewById(R.id.fieldLink);
			uriField = fieldLink.getText().toString().getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = (Byte) UP.get(currentPSelection);
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
					NdefRecord.RTD_URI, new byte[0], payload);
			newMessage = new NdefMessage(new NdefRecord[] { uriRecord });
			break;
		default:
			break;
		}
		/*
		 * //do something with tagFromIntent byte[] uriField =
		 * "dell.com".getBytes(Charset.forName("UTF-8")); byte[] payload = new
		 * byte[uriField.length + 1]; payload[0] = 0x05; //Code for http://www.
		 * System.arraycopy(uriField, 0, payload, 1, uriField.length);
		 * NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
		 * NdefRecord.RTD_URI, new byte[0], payload); NdefMessage newMessage =
		 * new NdefMessage(new NdefRecord[] {uriRecord});
		 */
		// WRITE DATA TO TAG
		Log.d("debug", "Starting writing process");
		// writeNdefMessageToTag(newMessage );
		return newMessage;

	}

	private boolean saveContent(String kind) {
		String payload = "";
		String payloadHeaderDesc = "";
		String payloadTypeDesc = "";
		int updateResult;
		TagContent content = null;
		boolean valid = false;
		boolean saved = false;
		switch (kind) {
		case "Telephone Number":
			EditText fieldPhone = (EditText) findViewById(R.id.fieldPhone);
			if (!fieldPhone.getText().toString().trim().equals("")) {
				payload = fieldPhone.getText().toString();
				payloadHeaderDesc = "tel:";
				payloadTypeDesc = kind;
				valid = true;
			}

			break;
		case "Email":
			EditText fieldTo = (EditText) findViewById(R.id.fieldTo);
			EditText fieldSubject = (EditText) findViewById(R.id.fieldSubject);
			EditText fieldBody = (EditText) findViewById(R.id.fieldBody);
			if (!fieldTo.getText().toString().trim().equals("")
					&& !fieldSubject.getText().toString().trim().equals("")
					&& !fieldBody.getText().toString().trim().equals("")) {
				payload = fieldTo.getText().toString() + "?subject="
						+ fieldSubject.getText().toString() + "&body="
						+ fieldBody.getText().toString();
				payloadHeaderDesc = "mailto:";
				payloadTypeDesc = kind;
				valid = true;
			}

			break;
		case "SMS":
			EditText fieldReceiver = (EditText) findViewById(R.id.fieldReceiver);
			EditText fieldMessage = (EditText) findViewById(R.id.fieldMessage);
			if (!fieldReceiver.getText().toString().trim().equals("")
					&& !fieldMessage.getText().toString().trim().equals("")) {
				payload = "sms:" + fieldReceiver.getText().toString()
						+ "?body=" + fieldMessage.getText().toString();
				payloadHeaderDesc = "";
				payloadTypeDesc = kind;
				valid = true;
			}
			break;
		case "Geo Location":
			EditText fieldLatitude = (EditText) findViewById(R.id.fieldLatitude);
			EditText fieldLongitude = (EditText) findViewById(R.id.fieldLongitude);
			if (!fieldLatitude.getText().toString().trim().equals("")
					&& !fieldLongitude.getText().toString().trim().equals("")) {
				payload = "geo:" + fieldLatitude.getText().toString() + ","
						+ fieldLongitude.getText().toString();
				payloadHeaderDesc = "";
				payloadTypeDesc = kind;
				valid = true;
			}

			break;
		case "Plain Text":
			EditText fieldText = (EditText) findViewById(R.id.fieldText);
			if (!fieldText.getText().toString().trim().equals("")) {
				payload = fieldText.getText().toString();
				payloadHeaderDesc = "("
						+ Locale.getDefault().getLanguage().toUpperCase() + ")";
				payloadTypeDesc = kind;
				valid = true;
			}

			break;
		case "Link":
			EditText fieldLink = (EditText) findViewById(R.id.fieldLink);
			if (!fieldLink.getText().toString().trim().equals("")) {
				payload = fieldLink.getText().toString();
				payloadHeaderDesc = currentPSelection;
				payloadTypeDesc = kind;
				valid = true;
			}

			break;
		default:
			break;
		}
		if (valid) {
			if (editMode.equals("EDIT")) {
				updateResult = datasource.updateContent(contenId, payload, payloadHeaderDesc, payloadTypeDesc);
				Log.d("debug update",updateResult+"");
				saved = true;
				
			} else {
				content = datasource.createContent(payload, payloadHeaderDesc,
						payloadTypeDesc);
				saved = true;
			}
			Toast.makeText(this, "Tag content saved!", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, "Enter your data first, please",
					Toast.LENGTH_SHORT).show();
		}

		return saved;
	}

	/*
	 * private class extends ArrayAdapter<String> {
	 * 
	 * HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	 * 
	 * public StableArrayAdapter(Context context, int textViewResourceId,
	 * List<String> objects) { super(context, textViewResourceId, objects); for
	 * (int i = 0; i < objects.size(); ++i) { mIdMap.put(objects.get(i), i); } }
	 * 
	 * @Override public long getItemId(int position) { String item =
	 * getItem(position); return mIdMap.get(item); }
	 * 
	 * @Override public boolean hasStableIds() { return true; }
	 * 
	 * }
	 */
}
