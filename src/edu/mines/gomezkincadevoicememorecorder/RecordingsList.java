package edu.mines.gomezkincadevoicememorecorder;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class RecordingsList extends Activity {
	private String recordingTitle;
	private String recordingDate;
	private String recordingLength;
	private String [] values;
	private ListView list;
	private RecordingListAdapter adapter;
	private MediaPlayer player;
	private String audioFilePath;
	private ArrayList<AudioRecording> recordings = new ArrayList<AudioRecording> ();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
		setContentView(R.layout.recordings_list);
		list = (ListView) findViewById(R.id.recording_list);
//		values = new String[3];
		AudioRecording recordingObject = (AudioRecording) getIntent().getSerializableExtra(MainActivity.RECORDING_OBJECT);
//		int recordingCount = (Integer) getIntent().getSerializableExtra(MainActivity.RECORDING_COUNT);
//		recordingTitle = recordingObject.getName();
//		recordingDate = recordingObject.getDate();
//		recordingLength = recordingObject.getLength();
//		values[0] = recordingTitle;
//		values[1] = recordingDate;
//		values[2] = recordingLength;
		recordings.add(recordingObject);
		Log.d("size", "size of array " + recordings.size());
		adapter = new RecordingListAdapter(this, recordings);
		list.setAdapter(adapter);

	}


	public void switchToRecordScreen() {
		Intent myIntent = new Intent(this, MainActivity.class);
		this.startActivity(myIntent);
	}

	public void playAudioFile( View v ) {
		Intent myIntent = new Intent(this, MainActivity.class);
		this.startActivity(myIntent);



		//		File audioFile = new File (audioFilePath);
		//
		//		if (audioFile.exists()) {
		//			player = new MediaPlayer();
		//			try {
		//				player.setDataSource(audioFilePath);
		//				player.prepare();
		//				player.start();
		//			} catch (IOException e) {
		//				Log.e("AUDIO PLAYER", "prepare() failed");
		//			}
		//		}
	}


	//	private class OnItemClick implements OnItemClickListener {
	//
	//		@Override
	//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	//			AudioRecording recordingObject = (AudioRecording) adapter.getItem(0); 
	////			audioFilePath = recordingObject.getName();
	//			
	//			
	//		}
	//		
	//	}


}
