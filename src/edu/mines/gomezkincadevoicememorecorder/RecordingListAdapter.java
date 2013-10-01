package edu.mines.gomezkincadevoicememorecorder;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordingListAdapter extends BaseAdapter {
	//private final Context context;
	private final String[] values;
	private LayoutInflater inflater;
	private ArrayList<AudioRecording> recordings;

	public RecordingListAdapter(Context context, String[] values, ArrayList<AudioRecording> recordings) {
		super();
		//this.context = context;
		this.values = values;
		this.recordings = recordings;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = inflater.inflate(R.layout.recording_item, null);
		TextView recordingTitle = (TextView) convertView.findViewById(R.id.recording_name);
		TextView recordingDate = (TextView) convertView.findViewById(R.id.recording_date);
		TextView recordingLength = (TextView) convertView.findViewById(R.id.recording_length);

		recordingTitle.setText(values[0]);
		recordingDate.setText(values[1]);
		recordingLength.setText(values[2]);

		return convertView;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return recordings.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
