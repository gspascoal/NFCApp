package com.example.objetos;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.nfc.NdefRecord;
import android.util.Log;





import com.example.proyecto.R;

public class TagRecord {

	
	private Map<String, String> NRTD =  new LinkedHashMap<String,String>();
	private Map<String, String> TNFMap =  new LinkedHashMap<String,String>();
	private Map<String, String> URIPFXMap =  new LinkedHashMap<String,String>();
	private Map<String, String> PLH =  new LinkedHashMap<String,String>();
	public Map<String, Integer> PLTI =  new LinkedHashMap<String,Integer>();
	private Map<String, String> WOP = new LinkedHashMap<String,String>(); 
		
	private int messageId = 0;
	private NdefRecord record;
	private short recordTNF = 0;
	private String recordType = "";
	private String recordPayload;
	private byte recordPayloadheader;
	private String recordTNFDesc = "";
	private String recordPayloadTypeDesc = "";
	private String recordPayloadHeaderDesc;
	private Integer iconId;
	private boolean isWOP =  false;
	private String recordLanguageCode = "";
	private Context context;
	
	
	

	public TagRecord(NdefRecord r, int messsageId, Context context){
		
		this.context = context;
		/*Initialize array of URI prefixes without protocol field*/
		WOP.put("sms:",context.getResources().getString(R.string.sms));
		WOP.put("geo:",context.getResources().getString(R.string.geoLoc));
		WOP.put("thesis:",context.getResources().getString(R.string.thesis));
		
		/*Initialize associative array of TNF values*/
		TNFMap.put("0", "Empty");
		TNFMap.put("1", "NFC Forum well-known type");
		TNFMap.put("2", "Media-type");
		TNFMap.put("3", "Absolute URI");
		TNFMap.put("4", "NFC Forum external type");
		TNFMap.put("5", "Unknown");
		TNFMap.put("6", "Unchanged");
		TNFMap.put("7", "Reserved");
		
		/*Initialize associative array of URI prefixes descriptions*/
		URIPFXMap.put("0", context.getResources().getString(R.string.nA));
		URIPFXMap.put("1", context.getResources().getString(R.string.link)); // http://www.
		URIPFXMap.put("2", context.getResources().getString(R.string.slink)); //https://www.
		URIPFXMap.put("3", context.getResources().getString(R.string.link)); // http://
		URIPFXMap.put("4", context.getResources().getString(R.string.slink)); //https://
		URIPFXMap.put("5", context.getResources().getString(R.string.tel)); // tel: 
		URIPFXMap.put("6", context.getResources().getString(R.string.mail)); // mailto:
		URIPFXMap.put("66", context.getResources().getString(R.string.bussinesCard));
		URIPFXMap.put("99", context.getResources().getString(R.string.appLauncher));
		
		/*Initialize associative array of record's types*/
		NRTD.put("U", context.getResources().getString(R.string.uri));
		NRTD.put("T", context.getResources().getString(R.string.text));
		NRTD.put("Sp", context.getResources().getString(R.string.smartPoster));
		NRTD.put("Sig", context.getResources().getString(R.string.sign));
		NRTD.put("android.com:pkg", context.getResources().getString(R.string.aar));
		
		/*Initialize associative array of URI prefixes values*/
		//PLH.put("0", "N/A");
		PLH.put("1", "http://www."); 
		PLH.put("2", "https://www."); 
		PLH.put("3", "http://"); 
		PLH.put("4", "https://");
		PLH.put("5", "tel:"); 
		PLH.put("6", "mailto:");
		PLH.put("66", context.getResources().getString(R.string.bussinesCard));
		PLH.put("99", context.getResources().getString(R.string.appLauncher));
		
		
		/*Initialize associative array of URI prefixes icons id*/
		
		PLTI.put(context.getResources().getString(R.string.nA), R.drawable.default64);
		PLTI.put(context.getResources().getString(R.string.link), R.drawable.link64);
		PLTI.put(context.getResources().getString(R.string.slink), R.drawable.link64);
		PLTI.put(context.getResources().getString(R.string.tel), R.drawable.tel64);
		PLTI.put(context.getResources().getString(R.string.mail), R.drawable.mail64);
		PLTI.put(context.getResources().getString(R.string.sms), R.drawable.sms64);
		PLTI.put(context.getResources().getString(R.string.geoLoc), R.drawable.geo64);
		PLTI.put(context.getResources().getString(R.string.bussinesCard), R.drawable.business_cardb24);
		PLTI.put(context.getResources().getString(R.string.plainText), R.drawable.text64);
		PLTI.put(context.getResources().getString(R.string.thesis), R.drawable.thesis64);
		PLTI.put(context.getResources().getString(R.string.report), R.drawable.report64);

		this.record = r;
		
		if (r.equals(null)) {
			Log.d("debug", "Empty Tag");
		}
		this.setMessageId(messsageId);
		setRecordTNF();
		setRecordTNFDesc();
		setRecordType();
		setRecordPayload();	
		setRecordPayloadTypeDesc();
		setIconId();
		
		
	}


	public short getRecordTNF() {
		return recordTNF;
	}

	public void setRecordTNF() {
		this.recordTNF = this.record.getTnf();
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType() {
		int asciiCode = 0;
		
		if (record.getType().length > 0) {
			
			for (int k = 0; k < record.getType().length; k++) {
				asciiCode = record.getType()[k];
				recordType += (char) asciiCode; 
			}
			
			Log.d("debug", "RAW  type:" + recordType );
			if (NRTD.containsKey( recordType) ) {
				this.recordType =  NRTD.get(recordType);
			}
			
		}
		
	}

	public String getRecordPayload() {
		return recordPayload;
	}

	public void setRecordPayload() {
		String payload = "";
		byte payloadHeader;
		try {
			Log.d("debug", "RAW payload: "+ new String(record.getPayload(), 0, record.getPayload().length, Charset.forName("UTF-8")));
			//Log.d("debug", "Record's type: " + recordType );
			if (recordType.equalsIgnoreCase(context.getResources().getString(R.string.aar))) {
				//Log.d("debug","Is an AAR");
				payload = new String(record.getPayload(), 0, record.getPayload().length, Charset.forName("UTF-8"));
				payloadHeader = record.getPayload()[0];
			} else 
			{
				if (recordType.equalsIgnoreCase(context.getResources().getString(R.string.text))) {
					int statusByte=record.getPayload()[0];
					int languageCodeLength = statusByte & 0x3F;
					Log.d("text debug","Language Code Length:" + languageCodeLength+"\n");
					recordLanguageCode = new String( record.getPayload(), 1,languageCodeLength, Charset.forName("UTF-8"));
					Log.d("text debug","Language Code:" + recordLanguageCode+"\n");
					int isUTF8 = statusByte-languageCodeLength;
					if(isUTF8 == 0x00){
						Log.d("text debug","Record is UTF-8");
					payload = new String( record.getPayload(), 1+languageCodeLength, record.getPayload().length-1-languageCodeLength,Charset.forName("UTF-8"));
					} else if (isUTF8==-0x80){
						Log.d("text debug","Record is UTF-16");
					payload = new String( record.getPayload(), 1+languageCodeLength,record.getPayload().length-1-languageCodeLength,Charset.forName("UTF-16"));
					}
					payloadHeader = 0x00;
				} else {
					payload = new String(record.getPayload(), 1, record.getPayload().length-1, Charset.forName("UTF-8"));
					payloadHeader = record.getPayload()[0];
				}
				
			}
 			recordPayload = payload;
	 		//payloadHeader = record.getPayload()[0]; 
	 		setRecordPayloadheader(payloadHeader);
		} catch (StringIndexOutOfBoundsException e) {
			// TODO: handle exception
			//Toast.makeText(this, "Empty tag", Toast.LENGTH_SHORT).show();
		}
	}

	public byte getRecordPayloadheader() {
		return recordPayloadheader;
	}

	public void setRecordPayloadheader(byte recordPayloadheader) {
		this.recordPayloadheader = recordPayloadheader;
		this.setRecordPayloadHeaderDesc();
	}
	
	public String getRecordPayloadTypeDesc() {
		return recordPayloadTypeDesc;
	}

	public void setRecordPayloadTypeDesc() {
		
		Log.d("TagInfo", "Payload Header:" + getRecordPayloadheader());
		int i = 0;
		Object[] wopArray = WOP.keySet().toArray();
		if(getRecordTNF() != 0){
			
			if (getRecordPayloadheader() == 0) {
				if (recordType.equalsIgnoreCase(context.getResources().getString(R.string.text))) {
					this.recordPayloadTypeDesc = context.getResources().getString(R.string.plainText);
				}
				while (i < wopArray.length) {
					if (getRecordPayload().contains((String)wopArray[i])) {
						this.recordPayloadTypeDesc = WOP.get(wopArray[i]);
						this.setWOP(true);
						break;
					}
				 i++;	
				}
			} else {
				if ( URIPFXMap.containsKey( String.valueOf(getRecordPayloadheader())  ) ) {
					this.recordPayloadTypeDesc =  URIPFXMap.get(String.valueOf(getRecordPayloadheader()) );
				}
				
				else {
					Log.d("TagInfo", "It not contains Payload Type Desc");
					this.recordPayloadTypeDesc = context.getResources().getString(R.string.nA);
				}
				
			}
			
			
		}
		else {
			Log.d("TagInfo", "It not contains Payload Type Desc");
			this.recordPayloadTypeDesc = context.getResources().getString(R.string.nA);;
		}
		
		
		
		
		
	}

	public String getRecordTNFDesc() {
		return recordTNFDesc;
	}

	public void setRecordTNFDesc() {
		
		if( TNFMap.containsKey( String.valueOf( getRecordTNF() ) ) ) {
		
			this.recordTNFDesc =  TNFMap.get(String.valueOf( getRecordTNF() ));
		}
		
		else {
			this.recordTNFDesc = context.getResources().getString(R.string.nA);
		}
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getRecordPayloadHeaderDesc() {
		return recordPayloadHeaderDesc;
	}

	public void setRecordPayloadHeaderDesc() {
		
		int i=0;
		Object[] wopArray  = WOP.keySet().toArray();
		
		if (getRecordPayloadheader() == 0) {
			if (recordType.equalsIgnoreCase(context.getResources().getString(R.string.text))) {
				this.recordPayloadHeaderDesc = context.getResources().getString(R.string.plainText);
				this.setWOP(true);
			}

			
			while (i < wopArray.length) {
				if (getRecordPayload().contains((String)wopArray[i])) {
					this.recordPayloadHeaderDesc = WOP.get(wopArray[i]);
					this.setWOP(true);
					break;
				}
			 i++;	
			}
		} else {
			if ( PLH.containsKey( String.valueOf(getRecordPayloadheader())  ) ) {
				this.recordPayloadHeaderDesc =  PLH.get(String.valueOf(getRecordPayloadheader()) );
			}
			else {
				Log.d("TagInfo", "It not contains Payload Header");
				this.recordPayloadHeaderDesc = context.getResources().getString(R.string.nA);
			}
		}

	}

	public Integer getIconId() {
		return iconId;
	}

	public void setIconId() {
		
		
		if ( PLTI.containsKey( String.valueOf(getRecordPayloadTypeDesc())  ) ) {
			this.iconId =  PLTI.get(String.valueOf(getRecordPayloadTypeDesc()) );
		}
		
		else {
			Log.d("TagInfo", "It not contains Icon");
			this.iconId =PLTI.get(context.getResources().getString(R.string.nA));
		}
	}

	public boolean isWOP() {
		return isWOP;
	}

	public void setWOP(boolean isWOP) {
		this.isWOP = isWOP;
	}

	public String getRecordLanguageCode() {
		return recordLanguageCode;
	}


	public void setRecordLanguageCode(String recordLanguageCode) {
		this.recordLanguageCode = recordLanguageCode;
	}

}
