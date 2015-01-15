package com.example.proyecto;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class TagUIContent extends RelativeLayout {

	private ImageView contentIcon;
	private TextView payload;
	private TextView contentDesc;
	private TextView contentId;
	private Context context;
	public Map<String, Integer> PLTI =  new LinkedHashMap<String,Integer>();
	
	public TagUIContent(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//this.context = context;
		
		/*Initialize associative array of URI prefixes icons id*/
		PLTI.put("N/A", R.drawable.default64);
		PLTI.put("Link", R.drawable.link64);
		PLTI.put("Secure Link", R.drawable.link64);
		PLTI.put("Telephone number", R.drawable.tel64);
		PLTI.put("Email", R.drawable.mail64);
		PLTI.put("sms:", R.drawable.sms64);
		PLTI.put("geo:", R.drawable.geo64);
		PLTI.put("Business card", R.drawable.business_cardb24);
		PLTI.put("Plain Text", R.drawable.text64);
		
		
		RelativeLayout rLayout = (RelativeLayout) inflate(context,R.layout.recent_content,this);
		payload = (TextView)findViewById(R.id.contentPayload);
		contentDesc = (TextView)findViewById(R.id.contentDescription);
		contentIcon = (ImageView)findViewById(R.id.contentIcon);
		contentId = (TextView)findViewById(R.id.contentId);
		
		
		rLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(), CreateTagContent.class);
			    String kind = getContentDesc().getText().toString();
			    String payload = getPayload().getText().toString();
			    Log.d("debug extra",payload);
				intent.putExtra("CONTENT_KIND", kind);
				intent.putExtra("CONTENT_PAYLOAD", payload);
				
				getContext().startActivity(intent);
			}
			
		});
	}

	public TextView getContentId() {
		return contentId;
	}

	public void setContentId(String id) {
		this.contentId.setText(id);
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
