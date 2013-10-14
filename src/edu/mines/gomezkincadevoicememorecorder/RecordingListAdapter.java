package edu.mines.gomezkincadevoicememorecorder;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordingListAdapter extends BaseAdapter {
	private final Context context;
	private LayoutInflater inflater;
	private ArrayList<AudioRecording> recordings;
	
	/** Initializes the inflater that will create each individual row view. Also initializes the context and recordings objects. **/
	public RecordingListAdapter(Context context, ArrayList<AudioRecording> recordings) {
		super();
		this.context = context;
		this.recordings = recordings;
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/** This function is called for however many recordings there are. It inflates a new view for a row,
	 * modifies the name, date, and length for each individual recording, and then returns the view for the row. **/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("VOICE MEMO RECORDER", "getView()");
		// Inflate the view for the row and get handles for all TextViews
		View rowView = inflater.inflate(R.layout.recording_item, parent, false);
		TextView recordingName = (TextView) rowView.findViewById(R.id.recording_name);
		TextView recordingDate = (TextView) rowView.findViewById(R.id.recording_date);
		TextView recordingLength = (TextView) rowView.findViewById(R.id.recording_length);
		
		// Get AudioRecording object for that row and modify the TextViews
		AudioRecording recording = (AudioRecording) getItem(position); 
		Log.d("RECORDING NAME", "a" + recording.getName() + "a");
		if (recording.getName().equals("")) {
			recordingName.setText(R.string.untitled);
		} else {
			recordingName.setText(recording.getName());
		}
		recordingDate.setText(recording.getDate());
		recordingLength.setText(recording.getDuration());
 
		return rowView;
	}

	/** Returns the size of the ListView so we know how many rows to inflate **/
	@Override
	public int getCount() {
		return recordings.size();
	}

	/** Returns the recording object for a certain position in the ListView **/
	@Override
	public Object getItem(int position) {
		return recordings.get(position);
	}

	/** Returns the index of an AudioRecording object from the list of recordings **/
	@Override
	public long getItemId(int position) {
		return recordings.indexOf(getItem(position));
	}

}
