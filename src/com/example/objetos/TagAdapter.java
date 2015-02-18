package com.example.objetos;

import java.util.ArrayList;
import java.util.List;

import com.example.proyecto.CustomDialog;
import com.example.proyecto.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TagAdapter extends ArrayAdapter<FilterKind> {

	private final Activity context;
	private List<FilterKind> objects;
	private TagContentDataSource datasource;
	private String filters;
	private int posCheck;
	private long currentItemId;
	
	public TagAdapter(Activity context, List<FilterKind> objects) {
		super(context, com.example.proyecto.R.layout.filter_kind, objects);
		// TODO Auto-generated constructor stub
		datasource = new TagContentDataSource(getContext());
		this.context = context;
		this.objects = objects;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					com.example.proyecto.R.layout.filter_kind, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.contentDesc = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.kindDescription);
			viewHolder.contentId = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.kindId);
			viewHolder.contentIcon = (ImageView) rowView
					.findViewById(com.example.proyecto.R.id.kindIcon);
			rowView.setTag(viewHolder);
			viewHolder.contentCheck = (CheckBox) rowView
					.findViewById(com.example.proyecto.R.id.kindCheck);
			rowView.setTag(viewHolder);			
			
			/*
			viewHolder.contentCheck.setEnabled(false);
			viewHolder.contentCheck.setFocusable(false);
			viewHolder.contentCheck.setFocusableInTouchMode(false);*/
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		//if(objects.get(position).getContentIcon().getBackground() != null){
			holder.contentIcon.setBackground(objects.get(position)
					.getContentIcon().getBackground());
		//}
		holder.contentDesc.setText(objects.get(position)
				.getContentDesc().getText());
		holder.contentId.setText(objects.get(position)
				.getContentId().getText());
		
		
		holder.contentCheck.setChecked(false);
		holder.contentCheck.setEnabled(true);
		
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).getContentDesc().getText().toString() == holder.contentDesc.getText().toString()) {
				if (objects.get(i).getContentCheck().isChecked()) {
					holder.contentCheck.setChecked(true);
					Log.d("debug checked",objects.get(i).getContentDesc().getText().toString() + " is Checked");
				}
				
			}
			
		}
		/*
		if(objects.get(posCheck).getContentCheck().isChecked()){
			Log.d("debug getView holder IF", holder.contentDesc.getText().toString()+" Checked");
			Log.d("debug getView holder IF 2",objects.get(posCheck).getContentDesc().getText().toString()+" Checked");
			for (FilterKind filterKind : objects) {
				if (filterKind.getContentDesc().getText().toString() == holder.contentDesc.getText().toString() ) {
					filterKind.getContentCheck().setChecked(true);
					holder.contentCheck.setEnabled(false);
					holder.contentCheck.setChecked(true);
				}
				filterKind.getContentCheck().setEnabled(false);
			}
			
		}*/
		
		
		holder.contentCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				// TODO Auto-generated method stub
				datasource.open();

				if (isChecked) {
					datasource.assignTag(currentItemId, Long.valueOf(holder.contentId.getText().toString() ));
				} else {
					datasource.deleteContentTag(currentItemId,Long.valueOf(holder.contentId.getText().toString() ));
				}
				
				/*
				if(isChecked){
					for (FilterKind filterKind : objects) {
						if (filterKind.getContentDesc().getText().toString() == holder.contentDesc.getText().toString() ) {
							//if (!filterKind.getContentCheck().isChecked()) {
								filterKind.getContentCheck().setChecked(true);
								
								Log.d("debug onchekedchangelistener IF", filterKind.getContentDesc().getText().toString()+" Checked");
							//}
						}
					}
				}
				
				else {
					
					for (FilterKind filterKind : objects) {
						if (filterKind.getContentDesc().getText().toString() == holder.contentDesc.getText().toString() ) {
							//if (filterKind.getContentCheck().isChecked()) {
								filterKind.getContentCheck().setChecked(false);
								Log.d("debug onchekedchangelistener ELSE1", filterKind.getContentDesc().getText().toString()+" unChecked");
								//datasource.deleteContentTag(currentItemId,Long.valueOf(holder.contentId.getText().toString() ));
							//}
						}
					}
				}*/
				datasource.close();
			}
			
		});
		
		holder.contentIcon.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View view) {
				ListView optionDialog = new ListView(getContext());
				String[] cOptionsArrayStrings = getContext().getResources().getStringArray(R.array.tOptions_array);

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getContext(), android.R.layout.simple_list_item_1,
						cOptionsArrayStrings);

				optionDialog.setAdapter(adapter);
				optionDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent,
							final View view, int position, long id) {
						// TODO Auto-generated method stub
						switch (position) {
						case 0:
							//Edit
							final AddTagLayout addTagLayout = new AddTagLayout(getContext());
							final CustomDialog dialogAddTag = new CustomDialog(getContext());
							addTagLayout.getTagList().setVisibility(View.GONE);
							addTagLayout.getAddTagField().setText(holder.contentDesc.getText().toString());
							
							addTagLayout.getNegative().setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									dialogAddTag.dismiss();
								}
							});
																
							addTagLayout.getPositive().setOnClickListener(new View.OnClickListener() {
								
								private int selectedCount;

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									
									selectedCount = 0;
									datasource.open();
									datasource.updateTag(holder.contentId.getText().toString(), addTagLayout.getAddTagField().getText().toString());
									objects.clear();
									objects.addAll(getContentFilter(String.valueOf(currentItemId)));
									notifyDataSetChanged();
									datasource.close();
									dialogAddTag.dismiss();
									
								}
							});
							
							//notifyDataSetChanged();
							//dialogAddTag.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
							dialogAddTag.setTitle(getContext().getResources().getString(R.string.tEdit_dialog_title));
							dialogAddTag.setContentView(addTagLayout);
							//tAdapater.notifyDataSetChanged();
							dialogAddTag.show();
							
							
	
							break;
						case 1:
							//Delete
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
							
							alertDialog.setTitle(getContext().getResources().getString(R.string.tDelete_dialog_title)); //
							
							alertDialog.setMessage(getContext().getResources().getString(R.string.tDelete_dialog_msg)); 
							
							//getContext().getResources().getString(R.string.dialogOkButton)
							alertDialog.setPositiveButton(getContext().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									datasource.open();
									datasource.deleteTag(Long.valueOf(holder.contentId.getText().toString()) , false);
									objects.clear();
									objects.addAll(getContentFilter(String.valueOf(currentItemId)));
									notifyDataSetChanged();
									datasource.close();
								}
							});
							
							alertDialog.setNegativeButton(getContext().getResources().getString(R.string.dialogCancelButton), new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
							
							alertDialog.show();
							break;
						default:
							break;
						}
					}
				});
				
				
				CustomDialog dialog = new CustomDialog(getContext());
				dialog.setTitle(getContext().getResources().getString(R.string.options));
				dialog.setContentView(optionDialog);
				dialog.show();
				
				
				// TODO Auto-generated method stub
				return true;
			}
		});
		return rowView;
	}

	public long getCurrentItemId() {
		return currentItemId;
	}

	public void setCurrentItemId(long currentItemId) {
		this.currentItemId = currentItemId;
	}

	static class ViewHolder {
		public CheckBox contentCheck;
		public TextView contentDesc;
		public ImageView contentIcon;
		public TextView contentId;
		
	}
	
	public List<FilterKind> getContentFilter(String itemId) {

		List<ContentTag> kind = datasource.getAllTags();
		List<ContentTag> checkedContentTags = datasource.getTagsOfContent(itemId);
		List<FilterKind> contentFilters = new ArrayList<FilterKind>();
		for (ContentTag contentTag : kind) {
			FilterKind nContentFilter = new FilterKind(getContext());
			nContentFilter.getContentDesc().setText(contentTag.getName());
			nContentFilter.getContentId().setText(String.valueOf(contentTag.getId()));			
			nContentFilter.getContentCheck().setChecked(false);
			for (ContentTag contentTag2 : checkedContentTags) {
				if (contentTag2.getId() == contentTag.getId()) {
					nContentFilter.getContentCheck().setChecked(true);
					Log.d("debug getContentFilterChecked -->", nContentFilter.getContentDesc().getText().toString() + " checked");
				}

			}			
			nContentFilter.setKindIcon("Tag");
			contentFilters.add(nContentFilter);
		}
		//datasource.close();
		return contentFilters;
	}
}
