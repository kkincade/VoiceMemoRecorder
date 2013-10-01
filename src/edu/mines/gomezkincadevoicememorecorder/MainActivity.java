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
 * http://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd
 **/
public class MainActivity extends Activity {

	private String audioFilePath1 = null;
	private String audioFilePath2 = null;
	private String audioFilePath3 = null;
	public final static String RECORDING_OBJECTS = "recording";
	
	private double length;
	private ArrayList<AudioRecording> recordings = new ArrayList<AudioRecording> ();
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
		audioFilePath3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_temp3.3gp";

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
				recorder.release();
				recorder = null;
				recorder = new MediaRecorder();
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setOutputFile(audioFilePath1);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

				chronometer.stop();
				length =  (SystemClock.elapsedRealtime() - chronometer.getBase())/1000.0;

				// Merge two audio files into fileOne, clear fileTwo, and set it as the recorder's output
				merge(audioFilePath1, audioFilePath2);


				saveButton.setEnabled(true);
				playbackButton.setEnabled(true);
			}
		}
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

		// Release the recorder object
		recorder.release();
		recorder = null;

		// Merge two audio files
		String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording_" + Integer.toString(numberOfSavedRecordings) + ".3gp";
		File fileOne = new File(audioFilePath1);
		File fileTwo = new File(audioFilePath2);
		merge(audioFilePath1, audioFilePath2);

		File newFile = new File(newPath);

		// Copy audio file to a unique name
		try {
			copy(fileOne, newFile);
			fileOne.delete();
			fileTwo.delete();
			saveButton.setEnabled(false);
			playbackButton.setEnabled(false);
		} catch (IOException e) {
			Log.d("VOICE MEMO RECORDER", "Failed to copy file to new location.");
			e.printStackTrace();
		}
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);
		String date = df.format(c.getTime());
		
		AudioRecording recording = new AudioRecording(newFile, nameEditText.getText().toString(), subjectEditText.getText().toString(), "", date, Double.toString(length));
		// TODO: Pass the recording created above to the new activity
		recordings.add(recording);
		Intent myIntent = new Intent(this, RecordingsList.class); // Had to add new activity tag in Manifest.xml
		myIntent.putExtra(RECORDING_OBJECTS, recordings);
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
	public void merge(String audioFilePath1, String audioFilePath2) {

		// Open both paths into File objects
		File fileOne = new File(audioFilePath1);
		File fileTwo = new File(audioFilePath2);
		try {
			// Create input and output streams
			FileInputStream in1 = new FileInputStream(fileOne);
			FileInputStream in2 = new FileInputStream(fileTwo);
			FileOutputStream out = new FileOutputStream(audioFilePath3);

			// Transfer bytes from in to out
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in1.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}


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
}