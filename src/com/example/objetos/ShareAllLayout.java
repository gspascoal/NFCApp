package com.example.objetos;

import com.example.proyecto.R;

import android.content.Context;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class ShareAllLayout extends RelativeLayout {
	private RadioButton asText;
	private RadioButton asImage;
	private Button positive, negative;
	private RadioGroup radioGroup;
	
	public ShareAllLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		RelativeLayout rLayout = (RelativeLayout) inflate(context, R.layout.share_all, this);
		asText = (RadioButton) findViewById(R.id.asText);
		asImage = (RadioButton) findViewById(R.id.asImage);
		setRadioGroup((RadioGroup)findViewById(R.id.radioGroupShare));
		setPositive((Button)findViewById(R.id.shareAllButton));
		setNegative((Button)findViewById(R.id.cshareAllButton));
	}

	

	public RadioButton getAsImage() {
		return asImage;
	}

	public void setAsImage(RadioButton asTImage) {
		this.asImage = asTImage;
	}

	public RadioButton getAsText() {
		return asText;
	}

	public void setAsText(RadioButton asText) {
		this.asText = asText;
	}



	public Button getPositive() {
		return positive;
	}



	public void setPositive(Button positive) {
		this.positive = positive;
	}



	public Button getNegative() {
		return negative;
	}



	public void setNegative(Button negative) {
		this.negative = negative;
	}



	public RadioGroup getRadioGroup() {
		return radioGroup;
	}



	public void setRadioGroup(RadioGroup radioGroup) {
		this.radioGroup = radioGroup;
	}

}
