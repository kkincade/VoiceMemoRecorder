package edu.mines.gomezkincadevoicememorecorder;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class RecordingsList extends FragmentActivity implements RecordingListFragment.OnRecordingSelectedListener {
	private AudioRecording recording;
	private RecordingsListAdapter databaseHelper;
	private Cursor recordingsCursor;
	private SimpleCursorAdapter adapter;
	private RecordingListFragment listFragment;
	private RecordingInformationFragment recordingInfoFragLarge;
	private RecordingInformationFragment recordingInfoFragSmall;
	private FragmentManager fragmentManager;


	/** Initializes the layout, grabs the recording object passed from MainActivity, creates a ListView, and then sets the adapter. **/ 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("RECORDINGS LIST", "onCreate()");
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // Hide keyboard initially
		setContentView(R.layout.recordings_container);
		recording = (AudioRecording) getIntent().getSerializableExtra(MainActivity.RECORDING);
		fragmentManager = (FragmentManager) this.getSupportFragmentManager();
		recordingInfoFragLarge = (RecordingInformationFragment) getSupportFragmentManager().findFragmentById(R.id.recording_information_fragment);
		databaseHelper = new RecordingsListAdapter(this);
		databaseHelper.open();

		// Create recording if the user recorded audio in MainActivity. Will be null if they used swipe gesture to access the ListView
		if (recording.getAudioFilePath() != null) {
			databaseHelper.createRecording(recording);	
		}

		fillData();
		
		// Small layout
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
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
			
		// Large layout
		} else {
			listFragment = (RecordingListFragment) fragmentManager.findFragmentById(R.id.recording_list_fragment);
			listFragment.setListAdapter(adapter);
		}
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
		if (recordingInfoFragLarge != null) {
			// In large-layout
			Log.d("RECORDINGS LIST", "displayRecordingInformation() --> large-layout");
			recordingInfoFragLarge.setPosition(position);
			recordingInfoFragLarge.clearAllFocus();
			recordingInfoFragLarge.updateRecordingInformationView(position);
//			((BaseAdapter) listFragment.getListAdapter()).notifyDataSetChanged();
		} else {
			// In normal layout
			Log.d("RECORDINGS LIST", "displayRecordingInformation() --> normal-layout");

			// Create fragment and give it an argument for the selected article
			recordingInfoFragSmall = new RecordingInformationFragment();
			Bundle args = new Bundle();

			// Pass position in list and recording object to the fragment
			args.putInt(RecordingInformationFragment.POSITION, position);
			recordingInfoFragSmall.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack so the user can navigate back
			transaction.replace(R.id.fragment_container, recordingInfoFragSmall);
			transaction.addToBackStack(null);
			transaction.commit();	
		}
	}
	
	@Override
	public void onRecordingSelected(int position) {
		Log.d("RECORDINGS LIST", "onRecordingSelected() --> osition = " + Integer.toString(position));
		displayRecordingInformation(position);
	}
	
	public void startPlayback(View v) {
		Log.d("RECORDINGS LIST", "startPlayback()");
		if (recordingInfoFragLarge != null) {
			recordingInfoFragLarge.startPlayback(v);
		} else {
			recordingInfoFragSmall.startPlayback(v);
		}
	}
	
	public void pausePlayback(View v) {
		Log.d("RECORDINGS LIST", "pausePlayback()");
		if (recordingInfoFragLarge != null) {
			recordingInfoFragLarge.pausePlayback(v);
		} else {
			recordingInfoFragSmall.pausePlayback(v);
		}
	}
	
	public void stopPlayback(View v) {
		Log.d("RECORDINGS LIST", "stopPlayback()");
		if (recordingInfoFragLarge != null) {
			recordingInfoFragLarge.stopPlayback(v);
		} else {
			recordingInfoFragSmall.stopPlayback(v);
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
//
//		// Pause playback if app is paused.
//		pausePlayback(null);
	}

	@Override
	protected void onStop() {
		super.onStop(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST", "onStop()..." );

		// Stop playback if app is stopped.
//		stopPlayback(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy(); // Must do this or app will crash!
		Log.d( "RECORDINGS LIST", "onDestroy()..." );
	}
}
