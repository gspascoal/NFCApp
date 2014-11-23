package com.example.proyecto;

import java.nio.charset.Charset;

import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class WriteMain extends Activity {
	
	private NfcAdapter myNfcAdapter;
	private TextView status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		status = (TextView) findViewById(R.id.status);
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (myNfcAdapter == null) {
			status.setText("NFC isn't available for the device");
		} else {
			status.setText("NFC is available for the device");
		}
		
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Log.d("debug","NDEF Discovered");
			Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			//PREPARE THE NDEF MESSAGE
			byte[] uriField =  "amazon.com".getBytes(Charset.forName("US-ACII"));
			byte[] payload = new byte[uriField.length + 1];
			payload[0] = 0x01; //Code for http://www.
			System.arraycopy(uriField, 0, payload, 1, payload.length);
			NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
			NdefMessage newMessage = new NdefMessage(new NdefRecord[] {uriRecord});
			
			
			// WRITE DATA TO TAG
			writeNdefMessageToTag(newMessage, detectedTag );
			
		}
		
	}

	private boolean writeNdefMessageToTag(NdefMessage message, Tag detectedTag) {
		// TODO Auto-generated method stub
		
		int size = message.toByteArray().length;
		
		try {
			Ndef ndef = Ndef.get(detectedTag);
			if (ndef != null) {
				ndef.connect();
				if (!ndef.isWritable()) {
					Toast.makeText(this, "Tag is read-only", Toast.LENGTH_SHORT).show();
					return false;
				}
				if (ndef.getMaxSize() < size) {
					Toast.makeText(this, "Tag data can't written to tag, Tag capacity is "+ ndef.getMaxSize() + "bytes, message is"
							+ size + " bytes."
				, Toast.LENGTH_SHORT).show();
					return false;
				}
				ndef.writeNdefMessage(message);
				ndef.close();
				Toast.makeText(this, "Message is written tag", Toast.LENGTH_SHORT).show();
				return true;
			} else {
				NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
				if (ndefFormat != null) {
					try {
						ndefFormat.connect();
						ndefFormat.format(message);
						ndefFormat.close();
						Toast.makeText(this, "The data is written to teh tag", Toast.LENGTH_SHORT).show();
						return true;
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(this, "Failed to format tag", Toast.LENGTH_SHORT).show();
						return false;
					}
					
				} else {
					Toast.makeText(this, "NDEF is not supported", Toast.LENGTH_SHORT).show();
					return false;
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "Write operation is failed", Toast.LENGTH_SHORT).show();
			return false;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.write_main, menu);
		return true;
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_write_main,
					container, false);
			return rootView;
		}
	}

}
