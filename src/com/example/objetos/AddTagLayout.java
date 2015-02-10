package com.example.objetos;

import android.content.Context;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.proyecto.R;

public class AddTagLayout extends RelativeLayout{

	private TextView addTagField;
	private ListView tagList;
	private Button positive, negative;
	
	public AddTagLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		RelativeLayout rLayout = (RelativeLayout) inflate(context, R.layout.add_ctag_dialog, this);
		addTagField = (TextView)findViewById(R.id.addTag);
		tagList = (ListView)findViewById(R.id.ctagList);
		setPositive((Button)findViewById(R.id.addButton));
		negative = (Button)findViewById(R.id.cancelButton);
		
	}

	public TextView getAddTagField() {
		return addTagField;
	}

	public void setAddTagField(TextView addTagField) {
		this.addTagField = addTagField;
	}

	public ListView getTagList() {
		return tagList;
	}

	public void setTagList(ListView tagList) {
		this.tagList = tagList;
	}

	public Button getNegative() {
		return negative;
	}

	public void setNegative(Button negative) {
		this.negative = negative;
	}

	public Button getPositive() {
		return positive;
	}

	public void setPositive(Button positive) {
		this.positive = positive;
	}

}
