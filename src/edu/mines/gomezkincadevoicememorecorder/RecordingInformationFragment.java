package edu.mines.gomezkincadevoicememorecorder;

import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class RecordingInformationFragment extends Fragment {
	final static String POSITION = "position";
	private AudioRecording recording;
	private RecordingsListAdapter databaseHelper;
	private Cursor c;
	int position = 0;
	
	EditText recordingNameEditText;
	EditText recordingSubjectEditText;
	EditText recordingNotesEditText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.recording_information, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		databaseHelper = new RecordingsListAdapter(this.getActivity());
		databaseHelper.open();
		
		recordingNameEditText = (EditText) getActivity().findViewById(R.id.recording_name_edit_text);
		recordingSubjectEditText = (EditText) getActivity().findViewById(R.id.recording_subject_edit_text);
		recordingNotesEditText = (EditText) getActivity().findViewById(R.id.recording_notes_edit_text);
		
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
		recording = getRecordingFromDatabase(position);
		recordingNameEditText.setText(recording.getName());
		recordingSubjectEditText.setText(recording.getSubject());
		recordingNotesEditText.setText(recording.getNotes());
	}
	
	public void setRecordingInformation() {
		recording.setName(recordingNameEditText.getText().toString());
		recording.setSubject(recordingSubjectEditText.getText().toString());
		recording.setNotes(recordingNotesEditText.getText().toString());
	}
	
	public AudioRecording getRecordingFromDatabase(int position) {
		c = databaseHelper.fetchAllRecordings();
		c.moveToPosition(position);
		AudioRecording recordingObject = new AudioRecording(null, null, null, null, null, null);
		
		recordingObject.setAudioFilePath(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_RECORDINGPATH)));
		recordingObject.setName(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_NAME)));
		recordingObject.setDate(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_DATE)));
		recordingObject.setSubject(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_SUBJECT)));
		recordingObject.setNotes(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_NOTES)));
		recordingObject.setDuration(c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_LENGTH)));

		return recordingObject;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the current article selection in case we need to recreate the fragment
		outState.putInt(POSITION, position);
	}
	
	@Override 
	public void onResume() {
		Log.d("RECORDING INFO FRAGMENT", "onResume()");
		super.onResume();
		updateRecordingInformationView(position);
	}
	
	@Override
	public void onPause() {
		Log.d("RECORDING INFO FRAGMENT", "onPause()");
		super.onPause();
		setRecordingInformation();
		databaseHelper.updateRecording(position, recording);
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
}
