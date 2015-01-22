package com.example.objetos;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class FilterAdapter extends ArrayAdapter<FilterKind> {

	private final Activity context;
	private List<FilterKind> objects;
	private TagContentDataSource datasource;
	private String filters; 
	
	public FilterAdapter(Activity context, List<FilterKind> objects) {
		super(context, com.example.proyecto.R.layout.filter_kind, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		/*
		 * final TextView payload =
		 * (TextView)convertView.findViewById(com.example
		 * .proyecto.R.id.contentPayload); final TextView payloadDesc =
		 * (TextView
		 * )convertView.findViewById(com.example.proyecto.R.id.contentDescription
		 * ); final TextView id =
		 * (TextView)convertView.findViewById(com.example.
		 * proyecto.R.id.contentId);
		 */
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					com.example.proyecto.R.layout.filter_kind, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.contentDesc = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.contentDescription);
			viewHolder.contentId = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.contentId);
			viewHolder.contentIcon = (ImageView) rowView
					.findViewById(com.example.proyecto.R.id.contentIcon);
			rowView.setTag(viewHolder);
			viewHolder.contentCheck = (CheckBox) rowView
					.findViewById(com.example.proyecto.R.id.checkContent);
			rowView.setTag(viewHolder);
			viewHolder.contentCheck.setEnabled(false);
			viewHolder.contentCheck.setFocusable(false);
			viewHolder.contentCheck.setFocusableInTouchMode(false);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		// LayoutInflater inflater = (LayoutInflater)
		// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.d("debug list write", "views");
		// Log.d("debug extra ID",objects[position].getContentId().getText().toString());
		holder.contentIcon.setBackground(objects.get(position)
				.getContentIcon().getBackground());
		holder.contentDesc.setText(objects.get(position)
				.getContentDesc().getText());
		holder.contentId.setText(objects.get(position)
				.getContentId().getText());
		
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
		
		
		return rowView;
	}

	static class ViewHolder {
		public CheckBox contentCheck;
		public TextView contentDesc;
		public ImageView contentIcon;
		public TextView contentId;
		
	}
}
