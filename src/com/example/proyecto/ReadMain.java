package com.example.proyecto;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
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
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.objetos.TagContent;
import com.example.objetos.TagContentDataSource;
import com.example.objetos.TagInfo;
import com.example.proyecto.R.string;

public class ReadMain extends Activity {

	private NfcAdapter myNfcAdapter;
	private TextView status;
	private TextView Type;
	private TextView Content;
	private TextView featureList;
	private TextView recordList;
	private ImageView payloadTypeIcon;
	private Button launchButton;
	private Button saveButton;
	private String[][] techListsArray;
	private IntentFilter[] intentFiltersArray;
	private PendingIntent pendingIntent;
	private CustomDialog dialog;
	private int LaunchButtonVisibility;
	private int SaveButtonVisibility;
	private TagFeature tagFeature;
	private LinearLayout featuresListLayout;
	private LinearLayout recordsListLayout;
	private String cntn;
	private TagContentDataSource datasource;
	private TagInfo tInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		/*Setting up the dialog*/
		dialog = new CustomDialog(this);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.read_tag_dialog);
		
		datasource = new TagContentDataSource(this);
	    //datasource.open();
	    
		dialog.show();
		
		/*Checking if the device support NFC*/
		//status = (TextView) findViewById(R.id.status);
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (myNfcAdapter == null) {
			//status.setText("NFC isn't available for the device");
		} else {
			//status.setText("NFC is available for the device");
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
	   //datasource.open();
	}

	public void onResume() {
	    super.onResume();
	   myNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
	   //datasource.open();
	}

	public void onNewIntent(Intent intent)
	{
		
		
		//Check if the dialog is showing
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		
		payloadTypeIcon =  (ImageView) findViewById(R.id.payloadTypeIcon);
		Type = (TextView) findViewById(R.id.type);
		Content = (TextView) findViewById(R.id.content);
		featureList = (TextView) findViewById(R.id.featuresText);
		launchButton = (Button) findViewById(R.id.launchButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		
		recordList = (TextView) findViewById(R.id.recordText);
		
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    Ndef ndef = Ndef.get(tagFromIntent);
	    
	    NdefMessage[] messages = getNdefMessages(intent);
	    
	    tInfo = new TagInfo(tagFromIntent, intent, this);
	    
	    Log.d("debug", "Records: " + tInfo.getTagRecords().size());
	    
	    if (messages != null)
	    {
	    	
	    	if (tInfo.getTagRecords().size() >= 1 && tInfo.getTagRecords().get(0).getRecordPayload() != null ) 
	    	{
	    		payloadTypeIcon.setBackgroundResource(tInfo.getTagRecords().get(0).getIconId());
			    Type.setText(tInfo.getTagRecords().get(0).getRecordPayloadTypeDesc());
			    if (tInfo.getTagRecords().get(0).isWOP()) {
			    	if (tInfo.getTagRecords().get(0).getRecordType().equalsIgnoreCase(getResources().getString(R.string.text))) {
						cntn = "("+tInfo.getTagRecords().get(0).getRecordLanguageCode().toUpperCase()+")";
					}
			    	cntn += tInfo.getTagRecords().get(0).getRecordPayload();
			    	
				}
			    else {
			    	cntn = tInfo.getTagRecords().get(0).getRecordPayloadHeaderDesc() + tInfo.getTagRecords().get(0).getRecordPayload();
				}
			    Content.setText(cntn);
			    SaveButtonVisibility = View.VISIBLE;
			    
			    
			}
	    	else 
			{
				Content.setText(""); // change for a system string
		    	Type.setText(R.string.readIntent_Empty);
		    	payloadTypeIcon.setBackgroundResource(R.drawable.default64);// Assign default icon
		    	Toast.makeText(this, "Empty tag", Toast.LENGTH_SHORT).show();
		    	SaveButtonVisibility = View.INVISIBLE;
			}
	    	
	    }	
	    
	    /*FOR DEBUG - ERASE ASAP*/
	    Log.d("TagInfo", tInfo.getTagId());
	    Log.d("TagInfo", tInfo.getTagType());
	    Log.d("TagInfo", tInfo.getTagTechList());
	    Log.d("TagInfo", tInfo.getCanBeReadOnly() ? "Can be made RO" : "Can't be made RO");
	    Log.d("TagInfo", tInfo.getIsWritable() ? "Writable" : "No writable");
	    Log.d("TagInfo", String.valueOf(tInfo.getTagSize()) + " bytes");
	    Log.d("TagInfo", String.valueOf(tInfo.getInUse()) + " bytes");
	    Log.d("TagInfo", String.valueOf(tInfo.getMessages())+ " Message(s)");
	    Log.d("TagInfo", "First Record's Payload Type: "+tInfo.getTagRecords().get(0).getRecordPayloadTypeDesc());
	    Log.d("TagInfo", "First Record's Payload: "+tInfo.getTagRecords().get(0).getRecordPayload());
	    Log.d("TagInfo", "First Record's Payload Header Desc: "+tInfo.getTagRecords().get(0).getRecordPayloadHeaderDesc());
	    Log.d("TagInfo", "First Record's Type Desc: "+tInfo.getTagRecords().get(0).getRecordType());
	    if ( tInfo.getTagRecords().size() > 1) {
		    Log.d("TagInfo", "Second Record's Payload Type: "+tInfo.getTagRecords().get(1).getRecordPayloadTypeDesc());
		    Log.d("TagInfo", "Second Record's Payload: "+tInfo.getTagRecords().get(1).getRecordPayload());
		    Log.d("TagInfo", "Second Record's Payload Header Desc: "+tInfo.getTagRecords().get(1).getRecordPayloadHeaderDesc());
		    Log.d("TagInfo", "Second Record's Type Desc: "+tInfo.getTagRecords().get(1).getRecordType());
		}

	    /*FOR DEBUG - ERASE ASAP*/
	    
	
	    payloadTypeIcon.setVisibility(View.VISIBLE);
	    Type.setVisibility(View.VISIBLE);
	    Content.setVisibility(View.VISIBLE);
	    featureList.setVisibility(View.VISIBLE);
	    recordList.setVisibility(View.VISIBLE);
	    
	    /*CHANGE VALIDATION ASAP*/
	    LaunchButtonVisibility = tInfo.getTagRecords().size() > 1 ? View.VISIBLE : View.INVISIBLE;
	    
	    launchButton.setVisibility(LaunchButtonVisibility);
	    saveButton.setVisibility(SaveButtonVisibility);
	    featuresListLayout = (LinearLayout) findViewById(R.id.featureList);
	    recordsListLayout = (LinearLayout) findViewById(R.id.recordsList);
	   
	    /*Add the tag's feature list*/
	    ArrayList<TagFeature> tagFeatures =  tInfo.getTagFeatures();
	    Log.d("debug", "Features length "+tagFeatures.size() );
	    if (featuresListLayout.getChildCount() > 0) {
			featuresListLayout.removeAllViews();
		}
	  
	    for (int i = 0; i < tagFeatures.size(); i++) {
	    	featuresListLayout.addView(tagFeatures.get(i));
		}
	    
	    
	    /*Add the tag's feature list*/
	    ArrayList<TagRecord> recordArrayList =  tInfo.getTagUIRecords();
	    Log.d("debug", "Features length "+recordArrayList.size() );
	    if (recordsListLayout.getChildCount() > 0) {
			recordsListLayout.removeAllViews();
		}
	  
	    for (int i = 0; i < recordArrayList.size(); i++) {
	    	recordsListLayout.addView(recordArrayList.get(i));
		}
	    
	    //do something with tagFromIntent
	    Log.d("debug", "tag detected");
		 // GET NDEF MESSAGE IN THE TAG
	    
	    
		 // PROCESS NDEF MESSAGE
		 String payload = null;
		 byte payloadHeader = 0x00;
		 
		 if (messages != null) {
			/*
			 for (int i = 0; i < messages.length; i++) {
				 //status.append("\n");
				 //status.append("Message "+(i+1)+" \n");
				 	for (int j = 0; j < messages[0].getRecords().length; j++) {
				 		
				 		NdefRecord record = messages[i].getRecords()[j];
				 		
				 		//status.append((j+1)+"th. Record Tnf: "+record.getTnf() +"\n");
				 		/*Getting the Well-Known NDEF Record type*/
				 		//int inttype = record.getType()[0];
				 		//char chartype = (char) inttype;
				 		//status.append((j+1)+"th. Record type: "+ chartype+"\n");
				 		/*for (int k = 0; k < record.getType().length; k++) {
							java.lang.System.out.println(record.getType()[k]);
						}
				 		//status.append((j+1)+"th. Record id: "+record.getId().toString()+"\n");
				 		//status.append((j+1)+"th. Record Contents: "+record.describeContents()+"\n");
				 		
				 		try {
				 			payload = new String(record.getPayload(), 1, record.getPayload().length-1, Charset.forName("UTF-8"));
					 		//status.append((j+1)+"th. Record payload:  "+payload +"\n");
					 		payloadHeader = record.getPayload()[0];
					 		//status.append((j+1)+"th. Record payload header:  "+payloadHeader +"\n");
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

	
	public void onClick(View view) {
	    @SuppressWarnings("unchecked")
	   // ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
	    TagContent content = null;
	    switch (view.getId()) {
		    case R.id.saveButton:
		    	 datasource.open();
		     /*FOR DEBUG ONLY - REMOVE ASAP	
		      String[] comments = new String[] { "Cool", "Very nice", "Hate it" };
		      int nextInt = new Random().nextInt(3);
		      */
		      // save the new tag_content to the database
		    String payloadHeaderDesc = tInfo.getTagRecords().get(0).isWOP() ? " " : tInfo.getTagRecords().get(0).getRecordPayloadHeaderDesc();
		      content = datasource.createContent(tInfo.getTagRecords().get(0).getRecordPayload(),
		    		  payloadHeaderDesc,
		    		  tInfo.getTagRecords().get(0).getRecordPayloadTypeDesc());
		      //status =  (TextView) findViewById(R.id.type);
		      //status.setText(content.toString());
		     // List<TagContent> test = datasource.getAllComments();
		      Toast.makeText(this, "Tag content saved!", Toast.LENGTH_SHORT).show();
		      /*for (int i = 0; i < test.size(); i++) {
				Log.d("List element", "tag_content: " + test.get(i));
			
			}*/
		      	Intent intent = new Intent(this, SaveResult.class);
				intent.putExtra("CONTENT_ID", content.getId());
				intent.putExtra("CONTENT_EDIT", "NEW");
				startActivity(intent);
		      
		      
	      //adapter.add(comment);
	      //datasource.close();
	      
	      /*
	      // Local database
	      InputStream input = new FileInputStream("comments.db");

	      // create directory for backup
	      File dir = new File("/data/data");
	      dir.mkdir();

	      // Path to the external backup
	      OutputStream output = new FileOutputStream(to);

	      // transfer bytes from the Input File to the Output File
	      byte[] buffer = new byte[1024];
	      int length;
	      while ((length = input.read(buffer))>0) {
	          output.write(buffer, 0, length);
	      }

	      output.flush();
	      output.close();
	      input.close();*/
	      datasource.close();
	      datasource.exportDB();
	     
	      break;
	      
		  case R.id.launchButton:
			  Log.d("debug", "Launch  button clicked");
			  Log.d("debug", "Second Record's Payload Type: "+tInfo.getTagRecords().get(1).getRecordPayloadTypeDesc());
			 if (tInfo.getTagRecords().get(1).getRecordPayloadTypeDesc().equalsIgnoreCase("App launcher")) {
			  String appString = tInfo.getTagRecords().get(1).getRecordPayload();
			  startNewActivity(this, appString);
			 }
			  
			  
	      /*
	    case R.id.delete:
	      if (getListAdapter().getCount() > 0) {
	        comment = (Comment) getListAdapter().getItem(0);
	        datasource.deleteComment(comment);
	        adapter.remove(comment);
	      }
	      break;*/
	    }
	    //adapter.notifyDataSetChanged();
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

	public void startNewActivity(Context context, String packageName) {
	    //Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		Intent intent = createIntent(context, tInfo.getTagRecords().get(0).getRecordPayload(), packageName);
	    if (intent != null) {
	        /* We found the activity now start the activity */
	    	
	        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("sms", "04267145067",null));
	        		 emailIntent.putExtra(Intent.EXTRA_SUBJECT, "This is my subject text");
	        		 context.startActivity(Intent.createChooser(emailIntent, null));
	        /*
	        intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
	        intent.putExtra(Intent.EXTRA_EMAIL,"test@mail.com");
		    intent.putExtra(Intent.EXTRA_SUBJECT, "test");
		    intent.putExtra(Intent.EXTRA_TEXT, "Is a test");
		    */
	       context.startActivity(Intent.createChooser(intent, null));
	       //startActivity(intent);
	    } else {
	        /* Bring user to the market or let them choose an app? */
	        intent = new Intent(Intent.ACTION_VIEW);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intent.setData(Uri.parse("market://details?id=" + packageName));
	        startActivity(intent);
	    }
	}
	
	/*Return amount of bytes in use*/
	/*
	public int inUse (NdefMessage[] mssgs){
		int total = 0;
		
		Log.d("debug", "# messages: " + mssgs.length);
		for (int i = 0; i < mssgs.length; i++) {
			total += mssgs[0].getByteArrayLength();
		}
		
		
		return total;
	} */
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//this.finish();
	}
	
	public Intent createIntent(Context context, String payload, String packageName){

		Intent actIntent = null;
		String protocol;
		Log.d("intent", "recordPayloadHeaderDesc: " + tInfo.getTagRecords().get(0).getRecordType());
		if (tInfo.getTagRecords().get(0).getRecordType().equalsIgnoreCase("URI")) {
			protocol = tInfo.getTagRecords().get(0).getRecordPayloadHeaderDesc();
			//parameters[0] 
			
			
			switch (protocol) {
			case "tel:":
				/*The telephone number is the whole payload */
				Log.d("intent", "telephone_number: " + payload);
				actIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(protocol.substring(0, protocol.length()-1), payload ,null));
				break;
				
			case "sms:":
				String number = payload.substring(payload.indexOf(":"), payload.indexOf("?"));
				String text = payload.substring(payload.lastIndexOf("body=")+5, payload.length());
				Log.d("intent", "sms_number: " + number);
				Log.d("intent", "sms_text: " + text);
				actIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(protocol.substring(0, protocol.length()-1), number,null));
				actIntent.putExtra(Intent.EXTRA_TEXT, text);
				break;
				
			case "mailto:":
				/*payload syntax = mail@server.com?subject=text&body=text*/
				String to = payload.substring(0, payload.indexOf("?"));
				String subject = payload.substring(payload.indexOf("subject=")+8, payload.indexOf("&"));
				String body = payload.substring(payload.lastIndexOf("body=")+5, payload.length());
				Log.d("intent", "email_to: " + to);
				Log.d("intent", "email_subject: " + subject);
				Log.d("intent", "email_body: " + body);
				actIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(protocol.substring(0, protocol.length()-1), to ,null));
				actIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				actIntent.putExtra(Intent.EXTRA_TEXT, body);
				break;
			case "geo:":
				Log.d("intent", "geo_location: " + payload);
				 actIntent = new Intent(Intent.ACTION_VIEW);
				 actIntent.setData(Uri.parse(payload));
				 
				break;
			default:
				actIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
				actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				break;
			}
		}
		
		return actIntent;
	}
}