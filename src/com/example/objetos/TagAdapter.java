package com.example.objetos;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TagAdapter extends ArrayAdapter<FilterKind> {

	private final Activity context;
	private List<FilterKind> objects;
	private TagContentDataSource datasource;
	private String filters;
	private int posCheck;
	private long currentItemId;
	
	public TagAdapter(Activity context, List<FilterKind> objects) {
		super(context, com.example.proyecto.R.layout.filter_kind, objects);
		// TODO Auto-generated constructor stub
		datasource = new TagContentDataSource(getContext());
		this.context = context;
		this.objects = objects;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					com.example.proyecto.R.layout.filter_kind, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.contentDesc = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.kindDescription);
			viewHolder.contentId = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.kindId);
			viewHolder.contentIcon = (ImageView) rowView
					.findViewById(com.example.proyecto.R.id.kindIcon);
			rowView.setTag(viewHolder);
			viewHolder.contentCheck = (CheckBox) rowView
					.findViewById(com.example.proyecto.R.id.kindCheck);
			rowView.setTag(viewHolder);			
			
			/*
			viewHolder.contentCheck.setEnabled(false);
			viewHolder.contentCheck.setFocusable(false);
			viewHolder.contentCheck.setFocusableInTouchMode(false);*/
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		if(objects.get(position).getContentIcon().getBackground() != null){
			holder.contentIcon.setBackground(objects.get(position)
					.getContentIcon().getBackground());
		}
		holder.contentDesc.setText(objects.get(position)
				.getContentDesc().getText());
		holder.contentId.setText(objects.get(position)
				.getContentId().getText());
		
		
		holder.contentCheck.setChecked(false);
		holder.contentCheck.setEnabled(true);
		
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).getContentDesc().getText().toString() == holder.contentDesc.getText().toString()) {
				posCheck = i;
			}
			
		}
		/*
		if(objects.get(posCheck).getContentCheck().isChecked()){
			Log.d("debug getView holder IF", holder.contentDesc.getText().toString()+" Checked");
			Log.d("debug getView holder IF 2",objects.get(posCheck).getContentDesc().getText().toString()+" Checked");
			for (FilterKind filterKind : objects) {
				if (filterKind.getContentDesc().getText().toString() == holder.contentDesc.getText().toString() ) {
					filterKind.getContentCheck().setChecked(true);
					holder.contentCheck.setEnabled(false);
					holder.contentCheck.setChecked(true);
				}
				filterKind.getContentCheck().setEnabled(false);
			}
			
		}*/
		
		holder.contentCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				// TODO Auto-generated method stub
				
				datasource.open();
				if(isChecked){
					/*Log.d("debug onchekedchangelistener IF1", holder.contentDesc.getText().toString()+" Checked");
					holder.contentCheck.setChecked(true);*/
					
					for (FilterKind filterKind : objects) {
						if (filterKind.getContentDesc().getText().toString() == holder.contentDesc.getText().toString() ) {
							filterKind.getContentCheck().setChecked(true);
							Log.d("debug onchekedchangelistener IF", filterKind.getContentDesc().getText().toString()+" Checked");
							datasource.assignTag(currentItemId, Long.valueOf(holder.contentId.getText().toString() ));
						}
					}
				}
				else {
					
					for (FilterKind filterKind : objects) {
						if (filterKind.getContentDesc().getText().toString() == holder.contentDesc.getText().toString() ) {
							filterKind.getContentCheck().setChecked(false);
							Log.d("debug onchekedchangelistener ELSE1", filterKind.getContentDesc().getText().toString()+" unChecked");
						}
					}
					/*Log.d("debug onchekedchangelistener ELSE 2", holder.contentDesc.getText().toString()+" unChecked");
					holder.contentCheck.setChecked(false);*/
					//Log.d("debug onchekedchangelistener", "unChecked");
				}
				datasource.close();
			}
			
		});
		
		/*
		rowView.setOnClickListener(new OnClickListener() {
			
			int objectId = Integer.valueOf(holder.contentId.getText().toString());
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				if (objects.get(objectId-1).getContentCheck().isChecked()) {
					objects.get(objectId-1).getContentCheck().setChecked(false);
					holder.contentCheck.setChecked(false);
				} else {
					objects.get(objectId-1).getContentCheck().setChecked(true);
					holder.contentCheck.setChecked(true);
				}
				
				notifyDataSetChanged();
			}
		});
		*/
		
		return rowView;
	}

	public long getCurrentItemId() {
		return currentItemId;
	}

	public void setCurrentItemId(long currentItemId) {
		this.currentItemId = currentItemId;
	}

	static class ViewHolder {
		public CheckBox contentCheck;
		public TextView contentDesc;
		public ImageView contentIcon;
		public TextView contentId;
		
	}
}
