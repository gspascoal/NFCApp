package com.example.proyecto;

import java.util.ArrayList;
import java.util.List;

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

import com.example.objetos.CustomAdapater;
import com.example.objetos.FilterAdapter;
import com.example.objetos.FilterKind;
import com.example.objetos.FilterLayout;
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
	private int selectedCount = 0;
	private String fromSearch;
	private Menu actionsMenu;
	private boolean filtered = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);

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

			String filterText = "";
			TextView filterTextView = (TextView) findViewById(R.id.action_filter);
			List<FilterKind> filterList = getContentFilter();
			Log.d("debug filters list size", filterList.size() + "");
			Log.d("debug filterAdapter size", filterListAdapter.getCount() + "");
			filterLayout = new FilterLayout(this);

			int selectedC = selectedCount;
			Log.d("debug filter button", filterTextView.getText().toString());
			
			//filterTextView.getText().toString() == "ON"
			if (filtered ) {
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
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(filterLayout);
			dialog.show();

			return true;
		}

		if (id == R.id.action_search) {
			// Get the SearchView and set the searchable configuration
			onSearchRequested();
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
			selectedCount = 0;
			boolean removeFilter = false;
			TextView filterTextView = (TextView) findViewById(R.id.action_filter);
			

			String filters = "";
			for (int i = 0; i < filterListAdapter.getCount(); i++) {
				if (filterListAdapter.getItem(i).getContentCheck().isChecked()) {
					selectedCount++;
				}
			}

			int c = 0;
			for (int i = 0; i < filterListAdapter.getCount(); i++) {
				if (filterListAdapter.getItem(i).getContentCheck().isChecked()) {
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
			// filterListAdapter.getItem(0).getContentCheck().isChecked()+ "");

			if (filterLayout.getFilterButton().getText().toString().toString() == "Remove filter") {
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

			adapterAdapater.addAll(getFilteredList(removeFilter));
			adapterAdapater.notifyDataSetChanged();

			if (filters != "") {
				
				//filterTextView.setText("ON");
				if (actionsMenu != null) {
					Log.d("debug filters button ", "Menu found");
					actionsMenu.getItem(1).setTitle("ON");
					actionsMenu.getItem(1).setIcon(R.drawable.ic_action_filter_on);
					filtered  = true;
				}
				//menuTags.findItem(R.id.action_filter);
			}

			dialog.dismiss();

			if (filterLayout.getFilterButton().getText().toString().toString() == "Remove filter") {
				//filterTextView.setText("Filter");
				filterLayout.getFilterButton().setText("Filter");
				selectedCount = 0;
				filtered = false;
				actionsMenu.getItem(1).setIcon(R.drawable.ic_action_filter_off);
			}
			datasource.close();
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

	public List<TagUIContent> getFilteredList(Boolean rFilters) {

		datasource.open();
		Log.d("debug filters list NF ", "selectedFilters Size: "
				+ selectedFilters.length);
		List<TagUIContent> tagUIContents = new ArrayList<TagUIContent>();
		int j;

		if (rFilters) {
			if (fromSearch != null) {
				tagUIContents = datasource.getContentbySearch(fromSearch);
			} else {
				tagUIContents = datasource.getTagUIContents();
			}

		} else {
			for (int i = 0; i < selectedFilters.length; i++) {
				j = 0;
				while (j < adapterAdapater.getCount()) {
					Log.d("debug filters list NF ", adapterAdapater.getItem(j)
							.getContentDesc().getText().toString()
							+ " :: " + selectedFilters[i]);
					if (adapterAdapater.getItem(j).getContentDesc().getText()
							.toString().equals(selectedFilters[i])) {
						tagUIContents.add(adapterAdapater.getItem(j));
					}
					j++;
				}
			}
		}

		Log.d("debug filters list NF ", "Size: " + tagUIContents.size());
		adapterAdapater.clear();
		datasource.close();
		return tagUIContents;
	}
	
	@Override
	public void invalidateOptionsMenu() {
		// TODO Auto-generated method stub
		super.invalidateOptionsMenu();
	}
	
}