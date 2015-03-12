package com.example.proyecto;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
	public Map<String, String> DBR = new LinkedHashMap<String, String>();
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
	private int currentPosition;
	private EditText shortened;
	private CheckBox shortUrl;
	protected String longUrl;
	private static Context mContext;
	private TextView contentSize;
	
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

		DBR.put("0",getResources().getString(R.string.link));
		DBR.put("1",getResources().getString(R.string.mail));
		DBR.put("2",getResources().getString(R.string.sms));
		DBR.put("3",getResources().getString(R.string.tel));
		DBR.put("4",getResources().getString(R.string.geoLoc));
		DBR.put("5",getResources().getString(R.string.plainText));
		DBR.put("6",getResources().getString(R.string.thesis));
		
		
		LNI.put(getResources().getString(R.string.link), R.layout.form_link);
		LNI.put(getResources().getString(R.string.tel), R.layout.form_telf);
		LNI.put(getResources().getString(R.string.mail), R.layout.form_mail);
		LNI.put(getResources().getString(R.string.sms), R.layout.form_sms);
		LNI.put(getResources().getString(R.string.geoLoc), R.layout.form_geo);
		LNI.put(getResources().getString(R.string.plainText), R.layout.form_text);
		LNI.put(getResources().getString(R.string.thesis), R.layout.form_thesis);

		UP.put("http://www.", (byte) 0x01);
		UP.put("https://www.", (byte) 0x02);
		UP.put("http://", (byte) 0x03);
		UP.put("https://", (byte) 0x04);
		UP.put("ftp://", (byte) 0x0D);
		UP.put("sftp://", (byte) 0x0A);
		UP.put("file://", (byte) 0x1D);
		UP.put("telnet://", (byte) 0x10);

		mContext = this;
		formContainer = (RelativeLayout) findViewById(R.id.formContainer);
		wsaveButton = (Button) findViewById(R.id.wsaveButton);
		wsaveWriteButton = (Button) findViewById(R.id.wsaveWriteButton);
		contentSize = (TextView) findViewById(R.id.fieldSize);

		if (getIntent().getStringExtra("CONTENT_ID") != null) {
			contenId = getIntent().getStringExtra("CONTENT_ID");
		}

		if (getIntent().getStringExtra("CONTENT_EDIT") != null) {
			
			editMode =  getIntent().getStringExtra("CONTENT_EDIT");
			Log.d("debug edit",editMode);
			wsaveButton.setText(getResources().getString(R.string.fupdate));
			wsaveWriteButton.setText(getResources().getString(R.string.fwrite));
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
		switch (currentPosition) {
		case 5 : //"Plain Text"
			EditText fieldText = (EditText) findViewById(R.id.fieldText);
			String text = payload.substring(payload.indexOf(")") + 1,
					payload.length());
			fieldText.setText(text);
			break;
		case 4 : //"Geo Location"
			EditText fieldLatitude = (EditText) findViewById(R.id.fieldLatitude);
			EditText fieldLongitude = (EditText) findViewById(R.id.fieldLongitude);
			String latitude = payload.substring(payload.indexOf(":") + 1,
					payload.indexOf(","));
			String longitude = payload.substring(payload.indexOf(",") + 1,
					payload.length());
			fieldLatitude.setText(latitude);
			fieldLongitude.setText(longitude);
			break;
		case 1 : //"Email"
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
		case 2 : //"SMS"
			EditText fieldReceiver = (EditText) findViewById(R.id.fieldReceiver);
			EditText fieldMessage = (EditText) findViewById(R.id.fieldMessage);
			String receiver = payload.substring(payload.indexOf(":") + 1,
					payload.indexOf("?"));
			String message = payload.substring(payload.indexOf("y=") + 2,
					payload.length());
			fieldReceiver.setText(receiver);
			fieldMessage.setText(message);
			break;
		case 3: // "Telephone Number"
			EditText fieldPhone = (EditText) findViewById(R.id.fieldPhone);
			String phone = payload.substring(payload.indexOf(":") + 1,
					payload.length());
			fieldPhone.setText(phone);
			break;
		case 0 : //"Link"
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
		case 6 : // "TEG"
			EditText fieldTitle = (EditText) findViewById(R.id.fieldTitle);
			EditText fieldAuthor = (EditText) findViewById(R.id.fieldAuthor);
			EditText fieldRef = (EditText) findViewById(R.id.fieldRef);
			EditText fieldURL = (EditText) findViewById(R.id.fieldURL);
			
			String title = payload.substring(payload.indexOf(":") + 1,
					payload.indexOf("?"));
			String author = payload.substring(payload.indexOf("a=") + 2,
					payload.indexOf("&"));
			String ref = payload.substring(payload.indexOf("r=") + 2,
					payload.lastIndexOf("&"));
			String url = payload.substring(payload.indexOf("u=") + 2,
					payload.length());
			
			fieldTitle.setText(title);
			fieldAuthor.setText(author);
			fieldRef.setText(ref);
			fieldURL.setText(url);
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
		currentPosition = position;
		currentSelection = kind;
		Log.d("debug", "item selected: " + kind);
		Log.d("debug", "cp: " + currentPosition);
		int layoutId = LNI.get(kind);
		form = new Form(this, layoutId);
		formContainer.removeAllViews();
		formContainer.addView(form);
		setTextWatchwers();
		contentSize.setText("3");
		if (layoutId == R.layout.form_thesis) {
			shortened = (EditText)findViewById(R.id.fieldURL);
			shortUrl = (CheckBox)findViewById(R.id.shortUrl);
			shortened.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					shortUrl.setEnabled(false);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					if (s.length() > 0) {
						shortUrl.setEnabled(true);
					}
					else {
						shortUrl.setEnabled(false);
						shortUrl.setChecked(false);
					}
				}
			});
			
			shortUrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					  
					if (isChecked) {
						String currentUrl = shortened.getText().toString();
						Pattern p = Patterns.WEB_URL;
						Matcher m = p.matcher(currentUrl);
						if (m.matches()) {
							Log.d("debug shortened URL", currentUrl);
							Log.d("debug shortened URL", shortUrl.isChecked()+"");
							if (!haveNetworkConnection()) {
								
								shortUrl.setChecked(false);
								
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
								
								alertDialog.setTitle(getResources().getString(R.string.dialogMTitle));
								
								alertDialog.setMessage(getResources().getString(R.string.dialogMMessage));
								
								alertDialog.setPositiveButton(getResources().getString(R.string.dialogOkButton), new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
										startActivity(intent);
									}
								});
								
								alertDialog.setNegativeButton(getResources().getString(R.string.dialogCancelButton), new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								});
								
								alertDialog.show();
							}
							else {
								ShortenUrlTask task = new ShortenUrlTask(); 
								task.execute(shortened.getText().toString());	
							}
							
						}
						else {
							Toast.makeText(getBaseContext(), "Invalid URL",Toast.LENGTH_LONG).show();
							shortUrl.setChecked(false);
						}
						
						
						
					}
					else {
						shortened.setText(longUrl);
					}
				}
			});
			
		
		}
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
					
					if(numbers.size() == 0){ numbers.add("0 Numbers found");}
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

									switch (currentPosition) {
									case 3 : //"Telephone Number"
										fieldPhone.setText(item);
										break;
									case 2: // "SMS"
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
					Toast.makeText(this, getResources().getString(R.string.plNumber) + contactname,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, getResources().getString(R.string.plEmpty), Toast.LENGTH_LONG).show();
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
		
		switch (currentPosition) {
		case 3 : //DBR.put("5",getResources().getString(R.string.text));
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
		case 1 : //"Email"
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
		case 2 : //"SMS"
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
		case 4 : //"Geo Location"
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
		case 5 : //"Plain Text"
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
		case 0 : //"Link"
			EditText fieldLink = (EditText) findViewById(R.id.fieldLink);
			uriField = fieldLink.getText().toString().getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = (Byte) UP.get(currentPSelection);
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
					NdefRecord.RTD_URI, new byte[0], payload);
			newMessage = new NdefMessage(new NdefRecord[] { uriRecord });
			break;
		case 6 : //"TEG"
			String externalType = "com.example:thesis";
			CheckBox shortUrl = (CheckBox)findViewById(R.id.shortUrl);
			EditText fieldTitle = (EditText) findViewById(R.id.fieldTitle);
			EditText fieldAuthor = (EditText) findViewById(R.id.fieldAuthor);
			EditText fieldRef = (EditText) findViewById(R.id.fieldRef);
			EditText fieldURL = (EditText) findViewById(R.id.fieldURL);
			content = "thesis:" + fieldTitle.getText().toString() + "?a="
					+ fieldAuthor.getText().toString() + "&r="
					+ fieldRef.getText().toString() + "&u="
					+ fieldURL.getText().toString();
			uriField = content.getBytes();
			payload = new byte[uriField.length + 1];
			payload[0] = 0x00; // Code for Thesis:
			System.arraycopy(uriField, 0, payload, 1, uriField.length);
			uriRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE,
					externalType.getBytes(), new byte[0], payload);
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
		String payloadTypeDesc = String.valueOf(currentPosition);
		int updateResult;
		TagContent content = null;
		boolean valid = false;
		boolean saved = false;
		switch (currentPosition) {
		case 3 : //"Telephone Number"
			EditText fieldPhone = (EditText) findViewById(R.id.fieldPhone);
			if (!fieldPhone.getText().toString().trim().equals("")) {
				payload = fieldPhone.getText().toString();
				payloadHeaderDesc = "tel:";
				//payloadTypeDesc = kind;
				valid = true;
			}

			break;
		case 1 : //"Email"
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
				//payloadTypeDesc = kind;
				valid = true;
			}

			break;
		case 2 : //"SMS"
			EditText fieldReceiver = (EditText) findViewById(R.id.fieldReceiver);
			EditText fieldMessage = (EditText) findViewById(R.id.fieldMessage);
			if (!fieldReceiver.getText().toString().trim().equals("")
					&& !fieldMessage.getText().toString().trim().equals("")) {
				payload = "sms:" + fieldReceiver.getText().toString()
						+ "?body=" + fieldMessage.getText().toString();
				payloadHeaderDesc = "";
				//payloadTypeDesc = kind;
				valid = true;
			}
			break;
		case 4: //Geo Location
			EditText fieldLatitude = (EditText) findViewById(R.id.fieldLatitude);
			EditText fieldLongitude = (EditText) findViewById(R.id.fieldLongitude);
			if (!fieldLatitude.getText().toString().trim().equals("")
					&& !fieldLongitude.getText().toString().trim().equals("")) {
				payload = "geo:" + fieldLatitude.getText().toString() + ","
						+ fieldLongitude.getText().toString();
				payloadHeaderDesc = "";
				//payloadTypeDesc = kind;
				valid = true;
			}

			break;
		case 5: //"Plain Text"
			EditText fieldText = (EditText) findViewById(R.id.fieldText);
			if (!fieldText.getText().toString().trim().equals("")) {
				payload = fieldText.getText().toString();
				payloadHeaderDesc = "("
						+ Locale.getDefault().getLanguage().toUpperCase() + ")";
				//payloadTypeDesc = kind;
				valid = true;
			}

			break;
		case 0 : //"Link"
			EditText fieldLink = (EditText) findViewById(R.id.fieldLink);
			if (!fieldLink.getText().toString().trim().equals("")) {
				payload = fieldLink.getText().toString();
				payloadHeaderDesc = currentPSelection;
				//payloadTypeDesc = kind;
				valid = true;
			}

			break;
		case 6 : //"TEG"
			CheckBox shortUrl = (CheckBox)findViewById(R.id.shortUrl);
			EditText fieldTitle = (EditText) findViewById(R.id.fieldTitle);
			EditText fieldAuthor = (EditText) findViewById(R.id.fieldAuthor);
			EditText fieldRef = (EditText) findViewById(R.id.fieldRef);
			EditText fieldURL = (EditText) findViewById(R.id.fieldURL);
			if (!fieldTitle.getText().toString().trim().equals("")
					&& !fieldAuthor.getText().toString().trim().equals("")
					&& !fieldRef.getText().toString().trim().equals("")
					&& !fieldURL.getText().toString().trim().equals("") ){

				Log.d("debug shortened URL", "shortened text: "+shortened.getText().toString());
				payload = fieldTitle.getText().toString() + "?a="
						+ fieldAuthor.getText().toString() + "&r="
						+ fieldRef.getText().toString() + "&u="
						+ shortened.getText().toString();
				payloadHeaderDesc = "thesis:";
				//payloadTypeDesc = kind;
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

	class ShortenUrlTask extends AsyncTask<String, Void, String> { 
		private final String GOOGLE_URL = "https://www.googleapis.com/urlshortener/v1/url"; 
		private String mLongUrl = null; 
		@Override protected String doInBackground(String... arg) { 
			mLongUrl = arg[0];
			try { 
					// Set connection timeout to 5 secs and socket timeout to 10 secs 
					HttpParams httpParameters = new BasicHttpParams(); 
					int timeoutConnection = 5000; 
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection); 
					int timeoutSocket = 10000; 
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 
					HttpClient hc = new DefaultHttpClient(httpParameters); 
					HttpPost request = new HttpPost(GOOGLE_URL); 
					request.setHeader("Content-type", "application/json"); 
					request.setHeader("Accept", "application/json"); 
					JSONObject obj = new JSONObject(); obj.put("longUrl", mLongUrl); 
					request.setEntity(new StringEntity(obj.toString(), "UTF-8")); 
					HttpResponse response = hc.execute(request); 
					if ( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ) { 
						ByteArrayOutputStream out = new ByteArrayOutputStream(); 
						response.getEntity().writeTo(out); out.close(); 
						return out.toString(); 
					}
					else{
						Toast.makeText(getBaseContext(), "Connection timed out",Toast.LENGTH_LONG).show();
						return null; 
					} 
				} catch ( Exception e ) { 
					e.printStackTrace(); 
				} return null; 
			} 
		@Override protected void onPostExecute(String result) {
			if ( result == null ) 
				return; 
			try { 
				final JSONObject json = new JSONObject(result); 
				final String id = json.getString("id");
				final String lurl = json.getString("longUrl");
				if ( json.has("id") ) { 
					((Activity) mContext).runOnUiThread(new Runnable() { 
						public void run() { 
							shortened.setText(id);
							longUrl = lurl;
						} 
					});
					Log.d("debug shortened URL", id);
					
				} 
			} catch (JSONException e) {
				e.printStackTrace(); 
			} 
		} 
		
	}
	
	private boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    
	    return haveConnectedWifi || haveConnectedMobile ;
	}
	
	private TextWatcher sizeWatcher = new TextWatcher() {
		
		
		
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	
	private void setTextWatchwers(){
		
		String payload = "";
		String payloadHeaderDesc = "";
		String payloadTypeDesc = String.valueOf(currentPosition);
		int updateResult;
		TagContent content = null;
		boolean valid = false;
		boolean saved = false;
		switch (currentPosition) {
		case 3 : //"Telephone Number"
			EditText fieldPhone = (EditText) findViewById(R.id.fieldPhone);
			fieldPhone.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					
					contentSize.setText("3");
					int currentSize = Integer.valueOf(contentSize.getText().toString());
					//currentSize += arg3;
					if (arg0.length() == 0) {
						contentSize.setText(String.valueOf(3));
					}
					else{
						contentSize.setText(String.valueOf(5+arg0.length()));
					}
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});

			break;
		case 1 : //"Email"
			final EditText fieldTo = (EditText) findViewById(R.id.fieldTo);
			final EditText fieldSubject = (EditText) findViewById(R.id.fieldSubject);
			final EditText fieldBody = (EditText) findViewById(R.id.fieldBody);
			
			fieldTo.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					int sbjctCZ = fieldSubject.getText().toString().length();
					int bdyCZ = fieldBody.getText().toString().length();
					
					if (arg0.length() == 0) {
						if ( sbjctCZ == 0 &&  bdyCZ == 0) {
							contentSize.setText(String.valueOf(5));
						}
						else {
							int nsize = 3;
							if (sbjctCZ > 0) { nsize += 9+fieldSubject.getText().toString().length();}
							if (bdyCZ > 0) {nsize += 6+fieldBody.getText().toString().length();}
							contentSize.setText(String.valueOf(2+nsize));
						}
						
					} else {
						int nsize = 3;
						if (sbjctCZ > 0) {nsize += 9+fieldSubject.getText().toString().length(); }
						if (bdyCZ > 0) {nsize += 6+fieldBody.getText().toString().length();}
						contentSize.setText(String.valueOf(2+nsize+arg0.length()));
					}
					
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			fieldSubject.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					int toCZ = fieldTo.getText().toString().length();
					int bdyCZ = fieldBody.getText().toString().length();
					
					if (arg0.length() == 0) {
						if ( toCZ == 0 &&  bdyCZ == 0) {
							contentSize.setText(String.valueOf(5));
						}
						else {
							int nsize = 3;
							if (toCZ > 0) { nsize += 2+fieldTo.getText().toString().length();}
							if (bdyCZ > 0) {nsize += 6+fieldBody.getText().toString().length();}
							contentSize.setText(String.valueOf(nsize));
						}
						
					} else {
						int nsize = 3;
						if (toCZ > 0) {nsize += 2+fieldTo.getText().toString().length(); }
						if (bdyCZ > 0) {nsize += 6+fieldBody.getText().toString().length();}
						contentSize.setText(String.valueOf(9+nsize+arg0.length()));
					}
					
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			fieldBody.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					int sbjctCZ = fieldSubject.getText().toString().length();
					int toCZ = fieldTo.getText().toString().length();
					
					if (arg0.length() == 0) {
						if ( sbjctCZ == 0 &&  toCZ == 0) {
							contentSize.setText(String.valueOf(5));
						}
						else {
							int nsize = 3;
							if (sbjctCZ > 0) { nsize += 9+fieldSubject.getText().toString().length();}
							if (toCZ > 0) {nsize += 2+fieldTo.getText().toString().length();}
							contentSize.setText(String.valueOf(nsize));
						}
						
					} else {
						int nsize = 3;
						if (sbjctCZ > 0) {nsize += 9+fieldSubject.getText().toString().length(); }
						if (toCZ > 0) {nsize += 2+fieldTo.getText().toString().length();}
						contentSize.setText(String.valueOf(6+nsize+arg0.length()));
					}
					
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});

			break;
		case 2 : //"SMS"
			final EditText fieldReceiver = (EditText) findViewById(R.id.fieldReceiver);
			final EditText fieldMessage = (EditText) findViewById(R.id.fieldMessage);
		
			fieldReceiver.addTextChangedListener(new TextWatcher() {
				int sizeBefore;
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					
					contentSize.setText("3");
					int currentSize = Integer.valueOf(contentSize.getText().toString());
					int msgCZ = fieldMessage.getText().toString().length();
					
					if (arg0.length() == 0) {
						if (msgCZ == 0) {
							contentSize.setText(String.valueOf(3));
						}
						if (msgCZ > 0) {
							contentSize.setText(String.valueOf(3+4+6+msgCZ));
						}
					} else {
						if (msgCZ == 0) {
							contentSize.setText(String.valueOf(3+4+arg0.length()));
						}
						if (msgCZ > 0) {
							contentSize.setText(String.valueOf(3+4+6+msgCZ+arg0.length()));
						}
					}
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					sizeBefore = Integer.valueOf(contentSize.getText().toString());
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			fieldMessage.addTextChangedListener(new TextWatcher() {
				int sizeBefore;
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					
					contentSize.setText("3");
					int currentSize = Integer.valueOf(contentSize.getText().toString());
					int numCZ = fieldReceiver.getText().toString().length();
					
					if (arg0.length() == 0) {
						if (numCZ == 0) {
							contentSize.setText(String.valueOf(3));
						}
						if (numCZ > 0) {
							contentSize.setText(String.valueOf(3+4+numCZ));
						}
						
					} else {
						if (numCZ == 0) {
							contentSize.setText(String.valueOf(3+4+6+arg0.length()));
						}
						if (numCZ > 0) {
							contentSize.setText(String.valueOf(3+4+6+numCZ+arg0.length()));
						}
					}
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					sizeBefore = Integer.valueOf(contentSize.getText().toString());
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			break;/*
		case 4: //Geo Location
			EditText fieldLatitude = (EditText) findViewById(R.id.fieldLatitude);
			EditText fieldLongitude = (EditText) findViewById(R.id.fieldLongitude);
			if (!fieldLatitude.getText().toString().trim().equals("")
					&& !fieldLongitude.getText().toString().trim().equals("")) {
				payload = "geo:" + fieldLatitude.getText().toString() + ","
						+ fieldLongitude.getText().toString();
				payloadHeaderDesc = "";
				//payloadTypeDesc = kind;
				valid = true;
			}

			break;*/
		case 5: //"Plain Text"
			EditText fieldText = (EditText) findViewById(R.id.fieldText);
			fieldText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					
					contentSize.setText("3");
					int currentSize = Integer.valueOf(contentSize.getText().toString());
					//currentSize += arg3;
					if (arg0.length() == 0) {
						contentSize.setText(String.valueOf(3));
					}
					else{
						contentSize.setText(String.valueOf(7+arg0.length()));
					}
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			break;
		case 0 : //"Link"
			EditText fieldLink = (EditText) findViewById(R.id.fieldLink);
			fieldLink.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					
					int currentSize = Integer.valueOf(contentSize.getText().toString());
					//currentSize += arg3;
					if (arg0.length() == 0) {
						contentSize.setText(String.valueOf(3));
					}
					else{
						contentSize.setText(String.valueOf(5+arg0.length()));
					}
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			break;/*
		case 6 : //"TEG"
			CheckBox shortUrl = (CheckBox)findViewById(R.id.shortUrl);
			EditText fieldTitle = (EditText) findViewById(R.id.fieldTitle);
			EditText fieldAuthor = (EditText) findViewById(R.id.fieldAuthor);
			EditText fieldRef = (EditText) findViewById(R.id.fieldRef);
			EditText fieldURL = (EditText) findViewById(R.id.fieldURL);
			if (!fieldTitle.getText().toString().trim().equals("")
					&& !fieldAuthor.getText().toString().trim().equals("")
					&& !fieldRef.getText().toString().trim().equals("")
					&& !fieldURL.getText().toString().trim().equals("") ){

				Log.d("debug shortened URL", "shortened text: "+shortened.getText().toString());
				payload = fieldTitle.getText().toString() + "?a="
						+ fieldAuthor.getText().toString() + "&r="
						+ fieldRef.getText().toString() + "&u="
						+ shortened.getText().toString();
				payloadHeaderDesc = "thesis:";
				//payloadTypeDesc = kind;
				valid = true;
			}

			break;*/
		default:
			break;
		}
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
