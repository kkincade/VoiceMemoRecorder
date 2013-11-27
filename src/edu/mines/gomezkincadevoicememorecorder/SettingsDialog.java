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
	private EditText defaultNameEditText, defaultSubjectEditText;
	public final static String DEFAULTNAME = "defaultName";
	public final static String DEFAULTSUBJECT = "defaultSubject";
	private SharedPreferences sharedPreferences;
	private String defaultName, defaultSubject;
	
	/** Grabs the default recording name from the shared preferences and loads the view **/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_dialog);
		defaultNameEditText = (EditText) findViewById(R.id.setting_default_name);
		defaultSubjectEditText = (EditText) findViewById(R.id.setting_default_subject);
		
		sharedPreferences = getSharedPreferences("voice_memo_preferences", Activity.MODE_PRIVATE);
		defaultName = (sharedPreferences.getString(MainActivity.DEFAULT_NAME, ""));
		defaultSubject = (sharedPreferences.getString(MainActivity.DEFAULT_SUBJECT, ""));
		Log.d("DEFAULT NAME IN SETTINGS", defaultName);
		defaultNameEditText.setText(defaultName);
		defaultSubjectEditText.setText(defaultSubject);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings_dialog, menu);
		return true;
	}
	
	/** Callback for confirm button. If default name is an empty string, it wipes the users changes. **/
	public void onConfirm(View v) {
		String name = defaultNameEditText.getText().toString();
		String subject = defaultSubjectEditText.getText().toString();
		if (name.equals("")) {
			name = defaultName;
		}
		if (subject.equals("")) {
			subject = defaultSubject;
		}
		Intent data = new Intent();
		setResult(RESULT_OK, data);
		data.putExtra(DEFAULTNAME, name);
		data.putExtra(DEFAULTSUBJECT, subject);
		finish();
	}
}
