package edu.mines.gomezkincadevoicememorecorder;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class RecordingInformationFragment extends Fragment {
	final static String POSITION = "position";
	private AudioRecording recording;
	int position = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.recording_information, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already been
		// applied to the fragment at this point so we can safely call the method
		// below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// Set article based on argument passed in
			position = args.getInt(POSITION);
			recording = (AudioRecording) args.getSerializable(MainActivity.RECORDING);
			updateRecordingInformationView(position, recording);
		} else if (position != -1) {
			// Set article based on saved instance state defined during onCreateView
//			updateRecordingInformationView();
		}
	}

	public void updateRecordingInformationView(int position, AudioRecording recording) {
		EditText recordingName = (EditText) getActivity().findViewById(R.id.recording_name_edit_text);
		EditText recordingSubject = (EditText) getActivity().findViewById(R.id.recording_subject_edit_text);
		recordingName.setText(recording.getName());
		recordingSubject.setText(recording.getSubject());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current article selection in case we need to recreate the fragment
		outState.putInt(POSITION, position);
	}
}
