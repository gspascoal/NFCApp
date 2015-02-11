package com.example.proyecto;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class CustomDialog extends Dialog{
	
	private String activityName;
	private Button positive, negative;

	
	public CustomDialog(Context context) {
		super(context);
		if (context instanceof Activity) {
	        setOwnerActivity((Activity) context);
	    }
		activityName = this.getOwnerActivity().getLocalClassName();
		Log.d("debug", "activity name?: "+this.getOwnerActivity().getLocalClassName());
		setCanceledOnTouchOutside(false);
	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		Intent intent;
		switch (activityName) {
		case "ReadMain":
			 intent = new Intent(getContext(), MainActivity.class);
			 getContext().startActivity(intent);
			break;
		case "TransferContent":
			intent = new Intent(getContext(), CreateTagContent.class);
			 getContext().startActivity(intent);
			break;
		
		default:
			break;
		}
		this.dismiss();
		/*Return to MainActivity*/
		
		
	}
	
	  public void setPositiveButton(String buttonText, View.OnClickListener listener) {
	        positive.setText(buttonText);
	        positive.setOnClickListener(listener);
	        positive.setVisibility(View.VISIBLE);
	    }

	    public void setNegativeButton(String buttonText, View.OnClickListener listener) {
	        negative.setText(buttonText);
	        negative.setOnClickListener(listener);
	        negative.setVisibility(View.VISIBLE);
	    }

}
