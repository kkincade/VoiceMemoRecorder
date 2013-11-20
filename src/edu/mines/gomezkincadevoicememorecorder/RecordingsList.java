package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.IOException;
import android.widget.Button;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.Window;


public class RecordingsList extends FragmentActivity implements RecordingListFragment.OnRecordingSelectedListener {
	private AudioRecording recording;
	private String currentAudioFilePath;
	private MediaPlayer player;
	private RecordingsListAdapter databaseHelper;
	private Cursor recordingsCursor;
	SimpleCursorAdapter adapter;
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private boolean playbackIsPaused = false;


	/** Initializes the layout, grabs the recording object passed from MainActivity, creates a ListView, and then sets the adapter. **/ 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("RECORDINGS LIST", "onCreate()");
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
		setContentView(R.layout.recordings_container);

		player = new MediaPlayer();
		playButton = (Button) findViewById(R.id.play_button);
		pauseButton = (Button) findViewById(R.id.pause_button);
		stopButton = (Button) findViewById(R.id.stop_button);
		recording = (AudioRecording) getIntent().getSerializableExtra(MainActivity.RECORDING);
		databaseHelper = new RecordingsListAdapter(this);
		databaseHelper.open();

		// Create recording if the user recorded audio in MainActivity. Will be null if they used swipe gesture to access the ListView
		if (recording.getAudioFilePath() != null) {
			databaseHelper.createRecording(recording);	
		}

		fillData();
		
		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first fragment
		if (findViewById(R.id.fragment_container) != null) {
			// However, if we're being restored from a previous state, then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}
			// Create an instance of ExampleFragment
			RecordingListFragment firstFragment = new RecordingListFragment();
			firstFragment.setListAdapter(adapter);
			
			// In case this activity was started with special instructions from an Intent, pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container, firstFragment).commit();
		}
		
		/** onClick Listener for the ListView items. Once an item is clicked, that row's audio file path
		 * is set as the currentAudioFilePath, and the play button is enabled for playback. **/
		//		listView.setOnItemClickListener(new OnItemClickListener() {
		//			@Override
		//			public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
		//				Log.d("RECORDINGS LIST", "onItemClick()");
		//				final Cursor c = recordingsCursor;
		//				c.moveToPosition(position);
		//				currentAudioFilePath = c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_RECORDING));
		//				//playButton.setEnabled(true);
		//				displayRecordingInformation();
		//			}
		//		});
		//
		//		/** longClick listener to see if user wants to delete the recording **/
		//		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
		//			public boolean onItemLongClick(AdapterView<?> adptr, View v, final int position, long id) {
		//				Log.d("RECORDINGS LIST", "onItemLongClick()");
		//
		//				final Cursor c = recordingsCursor;
		//				c.moveToPosition(position);
		//				String recordingName = c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_NAME));
		//
		//				// Display dialog asking if user wants to delete the voice memo
		//				new AlertDialog.Builder(context)
		//				.setTitle("Delete Recording?")
		//				.setMessage("Are you sure you want to delete " + recordingName + "?")
		//				.setIcon(android.R.drawable.ic_dialog_alert)
		//				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		//					public void onClick(DialogInterface dialog, int whichButton) {
		//						// Okay button clicked
		//						if (whichButton == -1) {
		//							databaseHelper.deleteRecording(c.getPosition() + 1);
		//							fillData();
		//							return;
		//						}
		//					}})
		//					.setNegativeButton(android.R.string.no, null).show();
		//				return true;
		//			}
		//		}); 
	}

	/** fillData() iterates over every row in the database table and creates a row in the ListView with the corresponding
	 * values substituted in. We understand that startManagingCursor() is deprecated and we plan to look into an alternative
	 * using the CursorLoader for App #3.
	 */
	@SuppressWarnings("deprecation")
	private void fillData() {
		Log.d("RECORDINGS LIST", "fillData()");
		// Get all of the rows from the database and create the item list
		recordingsCursor = databaseHelper.fetchAllRecordings();
		startManagingCursor(recordingsCursor);

		// Create an array to specify the fields we want to display in the list (only TITLE)
		String[] from = new String[]{RecordingsListAdapter.KEY_NAME, RecordingsListAdapter.KEY_DATE, RecordingsListAdapter.KEY_LENGTH};

		// Create an array of the widgets we want to set the fields to
		int[] to = new int[]{R.id.recording_name, R.id.recording_date, R.id.recording_length};

		// Now create a simple cursor adapter and set it to display
		adapter = new SimpleCursorAdapter(this, R.layout.recording_item, recordingsCursor, from, to);
	}


	public void displayRecordingInformation(int position) {
		// Capture the article fragment from the activity layout
		RecordingFragment recordingFrag = (RecordingFragment) getSupportFragmentManager().findFragmentById(R.id.recording_information_fragment);

		if (recordingFrag != null) {
			// In large-layout
			Log.d("RECORDINGS LIST", "displayRecordingInformation() --> large-layout");
			
			// Call a method in the ArticleFragment to update its content
            recordingFrag.updateRecordingInformationView();

		} else {
			// In normal layout
			Log.d("RECORDINGS LIST", "displayRecordingInformation() --> normal-layout");

			// Create fragment and give it an argument for the selected article
			RecordingFragment newFragment = new RecordingFragment();
			Bundle args = new Bundle();
			
			// Get information from database
			final Cursor c = recordingsCursor;
			c.moveToPosition(position);
			AudioRecording recordingObject = new AudioRecording(null, null, null, null, null, null);
			
			recordingObject.setAudioFilePath(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_RECORDINGPATH)));
			recordingObject.setName(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_NAME)));
			recordingObject.setDate(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_DATE)));
			
//			recordingObject.setSubject(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.));
//			recordingObject.setNotes(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_RECORDING));
			
			// Pass position in list and recording object to the fragment
			args.putInt(RecordingFragment.POSITION, position);
			args.putSerializable(MainActivity.RECORDING, recordingObject);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack so the user can navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
	
	@Override
	public void onRecordingSelected(int position) {
		// TODO Auto-generated method stub
		displayRecordingInformation(position);
	}


	/**------------------------------------------ PLAYBACK FUNCTIONS --------------------------------------------**/

	/** Plays back the currentAudioFilePath if the recording exists and the player is not already currently playing **/
	public void startPlayback( View v ) {
		Log.d("RECORDINGS LIST", "startPlayback() --> " + currentAudioFilePath);

		if (!player.isPlaying()) {
			if (playbackIsPaused) {
				// Just resume playback
				player.start();
				playbackIsPaused = false;
			} else {
				// Start audio from beginning
				player.reset();
				File audioFile = new File (currentAudioFilePath);
				if (audioFile.exists()) {
					try {
						player.setDataSource(currentAudioFilePath);
						player.prepare();
						player.start();
					} catch (IOException e) {
						Log.e("AUDIO PLAYER", "prepare() failed");
					}
				}
			}

			// Enable pause and stop buttons and disable play button
			pauseButton.setEnabled(true);
			stopButton.setEnabled(true);
			playButton.setEnabled(false);
		}
	}


	/** If MediaPlayer is playing audio, this PAUSES playback **/
	public void pausePlayback( View v ) {
		Log.d("RECORDINGS LIST", "pausePlayback()");
		if (player != null) {
			if (player.isPlaying()) {
				player.pause();
				playbackIsPaused = true;
				playButton.setEnabled(true);
				pauseButton.setEnabled(false);
				stopButton.setEnabled(false);
			}
		}
	}


	/** If MediaPlayer is playing audio, this STOPS playback and releases the MediaPlayer object **/
	public void stopPlayback( View v ) {
		Log.d("RECORDINGS LIST", "stopPlayback()");
		if (player != null) {
			if (player.isPlaying()) {
				player.stop();
				player.release();
				player = null;

				//Enable play button and disable pause and stop
				playButton.setEnabled(true);
				pauseButton.setEnabled(false);
				stopButton.setEnabled(false);
			}
		}
	}

	/**--------------------------------------------- OVERRIDEN METHODS ----------------------------------------------**/

	@Override
	protected void onStart() {
		super.onStart(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST", "onStart()..." );
	}

	@Override
	protected void onRestart() {
		super.onRestart(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST", "onRestart()..." );
	}

	@Override
	protected void onResume() {
		super.onResume(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST", "onResume()..." );
	}

	@Override
	protected void onPause() {
		super.onPause(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST", "onPause()..." );

		// Pause playback if app is paused.
		pausePlayback(null);
	}

	@Override
	protected void onStop() {
		super.onStop(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST", "onStop()..." );

		// Stop playback if app is stopped.
		stopPlayback(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST", "onDestroy()..." );
	}
}
