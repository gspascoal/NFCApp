package com.example.objetos;

import android.content.Context;
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
	
	
	public FilterKind(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		RelativeLayout rLayout = (RelativeLayout) inflate(context,R.layout.filter_kind,this);
		setContentDesc((TextView)findViewById(R.id.contentDescription));
		setContentIcon((ImageView)findViewById(R.id.contentIcon));
		setContentCheck((CheckBox)findViewById(R.id.checkContent));
		setContentId((TextView)findViewById(R.id.contentId));
		
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
}
