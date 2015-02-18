package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
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
import android.widget.TextView;

import com.example.objetos.ContentTag;
import com.example.objetos.CustomAdapater;
import com.example.objetos.FilterAdapter;
import com.example.objetos.FilterKind;
import com.example.objetos.FilterLayout;
import com.example.objetos.TagAdapter;
import com.example.objetos.TagContent;
import com.example.objetos.TagContentDataSource;

public class TagsMain extends ListActivity {

	private TagContentDataSource datasource;
	private TextView emptyDB;
	private ListView contentList;
	private TagUIContent[] arrayContents;
	private CustomAdapater adapterAdapater;
	private List<TagUIContent> tagUIContents;
	private CustomDialog dialog;
	private FilterAdapter filterListAdapter;
	private FilterLayout filterLayout;
	private String[] selectedFilters = new String[6];
	private ArrayList<String> selectedTagFilters;
	private int selectedCount = 0;
	private String fromSearch;
	private Menu actionsMenu;
	private boolean filtered = false; // Filtered by type
	private FilterLayout filterTagLayout;
	private FilterAdapter filterTagListAdapter;
	private int FilteredBy; // 1 - Type | 2 - Tag
	private boolean tagfiltered = false; // Filtered by tag
	private String dialogTitle;
	private int TypeFilter; // Tag Filter On
	
	private int TagFilter ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		selectedTagFilters = new ArrayList<String>();
		datasource = new TagContentDataSource(this);
		datasource.open();

		// contentList = (ListView)findViewById(R.id.contentList);
		/* FOR DEBUG ONLY - ERASE ASAP */
		List<TagContent> test = datasource.getAllComments();
		// Toast.makeText(this, "Tag content saved!",
		// Toast.LENGTH_SHORT).show();
		for (int i = test.size() - 1; i >= 0; i--) {
			Log.d("List element", "tag_content: " + test.get(i));
		}
		/**/

		if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			String query = getIntent().getStringExtra(SearchManager.QUERY);
			Log.d("debug search", "OC called by search widget . Query: "
					+ query);

			tagUIContents = datasource.getContentFiltered(String.valueOf("'"
					+ query + "'"));
		} else {
			tagUIContents = datasource.getTagUIContents();

		}

		arrayContents = new TagUIContent[tagUIContents.size()];
		for (int i = 0; i < tagUIContents.size(); i++) {
			arrayContents[i] = tagUIContents.get(i);
		}

		adapterAdapater = new CustomAdapater(this, tagUIContents);
		setListAdapter(adapterAdapater);

		// adapterAdapater.notifyDataSetChanged();

		// setListViewHeightBasedOnChildren(contentList);
		Log.d("debug", "Content length " + tagUIContents.size());

		/*
		 * if (contentList.getChildCount() > 0) { contentList.removeAllViews();
		 * }
		 */

		/*
		 * if (tagUIContents.size() > 0) { for (int i = tagUIContents.size()-1;
		 * i >= 0; i--) { contentList.addView(tagUIContents.get(i)); } } else {
		 * emptyDB = new TextView(this);
		 * emptyDB.setText("No recent tag content found!");
		 * emptyDB.setGravity(1); contentList.addView(emptyDB); }
		 */
		List<FilterKind> filterList = getContentFilter();
		filterLayout = new FilterLayout(this);
		// ListView filterListView = (ListView)findViewById(R.id.filterList);
		filterListAdapter = new FilterAdapter(this, filterList);
		filterLayout.getFilterList().setAdapter(filterListAdapter);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tags_main, menu);
		actionsMenu = menu;
		/*
		 * SearchManager searchManager = (SearchManager)
		 * getSystemService(Context.SEARCH_SERVICE); SearchView searchView =
		 * (SearchView) menu.findItem(R.id.action_search).getActionView(); //
		 * Assumes current activity is the searchable activity
		 * searchView.setSearchableInfo
		 * (searchManager.getSearchableInfo(getComponentName()));
		 * searchView.setIconifiedByDefault(false); // Do not iconify the
		 * widget; expand it by default
		 */
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

			FilteredBy = 1;
			TypeFilter = 1;
			String filterText = "";
			TextView filterTextView = (TextView) findViewById(R.id.action_filter);
			List<FilterKind> filterList = getContentFilter();
			Log.d("debug filters list size", filterList.size() + "");
			Log.d("debug filterAdapter size", filterListAdapter.getCount() + "");
			filterLayout = new FilterLayout(this);
			dialogTitle = "Filter by type";
			int selectedC = selectedCount;
			Log.d("debug filter button", filterTextView.getText().toString());

			// filterTextView.getText().toString() == "ON"
			if (filtered) {
				/*
				 * for (int k = 0; k < filterListAdapter.getCount();k++) {
				 * Log.d("debug adapterselection before setadapter", "is "+
				 * filterListAdapter
				 * .getItem(k).getContentDesc().getText().toString()+
				 * " Checked? "+
				 * filterListAdapter.getItem(k).getContentCheck().isChecked());
				 * if
				 * (filterListAdapter.getItem(k).getContentCheck().isChecked())
				 * { filterList.get(k).getContentCheck().setChecked(true); } }
				 * 
				 * Log.d("debug filters list ", "selectedCount: " + selectedC +
				 * "");
				 */
				filterLayout.getFilterList().setVisibility(View.INVISIBLE);
				filterLayout.getFilterImageView().setVisibility(View.VISIBLE);
				for (String filterKind : selectedFilters) {
					if (filterKind != "" && filterKind != null) {
						filterText += filterKind + ",";
					}

				}
				filterLayout.getFilterTextView().setText(
						"Filtered by: "
								+ filterText.substring(0,
										filterText.length() - 1));
				filterLayout.getFilterTextView().setVisibility(View.VISIBLE);
				filterLayout.getFilterButton().setText("Remove filter");
				dialogTitle = "Filtered by type";
			}

			/*
			 * for (FilterKind filterKind : filterList) {
			 * Log.d("debug filterlist before setadapter", "is "+
			 * filterKind.getContentDesc().getText().toString()+ " Checked? "+
			 * filterKind.getContentCheck().isChecked()); } for (int j = 0; j <
			 * filterListAdapter.getCount(); j++) {
			 * Log.d("debug adapterselection after setadapter", "is "+
			 * filterListAdapter
			 * .getItem(j).getContentDesc().getText().toString()+ " Checked? "+
			 * filterListAdapter.getItem(j).getContentCheck().isChecked()); }
			 */

			filterListAdapter = new FilterAdapter(this, filterList);
			filterLayout.getFilterList().setAdapter(filterListAdapter);
			filterListAdapter.notifyDataSetChanged();

			// ListView filterListView =
			// (ListView)findViewById(R.id.filterList);

			dialog = new CustomDialog(this);
			//dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.setTitle(dialogTitle);
			dialog.setContentView(filterLayout);
			dialog.show();

			return true;
		}

		if (id == R.id.action_search) {
			// Get the SearchView and set the searchable configuration
			onSearchRequested();
		}

		if (id == R.id.action_filterTag) {

			FilteredBy = 2;
			TagFilter = 1;
			datasource.open();
			String filterText = "";
			TextView filterTextView = (TextView) findViewById(R.id.action_filterTag);
			List<FilterKind> filterTagList = getContentTagFilter();
			// Log.d("debug filters list size", filterList.size() + "");
			// Log.d("debug filterAdapter size", filterListAdapter.getCount() +
			// "");
			filterTagLayout = new FilterLayout(this);
			dialogTitle = "Filter by tag";
			// int selectedC = selectedCount;
			// Log.d("debug filter button",
			// filterTextView.getText().toString());

			// filterTextView.getText().toString() == "ON"
			
			  if (tagfiltered) {
				  filterTagLayout.getFilterList().setVisibility(View.INVISIBLE);
				  filterTagLayout.getFilterImageView().setBackgroundResource(R.drawable.filter_v3);
				  filterTagLayout.getFilterImageView().setVisibility(View.VISIBLE);
				  for (String filterKind : selectedTagFilters) { 
					  if (filterKind != "" && filterKind != null) {
					  filterText += filterKind + ","; 
				  }
					  dialogTitle = "Filtered by tag";
			  } 
				  
			filterTagLayout.getFilterTextView().setText( "Filtered by: " + filterText.substring(0, filterText.length() - 1));
			filterTagLayout.getFilterTextView().setVisibility(View.VISIBLE);
			filterTagLayout.getFilterButton().setText("Remove filter"); }
		
			filterTagListAdapter = new FilterAdapter(this, filterTagList);
			filterTagLayout.getFilterList().setAdapter(filterTagListAdapter);
			filterTagListAdapter.notifyDataSetChanged();

			// ListView filterListView =
			// (ListView)findViewById(R.id.filterList);

			dialog = new CustomDialog(this);
			//dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.setTitle(dialogTitle);
			dialog.setContentView(filterTagLayout);
			dialog.show();
			datasource.close();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		datasource.open();
		if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			String query = getIntent().getStringExtra(SearchManager.QUERY);
			Log.d("debug search", "OR called by search widget. Query: " + query);
			fromSearch = query;
			tagUIContents = datasource.getContentbySearch(query);
		} else {
			tagUIContents = datasource.getTagUIContents();
		}
		Log.d("debug resumed", "Content length " + tagUIContents.size());

		adapterAdapater.clear();
		adapterAdapater.addAll(tagUIContents);
		adapterAdapater.notifyDataSetChanged();
		datasource.close();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		datasource.open();

		tagUIContents = datasource.getTagUIContents();
		adapterAdapater.notifyDataSetChanged();
		datasource.close();
	}

	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.filterButton:
				datasource.open();
				switch (FilteredBy) {
					case 1:
						selectedCount = 0;
						boolean removeFilter = false;
						TextView filterTextView = (TextView) findViewById(R.id.action_filter);
		
						String filters = "";
						for (int i = 0; i < filterListAdapter.getCount(); i++) {
							if (filterListAdapter.getItem(i).getContentCheck()
									.isChecked()) {
								selectedCount++;
							}
						}
		
						int c = 0;
						for (int i = 0; i < filterListAdapter.getCount(); i++) {
							if (filterListAdapter.getItem(i).getContentCheck()
									.isChecked()) {
								filters += "'"
										+ filterListAdapter.getItem(i).getContentDesc()
												.getText() + "'";
								selectedFilters[c] = filterListAdapter.getItem(i)
										.getContentDesc().getText().toString();
								Log.d("debug filters list ", selectedFilters[c]);
		
								if (c < selectedCount - 1) {
									filters += ",";
									c++;
								}
							}
		
						}
		
						// Log.d("debug filters list ", filters);
						// Log.d("debug filters list ",
						// filterListAdapter.getItem(0).getContentCheck().isChecked()+
						// "");
		
						if (filterLayout.getFilterButton().getText().toString()
								.toString() == "Remove filter") {
							filters = "";
							for (int i = 0; i < selectedFilters.length; i++) {
								selectedFilters[i] = "";
							}
							for (int j = 0; j < filterListAdapter.getCount(); j++) {
								filterListAdapter.getItem(j).getContentCheck()
										.setChecked(false);
							}
							removeFilter = true;
						}
						if (filters == "") {
							removeFilter = true;
						}
						TypeFilter = 0;
						adapterAdapater.addAll(getFilteredList(removeFilter));
						adapterAdapater.notifyDataSetChanged();
		
						if (filters != "") {
		
							// filterTextView.setText("ON");
							if (actionsMenu != null) {
								Log.d("debug filters button ", "Menu found");
								actionsMenu.findItem(R.id.action_filter).setTitle("ON");
								actionsMenu.findItem(R.id.action_filter).setIcon(
										R.drawable.ic_action_filter_on);
								filtered = true;
								TypeFilter = 1;
							}
							// menuTags.findItem(R.id.action_filter);
						}
		
						dialog.dismiss();
		
						if (filterLayout.getFilterButton().getText().toString()
								.toString() == "Remove filter") {
							// filterTextView.setText("Filter");
							filterLayout.getFilterButton().setText("Filter");
							selectedCount = 0;
							filtered = false;
							
							actionsMenu.findItem(R.id.action_filter).setIcon(
									R.drawable.ic_action_filter_off);
						}
		
						break; // END CASE FILTERED BY = 1
					case 2:
						selectedCount = 0;
						boolean removeTagFilter = false;
								
						String tagfilters = "";
						for (int i = 0; i < filterTagListAdapter.getCount(); i++) {
							if (filterTagListAdapter.getItem(i).getContentCheck()
									.isChecked()) {
								selectedCount++;
							}
						}
		
						int count = 0;
						for (int i = 0; i < filterTagListAdapter.getCount(); i++) {
							if (filterTagListAdapter.getItem(i).getContentCheck()
									.isChecked()) {
								tagfilters += "'"
										+ filterTagListAdapter.getItem(i).getContentDesc()
												.getText() + "'";
								selectedTagFilters.add(filterTagListAdapter.getItem(i).getContentDesc().getText().toString());
								
								Log.d("debug filters list ", selectedTagFilters.get(count).toString());
		
								if (count < selectedCount - 1) {
									tagfilters += ",";
									count++;
								}
							}
		
						}
		
						// Log.d("debug filters list ", filters);
						// Log.d("debug filters list ",
						// filterListAdapter.getItem(0).getContentCheck().isChecked()+
						// "");
		
						if (filterTagLayout.getFilterButton().getText().toString()
								.toString() == "Remove filter") {
							tagfilters = "";
							selectedTagFilters.clear();
							for (int j = 0; j < filterTagListAdapter.getCount(); j++) {
								filterTagListAdapter.getItem(j).getContentCheck()
										.setChecked(false);
							}
							removeTagFilter = true;
						}
						if (tagfilters == "") {
							removeTagFilter = true;
						}
						TagFilter = 0;
						adapterAdapater.addAll(getFilteredList(removeTagFilter)); 
						adapterAdapater.notifyDataSetChanged();
		
						if (tagfilters != "") {
		
							// filterTextView.setText("ON");
							if (actionsMenu != null) {
								//Log.d("debug filters button ", "Menu found");
								actionsMenu.findItem(R.id.action_filterTag).setTitle("ON");
								actionsMenu.findItem(R.id.action_filterTag).setIcon(R.drawable.ic_action_filter_on);
								tagfiltered = true;
								TagFilter = 1;
							}
							// menuTags.findItem(R.id.action_filter);
						}
		
						dialog.dismiss();
		
						if (filterTagLayout.getFilterButton().getText().toString()
								.toString() == "Remove filter") {
							// filterTextView.setText("Filter");
							filterTagLayout.getFilterButton().setText("Filter");
							selectedCount = 0;
							tagfiltered = false;
							
							
							actionsMenu.findItem(R.id.action_filterTag).setIcon(R.drawable.ic_action_filter_tag);
						}
		
								
						
						break; // END CASE FILTERED BY = 2
				default:
					break;
				} // END SWITCH FILTERED BY
				datasource.close();
			break;

		default:
			break;
		} // END SWITCH VIEW ID
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
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	public List<FilterKind> getContentFilter() {

		String[] kind = getResources().getStringArray(R.array.kinds_array);
		List<FilterKind> contentFilters = new ArrayList<FilterKind>();
		int id = 1;
		for (String kindString : kind) {
			FilterKind nContentFilter = new FilterKind(this);
			nContentFilter.getContentDesc().setText(kindString);
			nContentFilter.getContentId().setText(String.valueOf(id));
			nContentFilter.getContentCheck().setChecked(false);
			nContentFilter.setKindIcon(kindString);
			// SET CONTENT FILTER ICON
			contentFilters.add(nContentFilter);
			id++;
		}

		return contentFilters;
	}

	public List<FilterKind> getContentTagFilter() {

		List<ContentTag> kind = datasource.getAllTags();
		List<FilterKind> contentFilters = new ArrayList<FilterKind>();
		for (ContentTag contentTag : kind) {
			FilterKind nContentFilter = new FilterKind(this);
			nContentFilter.getContentDesc().setText(contentTag.getName());
			nContentFilter.getContentId().setText(
					String.valueOf(contentTag.getId()));
			nContentFilter.getContentCheck().setChecked(false);
			nContentFilter.setKindIcon("Tag");
			contentFilters.add(nContentFilter);
		}
		// datasource.close();
		return contentFilters;
	}

	public List<TagUIContent> getFilteredList(Boolean rFilters) {

		datasource.open();
		/*Log.d("debug filters list NF ", "selectedFilters Size: "+ selectedFilters.length);
		Log.d("debug filters list NF ", "selectedFilters Size: "+ selectedTagFilters.size());
		Log.d("debug filters on", rFilters+" <-- rfilters");
		Log.d("debug filters on","TF: "+TagFilter +" - TpF: "+TypeFilter);
		Log.d("debug filters on","TF: "+tagfiltered +" - TpF: "+filtered);*/
		List<TagUIContent> tagUIContents = new ArrayList<TagUIContent>();
		int j;

		if (rFilters) {
			if (fromSearch != null) {
				tagUIContents = datasource.getContentbySearch(fromSearch);
			} else {
				adapterAdapater.clear();
				adapterAdapater.addAll(datasource.getTagUIContents());
				
				if (TagFilter == 1) {
					//Log.d("debug filters on", selectedTagFilters.size()+" <-- selectedTagFilters Size");
					for (int i = 0; i < selectedTagFilters.size(); i++) {
						j = 0;
						int aas = adapterAdapater.getCount();
						while (j < aas) {
							if (adapterAdapater.getItem(j).getContentTags().getText().toString().contains(selectedTagFilters.get(i))) {
								if (!tagUIContents.contains(adapterAdapater.getItem(j))) {
									tagUIContents.add(adapterAdapater.getItem(j));
								}
							}
							j++;
						}
					}
				}
				else if (TypeFilter == 1) {
					for (int i = 0; i < selectedFilters.length; i++) {
						j = 0;
						while (j < adapterAdapater.getCount()) {
							if (adapterAdapater.getItem(j).getContentDesc().getText()
									.toString().equals(selectedFilters[i])) {
								tagUIContents.add(adapterAdapater.getItem(j));
							}
							j++;
						}
					}
				}
				else{
					tagUIContents = datasource.getTagUIContents();
				}
				
			}

		} else {
			if (FilteredBy == 1) {
				for (int i = 0; i < selectedFilters.length; i++) {
					j = 0;
					while (j < adapterAdapater.getCount()) {
						//Log.d("debug filters list NF ", adapterAdapater.getItem(j).getContentDesc().getText().toString()+ " :: " + selectedFilters[i]);
						if (adapterAdapater.getItem(j).getContentDesc().getText()
								.toString().equals(selectedFilters[i])) {
							tagUIContents.add(adapterAdapater.getItem(j));
						}
						j++;
					}
				}
			}
			if (FilteredBy == 2) {
				
				for (int i = 0; i < selectedTagFilters.size(); i++) {
					j = 0;
					int aas = adapterAdapater.getCount();
					while (j < aas) {
						//Log.d("debug filters list AAS ", "adapterAdapter Size: "+ adapterAdapater.getCount());
						//Log.d("debug filters list AAS ", adapterAdapater.getItem(j).getContentDesc().getText().toString()+ " :: " + selectedTagFilters.get(i).toString());
						if (adapterAdapater.getItem(j).getContentTags().getText().toString().contains(selectedTagFilters.get(i))) {
							if (!tagUIContents.contains(adapterAdapater.getItem(j))) {
								tagUIContents.add(adapterAdapater.getItem(j));
								//adapterAdapater.remove(adapterAdapater.getItem(j));
							}
							
						}
						j++;
					}
				}
			}

		}

		//Log.d("debug filters list NF ", "Size: " + tagUIContents.size());
		adapterAdapater.clear();
		datasource.close();
		return tagUIContents;
	}

}