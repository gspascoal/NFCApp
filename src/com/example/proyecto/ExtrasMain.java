package com.example.proyecto;

import com.example.proyecto.R;
import com.example.proyecto.R.id;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ExtrasMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extras_main);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.extras_main, menu);
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
	
	public void onClick(View view){
		Intent intent;
		switch (view.getId()) {
		case R.id.erase:
			intent = new Intent(ExtrasMain.this, EraseTag.class);
			intent.putExtra("CALLING_ACTIVITY", this.getClass().getSimpleName());
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			break;
		case R.id.restoreData:
			intent = new Intent(ExtrasMain.this, RestoreData.class );
			//intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra("CALLING_ACTIVITY", this.getClass().getSimpleName());
			startActivity(intent);
			break;
		case R.id.backupData:
			intent = new Intent(ExtrasMain.this, BackupData.class );
			intent.putExtra("CALLING_ACTIVITY", this.getClass().getSimpleName());
			//intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		default:
			break;
		}
		
	}
	
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		//this.finish();
	}

}
