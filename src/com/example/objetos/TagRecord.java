package com.example.objetos;

import java.nio.charset.Charset;

import android.R.bool;
import android.nfc.NdefRecord;



public class TagRecord {

	private int messageId = 0;
	private NdefRecord record;
	private short recordTNF = 0;
	private String recordType = "";
	private String recordPayload;
	private byte recordPayloadheader;
	private String[] NRTD = {"U","T","Sp","Sig"};
	private String[] NRTDESC = {"URI","TEXT","Smart poster","Signature"};
	
	public TagRecord(NdefRecord r, int messsageId){
		this.record = r;
		this.messageId = messsageId;
		setRecordTNF();
		setRecordType();
		setRecordPayload();
		
		
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
		int i=0;
		boolean WK = false;
		for (int k = 0; k < record.getType().length; k++) {
			asciiCode = record.getType()[k];
			recordType += (char) asciiCode; 
		}
		
		while ( (i < NRTD.length) && (!recordType.substring(0, 1).equals(NRTD[i]))) {
			if (recordType.substring(0, 1).equals(NRTD[i]) ) {
				WK = true;
				break;
			}
			i++;
		}
		
		if (WK) {
			recordType = NRTDESC[i-1];
		}
		else{
			recordType = "--";
		}
		
	}

	public String getRecordPayload() {
		return recordPayload;
	}

	public void setRecordPayload() {
		String payload = "";
		byte payloadHeader;
		try {
 			payload = new String(record.getPayload(), 1, record.getPayload().length-1, Charset.forName("UTF-8"));
	 		recordPayload = payload;
 			//status.append((j+1)+"th. Record payload:  "+payload +"\n");
	 		payloadHeader = record.getPayload()[0]; 
	 		setRecordPayloadheader(payloadHeader);
	 		//status.append((j+1)+"th. Record payload header:  "+payloadHeader +"\n");
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
	}
}
