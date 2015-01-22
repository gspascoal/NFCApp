package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.objetos.CustomAdapater;
import com.example.objetos.FilterAdapter;
import com.example.objetos.FilterLayout;
import com.example.objetos.TagContent;
import com.example.objetos.TagContentDataSource;
import com.example.objetos.FilterKind;



public class TagsMain extends Activity {

	private TagContentDataSource datasource;
	private TextView emptyDB;
	private ListView contentList;
	private TagUIContent[] arrayContents;
	private CustomAdapater adapterAdapater;
	private List<TagUIContent> tagUIContents;
	private CustomDialog dialog;
	private FilterAdapter filterListAdapter;
	private FilterLayout filterLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		datasource = new TagContentDataSource(this);
	    datasource.open();
	    
	    contentList = (ListView)findViewById(R.id.contentList);
	    
	    List<TagContent> test = datasource.getAllComments();
	      //Toast.makeText(this, "Tag content saved!", Toast.LENGTH_SHORT).show();
	      for (int i = test.size()-1; i >= 0; i--) {
			Log.d("List element", "tag_content: " + test.get(i));
		}
	    
	    
	    tagUIContents = datasource.getTagUIContents();
	    arrayContents = new TagUIContent[tagUIContents.size()];
	    
		   for (int i = 0; i < tagUIContents.size(); i++) {
			arrayContents[i] = tagUIContents.get(i);
		}
		    
	    adapterAdapater = new CustomAdapater(this,tagUIContents);
	    contentList.setAdapter(adapterAdapater);
	    //adapterAdapater.notifyDataSetChanged();
	    
	    //setListViewHeightBasedOnChildren(contentList);
	    Log.d("debug", "Content length "+tagUIContents.size() );
	    /*
	    if (contentList.getChildCount() > 0) {
			contentList.removeAllViews();
		}*/
	  
	    /*
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
	    List<FilterKind> filterList = getContentFilter();
		filterLayout =  new FilterLayout(this);
		//ListView filterListView = (ListView)findViewById(R.id.filterList);
		filterListAdapter = new FilterAdapter(this, filterList);
		filterLayout.getFilterList().setAdapter(filterListAdapter);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tags_main, menu);
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
		if (id == R.id.action_filter) {
			
			
			List<FilterKind> filterList = getContentFilter();
			filterLayout =  new FilterLayout(this);
			//ListView filterListView = (ListView)findViewById(R.id.filterList);
			filterListAdapter = new FilterAdapter(this, filterList);
			filterLayout.getFilterList().setAdapter(filterListAdapter);
			
	
			dialog = new CustomDialog(this);
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(filterLayout);
			
			dialog.show();
			
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		 datasource.open();
		 Log.d("debug resumed", "Content length "+tagUIContents.size() );
		 adapterAdapater.clear();
		 adapterAdapater.addAll(datasource.getTagUIContents());
		 adapterAdapater.notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 datasource.open();

		 tagUIContents = datasource.getTagUIContents();
		 adapterAdapater.notifyDataSetChanged();
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.filterButton:
			String filters = "";
			for (int i = 0; i < filterListAdapter.getCount(); i++) {
				if (filterListAdapter.getItem(i).getContentCheck().isChecked()) { 
					filters += filterListAdapter.getItem(i).getContentDesc().getText()+",";
				}
				
			}
			Log.d("debug filters list ",filters.substring(0, filters.length()-1));
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
			View rootView = inflater.inflate(R.layout.fragment_tags_main,
					container, false);
			return rootView;
		}
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {

	    ListAdapter listAdapter = listView.getAdapter(); 
	    if (listAdapter == null) {
	        return;
	    }

	    int totalHeight = 0;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        View listItem = listAdapter.getView(i, null, listView);
	        listItem.measure(0, MeasureSpec.UNSPECIFIED);
	        totalHeight += listItem.getMeasuredHeight();
	    }

	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	} 
	
	public List<FilterKind> getContentFilter(){
		
		String[] kind = getResources().getStringArray(R.array.kinds_array);
		List<FilterKind> contentFilters = new ArrayList<FilterKind>();
		int id = 1;
		for (String kindString : kind) {
			FilterKind nContentFilter = new FilterKind(this);
			nContentFilter.getContentDesc().setText(kindString);
			nContentFilter.getContentId().setText(String.valueOf(id));
			//SET CONTENT FILTER ICON
			contentFilters.add(nContentFilter);
			id++;
		}
		
		return contentFilters;
	}
}