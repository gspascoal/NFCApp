package com.example.proyecto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.objetos.TagContent;
import com.example.objetos.TagContentAdapter;
import com.example.objetos.TagContentDataSource;

public class SaveResult extends Activity {

	private String contentId;
	private TagContentDataSource datasource;
	private TextView cDescription;
	private TextView cPayload;
	private TextView cId;
	private ImageView cIcon;
	public Map<String, Integer> PLTI = new LinkedHashMap<String, Integer>();
	public Map<String, String> DBR = new LinkedHashMap<String, String>();
	private TagContent nTagContent;
	private ArrayList<TagContent> contents;
	private TagContentAdapter tagContentAdapter;
	private ListView savedContents;
	private TextView saveResultText;
	private TextView savedContentLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_result);
		
		DBR.put("-1",getResources().getString(R.string.nA));
		DBR.put("0",getResources().getString(R.string.link));
		DBR.put("1",getResources().getString(R.string.mail));
		DBR.put("2",getResources().getString(R.string.sms));
		DBR.put("3",getResources().getString(R.string.tel));
		DBR.put("4",getResources().getString(R.string.geoLoc));
		DBR.put("5",getResources().getString(R.string.plainText));
		DBR.put("6",getResources().getString(R.string.thesis));
		DBR.put("7",getResources().getString(R.string.report));

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

		cDescription = (TextView) findViewById(R.id.contentDescription);
		cPayload = (TextView) findViewById(R.id.contentPayload);
		cIcon = (ImageView) findViewById(R.id.contentIcon);
		cId = (TextView) findViewById(R.id.contentId);
		
		savedContents = (ListView)findViewById(R.id.savedContents);
		contents = new ArrayList<TagContent>();
		saveResultText =  (TextView)findViewById(R.id.saveResultText);
		savedContentLabel =  (TextView)findViewById(R.id.savedContentLabel);
		
		datasource = new TagContentDataSource(this);
		datasource.open();
		
		if (getIntent().getStringExtra("CONTENT_EDIT").equals("RESTORED")) {
			
			if(getIntent().getBundleExtra("CONTENT_LIST_BUNDLE") != null) {
				Bundle b = getIntent().getBundleExtra("CONTENT_LIST_BUNDLE");
				contents =  (ArrayList<TagContent>) b.getSerializable("CONTENT_LIST");
				Log.d("debug extra list SR", contents.size()+"");
				
				if (contents.size() == 0) {
					saveResultText.setText(R.string.resultTextB);
					savedContentLabel.setVisibility(View.GONE);
				}

			}
		}
		
		if (getIntent().getStringExtra("CONTENT_EDIT").equals("EDIT")) {

			if (getIntent().getStringExtra("CONTENT_ID") != null) {
				contentId = getIntent().getStringExtra("CONTENT_ID");
				Log.d("debug edit", contentId + "");
			}

			nTagContent = datasource.getContentById(contentId);
			contents.add(nTagContent);

		} 
		if (getIntent().getStringExtra("CONTENT_EDIT").equals("NEW")){

			nTagContent = datasource.getAllComments().get(
					datasource.getAllComments().size() - 1);
			contents.add(nTagContent);

		}
		
		
		tagContentAdapter = new TagContentAdapter(this, contents);
		savedContents.setAdapter(tagContentAdapter);
		
		/*
		cIcon.setBackgroundResource(PLTI.get(DBR.get(nTagContent.getPayloadType())));
		cDescription.setText(DBR.get(nTagContent.getPayloadType()));
		cPayload.setText(nTagContent.getPayloadHeader()+nTagContent.getPayload());
		cId.setText(String.valueOf(nTagContent.getId()));
		*/
		datasource.close();

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.save_result, menu);
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

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.saveButton:
			Intent intent = new Intent(SaveResult.this, MainActivity.class);
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
			View rootView = inflater.inflate(R.layout.fragment_save_result,
					container, false);
			return rootView;
		}
	}

}
