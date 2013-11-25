package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.IOException;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RecordingInformationFragment extends Fragment implements TextWatcher {
	final static String POSITION = "position";
	private AudioRecording recording;
	private RecordingsListAdapter databaseHelper;
	private CustomTextWatcher textWatcher;
	private Cursor c;
	private int position;
	
	private EditText recordingNameEditText;
	private EditText recordingSubjectEditText;
	private EditText recordingNotesEditText;
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private MediaPlayer player;
	private boolean playbackIsPaused = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("RECORDING INFO FRAGMENT", "onCreateView()");
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.recording_information, container, false);
	}

	@Override
	public void onStart() {
		Log.d("RECORDING INFO FRAGMENT", "onStart()");
		super.onStart();
		databaseHelper = new RecordingsListAdapter(this.getActivity());
		databaseHelper.open();
		
		if (!playbackIsPaused) {
			player = new MediaPlayer();
		}
		
		// Listener for when playback finishes
		player.setOnCompletionListener(new OnCompletionListener(){ 
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d("RECORDING INFO FRAGMENT", "onCompletion()");
				mp.reset();
				playButton.setEnabled(true);
				pauseButton.setEnabled(false);
				stopButton.setEnabled(false);	
			}
		});
		
		playButton = (Button) getActivity().findViewById(R.id.play_button);
		pauseButton = (Button) getActivity().findViewById(R.id.pause_button);
		stopButton = (Button) getActivity().findViewById(R.id.stop_button);
		
		recordingNameEditText = (EditText) getActivity().findViewById(R.id.recording_name_edit_text);
		recordingSubjectEditText = (EditText) getActivity().findViewById(R.id.recording_subject_edit_text);
		recordingNotesEditText = (EditText) getActivity().findViewById(R.id.recording_notes_edit_text);
		
		textWatcher = new CustomTextWatcher();
		recordingNameEditText.addTextChangedListener(textWatcher);
		recordingSubjectEditText.addTextChangedListener(textWatcher);
		recordingNotesEditText.addTextChangedListener(textWatcher);
		
		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already been
		// applied to the fragment at this point so we can safely call the method
		// below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// Set article based on argument passed in
			position = args.getInt(POSITION);
		}
	}

	public void updateRecordingInformationView(int position) {
		Log.d("RECORDING INFO FRAGMENT", "updateRecordingInformationView()");
		recording = getRecordingFromDatabase(position);
		if (recording != null) {
			recordingNameEditText.setText(recording.getName());
			recordingSubjectEditText.setText(recording.getSubject());
			recordingNotesEditText.setText(recording.getNotes());
		}
	}
	
	public void setRecordingInformation(Boolean setAll) {
		Log.d("RECORDING INFO FRAGMENT", "setRecordingInformation()");
		// Get the id of the EditText currently in focus
		if (this.getActivity().getCurrentFocus() != null) {
			int id = this.getActivity().getCurrentFocus().getId();
			if (id == recordingNameEditText.getId()) {
				Log.d("UPDATE", "NAME");
				recording.setName(recordingNameEditText.getText().toString());
				updateListItemName();
			} else if (id == recordingSubjectEditText.getId()) {
				Log.d("UPDATE", "SUBJECT");
				recording.setSubject(recordingSubjectEditText.getText().toString());
			} else if (id == recordingNotesEditText.getId()) {
				Log.d("UPDATE", "NOTES");
				recording.setNotes(recordingNotesEditText.getText().toString());
			} 
		}
		
		if (setAll) {
			recording.setName(recordingNameEditText.getText().toString());
			recording.setSubject(recordingSubjectEditText.getText().toString());
			recording.setNotes(recordingNotesEditText.getText().toString());
		}
	}
	
	public void setPosition(int positionArg) {
		Log.d("RECORDING INFO FRAGMENT", "setPosition()");
		this.position = positionArg;
	}
	
	public AudioRecording getRecordingFromDatabase(int position) {
		Log.d("RECORDING INFO FRAGMENT", "getRecordingFromDatabase()");
		c = databaseHelper.fetchAllRecordings();
		if (c.getCount() >= 1) {
			c.moveToPosition(position);
			AudioRecording recordingObject = new AudioRecording(null, null, null, null, null, null);
			
			recordingObject.setAudioFilePath(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_RECORDINGPATH)));
			recordingObject.setName(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_NAME)));
			recordingObject.setDate(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_DATE)));
			recordingObject.setSubject(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_SUBJECT)));
			recordingObject.setNotes(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_NOTES)));
			recordingObject.setDuration(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_LENGTH)));
			return recordingObject;
		} else {
			recordingNameEditText.setVisibility(View.GONE);
			recordingSubjectEditText.setVisibility(View.GONE);
			recordingNotesEditText.setVisibility(View.GONE);
		}
		
		return null;
	}
	
	public void clearAllFocus() {
		Log.d("RECORDING INFO FRAGMENT", "clearAllFocus()");
		recordingNameEditText.clearFocus();
		recordingSubjectEditText.clearFocus();
		recordingNotesEditText.clearFocus();
	}
	
	public void updateListItemName() {
		Log.d("RECORDING INFO FRAGMENT", "updateListItemName()");
		RecordingListFragment recordingListFrag = (RecordingListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.recording_list_fragment);;
		TextView nameLabel = (TextView) recordingListFrag.getListView().getChildAt(position).findViewById(R.id.recording_name);
		nameLabel.setText(recording.getName());
	}
	
	/**------------------------------------------ PLAYBACK FUNCTIONS --------------------------------------------**/

	/** Plays back the currentAudioFilePath if the recording exists and the player is not already currently playing **/
	public void startPlayback( View v ) {
		if (recording != null) {		
			String currentAudioFilePath = recording.getAudioFilePath();
			Log.d("RECORDING INFO FRAGMENT", "startPlayback() --> " + currentAudioFilePath);
	
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
	}


	/** If MediaPlayer is playing audio, this PAUSES playback **/
	public void pausePlayback( View v ) {
		Log.d("RECORDING INFO FRAGMENT", "pausePlayback()");

		if (player != null) {
			if (player.isPlaying()) {
				Log.d("PAUSE", "PLAYBACK");
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
		Log.d("RECORDING INFO FRAGMENT", "stopPlayback()");

		if (player != null) {
			Log.d("RECORDING INFO FRAGMENT", "Player is not null");
			if (player.isPlaying()) {
				player.stop();
				player.reset();

				//Enable play button and disable pause and stop
				playButton.setEnabled(true);
				pauseButton.setEnabled(false);
				stopButton.setEnabled(false);
			} else {
				playButton.setEnabled(true);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d("RECORDING INFO FRAGMENT", "onSaveInstanceState()");
		super.onSaveInstanceState(outState);
		// Save the current article selection in case we need to recreate the fragment
		outState.putInt(POSITION, position);
	}
	
	@Override 
	public void onResume() {
		Log.d("RECORDING INFO FRAGMENT", "onResume()");
		super.onResume();
	}
	
	@Override
	public void onPause() {
		Log.d("RECORDING INFO FRAGMENT", "onPause()");
		super.onPause();
		c = databaseHelper.fetchAllRecordings();
		if (c.getCount() >= 1) {
			setRecordingInformation(true);
			databaseHelper.updateRecording(position, recording);
		}
		pausePlayback(null);
	}
	
	@Override
	public void onStop() {
		Log.d("RECORDING INFO FRAGMENT", "onStop()");
		super.onStop();
		Log.d("playbackIsPaused", Boolean.toString(playbackIsPaused));
		if (!playbackIsPaused) {
			stopPlayback(null);
		}
	}

	
	/** Text Listener to update database any time the user changes information about the recording **/
	@Override
	public void afterTextChanged(Editable arg0) {}
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
	
	private class CustomTextWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable editable) {
			Log.d("RECORDING INFO FRAGMENT", "afterTextChanged --> " + Integer.toString(position));
			c = databaseHelper.fetchAllRecordings();
			if (c.getCount() < 1) {
				return;
			}
			setRecordingInformation(false);
			databaseHelper.updateRecording(position, recording);
			Log.d("UPDATE", "DATABASE");
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
		
	}
}
