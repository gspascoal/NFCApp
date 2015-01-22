package com.example.objetos;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.proyecto.R;

public class FilterLayout extends RelativeLayout{

	private ListView filterList;
	private Button filterButton;
	
	public FilterLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		RelativeLayout rLayout = (RelativeLayout) inflate(context,R.layout.filter_dialog,this);
		filterList = (ListView)findViewById(R.id.filterList);
		setFilterButton((Button)findViewById(R.id.filterButton));
		
		getFilterList().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getFilterList().getChildCount();
		/*
		getFilterButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getFilterList() != null) {
					Log.d("debug click filter",getFilterList().getChildCount()+"");
				
				for (int j = 0; j < getFilterList().getChildCount(); j++) {
					View nFilterKind = getFilterList().getChildAt(j);
					
					/*
					if (nFilterKind.getContentCheck().isChecked()) {
						Log.d("debug checking test",nFilterKind.getContentDesc().getText().toString());
					}
				}
			}
				else{Log.d("debug click filter","Try again");}
				
			}
		});*/
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

	
}
