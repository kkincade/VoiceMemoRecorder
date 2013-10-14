package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.IOException;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;


public class RecordingsList extends Activity {
	private ListView listView;
	private RecordingListAdapter adapter;
	private ArrayList<AudioRecording> recordings = new ArrayList<AudioRecording> ();
	private AudioRecording currentRecording;
	private MediaPlayer player;
	
	/** Initializes the layout, grabs the serialized recordings ArrayList object, creates a ListView, and then sets the adapter. **/ 
	@SuppressWarnings("unchecked") // We suppressed this warning because we know that the only object we are serializing is the ArrayList of AudioRecording objects
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("VOICE MEMO RECORDER", "RecordingList onCreate()");
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
		setContentView(R.layout.recordings_list);
		
		listView = (ListView) findViewById(R.id.recording_list);
		recordings = (ArrayList<AudioRecording>) getIntent().getSerializableExtra(MainActivity.RECORDINGS);
		adapter = new RecordingListAdapter(this, recordings);
		listView.setAdapter(adapter);
		
		// onClick Listener for the ListView items
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
	            currentRecording = (AudioRecording) adapter.getItemAtPosition(position);
				Toast.makeText(getApplicationContext(), currentRecording.getName(), Toast.LENGTH_SHORT).show();
			}
	    });
		
	}


	/** Returns the list of recordings, just in case any of the recording objects were modified within the List View. Then finishes the Activity. **/
	public void switchToRecordScreen( View v ) {
		Intent myIntent = new Intent();
		myIntent.putExtra(MainActivity.RECORDINGS, recordings);
		setResult(RESULT_OK, myIntent);
		finish();
	}
	
	
	/** This method instantiates a MediaPlayer object and plays the current selection from the ListView **/
	public void playAudioFile( View v ) {
		String audioFilePath = currentRecording.getAudioFile().getAbsolutePath();
		startPlayback(audioFilePath);
	}
	
	
	/**------------------------------------------ PLAYBACK FUNCTIONS --------------------------------------------**/

	/** Play back the audio the user has recorded if the recording exists **/
	public void startPlayback(String audioFilePath) {
		Log.d("VOICE MEMO RECORDER", "startPlayback()");
		File audioFile = new File (audioFilePath);

		if (audioFile.exists()) {
			player = new MediaPlayer();
			try {
				player.setDataSource(audioFilePath);
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
