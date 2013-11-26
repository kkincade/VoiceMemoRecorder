package edu.mines.gomezkincadevoicememorecorder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class SettingsDialog extends Activity {
	private EditText defaultNameEditText;
	public final static String DEFAULTNAME = "defaultName";
	private SharedPreferences sharedPreferences;
	private String defaultName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_dialog);
		defaultNameEditText = (EditText) findViewById(R.id.setting_default_name);
		
		sharedPreferences = getSharedPreferences("voice_memo_preferences", Activity.MODE_PRIVATE);
		defaultName = (sharedPreferences.getString(MainActivity.DEFAULT_NAME, ""));
		Log.d("DEFAULT NAME IN SETTINGS", defaultName);
		defaultNameEditText.setText(defaultName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings_dialog, menu);
		
		return true;
	}
	
	public void onConfirm(View v) {
		String temp = defaultNameEditText.getText().toString();
		if (temp.equals("")) {
			temp = defaultName;
		}
		Intent data = new Intent();
		setResult(RESULT_OK, data);
		data.putExtra(DEFAULTNAME, temp);
		finish();
	}

}
