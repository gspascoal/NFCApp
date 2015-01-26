package com.example.objetos;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.proyecto.R;

public class FilterLayout extends RelativeLayout{

	private ListView filterList;
	private Button filterButton;
	private ImageView filterImageView;
	private RelativeLayout filterHeaderLayout;
	private TextView filterTextView;
	
	public FilterLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		RelativeLayout rLayout = (RelativeLayout) inflate(context,R.layout.filter_dialog,this);
		filterList = (ListView)findViewById(R.id.filterList);
		setFilterButton((Button)findViewById(R.id.filterButton));
		setFilterImageView((ImageView)findViewById(R.id.filterImage));
		setFilterHeaderLayout((RelativeLayout)findViewById(R.id.filterHeader));
		setFilterTextView((TextView)findViewById(R.id.filterText));
			
	}

	public ListView getFilterList() {
		return filterList;
	}

	public void setFilterList(ListView filterList) {
		this.filterList = filterList;
	}

	public Button getFilterButton() {
		return filterButton;
	}

	public void setFilterButton(Button filterButton) {
		this.filterButton = filterButton;
	}

	public ImageView getFilterImageView() {
		return filterImageView;
	}

	public void setFilterImageView(ImageView filterImageView) {
		this.filterImageView = filterImageView;
	}

	public RelativeLayout getFilterHeaderLayout() {
		return filterHeaderLayout;
	}

	public void setFilterHeaderLayout(RelativeLayout filterHeaderLayout) {
		this.filterHeaderLayout = filterHeaderLayout;
	}

	public TextView getFilterTextView() {
		return filterTextView;
	}

	public void setFilterTextView(TextView filterTextView) {
		this.filterTextView = filterTextView;
	}

	
}
