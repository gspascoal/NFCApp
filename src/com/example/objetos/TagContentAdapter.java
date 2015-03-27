package com.example.objetos;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.objetos.CustomAdapater.ViewHolder;
import com.example.proyecto.BackupData;
import com.example.proyecto.CreateTagContent;
import com.example.proyecto.CustomDialog;
import com.example.proyecto.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TagContentAdapter extends ArrayAdapter<TagContent> {

	private Activity context;
	private List<TagContent> objects;
	private TagContentDataSource datasource;
	private TagContentAdapter tAdapater;
	public Map<String, String> DBR = new LinkedHashMap<String, String>(); // DataBaseResource
	public Map<String, Integer> PLTI = new LinkedHashMap<String, Integer>();


	
	public TagContentAdapter(Activity context, List<TagContent> objects) {
		super(context,com.example.proyecto.R.layout.recent_content, objects);
		// TODO Auto-generated constructor stub
		DBR.put("0", context.getResources().getString(R.string.link));
		DBR.put("1", context.getResources().getString(R.string.mail));
		DBR.put("2", context.getResources().getString(R.string.sms));
		DBR.put("3", context.getResources().getString(R.string.tel));
		DBR.put("4", context.getResources().getString(R.string.geoLoc));
		DBR.put("5", context.getResources().getString(R.string.plainText));
		DBR.put("6", context.getResources().getString(R.string.thesis));
		DBR.put("7", context.getResources().getString(R.string.report));
		
		PLTI.put(context.getResources().getString(R.string.nA), R.drawable.default64);
		PLTI.put(context.getResources().getString(R.string.link), R.drawable.link64);
		PLTI.put(context.getResources().getString(R.string.link), R.drawable.link64);
		PLTI.put(context.getResources().getString(R.string.tel), R.drawable.tel64);
		PLTI.put(context.getResources().getString(R.string.mail), R.drawable.mail64);
		PLTI.put(context.getResources().getString(R.string.sms), R.drawable.sms64);
		PLTI.put(context.getResources().getString(R.string.geoLoc), R.drawable.geo64);
		PLTI.put(context.getResources().getString(R.string.bussinesCard), R.drawable.business_cardb24);
		PLTI.put(context.getResources().getString(R.string.plainText), R.drawable.text64);
		PLTI.put(context.getResources().getString(R.string.thesis), R.drawable.thesis64);
		PLTI.put(context.getResources().getString(R.string.report), R.drawable.report64);

		
		
		this.context = context;
		this.objects = objects;
		datasource = new TagContentDataSource(getContext());
		tAdapater = this;
	}
	
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
					com.example.proyecto.R.layout.recent_content, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.payloadContent = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.contentPayload);
			viewHolder.payloadDescContent = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.contentDescription);
			viewHolder.payloadContentId = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.contentId);
			viewHolder.payloadIconContent = (ImageView) rowView
					.findViewById(com.example.proyecto.R.id.contentIcon);
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();
		
		
		holder.payloadIconContent.setBackgroundResource(PLTI.get(DBR.get(objects.get(position).getPayloadType())));
		holder.payloadContent.setText(objects.get(position).getPayload());
		holder.payloadDescContent.setText(DBR.get(objects.get(position).getPayloadType()));
		holder.payloadContentId.setText(String.valueOf(objects.get(position).getId()));

		datasource.open();
		List<ContentTag> tagList = datasource
				.getTagsOfContent(holder.payloadContentId.getText().toString());
		for (ContentTag contentTag : tagList) {
			Log.d("debug tags", contentTag.toString());
			// holder.payloadContentTags.setText(objects.get(position).getContentTags().getText());
		}

		// datasource.describeTable();
		datasource.close();

		
		

		return rowView;
	}
	
	
	
	static class ViewHolder {
		public TextView payloadContent;
		public TextView payloadDescContent;
		public ImageView payloadIconContent;
		public TextView payloadContentId;
		
	}

}
