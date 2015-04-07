package com.example.objetos;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.BackupData;
import com.example.proyecto.CreateTagContent;
import com.example.proyecto.CustomDialog;
import com.example.proyecto.R;
import com.example.proyecto.SaveResult;
import com.example.proyecto.TagUIContent;

@SuppressLint("NewApi")
public class CustomAdapater extends ArrayAdapter<TagUIContent> {

	private final Activity context;
	private List<TagUIContent> objects;
	private TagContentDataSource datasource;
	private CustomAdapater tAdapater;

	public CustomAdapater(Activity context, List<TagUIContent> objects) {
		super(context, com.example.proyecto.R.layout.recent_content, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.objects = objects;
		datasource = new TagContentDataSource(getContext());
		tAdapater = this;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		/*
		 * final TextView payload =
		 * (TextView)convertView.findViewById(com.example
		 * .proyecto.R.id.contentPayload); final TextView payloadDesc =
		 * (TextView
		 * )convertView.findViewById(com.example.proyecto.R.id.contentDescription
		 * ); final TextView id =
		 * (TextView)convertView.findViewById(com.example.
		 * proyecto.R.id.contentId);
		 */
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(
					com.example.proyecto.R.layout.recent_content, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.payloadContent = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.contentPayload);
			viewHolder.payloadDescContent = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.contentDescription);
			viewHolder.payloadContentId = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.contentId);
			viewHolder.payloadContentTags = (TextView) rowView
					.findViewById(com.example.proyecto.R.id.contentTags);
			viewHolder.payloadIconContent = (ImageView) rowView
					.findViewById(com.example.proyecto.R.id.contentIcon);
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		// LayoutInflater inflater = (LayoutInflater)
		// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Log.d("debug list write", "views");
		// Log.d("debug extra ID",objects[position].getContentId().getText().toString());
		holder.payloadIconContent.setBackground(objects.get(position)
				.getContentIcon().getBackground());
		holder.payloadContent.setText(objects.get(position).getPayload()
				.getText());
		holder.payloadDescContent.setText(objects.get(position)
				.getContentDesc().getText());
		holder.payloadContentTags.setText(objects.get(position)
				.getContentTags().getText());
		if (!holder.payloadContentTags
				.getText()
				.toString()
				.equalsIgnoreCase(
						getContext().getResources().getString(R.string.tagName))) {
			holder.payloadContentTags.setVisibility(View.VISIBLE);
		}

		holder.payloadContentId.setText(objects.get(position).getContentId()
				.getText());

		datasource.open();
		List<ContentTag> tagList = datasource
				.getTagsOfContent(holder.payloadContentId.getText().toString());
		for (ContentTag contentTag : tagList) {
			Log.d("debug tags", contentTag.toString());
			// holder.payloadContentTags.setText(objects.get(position).getContentTags().getText());
		}

		// datasource.describeTable();
		datasource.close();

		rowView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getContext(), CreateTagContent.class);
				String itemId = holder.payloadContentId.getText().toString();
				String kind = holder.payloadDescContent.getText().toString();
				String pLoad = holder.payloadContent.getText().toString();
				String activityName = getContext().getClass().getSimpleName();
				Log.d("debug", "activity name?: " + activityName);
				Log.d("debug extra", kind);
				intent.putExtra("CALLING_ACTIVITY", activityName);
				intent.putExtra("CONTENT_KIND", kind);
				intent.putExtra("CONTENT_PAYLOAD", pLoad);
				intent.putExtra("CONTENT_ID", itemId);
				intent.putExtra("CONTENT_EDIT", "EDIT");
				
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				getContext().startActivity(intent);
				context.finish();
				
			}

		});

		rowView.setOnLongClickListener(new View.OnLongClickListener() {

			Long itemId = Long.valueOf(holder.payloadContentId.getText()
					.toString());
			String kind = holder.payloadDescContent.getText().toString();
			String pLoad = holder.payloadContent.getText().toString();
			private ListView optionDialog;
			private CustomDialog dialog;

			// private TagContentDataSource datasource;

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub

				// Log.d("debug extra ID", itemId.toString());
				optionDialog = new ListView(getContext());
				String[] cOptionsArrayStrings = getContext().getResources()
						.getStringArray(
								com.example.proyecto.R.array.cOptions_array);

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getContext(), android.R.layout.simple_list_item_1,
						cOptionsArrayStrings);

				optionDialog.setAdapter(adapter);
				optionDialog
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							private CustomDialog dialogAddTag;
							private FilterLayout filterLayout;
							private List<FilterKind> filterList;
							private TagAdapter filterListAdapter;

							@Override
							public void onItemClick(AdapterView<?> parent,
									final View view, int position, long id) {
								final String item = (String) parent
										.getItemAtPosition(position);

								switch (position) {
								case 0: // Delete
									AlertDialog.Builder alertDialog = new AlertDialog.Builder(
											context);

									alertDialog
											.setTitle(getContext()
													.getResources()
													.getString(
															R.string.cDelete_dialog_title)); //

									alertDialog
											.setMessage(getContext()
													.getResources()
													.getString(
															R.string.cDelete_dialog_msg));

									// getContext().getResources().getString(R.string.dialogOkButton)
									alertDialog
											.setPositiveButton(
													getContext()
															.getResources()
															.getString(
																	R.string.accept),
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialogC,
																int which) {
															int i = 0;
															datasource.open();
															while (i < objects
																	.size()) {
																Log.d("deleting",
																		"i value: "
																				+ i);
																if (objects
																		.get(i)
																		.getContentId()
																		.getText()
																		.toString()
																		.equals(itemId
																				.toString())) {
																	break;
																}
																i++;
															}
															Log.d("deleting",
																	"Item id: "
																			+ itemId);
															Log.d("deleting",
																	"Item row: "
																			+ i);
															objects.remove(i);
															datasource
																	.deleteContent(itemId);
															notifyDataSetChanged();
															/*
															 * Toast.makeText(
															 * getContext(),
															 * "Item " + itemId
															 * + " deleted!",
															 * Toast
															 * .LENGTH_LONG)
															 * .show();
															 */
															datasource.close();
															dialogC.dismiss();
															dialog.dismiss();

														}
													});

									alertDialog
											.setNegativeButton(
													getContext()
															.getResources()
															.getString(
																	R.string.dialogCancelButton),
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.cancel();
														}
													});

									alertDialog.show();

									break;
								case 1: // Share
									Intent sendIntent = new Intent();
									sendIntent.setAction(Intent.ACTION_SEND);
									sendIntent.putExtra(Intent.EXTRA_TEXT,
											pLoad);
									sendIntent.setType("text/plain");
									getContext().startActivity(sendIntent);
									break;
								case 2: // Add Tag
									datasource.open();
									final AddTagLayout addTagLayout = new AddTagLayout(
											getContext());
									dialogAddTag = new CustomDialog(
											getContext());
									List<FilterKind> filterList = getContentFilter(String
											.valueOf(itemId));
									if (filterList.size() != 0) {
										filterListAdapter = new TagAdapter(
												context, filterList);
										filterListAdapter
												.setCurrentItemId(itemId);
										addTagLayout.getTagList().setAdapter(
												filterListAdapter);
										addTagLayout.getTagList()
												.setVisibility(View.VISIBLE);
									} else {
										Log.d("debug tag list", "Empty List");
									}

									addTagLayout.getNegative()
											.setOnClickListener(
													new View.OnClickListener() {

														@Override
														public void onClick(
																View v) {
															// TODO
															// Auto-generated
															// method stub
															datasource.open();
															objects.clear();
															objects.addAll(datasource
																	.getTagUIContents());
															notifyDataSetChanged();
															datasource.close();

															dialogAddTag
																	.dismiss();
														}
													});

									addTagLayout.getPositive()
											.setOnClickListener(
													new View.OnClickListener() {

														private int selectedCount;

														@Override
														public void onClick(
																View v) {
															// TODO
															// Auto-generated
															// method stub

															selectedCount = 0;
															datasource.open();
															/*
															 * for (int i = 0; i
															 * <
															 * filterListAdapter
															 * .getCount(); i++)
															 * { if
															 * (filterListAdapter
															 * .getItem(i).
															 * getContentCheck
															 * ().isChecked()) {
															 * selectedCount ++;
															 * } }
															 * 
															 * Log.d(
															 * "debug select tag"
															 * ,
															 * "selectedCount: "
															 * +selectedCount);
															 * if(selectedCount
															 * != 0){
															 * 
															 * }
															 */

															if (addTagLayout
																	.getAddTagField()
																	.getText()
																	.length() > 0) {
																ContentTag nContentTag = datasource
																		.createTag(addTagLayout
																				.getAddTagField()
																				.getText()
																				.toString());
																datasource
																		.assignTag(
																				itemId,
																				nContentTag
																						.getId());
																// objects.clear();
																//
																notifyDataSetChanged();
															} else {
															}
															objects.clear();
															objects.addAll(datasource
																	.getTagUIContents());
															notifyDataSetChanged();
															datasource.close();

															dialogAddTag
																	.dismiss();

														}
													});

									// notifyDataSetChanged();
									// dialogAddTag.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
									dialogAddTag
											.setTitle(getContext()
													.getResources()
													.getString(
															R.string.tChoose_dialog_title));
									dialogAddTag.setContentView(addTagLayout);
									// tAdapater.notifyDataSetChanged();
									dialogAddTag.show();
									datasource.close();

									break;
								case 3: // QR Code
									String activityName = getContext().getClass().getSimpleName();
									Intent intent = new Intent(getContext(), BackupData.class);
									intent.putExtra("CONTENT_ID", itemId.toString());
									//intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
									intent.putExtra("CALLING_ACTIVITY", activityName);
									getContext().startActivity(intent);
									
									break;
								default:
									break;
								}
								// notifyDataSetChanged();
								dialog.dismiss();
							}

						});

				dialog = new CustomDialog(getContext());
				// dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				dialog.setTitle(getContext().getResources().getString(
						R.string.options));
				dialog.setContentView(optionDialog);
				dialog.show();

				return true;
			}
		});

		return rowView;
	}

	static class ViewHolder {
		public TextView payloadContent;
		public TextView payloadDescContent;
		public ImageView payloadIconContent;
		public TextView payloadContentId;
		public TextView payloadContentTags;
	}

	public List<FilterKind> getContentFilter(String itemId) {

		List<ContentTag> kind = datasource.getAllTags();
		List<ContentTag> checkedContentTags = datasource
				.getTagsOfContent(itemId);
		List<FilterKind> contentFilters = new ArrayList<FilterKind>();
		for (ContentTag contentTag : kind) {
			FilterKind nContentFilter = new FilterKind(getContext());
			nContentFilter.getContentDesc().setText(contentTag.getName());
			nContentFilter.getContentId().setText(
					String.valueOf(contentTag.getId()));
			nContentFilter.getContentCheck().setChecked(false);
			for (ContentTag contentTag2 : checkedContentTags) {
				if (contentTag2.getId() == contentTag.getId()) {
					nContentFilter.getContentCheck().setChecked(true);
					Log.d("debug getContentFilterChecked -->", nContentFilter
							.getContentDesc().getText().toString()
							+ " checked");
				}

			}
			nContentFilter.setKindIcon("Tag");
			contentFilters.add(nContentFilter);
		}
		// datasource.close();
		return contentFilters;
	}
}
