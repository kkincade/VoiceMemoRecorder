package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
 * http://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd
 **/
public class MainActivity extends Activity {

	private String audioFilePath1 = null;
	private String audioFilePath2 = null;
	
	// List of widgets
	private EditText nameEditText;
	private EditText subjectEditText;
	private Chronometer chronometer;
	private ToggleButton recordButton;
	private Button saveButton;
	private Button playbackButton;
	
	private MediaRecorder recorder = null;
	private MediaPlayer player;
	private Integer numberOfSavedRecordings = 0; // TODO: Save this as a preference

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
		setContentView(R.layout.activity_main);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		audioFilePath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_temp1.3gp";
		audioFilePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_temp2.3gp";
		
		nameEditText = (EditText) findViewById(R.id.name_edit_text);
		subjectEditText = (EditText) findViewById(R.id.subject_edit_text);
		chronometer = (Chronometer) findViewById(R.id.chronometer); // TODO: Format this later
		recordButton = (ToggleButton) findViewById(R.id.record_button);
		saveButton = (Button) findViewById(R.id.save_button);
		playbackButton = (Button) findViewById(R.id.playback_button);
	}

	
	/** Handles functionality for Record and Pause button (seeing as how they are the same button). **/
	public void record(View v) {
		
		if (v.getId() == R.id.record_button) {
			// Record
			if (recordButton.isChecked()) {
				// If recorder has yet to be initialized
				if (null == recorder) {
					Log.d("VOICE MEMO RECORDER", "Start Recording");
					
					recorder = new MediaRecorder();
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					recorder.setOutputFile(audioFilePath1);
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				}
				
				try {
					recorder.prepare();
				} catch (IOException e) {
					Log.d("MEDIA RECORDER", "prepare() failed");
				}
				recorder.start();
				chronometer.start();
				chronometer.setBase(SystemClock.elapsedRealtime());
			// Pause
			} else {
				Log.d("VOICE MEMO RECORDER", "Pause Recording");
				recorder.stop();
				chronometer.stop();
				recorder.setOutputFile(audioFilePath2);
				// TODO: Merge recordings somehow...
				
				saveButton.setEnabled(true);
				playbackButton.setEnabled(true);
			}
		}
	}


	/** Stop the recording and release the recorder object **/
	public void stop(View v) {
		Log.d("VOICE MEMO RECORDER", "Stop Recording");
		recordButton.setChecked(false);
		chronometer.stop();
		recorder.stop();
		recorder.release();
		recorder = null;
	}

	
	/** Play back the audio the user has recorded if the recording exists **/
	public void playback(View v) {
		Log.d("VOICE MEMO RECORDER", "Playback Recording");
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

	
	/** Copy the audio file to a unique name so it won't get overridden. 
	 * Create a new AudioRecording object and pass it to the RecordingsList view. **/
	public void save(View v) {
		Log.d("VOICE MEMO RECORDER", "Save Recording");

		String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_" + Integer.toString(numberOfSavedRecordings) + ".3gp";
		File oldFile = new File(audioFilePath1);
		File newFile = new File(newPath);
		
		try {
			copy(oldFile, newFile);
			File file = new File(audioFilePath1); 
			file.delete();
			saveButton.setEnabled(false);
			playbackButton.setEnabled(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		AudioRecording recording = new AudioRecording(newFile, nameEditText.getText().toString(), subjectEditText.getText().toString(), "");
		// TODO: Pass the recording created above to the new activity
		
		Intent myIntent = new Intent(this, RecordingsList.class); // Had to add new activity tag in Manifest.xml
		this.startActivity(myIntent);
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
	public void merge(File fileOne, File fileTwo) throws IOException {

		FileInputStream in = new FileInputStream(fileOne);
		FileOutputStream out = new FileOutputStream(audioFilePath1);
		
		
		
		
		
		
		
		
		//		short[] newData = new short[dataOne.length + dataTwo.length];
//		for(int i=0;i<dataOne.length;i++)
//		    newData[i] = dataOne[i];
//		for(int i=0;i<dataTwo.length;i++)
//		    newData[i+dataOne.length] = dataTwo[i];
	}
}
