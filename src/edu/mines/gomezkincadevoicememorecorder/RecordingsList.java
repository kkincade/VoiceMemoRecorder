package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.IOException;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
		Log.d("RECORDINGS LIST VIEW", "onCreate()");
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
		setContentView(R.layout.recordings_list);
		final Context context = this;

		listView = (ListView) findViewById(R.id.recording_list);
		recordings = (ArrayList<AudioRecording>) getIntent().getSerializableExtra(MainActivity.RECORDINGS);
		adapter = new RecordingListAdapter(this, recordings);
		listView.setAdapter(adapter);
		player = new MediaPlayer();
		if (recordings.size() > 0) {
			currentRecording = (AudioRecording) adapter.getItem(recordings.size() - 1); // Set current recording to the last audio recording
		}
		// onClick Listener for the ListView items
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
				currentRecording = (AudioRecording) adapter.getItemAtPosition(position);
			}
		});

		// longClick listener to see if user wants to delete the recording
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> adptr, View v, final int position, long id) {
				String recordingName = recordings.get(position).getName();
				new AlertDialog.Builder(context)
				.setTitle("Delete Recording?")
				.setMessage("Are you sure you want to delete " + recordingName + "?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Okay button not clicked
						if (whichButton == -1) {
							recordings.remove(position);
							adapter.notifyDataSetChanged();
							prepareRecordingsList();
							return;
						}
					}})
					.setNegativeButton(android.R.string.no, null).show();
				return true;
			}
		}); 

	}


	/** Returns the list of recordings, just in case any of the recording objects were modified within the List View. Then finishes the Activity. **/
	public void prepareRecordingsList() {
		Log.d("RECORDINGS LIST VIEW", "prepareRecordingsList()");
		Intent myIntent = new Intent();
		myIntent.putExtra(MainActivity.RECORDINGS, recordings);
		setResult(RESULT_OK, myIntent);
	}


	/** This method instantiates a MediaPlayer object and plays the current selection from the ListView **/
	public void playAudioFile( View v ) {
		String audioFilePath = currentRecording.getAudioFile().getAbsolutePath();
		startPlayback(audioFilePath);
	}


	/**------------------------------------------ PLAYBACK FUNCTIONS --------------------------------------------**/

	/** Play back the audio the user has recorded if the recording exists and the player is not currently playing **/
	public void startPlayback(String audioFilePath) {
		Log.d("RECORDINGS LIST VIEW", "startPlayback()");
		if (!player.isPlaying()) {
			player.reset();
			File audioFile = new File (audioFilePath);
			if (audioFile.exists()) {
				try {
					player.setDataSource(audioFilePath);
					player.prepare();
					player.start();
				} catch (IOException e) {
					Log.e("AUDIO PLAYER", "prepare() failed");
				}
			}
		}
	}


	/** If MediaPlayer is playing audio, this PAUSES playback **/
	public void pausePlayback() {
		Log.d("RECORDINGS LIST VIEW", "pausePlayback()");
		if (player != null) {
			if (player.isPlaying()) {
				player.pause();
			}
		}
	}


	/** If MediaPlayer is playing audio, this STOPS playback and releases the MediaPlayer object **/
	public void stopPlayback() {
		Log.d("RECORDINGS LIST VIEW", "stopPlayback()");
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
		Log.d( "RECORDINGS LIST VIEW", "onStart()..." );
	}

	@Override
	protected void onRestart() {
		super.onRestart(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST VIEW", "onRestart()..." );
	}

	@Override
	protected void onResume() {
		super.onResume(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST VIEW", "onResume()..." );
	}

	@Override
	protected void onPause() {
		super.onPause(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST VIEW", "onPause()..." );
		pausePlayback();
	}

	@Override
	protected void onStop() {
		super.onStop(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST VIEW", "onStop()..." );
		stopPlayback();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST VIEW", "onDestroy()..." );
	}
}
