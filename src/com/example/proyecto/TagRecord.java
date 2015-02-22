package com.example.proyecto;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TagRecord extends RelativeLayout {

	private Map<String, Integer> recordIcon =  new LinkedHashMap<String,Integer>();
	/*UI elements*/
	
	
	
	private ImageView r_Icon;
	private TextView r_Number;
	private TextView r_TNF;
	private TextView r_Type;
	private TextView r_PLHeader; // Protocol
	private TextView r_PLType;
	private TextView r_Payload;
	private Context context;
	
	public TagRecord(Context context) {
		super(context);
		this.context = context;
		
		recordIcon.put(context.getResources().getString(R.string.nA), R.drawable.default64);
		recordIcon.put(context.getResources().getString(R.string.link), R.drawable.link64);
		recordIcon.put(context.getResources().getString(R.string.slink), R.drawable.link64);
		recordIcon.put(context.getResources().getString(R.string.tel), R.drawable.tel64);
		recordIcon.put(context.getResources().getString(R.string.mail), R.drawable.mail64);
		recordIcon.put(context.getResources().getString(R.string.sms), R.drawable.sms64);
		recordIcon.put(context.getResources().getString(R.string.geoLoc), R.drawable.geo64);
		recordIcon.put(context.getResources().getString(R.string.bussinesCard), R.drawable.business_cardb24);
		recordIcon.put(context.getResources().getString(R.string.plainText), R.drawable.text64);
		
		// TODO Auto-generated constructor stub
		RelativeLayout rLayout = (RelativeLayout) inflate(context,R.layout.tag_record,this);
		
		r_Icon = (ImageView)findViewById(R.id.recordIcon);
		r_Number = (TextView)findViewById(R.id.recordNumber);
		r_TNF = (TextView)findViewById(R.id.recordTNF);
		r_Type = (TextView)findViewById(R.id.recordType);
		r_PLHeader = (TextView)findViewById(R.id.recordHeaderDesc);
		r_PLType = (TextView)findViewById(R.id.recordPayloadType);
		r_Payload = (TextView)findViewById(R.id.recordPayload);
		
		
		
	}


	public ImageView getR_Icon() {
		return r_Icon;
	}


	public void setR_Icon(ImageView r_Icon) {
		this.r_Icon = r_Icon;
	}


	public TextView getR_Number() {
		return r_Number;
	}


	public void setR_Number(TextView r_Number) {
		this.r_Number = r_Number;
	}


	public TextView getR_TNF() {
		return r_TNF;
	}


	public void setR_TNF(TextView r_TNF) {
		this.r_TNF = r_TNF;
	}


	public TextView getR_Type() {
		return r_Type;
	}


	public void setR_Type(TextView r_Type) {
		this.r_Type = r_Type;
	}


	public TextView getR_PLHeader() {
		return r_PLHeader;
	}


	public void setR_PLHeader(TextView r_PLHeader) {
		this.r_PLHeader = r_PLHeader;
	}


	public TextView getR_PLType() {
		return r_PLType;
	}


	public void setR_PLType(TextView r_PLType) {
		this.r_PLType = r_PLType;
	}


	public TextView getR_Payload() {
		return r_Payload;
	}


	public void setR_Payload(TextView r_Payload) {
		this.r_Payload = r_Payload;
	}
	
	public void setRecordIcon(String text) {
		if ( recordIcon.containsKey( text ) ) {
			r_Icon.setBackgroundResource(recordIcon.get(text));
		}
		
		else {
			Log.d("TagInfo", "It not contains");
			r_Icon.setBackgroundResource(recordIcon.get(context.getResources().getString(R.string.nA)));
		}
	}

	
}
