package com.example.proyecto;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class Form extends RelativeLayout {

	private Button contactButton;
	private EditText fieldPhone;
		 
	private final static int PICK_CONTACT = 1;
	
	
	public Form(Context context, int LAYOUT_ID) {
		super(context);
		// TODO Auto-generated constructor stub
		RelativeLayout rLayout = (RelativeLayout) inflate(context,LAYOUT_ID,this);
		setContactButton((Button)findViewById(R.id.contactButton));
		fieldPhone  = (EditText)findViewById(R.id.fieldPhone);
	}
	public Button getContactButton() {
		return contactButton;
	}
	
	public void setContactButton(Button contactButton) {
		this.contactButton = contactButton;
	}
	public EditText getFieldPhone() {
		return fieldPhone;
	}
	public void setFieldPhone(EditText fieldPhone) {
		this.fieldPhone = fieldPhone;
	}
	
	
}
