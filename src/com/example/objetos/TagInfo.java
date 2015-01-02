package com.example.objetos;

import java.nio.charset.Charset;
import java.util.ArrayList;

import com.example.proyecto.R;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

public class TagInfo {

	private Tag tag;
	private Ndef ndef;
	private NdefMessage[] mssgs;
	private String tagId = "";
	private int tagSize;
	private int inUse = 0;
	private boolean canBeReadOnly;
	private boolean isWritable;
	private String tagType;
	private String tagTechList = "";
	private int Messages = 0;
	private ArrayList<TagFeature> tagFeatures = new ArrayList<TagFeature>();
	private ArrayList<TagRecord> tagRecords = new ArrayList<TagRecord>();
	
	public TagInfo(Tag t, Intent intent, Context context){
		
		this.tag =  t;
		this.ndef =  Ndef.get(tag);
		this.mssgs = getNdefMessages(intent);
		setTagId();
		setTagSize();
		setInUse();
		setCanBeReadOnly();
		setIsWritable();
		setTagTechList();
		setTagType();
		setTagFeatures(context);
		
		processNdefMessages();
	}
	
	public String getTagId() {
		return this.tagId;
	}
	
	public void setTagId() {

		byte[] Id = tag.getId();
	    for (int i = 0; i < Id.length; i++) {
			this.tagId += Integer.toHexString(Id[i]& 0xFF);
			if (i < Id.length-1) {
				this.tagId += ":";
			}
		}

	}

	public int getTagSize() {
		return tagSize;
	}

	public void setTagSize() {
		this.tagSize = ndef.getMaxSize();
	}

	public int getInUse() {
		return inUse;
	}

	public void setInUse() {
			
		for (int i = 0; i < mssgs.length; i++) {
			inUse += mssgs[0].getByteArrayLength();
		}
			
	} 
	
	public boolean getCanBeReadOnly() {
		return canBeReadOnly;
	}

	public void setCanBeReadOnly() {
		this.canBeReadOnly = ndef.canMakeReadOnly();
	}

	public boolean getIsWritable() {
		return isWritable;
	}

	public void setIsWritable() {
		this.isWritable = ndef.isWritable();
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType() {
		String raw = ndef.getType();
		
		if(raw.contains("nfcforum")){
			int tIndex = raw.indexOf("type");
			this.tagType = "NFC Forum Type "+raw.substring(tIndex+4);
		}
		else{
			this.tagType = raw;
		}
		
	}

	public String getTagTechList() {
		return tagTechList;
	}

	public void setTagTechList() {
		String[] techList = tag.getTechList();
		for (int i = 0; i < techList.length; i++) {
	    	tagTechList += techList[i].substring(17);
	    	if (i < techList.length-1) {
				tagTechList += ",";
			}
		}
	}

	public int getMessages() {
		return Messages;
	}

	public void setMessages(int messages) {
		Messages = messages;
	}
	
	public ArrayList<TagRecord> getTagRecords() {
		return tagRecords;
	}

	public void setTagRecords() {
		
	}

	private NdefMessage[] getNdefMessages(Intent intent) {
		// TODO Auto-generated method stub
		NdefMessage[] message = null;
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Log.d("debug", "I found some shit.");
			Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMessages != null) {
				message = new NdefMessage[rawMessages.length];
				for (int i = 0; i < rawMessages.length; i++) {
					message[i] = (NdefMessage) rawMessages[i];
				}
			} else {
				Log.d("debug", "0 Ndef Messages.");
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
				message = new NdefMessage[] {msg};
			}
		}
		else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Log.d("debug", "NDEF intent.");
			Log.d("debug", "I found some shit.");
			Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMessages != null) {
				message = new NdefMessage[rawMessages.length];
				for (int i = 0; i < rawMessages.length; i++) {
					message[i] = (NdefMessage) rawMessages[i];
				}
			} else {
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
				message = new NdefMessage[] {msg};
			}
		}
		else {
			Log.d("debug", "Unknow intent.");
			
		}
		return message;
	}

	private void processNdefMessages(){
		
		 if (mssgs != null) {
			 this.setMessages(mssgs.length);
			 //Log.d("TagInfo",String.valueOf(mssgs.length));
			 for (int i = 0; i < mssgs.length; i++) {
				 //Log.d("TagInfo","Message "+(i+1)+" \n");
				 	for (int j = 0; j < mssgs[0].getRecords().length; j++) {
				 		//Log.d("TagInfo","inner loop. j: "+j);
				 		NdefRecord record = mssgs[i].getRecords()[j];
				 		//Log.d("TagInfo","Record check!");
				 		TagRecord tRecord = new TagRecord(record, i);
				 		//Log.d("TagInfo","tRecord check!");
				 		tagRecords.add(tRecord);		 		
				 	}
				 }
		 }
		 else {
			Log.d("debug", "that shit is so empty");
		}
	}

	
	public ArrayList<TagFeature> getTagFeatures() {
		return tagFeatures;
	}

	
	public void setTagFeatures(Context context) {
		
		TagFeature tagFeature = new TagFeature(context);
		
		/*Assign ID Feature*/
		tagFeature.setFeatureName(R.string.f_SerialNumber); // Cambiar por un string del sistema 
		tagFeature.setFeatureValue(getTagId());
		tagFeature.setFeatureIcon("ID");
		tagFeatures.add(tagFeature);
		tagFeature = new TagFeature(context);

		/*Assign NFC Forum Class Feature*/
		tagFeature.setFeatureName(R.string.f_DataFormat); // Cambiar por un string del sistema 
		tagFeature.setFeatureValue(getTagType());
		tagFeature.setFeatureIcon("Class");
		tagFeatures.add(tagFeature);
		tagFeature = new TagFeature(context);
		
		/*Assign CBMRO Feature*/
		tagFeature.setFeatureName(R.string.f_CBMRO); // Cambiar por un string del sistema 
		tagFeature.setFeatureValue(getCanBeReadOnly() ? context.getString(R.string.affirmation) : context.getString(R.string.negation));
		tagFeature.setFeatureIcon("CBMRO");
		tagFeatures.add(tagFeature);
		tagFeature = new TagFeature(context);
		
		/*Assign Memory Feature*/
		tagFeature.setFeatureName(R.string.f_Storage); // Cambiar por un string del sistema 
		String mUse = context.getString(R.string.f_Storage_T) +":" + getTagSize() + " " +context.getString(R.string.f_Storage_F)+":" + Integer.valueOf(getTagSize() - getInUse()) ;
		tagFeature.setFeatureValue(mUse);
		tagFeature.setFeatureIcon("Size");
		tagFeatures.add(tagFeature);
		tagFeature = new TagFeature(context);
		
		/*Assign Writable Feature*/
		tagFeature.setFeatureName(R.string.f_WRTBL); // Cambiar por un string del sistema 
		tagFeature.setFeatureValue(getIsWritable() ? context.getString(R.string.affirmation) : context.getString(R.string.negation));
		tagFeature.setFeatureIcon("WRTBL");
		tagFeatures.add(tagFeature);
		tagFeature = new TagFeature(context);
		
		/*Assign Supported Technologies Feature*/
		tagFeature.setFeatureName(R.string.f_TechList); // Cambiar por un string del sistema 
		tagFeature.setFeatureValue(getTagTechList());
		tagFeature.setFeatureIcon("TechList");
		tagFeatures.add(tagFeature);
		tagFeature = new TagFeature(context);
	}

	
	
}
