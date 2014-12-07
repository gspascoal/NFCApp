package com.example.proyecto;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

public class CustomDialog extends Dialog{

	public CustomDialog(Context context) {
		super(context);
		setCanceledOnTouchOutside(false);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		this.dismiss();
		/*Return to MainActivity*/
		Intent intent = new Intent(getContext(), MainActivity.class);
		getContext().startActivity(intent);
	}

}
