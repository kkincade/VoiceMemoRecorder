package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.media.MediaPlayer;
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
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ToggleButton;

/**
 * MainActivity allows users to record, pause, stop, and playback voice recordings. Once a voice recording
 * is saved, it is stored into an AudioRecording object and passed to RecordingsList where it is displayed
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
	private String audioFilePath1 = null;
	private String audioFilePath2 = null;
	private String audioFilePath3 = null;
	private double recordingDuration = 0;
	private long timeWhenStopped = 0;
	private MediaRecorder recorder = null;
	private MediaPlayer player = null;
	private Integer numberOfSavedRecordings = 0; // TODO: Save this as a preference

	// List of widgets
	private EditText nameEditText;
	private EditText subjectEditText;
	private Chronometer chronometer;
	private ToggleButton recordButton;
	private Button saveButton;
	private Button playbackButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // Hide keyboard initially
		setContentView(R.layout.activity_main);

		audioFilePath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_temp1.3gp";
		audioFilePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_temp2.3gp";
		audioFilePath3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_temp3.3gp";

		nameEditText = (EditText) findViewById(R.id.name_edit_text);
		subjectEditText = (EditText) findViewById(R.id.subject_edit_text);
		chronometer = (Chronometer) findViewById(R.id.chronometer); // TODO: Format this later
		recordButton = (ToggleButton) findViewById(R.id.record_button);
		saveButton = (Button) findViewById(R.id.save_button);
		playbackButton = (Button) findViewById(R.id.playback_button);
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
					recorder.setOutputFile(audioFilePath1);
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

			// Pause
			} else {
				Log.d("VOICE MEMO RECORDER", "Pause Recording");
				// Stop recording
				recorder.stop();
				recorder.release();
				recorder = null;

				// Stop chronometer
				timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
				chronometer.stop();
				recordingDuration = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000.0;

				// Re-instantiate recorder object
				recorder = new MediaRecorder();
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setOutputFile(audioFilePath1);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

				// Merge two audio files into fileOne, clear fileTwo, and set it as the recorder's output
				merge(audioFilePath1, audioFilePath2);

				saveButton.setEnabled(true);
				playbackButton.setEnabled(true);
			}
		}
	}


	/** Copies the audio file to a unique name so it won't get overridden.
	 * Then passes the AudioRecording object to the ListView. **/
	public void save(View v) {
		Log.d("VOICE MEMO RECORDER", "Save Recording");

		// Release the recorder object
		recorder.release();
		recorder = null;

		// Reset the chronometer
		chronometer.setBase(SystemClock.elapsedRealtime());
		timeWhenStopped = 0;

		// Merge two audio files
		String uniquePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_" + Integer.toString(numberOfSavedRecordings) + ".3gp";
		File fileOne = new File(audioFilePath1);
		File fileTwo = new File(audioFilePath2);
		merge(audioFilePath1, audioFilePath2);

		File uniqueFile = new File(uniquePath);

		// Copy audio file to a unique name
		try {
			copy(fileOne, uniqueFile);
			fileOne.delete();
			fileTwo.delete();
			saveButton.setEnabled(false);
			playbackButton.setEnabled(false);
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

		// Disable save and playback buttons and reset text fields
		saveButton.setEnabled(false);
		playbackButton.setEnabled(false);
		nameEditText.setText("");
		subjectEditText.setText("");

		// Start ListView Activity
		Intent myIntent = new Intent(this, RecordingsList.class); // Had to add new activity tag in Manifest.xml
		myIntent.putExtra(RECORDINGS, recordings);
		this.startActivityForResult(myIntent, 100); // 100 is just a code to identify the returning result
	}


	/** This method is called when the user quits the ListView screen. It simply copies the ArrayList of AudioRecordings from the ListView
	 * back into the ArrayList of AudioRecordings in MainActivity. This is needed if the user modified any information of any recordings in the ListView. **/
	@SuppressWarnings("unchecked") // Added because we know that the recordings are the only serialized objects we are passing back and forth.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			if (resultCode == RESULT_OK) {      
				recordings = (ArrayList<AudioRecording>) data.getSerializableExtra(RECORDINGS);        
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

	/** Helper method to merge two recordings into one (used in the Pause functionality) **/
	public void merge(String audioFilePath1, String audioFilePath2) {

		// Open both paths into File objects
		File fileOne = new File(audioFilePath1);
		File fileTwo = new File(audioFilePath2);
		try {
			// Create input and output streams
			FileInputStream in1 = new FileInputStream(fileOne);
			FileInputStream in2 = new FileInputStream(fileTwo);
			FileOutputStream out = new FileOutputStream(audioFilePath3);

			// Transfer bytes from first file to out
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in1.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			// Transfer bytes from first file to out
			buffer = new byte[1024];
			while ((length = in2.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			// Close streams
			in1.close();
			in2.close();
			out.close();

		} catch (IOException e) {
			Log.d("VOICE MEMO RECORDER", "Merge Failed. IO Exception.");
			e.printStackTrace();
		}
	}


	/**------------------------------------------ PLAYBACK METHODS --------------------------------------------**/

	/** Play back the audio the user has recorded if the recording exists **/
	public void startPlayback() {
		Log.d("VOICE MEMO RECORDER", "startPlayback()");
		File audioFile = new File (audioFilePath1);

		if (audioFile.exists()) {
			player = new MediaPlayer();
			try {
				player.setDataSource(audioFilePath1);
				player.prepare();
				player.start();
			} catch (IOException e) {
				Log.e("AUDIO PLAYER", "prepare() failed");
			}
		}
	}

	/** If MediaPlayer is playing audio, this PAUSES playback **/
	public void pausePlayback() {
		Log.d("VOICE MEMO RECORDER", "pausePlayback()");

		if (player != null) {
			if (player.isPlaying()) {
				player.pause();
			}
		}
	}

	/** If MediaPlayer is playing audio, this STOPS playback and releases the MediaPlayer object **/
	public void stopPlayback() {
		Log.d("VOICE MEMO RECORDER", "stopPlayback()");

		if (player != null) {
			if (player.isPlaying()) {
				player.stop();
				player.release();
				player = null;
			}
		}
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
		pausePlayback();
	}

	@Override
	protected void onStop() {
		super.onStop(); // Must do this or app will crash!
		Log.d( "VOICE RECORDER", "onStop()..." );
		stopPlayback();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy(); // Must do this or app will crash!
		Log.d( "VOICE RECORDER", "onDestroy()..." );
	}
}