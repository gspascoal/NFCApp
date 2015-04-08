package com.example.proyecto;

import com.example.proyecto.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EraseTag extends Activity {

	private CustomDialog dialog;
	private NfcAdapter myNfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFiltersArray;
	private String[][] techListsArray;
	private String writeMessage;
	private TextView eraseMessage;
	private RelativeLayout eraseContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_erase_tag);
		dialog = new CustomDialog(this);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.erase_tag_dialog);

		eraseContainer = (RelativeLayout) findViewById(R.id.eraseContainer);
		eraseMessage = (TextView) findViewById(R.id.eraseResult);
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		Log.d("debug Erase", "activity name?: " + getIntent().getStringExtra("CALLING_ACTIVITY"));
		
		dialog.show();

		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

		try {
			ndef.addDataType("*/*"); /*
									 * Handles all MIME based dispatches. // You
									 * should specify only the ones that you
									 * need.
									 */
			// ndef.addDataScheme("http");
			// ndef.addAction(Intent.ACTION_VIEW);
		}

		catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		intentFiltersArray = new IntentFilter[] { ndef, };

		techListsArray = new String[][] {
				new String[] { NfcA.class.getName(), Ndef.class.getName() },
				{ MifareUltralight.class.getName() } };

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.erase_tag, menu);
		return true;
	}

	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.eraseDone:
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		// super.onNewIntent(intent);

		if (dialog.isShowing()) {
			dialog.dismiss();
		}

		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef ndef = Ndef.get(detectedTag);

		NdefMessage msgUNK = new NdefMessage(new NdefRecord[] { new NdefRecord(
				NdefRecord.TNF_UNKNOWN, null, null,
				new byte[ndef.getMaxSize() - 6]) });
		NdefMessage msgEMP = new NdefMessage(new NdefRecord[] { new NdefRecord(
				NdefRecord.TNF_EMPTY, null, null, null) });

		writeNdefMessageToTag(msgUNK, detectedTag);
		writeNdefMessageToTag(msgEMP, detectedTag);

		eraseMessage.setText(writeMessage);
		eraseContainer.setVisibility(View.VISIBLE);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		myNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		myNfcAdapter.enableForegroundDispatch(this, pendingIntent,
				intentFiltersArray, techListsArray);
	}

	private boolean writeNdefMessageToTag(NdefMessage message, Tag detectedTag) {
		// TODO Auto-generated method stub

		int size = message.toByteArray().length;

		Log.d("debug", "Before TRY");
		try {
			Ndef ndef = Ndef.get(detectedTag);
			if (ndef != null) {
				ndef.connect();
				Log.d("debug", "After Connect");
				if (!ndef.isWritable()) {
					
					 /*Toast.makeText(this,
					 getResources().getString(R.string.tc_ro),
					 Toast.LENGTH_SHORT) .show();*/
					 
					writeMessage = getResources().getString(
							R.string.ereaseResultB);
					return false;
				}
				if (ndef.getMaxSize() < size) {
					
					/* Toast.makeText( this,
					 "Tag data can't written to tag, Tag capacity is " +
					 ndef.getMaxSize() + "bytes, message is" + size +
					 " bytes.", Toast.LENGTH_SHORT) .show();*/
					 
					Log.d("debug erase",
							"Tag data can't written to tag, Tag capacity is "
									+ ndef.getMaxSize() + "bytes, message is"
									+ size + " bytes.");

					String msg = getResources().getString(R.string.tc_oom);
					writeMessage = getResources().getString(
							R.string.ereaseResultB);
					return false;
				}
				ndef.writeNdefMessage(message);
				ndef.close();
				/*
				 * Toast.makeText(this,
				 * getResources().getString(R.string.tc_ok),
				 * Toast.LENGTH_SHORT).show();
				 */
				writeMessage = getResources().getString(
						R.string.ereaseResult);
				return true;
			} else {
				NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
				if (ndefFormat != null) {
					try {
						ndefFormat.connect();
						ndefFormat.format(message);
						ndefFormat.close();
						
						 /*Toast.makeText(this,
						 getResources().getString(R.string.tc_ok),
						 Toast.LENGTH_SHORT).show();*/
						 
						writeMessage = getResources().getString(
								R.string.ereaseResult);
						return true;
					} catch (Exception e) {
						// TODO: handle exception
						
						/*  Toast.makeText(this,
						 getResources().getString(R.string.tc_ff),
						 Toast.LENGTH_SHORT).show();*/
						 
						writeMessage = getResources().getString(
								R.string.ereaseResultB);
						return false;
					}

				} else {
					
					 /*Toast.makeText(this,
					 getResources().getString(R.string.tc_ndef),
					 Toast.LENGTH_SHORT).show();*/
					 
					writeMessage = getResources().getString(
							R.string.ereaseResultB);
					return false;
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("debug", "Exception: " + e.toString());
			
			 /*Toast.makeText(this, getResources().getString(R.string.tc_wrong),
			 Toast.LENGTH_SHORT).show();*/
			 
			writeMessage = getResources().getString(
					R.string.ereaseResultB);
			return false;
		}

	}

	private void checkNFCConnection() {
		if (myNfcAdapter != null) {
			Log.d("debug NFC Connection", "NFC is available for the device");
			if (myNfcAdapter.isEnabled()) {
				Log.d("debug NFC Connection", "Connected");
				dialog.show();
			} else {
				Log.d("debug NFC Connection", "Disonnected");
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

				alertDialog.setTitle(getResources().getString(
						R.string.dialogNTitle));

				alertDialog.setMessage(getResources().getString(
						R.string.dialogNMessage));

				alertDialog.setPositiveButton(
						getResources().getString(R.string.dialogOkButton),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										Settings.ACTION_NFC_SETTINGS);
								startActivity(intent);
							}
						});

				alertDialog.setNegativeButton(
						getResources().getString(R.string.dialogCancelButton),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				alertDialog.show();

			}

		} else {
			Log.d("debug NFC Connection",
					"NFC is not  available for the device");

		}

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// this.finish();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		checkNFCConnection();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this, ExtrasMain.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
		this.finish();
	}
}
