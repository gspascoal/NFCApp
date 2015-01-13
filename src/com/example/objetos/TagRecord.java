package com.example.objetos;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.R.integer;
import android.nfc.NdefRecord;
import android.util.Log;



import com.example.proyecto.R;

public class TagRecord {

	
	private Map<String, String> NRTD =  new LinkedHashMap<String,String>();
	private Map<String, String> TNFMap =  new LinkedHashMap<String,String>();
	private Map<String, String> URIPFXMap =  new LinkedHashMap<String,String>();
	private Map<String, String> PLH =  new LinkedHashMap<String,String>();
	public Map<String, Integer> PLTI =  new LinkedHashMap<String,Integer>();
	private ArrayList<String> WOP = new ArrayList<String>(); 
		
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
	
	
	

	public TagRecord(NdefRecord r, int messsageId){
		
		/*Initialize array of URI prefixes without protocol field*/
		WOP.add("sms:");
		WOP.add("geo:");	
		
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
		URIPFXMap.put("0", "N/A");
		URIPFXMap.put("1", "Link"); // http://www.
		URIPFXMap.put("2", "Secure Link"); //https://www.
		URIPFXMap.put("3", "Link"); // http://
		URIPFXMap.put("4", "Secure Link"); //https://
		URIPFXMap.put("5", "Telephone number"); // tel: 
		URIPFXMap.put("6", "Email"); // mailto:
		URIPFXMap.put("66", "Business card");
		URIPFXMap.put("99", "App launcher");
		
		/*Initialize associative array of record's types*/
		NRTD.put("U", "URI");
		NRTD.put("T", "Text");
		NRTD.put("Sp", "Smart poster");
		NRTD.put("Sig", "Signature");
		NRTD.put("android.com:pkg", "Android Aplication record");
		
		/*Initialize associative array of URI prefixes values*/
		//PLH.put("0", "N/A");
		PLH.put("1", "http://www."); 
		PLH.put("2", "https://www."); 
		PLH.put("3", "http://"); 
		PLH.put("4", "https://");
		PLH.put("5", "tel:"); 
		PLH.put("6", "mailto:");
		PLH.put("66", "Bussiness card");
		PLH.put("99", "App launcher");
		
		
		/*Initialize associative array of URI prefixes icons id*/
		
		PLTI.put("N/A", R.drawable.default64);
		PLTI.put("Link", R.drawable.link64);
		PLTI.put("Secure Link", R.drawable.link64);
		PLTI.put("Telephone number", R.drawable.tel64);
		PLTI.put("Email", R.drawable.mail64);
		PLTI.put("sms:", R.drawable.sms64);
		PLTI.put("geo:", R.drawable.geo64);
		PLTI.put("Business card", R.drawable.business_cardb24);
		PLTI.put("Text", R.drawable.text64);

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
			if (recordType.equalsIgnoreCase("Android Aplication record")) {
				//Log.d("debug","Is an AAR");
				payload = new String(record.getPayload(), 0, record.getPayload().length, Charset.forName("UTF-8"));
				payloadHeader = record.getPayload()[0];
			} else 
			{
				if (recordType.equalsIgnoreCase("Text")) {
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
		if (getRecordPayloadheader() == 0) {
			if (recordType.equalsIgnoreCase("Text")) {
				this.recordPayloadTypeDesc = "Plain text";
			}
			while (i < WOP.size()) {
				if (getRecordPayload().contains(WOP.get(i))) {
					this.recordPayloadTypeDesc = WOP.get(i);
					break;
				}
			 i++;	
			}
		} else {
			if ( URIPFXMap.containsKey( String.valueOf(getRecordPayloadheader())  ) ) {
				this.recordPayloadTypeDesc =  URIPFXMap.get(String.valueOf(getRecordPayloadheader()) );
			}
			
			else {
				Log.d("TagInfo", "It not contains");
				this.recordPayloadTypeDesc = "N/A";
			}
			
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
			this.recordTNFDesc = "N/A";
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
		if (getRecordPayloadheader() == 0) {
			if (recordType.equalsIgnoreCase("Text")) {
				this.recordPayloadHeaderDesc = "Plain text";
				this.setWOP(true);
			}
			
			
			while (i < WOP.size()) {
				if (getRecordPayload().contains(WOP.get(i))) {
					this.recordPayloadHeaderDesc = WOP.get(i);
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
				Log.d("TagInfo", "It not contains");
				this.recordPayloadHeaderDesc = "N/A";
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
			Log.d("TagInfo", "It not contains");
			this.iconId =PLTI.get("N/A");
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
