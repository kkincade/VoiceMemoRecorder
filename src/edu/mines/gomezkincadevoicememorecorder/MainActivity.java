package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;

/*
 * http://stackoverflow.com/questions/8499042/android-audiorecord-example
 */
public class MainActivity extends Activity {

	private String audio_file = null;
	private Chronometer chronometer;
	private MediaRecorder recorder;
	private MediaPlayer player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		chronometer = (Chronometer) findViewById(R.id.chronometer); // TODO: Format this later
	}

	public void record(View v) {
		Log.d("AUDIO RECORDER", "Start Recording");
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        audio_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_recording.3gp";
        
        recorder.setOutputFile(audio_file);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.d("AUDIO RECORDER", "prepare() failed");
        }

        recorder.start();
		chronometer.start();
		chronometer.setBase(SystemClock.elapsedRealtime());
	}


	public void stop(View v) {
		Log.d("AUDIO RECORDER", "Stop Recording");
		chronometer.stop();
        recorder.stop();
        recorder.release();
        recorder = null;
	}
	
	
	public void playback(View v) {
		Log.d("PATH", audio_file);
		
        player = new MediaPlayer();
        try {
            player.setDataSource(audio_file);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("AUDIO PLAYER", "prepare() failed");
        }
	}

	public void save(View v) {
		Log.d("AUDIO RECORDER", "Save Recording");
		
        player = new MediaPlayer();
        try {
            player.setDataSource(audio_file);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("AUDIO PLAYER", "prepare() failed");
        }
//		AudioRecording recording = new AudioRecording();
		
	}

}
