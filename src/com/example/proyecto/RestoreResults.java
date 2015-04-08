package com.example.proyecto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Text;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.objetos.TagContent;
import com.example.objetos.TagContentAdapter;
import com.example.objetos.TagContentDataSource;

public class RestoreResults extends Activity implements OnClickListener{

	private String content;
	private TextView imageContent;
	public Map<String, String> DBR = new LinkedHashMap<String, String>(); // DataBaseResource
	public Map<String, Integer> PLTI = new LinkedHashMap<String, Integer>();
	private LinearLayout savedQRDataLayout;
	private TextView cDescription;
	private TextView cPayload;
	private ImageView myImage;
	private ImageView cIcon;
	private TextView cId;
	private TagContent nTagContent;
	private String contentHeader;
	private TagContentDataSource datasource;
	private String typeCode;
	private String payload;
	private String header;
	private LinearLayout singleContent;
	private ListView contents;
	private int restoreMode;
	private List<TagContent> tagContents;
	private Button saveButton;
	private TextView restoreTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restore_results);		
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		contentHeader = "NFCTag (c) 2015 \n";
		
		datasource = new TagContentDataSource(this);
		
		Button button1 = (Button) findViewById(R.id.restoreQRsave);
		button1.setOnClickListener(this);
		
		DBR.put("0", getResources().getString(R.string.link));
		DBR.put("1", getResources().getString(R.string.mail));
		DBR.put("2", getResources().getString(R.string.sms));
		DBR.put("3", getResources().getString(R.string.tel));
		DBR.put("4", getResources().getString(R.string.geoLoc));
		DBR.put("5", getResources().getString(R.string.plainText));
		DBR.put("6", getResources().getString(R.string.thesis));
		DBR.put("7", getResources().getString(R.string.report));
		
		
		PLTI.put(getResources().getString(R.string.nA), R.drawable.default64);
		PLTI.put(getResources().getString(R.string.link), R.drawable.link64);
		PLTI.put(getResources().getString(R.string.link), R.drawable.link64);
		PLTI.put(getResources().getString(R.string.tel), R.drawable.tel64);
		PLTI.put(getResources().getString(R.string.mail), R.drawable.mail64);
		PLTI.put(getResources().getString(R.string.sms), R.drawable.sms64);
		PLTI.put(getResources().getString(R.string.geoLoc), R.drawable.geo64);
		PLTI.put(getResources().getString(R.string.bussinesCard), R.drawable.business_cardb24);
		PLTI.put(getResources().getString(R.string.plainText), R.drawable.text64);
		PLTI.put(getResources().getString(R.string.thesis), R.drawable.thesis64);
		PLTI.put(getResources().getString(R.string.report), R.drawable.default64);

		imageContent = (TextView)findViewById(R.id.restoreResultContent);
		savedQRDataLayout = (LinearLayout)findViewById(R.id.savedQRContent);
		singleContent = (LinearLayout)findViewById(R.id.restoredQRContent);
		contents = (ListView)findViewById(R.id.contents);
		saveButton = (Button)findViewById(R.id.restoreQRsave);
		restoreTitle = (TextView)findViewById(R.id.restoreResultTitle);
		
		
		myImage = (ImageView) findViewById(R.id.backupQRImage);
		cDescription = (TextView) findViewById(R.id.contentDescription);
		cPayload = (TextView) findViewById(R.id.contentPayload);
		cIcon = (ImageView) findViewById(R.id.contentIcon);
		cId = (TextView) findViewById(R.id.contentId);
				
		if (getIntent().getStringExtra("IMAGE_CONTENT") != null) {
			content= getIntent().getStringExtra("IMAGE_CONTENT");
			imageContent.setText(content);
			
			
			if (!content.contains("Saved at") && content.contains(contentHeader) ) {
				restoreMode = 1;
				typeCode = content.substring(content.indexOf("Type:")-2, content.indexOf("Type:")-1);
				header = content.substring(content.indexOf("Header:")+7,  content.indexOf("Content:"));
				payload = content.substring(content.indexOf("Content:")+9, content.length());
				
				
				Log.d("debug content", typeCode+" - "+DBR.get(typeCode));
				cIcon.setBackgroundResource(PLTI.get(DBR.get(typeCode)));
				cDescription.setText(DBR.get(typeCode));
				cPayload.setText(header+payload);
				cId.setText(String.valueOf(-2));
				singleContent.setVisibility(View.VISIBLE);
				saveButton.setText(getResources().getString(R.string.saveButton));
				
			}
			else{
				if (content.contains("Saved at") && content.contains(contentHeader)) {
					restoreMode = 2;
					StringTokenizer st = new StringTokenizer(content, "\n");
					tagContents = new ArrayList<TagContent>();
					while (st.hasMoreElements()) {
						String line = st.nextElement().toString();
						Log.d("debug content", "Item: "+line);
						if (line.contains("Item:")) {
							typeCode = line.substring(line.indexOf("Type:")-2, line.indexOf("Type:")-1);
							header = line.substring(line.indexOf("Header:")+7,  line.indexOf("Content:"));
							payload = line.substring(line.indexOf("Content:")+9, line.length());
							
							TagContent nTagContent  = new TagContent(payload,header,typeCode);
							tagContents.add(nTagContent);
							TagContentAdapter tagContentAdapter = new TagContentAdapter(this, tagContents);
							contents.setAdapter(tagContentAdapter);
							saveButton.setText(getResources().getString(R.string.saveButton2));
						}
						
					}
				}
				else
				{
					restoreMode=0;
					restoreTitle.setText(getResources().getString(R.string.rr_titleB));
					saveButton.setText(getResources().getString(R.string.doneButton));
				}
				
			}
			
		}
		
		Log.d("debug restorMode", restoreMode+"");
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.restore_results, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_restore_results,
					container, false);
			return rootView;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.restoreQRsave:
			 datasource.open();
			 if (restoreMode == 0) {
				 Intent intent = new Intent(this, ExtrasMain.class);
				 intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				 startActivity(intent);
			}
			 if (restoreMode == 1) {
				 TagContent tagContent = datasource.createContent(payload,
			    		  header,
			    		 typeCode);

			      if (tagContent != null) {
			    	  //Toast.makeText(this, getResources().getString(R.string.resultText), Toast.LENGTH_SHORT).show();
				      	Intent intent = new Intent(this, SaveResult.class);
						intent.putExtra("CONTENT_ID", tagContent.getId());
						intent.putExtra("CONTENT_EDIT", "NEW");
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(intent);
						finish();
				} else {
					//Toast.makeText(this,getResources().getString(R.string.resultTextA), Toast.LENGTH_SHORT).show();
				}
			     
			}
			 if (restoreMode == 2) {
				 ArrayList<TagContent> restoredContents = new ArrayList<TagContent>();
				for (TagContent tagContent : tagContents) {
					TagContent ntagContent = datasource.createContent(tagContent.getPayload(),
				    		 tagContent.getPayloadHeader(),
				    		 tagContent.getPayloadType());

				      if (ntagContent != null) {
				    	  restoredContents.add(ntagContent);
					} 
				}
				
				 
					//Toast.makeText(this, getResources().getString(R.string.resultText), Toast.LENGTH_SHORT).show();
					Log.d("debug extra list RD", restoredContents.size()+"");
					
			      	Intent intent = new Intent(this, SaveResult.class);
			      	Bundle bundle = new Bundle();
			      	bundle.putSerializable("CONTENT_LIST", restoredContents);
					intent.putExtra("CONTENT_LIST_BUNDLE", bundle);
					intent.putExtra("CONTENT_EDIT", "RESTORED");
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
					finish();
			}
			 
			
			 
			 datasource.close();
		     
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this, ExtrasMain.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
		//this.finish();
	}

}
