package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 
 * NOTE: This app must be deployed to a device and cannot be run through an emulator. Android emulators do not support
 * the use of the computer's built in microphone and the application will crash when trying to record audio. Any Android
 * device will suffice.
 * 
 * The Voice Memo Recorder app allows you to record audio recordings which are saved to a database built into your 
 * Android device. There are two main screens in the application: the recording screen (MainActivity) and a list of your
 * saved recordings (RecordingsList).
 * 
 * MainActivity allows users to record and stop recording audio. Simply enter a title and subject for your recording and
 * press the record button to begin recording audio. Once a voice recording is stopped, it is passed to RecordingsList 
 * where it is saved to the database and populated in a table view. If you do not wish to record anything but simply view
 * your list of recordings, swipe to the left on the screen.
 * 
 * Once in the list of recordings, tap on a saved recording to access information regarding the recording. You can play back
 * any of the voice memos you have saved in the database. To delete a recording, long press on a ListView item and click "OK". 
 * Simply press the back button to get back to the main recording screen.
 * 
 * The options menu presents three options:
 * 1.) About    - information regarding the developers
 * 2.) Help     - some tips on how to use the application
 * 3.) Settings - options to change default text color for list view as well as change the default recording name
 * 
 * The action bar also contains a button for muting/unmuting the media volume on the device.
 * 
 * As far as back/up functionality, our group decided that the back navigation would be an annoyance to the user. For instance,
 * in the tablet view, if the user selects a recording and edits it, then selects another recording for editing, our desired 
 * functionality is still to return to the main recording screen.
 * 
 * POINT DISTRIBUTION: We feel we all contributed equally to the development of the application and would like to each receive
 * a third of the credit.
 * 
 * References:
 * (Media recorder).........http://developer.android.com/reference/android/media/MediaRecorder.html
 * (BaseAdapter/ListView)...http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
 * (Copy method)............http://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd 
 * (Database)...............http://developer.android.com/training/notepad/index.html
 * (Swipe Gesture)..........http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
 * (Fragments)..............http://developer.android.com/training/basics/fragments/index.html
 * 
 * @authors - Kameron Kincade, Gonzalo Gomez, and Israel Gomez
 **/

public class MainActivity extends Activity {
	public final static String RECORDING = "recording";
	public final static String DEFAULT_NAME = "default_name";
	public final static int DEFAULT = 1;
	
	private String originalAudioFilePath = null;
	private double recordingDuration = 0;
	private int numberOfSavedRecordings = 0;
	private MediaRecorder recorder = null;
	private SharedPreferences sharedPreferences;
	private Drawable muteIcon;
	private Drawable playIcon;
	private AudioManager audioManager;
	private int volumeLevel;

	// List of widgets
	private EditText nameEditText;
	private EditText subjectEditText;
	private Chronometer chronometer;
	private ToggleButton recordButton;


	/** In onCreate(), the application grabs the initial volume and sets the volume level. It then loads 
	 * a couple preferences, and instantiates the application's widgets, variables, and an OnSwipeTouchListener 
	 * for navigating to the RecordingsList activity. **/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // Hide keyboard initially
		setContentView(R.layout.activity_main);

		originalAudioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_temp1.3gp";
		nameEditText = (EditText) findViewById(R.id.name_edit_text);
		subjectEditText = (EditText) findViewById(R.id.subject_edit_text);
		chronometer = (Chronometer) findViewById(R.id.chronometer); // TODO: Format this later
		recordButton = (ToggleButton) findViewById(R.id.record_button);
		muteIcon = getResources().getDrawable(R.drawable.mute);
		playIcon = getResources().getDrawable(R.drawable.play);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		View mainView = findViewById(android.R.id.content);
		final Intent recordingListIntent = new Intent(this, RecordingsList.class);

		// Load number of recordings (used for uniquely naming audio file paths) from shared preferences
		sharedPreferences = getSharedPreferences("voice_memo_preferences", Activity.MODE_PRIVATE);
		numberOfSavedRecordings = sharedPreferences.getInt("number_of_saved_recordings", -1);

		// Check if defaultName exists in preferences. If so, use it. If not, set to default.
		String defaultName = (sharedPreferences.getString(MainActivity.DEFAULT_NAME, ""));
		Log.d("DEFAULT NAME - defaultName", defaultName);
		if (defaultName.equals("")) {
			String temp = this.getString(R.string.untitled);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(DEFAULT_NAME, temp);
			editor.commit();
			Log.d("DEFAULT NAME - TEMP", temp);
		}

		// OnSwipeListener
		mainView.setOnTouchListener(new OnSwipeTouchListener() {
			public void onSwipeLeft() {
				recordingListIntent.putExtra(RECORDING, new AudioRecording(null, null, null, null, null, null));
				startActivityForResult(recordingListIntent, 100);
			}
		});
	}
	

	/** onCreateOptionsMenu() method finds the XML action bar file in menu and inflates it. It then sets the action items
	 * on the action bar on top of the screen **/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("VOICE RECORDER", "onCreateOptionsMenu()");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar, menu);
		
		volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (volumeLevel == 0) {
			menu.findItem(R.id.action_mute).setIcon(muteIcon);
		} else {
			menu.findItem(R.id.action_mute).setIcon(playIcon);
		}
		return true;
	}

	
	/** onPrepareOptionsMenu() method is called every time invalidateOptionsMenu() is called. This method
	 * checks the volume level and sets the icon in the action bar to mute/unmute accordingly.**/
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d("VOICE RECORDER", "onPrepareOptionsMenu");
		volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (volumeLevel == 0) {
			menu.findItem(R.id.action_mute).setIcon(muteIcon);
		} else {
			menu.findItem(R.id.action_mute).setIcon(playIcon);
		}
		return true;
	}
	
	
	/** onOptionsItemSelected method distinguishes which options menu icon was clicked and executes the appropriate actions. **/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("VOICE RECORDER", "onOptionsItemSelected()");
		switch (item.getItemId()) {
		case R.id.action_mute:
			volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (volumeLevel != 0) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
				item.setIcon(muteIcon);
				Toast muteToast = Toast.makeText(getApplicationContext(), getString(R.string.mute_message), Toast.LENGTH_SHORT);
				muteToast.setGravity(Gravity.CENTER, 0, 0);
				muteToast.show();
			} else {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
				item.setIcon(playIcon);
				Toast muteToast = Toast.makeText(getApplicationContext(), getString(R.string.play_message), Toast.LENGTH_SHORT);
				muteToast.setGravity(Gravity.CENTER, 0, 0);
				muteToast.show();
			}
			break;
		case R.id.action_about:
			new AlertDialog.Builder(this)
			.setTitle(R.string.about_action)
			.setMessage(R.string.about_message)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Okay button clicked
					if (whichButton == -1) {
						return;
					}
				}})
				.setNegativeButton(android.R.string.no, null).show();
			break;
		case R.id.action_help:
			new AlertDialog.Builder(this)
			.setTitle(R.string.help_action)
			.setMessage(R.string.help_message_1)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Okay button clicked
					if (whichButton == -1) {
						return;
					}
				}})
				.setNegativeButton(android.R.string.no, null).show();
			break;

		case R.id.action_settings:
			Intent i = new Intent(this, SettingsDialog.class);
			startActivityForResult(i, DEFAULT);
		default:
			break;
		}

		return true;
	} 

	
	/** onActivityResult() is used if the Settings menu is clicked. Updates the default name from the value the user entered. **/
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
		super.onActivityResult(requestCode, resultCode, data); 
		if (resultCode == Activity.RESULT_OK) { 
			if (requestCode == DEFAULT) {
				String temp = data.getExtras().getString(SettingsDialog.DEFAULTNAME);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(DEFAULT_NAME, temp);
				editor.commit();
			}
		}
	}

	
	/** Record/Pause button is a toggle button. This method first determines whether "record" or "stop" was clicked. 
	 * Record: Initializes the MediaRecorder object, starts the chronometer, and starts recording audio.
	 * Stop: Stops recording and saves it.  **/
	public void record( View v ) {
		if (v.getId() == R.id.record_button) {
			// Record
			if (recordButton.isChecked()) {
				Log.d("VOICE MEMO RECORDER", "Start Recording");
				recorder = new MediaRecorder();
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setOutputFile(originalAudioFilePath);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				chronometer.setBase(SystemClock.elapsedRealtime());

				// Prepare MediaRecorder
				try {
					recorder.prepare();
				} catch (IOException e) {
					Log.d("MEDIA RECORDER", "prepare() failed");
					return;
				}

				// Start recorder and chronometer
				recorder.start();
				chronometer.start();

				// Stop
			} else {
				stopRecording();
				saveRecording();
			}
		}
	}


	/** Stops the recorder and the chronometer **/
	public void stopRecording() {
		// Stop chronometer
		chronometer.stop();
		recordingDuration = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000.0;

		Log.d("VOICE MEMO RECORDER", "Stop Recording");
		// Stop recording
		recorder.stop();
		recorder.release();
		recorder = null;
	}


	/** Copies the audio file to a unique name so it won't get overridden.
	 * Then calls pushRecordingToList(), passing it the path to the audio file it just created. **/
	public void saveRecording() {
		Log.d("VOICE MEMO RECORDER", "Save Recording");

		// Create unique path for audio file
		String uniquePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_" + Integer.toString(numberOfSavedRecordings) + ".3gp";
		numberOfSavedRecordings += 1;

		// Copy audio file to the unique path
		try {
			File originalFile = new File(originalAudioFilePath);
			File uniqueFile = new File(uniquePath);
			copy(originalFile, uniqueFile);
			originalFile.delete();
		} catch (IOException e) {
			Log.d("VOICE MEMO RECORDER", "Failed to copy file to new location.");
			e.printStackTrace();
		}
		pushRecordingToList(uniquePath);
	}


	/** Creates an AudioRecording object with appropriate audio recording and info from text boxes,
	 *  starts the ListView activity, and passes the AudioRecording object to the ListView.
	 *  
	 *  @param: audioFilePath - The string path of where the voice memo's audio file is saved **/ 
	public void pushRecordingToList(String audioFilePath) {
		// Get current date
		String date = formatDate();
		
		// Format the time
		DecimalFormat df = new DecimalFormat("#.#");
		String durationString = df.format(recordingDuration);
		durationString += getString(R.string.s);

		// Create new AudioRecording object and add it to the ArrayList
		AudioRecording recording = new AudioRecording(audioFilePath, nameEditText.getText().toString(), subjectEditText.getText().toString(), "", date, durationString);

		// Reset MainActivity for a fresh recording
		nameEditText.setText("");
		subjectEditText.setText("");

		// Reset the chronometer
		chronometer.setBase(SystemClock.elapsedRealtime());

		// Save the number of recordings currently in database (used for uniquely naming the audio file paths)
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("number_of_saved_recordings", numberOfSavedRecordings);
		editor.commit();

		// Start ListView Activity
		Intent myIntent = new Intent(this, RecordingsList.class); // Had to add new activity tag in Manifest.xml
		myIntent.putExtra(RECORDING, recording);
		startActivity(myIntent);
	}


	/**--------------------------------------------- HELPER METHODS -------------------------------------------------**/

	/** Gets the current date and returns it in a string format **/
	public String formatDate() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);
		return df.format(c.getTime());
	}


	/** Helper method to copy file to a unique path, so it won't get overridden later by a new recording.
	 * This piece of code was borrowed from Stack Overflow (see references at top).
	 * 
	 * @param: src - source audio file that needs to be copied
	 * @param: dst - destination file where the audio file should be copied to**/
	public void copy(File src, File dst) throws IOException {
		FileInputStream in = new FileInputStream(src);
		FileOutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}


	/**--------------------------------------------- OVERRIDEN METHODS ----------------------------------------------**/

	@Override
	protected void onStart() {
		super.onStart(); // Must do this or app will crash!
		Log.d( "VOICE RECORDER", "onStart()..." );
	}

	@Override
	protected void onRestart() {
		super.onRestart(); // Must do this or app will crash!
		Log.d( "VOICE RECORDER", "onRestart()..." );
	}

	@Override
	protected void onResume() {
		super.onResume(); // Must do this or app will crash!
		Log.d( "VOICE RECORDER", "onResume()..." );
		invalidateOptionsMenu();
	}

	@Override
	protected void onPause() {
		super.onPause(); // Must do this or app will crash!
		Log.d( "VOICE RECORDER", "onPause()..." );
	}

	@Override
	protected void onStop() {
		super.onStop(); // Must do this or app will crash!
		Log.d( "VOICE RECORDER", "onStop()..." );
	}

	@Override
	protected void onDestroy() {
		super.onDestroy(); // Must do this or app will crash!
		Log.d( "VOICE RECORDER", "onDestroy()..." );
	}
}