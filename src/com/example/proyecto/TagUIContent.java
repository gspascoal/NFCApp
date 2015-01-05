package com.example.proyecto;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class TagUIContent extends RelativeLayout {

	private ImageView contentIcon;
	private TextView payload;
	private TextView contentDesc;
	public Map<String, Integer> PLTI =  new LinkedHashMap<String,Integer>();
	
	public TagUIContent(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		/*Initialize associative array of URI prefixes icons id*/
		PLTI.put("N/A", R.drawable.default64);
		PLTI.put("Link", R.drawable.link64);
		PLTI.put("Secure Link", R.drawable.link64);
		PLTI.put("Telephone number", R.drawable.tel64);
		PLTI.put("Email", R.drawable.mail64);
		PLTI.put("sms:", R.drawable.sms64);
		PLTI.put("geo:", R.drawable.geo64);
		PLTI.put("Business card", R.drawable.business_cardb24);
		
		RelativeLayout rLayout = (RelativeLayout) inflate(context,R.layout.recent_content,this);
		payload = (TextView)findViewById(R.id.contentPayload);
		contentDesc = (TextView)findViewById(R.id.contentDescription);
		contentIcon = (ImageView)findViewById(R.id.contentIcon);
	}

	public ImageView getContentIcon() {
		return contentIcon;
	}

	public void setContentIcon(ImageView contentIcon) {
		this.contentIcon = contentIcon;
	}

	public TextView getPayload() {
		return payload;
	}

	public void setPayload(String text) {
		this.payload.setText(text);
	}

	public TextView getContentDesc() {
		return contentDesc;
	}

	public void setContentDesc(String text) {
		this.contentDesc.setText(text);;
	}

	public void setContentIcon(String text) {
		if ( PLTI.containsKey( text ) ) {
			contentIcon.setBackgroundResource(PLTI.get(text));
		}
		
		else {
			Log.d("TagInfo", "It not contains");
			contentIcon.setBackgroundResource(PLTI.get("N/A"));
		}
	}
}
