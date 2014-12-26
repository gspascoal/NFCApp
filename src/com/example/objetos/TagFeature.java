package com.example.objetos;

import java.util.LinkedHashMap;
import java.util.Map;

import com.example.proyecto.R;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TagFeature extends RelativeLayout{

	private Map<String, Integer> featureIcons =  new LinkedHashMap<String,Integer>();
	
	private TextView featureName;
	private TextView featureValue;
	private ImageView featureIcon;
		
	public TagFeature(Context context) {
		super(context);
		
		/*Initialize associative array of Feature icons id*/
		featureIcons.put("N/A", R.drawable.logo_app);
		featureIcons.put("ID", R.drawable.logo_app);
		featureIcons.put("Class", R.drawable.logo_app);
		featureIcons.put("CBMRO", R.drawable.logo_app);
		featureIcons.put("Size", R.drawable.logo_app);
		featureIcons.put("WRTBL", R.drawable.logo_app);
		featureIcons.put("TechList", R.drawable.logo_app);
		
		
		RelativeLayout rLayout = (RelativeLayout) inflate(context,R.layout.tag_feature,this);
		featureName = (TextView)findViewById(R.id.firstLine);
		featureValue = (TextView)findViewById(R.id.secondLine);
		featureIcon = (ImageView)findViewById(R.id.icon);
	}

	public TextView getFeatureName() {
		return featureName;
	}

	public void setFeatureName(Integer id) {
		this.featureName.setText(id);
		
	}

	public TextView getFeatureValue() {
		return featureValue;
	}

	public void setFeatureValue(String text) {
		this.featureValue.setText(text);
	}

	public ImageView getFeatureIcon() {
		return featureIcon;
	}

	public void setFeatureIcon(String text) {
		if ( featureIcons.containsKey( text ) ) {
			featureIcon.setBackgroundResource(featureIcons.get(text));
		}
		
		else {
			Log.d("TagInfo", "It not contains");
			featureIcon.setBackgroundResource(featureIcons.get("N/A"));
		}
	}

}
