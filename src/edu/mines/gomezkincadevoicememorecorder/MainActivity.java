package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * MainActivity allows users to record and stop recording audio. Once a voice recording
 * is stoped, it is saved andstored into an AudioRecording object and passed to RecordingsList where it is displayed
 * in a table view.
 *
 * http://stackoverflow.com/questions/8499042/android-audiorecord-example
 * http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
 * http://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd
 * http://developer.android.com/reference/android/media/MediaRecorder.html
 * 
 * @authors - Kameron Kincade, Gonzalo Gomez, and Israel Gomez
 **/

public class MainActivity extends Activity {

	private ArrayList<AudioRecording> recordings = new ArrayList<AudioRecording> ();
	public final static String RECORDINGS = "recordings";
	private String originalAudioFilePath = null;
	private double recordingDuration = 0;
	private long timeWhenStopped = 0;
	private MediaRecorder recorder = null;
	private int numberOfSavedRecordings = 0; // TODO: Save this as a preference

	// List of widgets
	private EditText nameEditText;
	private EditText subjectEditText;
	private Chronometer chronometer;
	private ToggleButton recordButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // Hide keyboard initially
		setContentView(R.layout.activity_main);

		originalAudioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_temp1.3gp";
		nameEditText = (EditText) findViewById(R.id.name_edit_text);
		subjectEditText = (EditText) findViewById(R.id.subject_edit_text);
		chronometer = (Chronometer) findViewById(R.id.chronometer); // TODO: Format this later
		recordButton = (ToggleButton) findViewById(R.id.record_button);
		
		View mainView = findViewById(android.R.id.content);
		final Intent recordingListIntent = new Intent(this, RecordingsList.class);
		
		// Uses an on swipe listener
		mainView.setOnTouchListener(new OnSwipeTouchListener() {
		    public void onSwipeLeft() {
		        Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
		        recordingListIntent.putExtra(RECORDINGS, recordings);
				startActivityForResult(recordingListIntent, 100);
		    }
		});
	}


	/** Record/Pause button is a toggle button. This method first determines whether record or pause was clicked. 
	 * Record: Initializes the MediaRecorder object, starts the chronometer, and starts recording audio.
	 * Pause: Stops recording, merges audio files (if it is the second or more time pause has been clicked), enables save button.  **/
	public void record(View v) {

		if (v.getId() == R.id.record_button) {
			// Record
			if (recordButton.isChecked()) {
				// Starting fresh recording
				if (null == recorder) {
					Log.d("VOICE MEMO RECORDER", "Start Recording");
					recorder = new MediaRecorder();
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					recorder.setOutputFile(originalAudioFilePath);
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					chronometer.setBase(SystemClock.elapsedRealtime());
				// Continuing paused recording
				} else {
					chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
				}

				try {
					recorder.prepare();
				} catch (IOException e) {
					Log.d("MEDIA RECORDER", "prepare() failed");
					return;
				}
				
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
		Log.d("VOICE MEMO RECORDER", "Stop Recording");
		// Stop recording
		recorder.stop();
		recorder.release();
		recorder = null;

		// Stop chronometer
		chronometer.stop();
		timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
		recordingDuration = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000.0;
	}

	
	/** Copies the audio file to a unique name so it won't get overridden.
	 * Then passes the AudioRecording object to the ListView. **/
	public void saveRecording() {
		Log.d("VOICE MEMO RECORDER", "Save Recording");

		// Merge two audio files
		String uniquePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_" + Integer.toString(numberOfSavedRecordings) + ".3gp";
		numberOfSavedRecordings += 1;
		File originalFile = new File(originalAudioFilePath);

		File uniqueFile = new File(uniquePath);

		// Copy audio file to a unique name
		try {
			copy(originalFile, uniqueFile);
			originalFile.delete();
		} catch (IOException e) {
			Log.d("VOICE MEMO RECORDER", "Failed to copy file to new location.");
			e.printStackTrace();
		}

		pushRecordingToList(uniqueFile);
	}


	/** Creates an AudioRecording object with appropriate audio recording and info from text boxes, adds the object to the 
	 * ArrayList "recordings", and starts the ListView activity **/ 
	public void pushRecordingToList(File audioFile) {
		// Get current date
		String date = formatDate();

		// Create new AudioRecording object and add it to the ArrayList
		AudioRecording recording = new AudioRecording(audioFile, nameEditText.getText().toString(), subjectEditText.getText().toString(), "", date, Double.toString(recordingDuration));
		recordings.add(recording);

		// Reset MainActivity for a fresh recording
		nameEditText.setText("");
		subjectEditText.setText("");
		
		// Reset the chronometer
		chronometer.setBase(SystemClock.elapsedRealtime());
		timeWhenStopped = 0;

		// Start ListView Activity
		Intent myIntent = new Intent(this, RecordingsList.class); // Had to add new activity tag in Manifest.xml
		myIntent.putExtra(RECORDINGS, recordings);
		startActivityForResult(myIntent, 100); // 100 is just a code to identify the returning result
	}


	/** This method is called when the user quits the ListView screen. It simply copies the ArrayList of AudioRecordings from the ListView
	 * back into the ArrayList of AudioRecordings in MainActivity. This is needed if the user modified any information of any recordings in the ListView. **/
	@SuppressWarnings("unchecked") // Added because we know that the recordings are the only serialized objects we are passing back and forth.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("VOICE RECORDER", "onActivityResult --- " + Integer.toString(resultCode));
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 100) {
			if (resultCode == RESULT_OK) {     
				recordings = (ArrayList<AudioRecording>) data.getSerializableExtra(RECORDINGS);     
				Log.d("RECORDINGS SIZE", Integer.toString(recordings.size()));
			}
		}
	}


	/**--------------------------------------------- HELPER METHODS -------------------------------------------------**/

	/** Gets the current date and returns it in a string format **/
	public String formatDate() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);
		return df.format(c.getTime());
	}


	/** Helper method to copy file to a unique path, so it won't get overridden later by a new recording **/
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