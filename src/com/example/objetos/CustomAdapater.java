package com.example.objetos;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyecto.TagUIContent;

public class CustomAdapater extends ArrayAdapter<TagUIContent> {

	private Context context;
	private TagUIContent[] objects;

	public CustomAdapater(Context context,  TagUIContent[] objects) {
		super(context, com.example.proyecto.R.layout.recent_content, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.objects = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 convertView = inflater.inflate(com.example.proyecto.R.layout.recent_content, parent, false); 
		 ImageView icon = (ImageView)convertView.findViewById(com.example.proyecto.R.id.contentIcon);
		 TextView payload = (TextView)convertView.findViewById(com.example.proyecto.R.id.contentPayload);
		 TextView payloadDesc = (TextView)convertView.findViewById(com.example.proyecto.R.id.contentDescription);
		 TextView id = (TextView)convertView.findViewById(com.example.proyecto.R.id.contentId);
		 
		 icon.setBackground(objects[position].getContentIcon().getBackground());
		 payload.setText(objects[position].getPayload().getText());
		 payloadDesc.setText(objects[position].getContentDesc().getText());
		 id.setText(objects[position].getContentId().getText());
		 
		return convertView;
	}
	
	

}
