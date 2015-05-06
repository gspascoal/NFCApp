package com.example.proyecto;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.proyecto.R.string;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AccidentAssistance extends Activity {

	private TextView footer;
	private NfcAdapter myNfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFiltersArray;
	private String[][] techListsArray;
	private TextView pNumber;
	private TextView policy;
	private String pol;
	private String plate;
	private static Context context;
	private String incidence_id;
	private TextView header;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accident_assistance);

		footer = (TextView) findViewById(R.id.assistanceFooter);
		header = (TextView) findViewById(R.id.assistanceHeader);
		pNumber = (TextView) findViewById(R.id.dataPNumberField);
		policy = (TextView) findViewById(R.id.dataPolicyField);

		context = this;

		Intent intent = getIntent();
		String data = intent.getDataString();

		pol = data.substring(data.indexOf("?p=") + 3, data.indexOf("&"));
		plate = data.substring(data.indexOf("&pl=") + 4, data.indexOf("&bd"));

		Log.d("debug", data);

		policy.setText(pol);
		pNumber.setText(plate);

		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (myNfcAdapter == null) {
			// status.setText("NFC isn't available for the device");
		} else {
			// status.setText("NFC is available for the device");
		}

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			Log.d("debug", "NDEF Discovered");
			Tag detectedTag = getIntent().getParcelableExtra(
					NfcAdapter.EXTRA_TAG);

			// GET NDEF MESSAGE IN THE TAG
			// NdefMessage[] messages = getNdefMessages(getIntent());

			// PROCESS NDEF MESSAGE
			String payload = null;
			byte payloadHeader;
			/*
			 * for (int i = 0; i < messages.length; i++) {
			 * //status.append("Message "+(i+1)+" \n"); for (int j = 0; j <
			 * messages[0].getRecords().length; j++) { NdefRecord record =
			 * messages[i].getRecords()[j];
			 * //status.append((j+1)+"th. Record Tnf: "+record.getTnf()+"\n");
			 * //status.append((j+1)+"th. Record type: "+record.getType()+"\n");
			 * //status.append((j+1)+"th. Record id: "+record.getId()+"\n");
			 * 
			 * payload = new String(record.getPayload(), 1,
			 * record.getPayload().length-1, Charset.forName("UTF-8"));
			 * //status.append((j+1)+"th. Record payload:  "+payload +"\n");
			 * payloadHeader = record.getPayload()[0];
			 * //status.append((j+1)+"th. Record payload header:  "
			 * +payloadHeader +"\n"); } } // DO WHATEVER WITH THE DATA
			 * 
			 * tInfo = new TagInfo(detectedTag, getIntent(), this);
			 * 
			 * Log.d("debug ReadTEG", "Payload: "+payload);
			 * 
			 * title.setText(payload.substring(payload.indexOf(":") + 1,
			 * payload.indexOf("?")));
			 * author.setText(payload.substring(payload.indexOf("a=") + 2,
			 * payload.indexOf("&")));
			 * tutor.setText(payload.substring(payload.indexOf("t=") + 2,
			 * payload.indexOf("&s")));
			 * reference.setText(payload.substring(payload.indexOf("s=") + 2,
			 * payload.indexOf("&y")));
			 * year.setText(payload.substring(payload.indexOf("y=") + 2,
			 * payload.lastIndexOf("&")));
			 * url.setText(payload.substring(payload.indexOf("u=") + 2,
			 * payload.length()));
			 */
		} else {
			Log.d("debug", "Nothing detected");
		}

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
		getMenuInflater().inflate(R.menu.accident_assistance, menu);
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

	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();

		if (on) {
			UpdateReportTask task = new UpdateReportTask();
			task.execute(pol, plate);
			footer.setVisibility(View.VISIBLE);
		} else {
			EndReportTask task = new EndReportTask();
			task.execute(incidence_id);
			view.setVisibility(View.INVISIBLE);
			footer.setVisibility(View.INVISIBLE);
			header.setText(getResources().getString(R.string.aa_finished));
			
			
		}
	}

	class UpdateReportTask extends AsyncTask<String, Void, String> {
		private String report_url = "http://alstelecom.com/Pruebas/appSMS/report_update.php?op=4";
		private String pol = null;
		private String pnumber = null;

		@Override
		protected String doInBackground(String... arg) {
			pol = arg[0];
			pnumber = arg[1];
			report_url += "&pol=" + pol;
			report_url += "&pnumber=" + pnumber;
			try {
				// Set connection timeout to 5 secs and socket timeout to 10
				// secs
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 5000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				int timeoutSocket = 10000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient hc = new DefaultHttpClient(httpParameters);
				HttpPost request = new HttpPost(report_url);
				request.setHeader("Content-type", "application/json");
				request.setHeader("Accept", "application/json");
				// JSONObject obj = new JSONObject(); obj.put("longUrl",
				// mLongUrl);
				// request.setEntity(new StringEntity(obj.toString(), "UTF-8"));
				HttpResponse response = hc.execute(request);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					return out.toString();
				} else {
					Toast.makeText(getBaseContext(), "Connection timed out",
							Toast.LENGTH_LONG).show();
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null)
				return;
			try {
				final JSONObject json = new JSONObject(result);
				final String report_id = json.getString("report_id");
				// final String lurl = json.getString("longUrl");
				if (json.has("report_id")) {
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							// shortened.setText(id);
							incidence_id = report_id;
							// Log.d("debug json", report_id );
							StartReportTask task = new StartReportTask();
							task.execute(report_id);
						}
					});
					Log.d("debug json", report_id);
					//incidence_id = report_id;
					
				} else {
					Log.d("debug json", "vacio");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
	
	class StartReportTask extends AsyncTask<String, Void, String> {
		private String report_url = "http://alstelecom.com/Pruebas/appSMS/report_update.php?op=2";
		private String report_id = null;

		@Override
		protected String doInBackground(String... arg) {
			report_id= arg[0];
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			report_url += "&report_id=" + report_id;
			report_url += "&dateStart=" + String.valueOf(dateFormat.format(date));
			
			String urlStr = report_url;
			URL url = null;
			try {
				url = new URL(urlStr);
			} catch (MalformedURLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			URI uri = null;
			try {
				uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				url = uri.toURL();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		
			Log.d("debug",report_url);
			try {
				// Set connection timeout to 5 secs and socket timeout to 10
				// secs
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 5000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				int timeoutSocket = 10000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient hc = new DefaultHttpClient(httpParameters);
				HttpPost request = new HttpPost(url.toString());
				request.setHeader("Content-type", "application/json");
				request.setHeader("Accept", "application/json");
				// JSONObject obj = new JSONObject(); obj.put("longUrl",
				// mLongUrl);
				// request.setEntity(new StringEntity(obj.toString(), "UTF-8"));
				HttpResponse response = hc.execute(request);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					return out.toString();
				} else {
					Toast.makeText(getBaseContext(), "Connection timed out",
							Toast.LENGTH_LONG).show();
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null)
				return;
			try {
				final JSONObject json = new JSONObject(result);
				final String report_result = json.getString("result");
				// final String lurl = json.getString("longUrl");
				if (json.has("result")) {
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							// shortened.setText(id);
							//incidence_id = report_id;
							// Log.d("debug json", report_id );
						}
					});
					Log.d("debug json", report_result);

				} else {
					Log.d("debug json", "vacio");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
	
	class EndReportTask extends AsyncTask<String, Void, String> {
		private String report_url = "http://alstelecom.com/Pruebas/appSMS/report_update.php?op=3";
		private String report_id = null;

		@Override
		protected String doInBackground(String... arg) {
			report_id= arg[0];
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			report_url += "&report_id=" + report_id;
			report_url += "&dateEnd=" + String.valueOf(dateFormat.format(date));
			
			String urlStr = report_url;
			URL url = null;
			try {
				url = new URL(urlStr);
			} catch (MalformedURLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			URI uri = null;
			try {
				uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				url = uri.toURL();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		
			Log.d("debug",report_url);
			try {
				// Set connection timeout to 5 secs and socket timeout to 10
				// secs
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 5000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				int timeoutSocket = 10000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient hc = new DefaultHttpClient(httpParameters);
				HttpPost request = new HttpPost(url.toString());
				request.setHeader("Content-type", "application/json");
				request.setHeader("Accept", "application/json");
				// JSONObject obj = new JSONObject(); obj.put("longUrl",
				// mLongUrl);
				// request.setEntity(new StringEntity(obj.toString(), "UTF-8"));
				HttpResponse response = hc.execute(request);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					return out.toString();
				} else {
					Toast.makeText(getBaseContext(), "Connection timed out",
							Toast.LENGTH_LONG).show();
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null)
				return;
			try {
				final JSONObject json = new JSONObject(result);
				final String report_result = json.getString("result");
				// final String lurl = json.getString("longUrl");
				if (json.has("result")) {
					((Activity) context).runOnUiThread(new Runnable() {
						public void run() {
							// shortened.setText(id);
							//incidence_id = report_id;
							// Log.d("debug json", report_id );
						}
					});
					Log.d("debug json", report_result);

				} else {
					Log.d("debug json", "vacio");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
}
