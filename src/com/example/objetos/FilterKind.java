package com.example.objetos;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.proyecto.R;



public class FilterKind extends RelativeLayout {

	
	private ImageView contentIcon;
	private CheckBox contentCheck;
	private TextView contentDesc;
	private TextView contentId;
	private long currentItemId;
	private Map<String, Integer> kindIcons =  new LinkedHashMap<String,Integer>();
	
	
	public FilterKind(Context context) {
		super(context);
		
		
		kindIcons.put(getResources().getString(R.string.nA), R.drawable.default32);
		kindIcons.put(getResources().getString(R.string.link), R.drawable.link32);
		kindIcons.put(getResources().getString(R.string.link), R.drawable.link32);
		kindIcons.put(getResources().getString(R.string.tel), R.drawable.tel32);
		kindIcons.put(getResources().getString(R.string.mail), R.drawable.mail32);
		kindIcons.put(getResources().getString(R.string.sms), R.drawable.sms32);
		kindIcons.put(getResources().getString(R.string.geoLoc), R.drawable.geo32);
		kindIcons.put(getResources().getString(R.string.bussinesCard), R.drawable.business_cardb24);
		kindIcons.put(getResources().getString(R.string.plainText), R.drawable.text32);
		kindIcons.put(getResources().getString(R.string.thesis), R.drawable.default32);
		kindIcons.put(getResources().getString(R.string.tag), R.drawable.tag32);
		
		
		// TODO Auto-generated constructor stub
		RelativeLayout rLayout = (RelativeLayout) inflate(context,R.layout.filter_kind,this);
		setContentDesc((TextView)findViewById(R.id.kindDescription));
		setContentIcon((ImageView)findViewById(R.id.kindIcon));
		setContentCheck((CheckBox)findViewById(R.id.kindCheck));
		setContentId((TextView)findViewById(R.id.kindId));
		
	}


	public ImageView getContentIcon() {
		return contentIcon;
	}


	public void setContentIcon(ImageView contentIcon) {
		this.contentIcon = contentIcon;
	}


	public CheckBox getContentCheck() {
		return contentCheck;
	}


	public void setContentCheck(CheckBox contentCheck) {
		this.contentCheck = contentCheck;
	}


	public TextView getContentDesc() {
		return contentDesc;
	}


	public void setContentDesc(TextView contentDesc) {
		this.contentDesc = contentDesc;
	}


	public TextView getContentId() {
		return contentId;
	}


	public void setContentId(TextView contentId) {
		this.contentId = contentId;
	}
	
	public void setKindIcon(String text) {
		if ( kindIcons.containsKey( text ) ) {
			contentIcon.setBackgroundResource(kindIcons.get(text));
		}
		
		else {
			Log.d("TagInfo", "It not contains");
			contentIcon.setBackgroundResource(kindIcons.get(getResources().getString(R.string.nA)));
		}
	}


	public long getCurrentItemId() {
		return currentItemId;
	}


	public void setCurrentItemId(long currentItemId) {
		this.currentItemId = currentItemId;
	}
}
