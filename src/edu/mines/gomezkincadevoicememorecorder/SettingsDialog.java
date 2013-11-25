package edu.mines.gomezkincadevoicememorecorder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class SettingsDialog extends Activity {
	private EditText defaultName;
	public final static String DEFAULTNAME = "defaultName";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_dialog);
		defaultName = (EditText) findViewById(R.id.setting_default_name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings_dialog, menu);
		return true;
	}
	
	public void onConfirm(View v) {
		String temp = defaultName.getText().toString();
		Intent data = new Intent();
		setResult(RESULT_OK, data);
		data.putExtra(DEFAULTNAME, temp);
		finish();
	}

}
