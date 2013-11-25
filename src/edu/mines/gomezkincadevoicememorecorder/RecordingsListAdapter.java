package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordingsListAdapter extends BaseAdapter {
	
	public static final String KEY_NAME = "name";
	public static final String KEY_DATE = "date";
	public static final String KEY_LENGTH = "length";
	public static final String KEY_RECORDINGPATH = "recording";
	public static final String KEY_SUBJECT = "subject";
	public static final String KEY_NOTES = "notes";
	public static final String KEY_ROWID = "_id";
	
	//Database creation sql statement
	private static final String DATABASE_CREATE ="create table recordings (_id integer primary key autoincrement, name text not null, date text not null, length text not null, subject text, recording text not null, notes text);";
	private static final String DATABASE_NAME = "voice_recorder_db";
	private static final String DATABASE_TABLE = "recordings";
	private static final int DATABASE_VERSION = 2;
	
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase database;
	private LayoutInflater inflater;
	private final Context context;

	
	/** DatabaseHelper class. **/
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS recordings");
			onCreate(db);
		}
	}


	/** Initializes the inflater that will create each individual row view. **/
	public RecordingsListAdapter(Context context) {
		super();
		this.context = context;
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	/** Opens the database. If it won't open, tries to create a new instance. Else throws an exception. **/
	public RecordingsListAdapter open() throws SQLException {
		databaseHelper = new DatabaseHelper(context);
		database = databaseHelper.getWritableDatabase();
		return this;
	}

	
	/** Closes the database. **/
	public void close() {
		databaseHelper.close();
	}

	
	/** Create a new list row using the info from the recording object. Returns -1 if failure happens, else returns the row ID for that recording. **/
	public long createRecording(AudioRecording recording) {
		ContentValues initialValues = new ContentValues();
		SharedPreferences shared = context.getSharedPreferences("voice_memo_preferences", Activity.MODE_PRIVATE);
		String defaultName = (shared.getString(MainActivity.DEFAULT_NAME, ""));
		if (recording.getName().equals("")) {
			if (!defaultName.equals("")) 
				initialValues.put(KEY_NAME, defaultName);
			else 
				initialValues.put(KEY_NAME, context.getString(R.string.untitled));
		} else {
			initialValues.put(KEY_NAME, recording.getName());
		}
		initialValues.put(KEY_DATE, recording.getDate());
		initialValues.put(KEY_LENGTH, recording.getDuration());
		initialValues.put(KEY_RECORDINGPATH, recording.getAudioFilePath());
		initialValues.put(KEY_SUBJECT, recording.getSubject());
		
		return database.insert(DATABASE_TABLE, null, initialValues);
	}
	
	
	/** Deletes a recording from the database using the recording's row ID
	 * 
	 * @param: rowID - the position in the ListView of the element that needs to be deleted **/
	public boolean deleteRecording(int rowId) {
		Cursor c = fetchAllRecordings();
		c.moveToPosition(rowId);
		
		// Delete the audio file before deleting the database
		String audioFilePath = c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_RECORDINGPATH));
		File file = new File(audioFilePath);
		boolean deleted = file.delete();
		
		if (deleted) {
			return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + c.getInt(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_ROWID)), null) > 0;
		} else {
			return false;
		}
    }
	
	
	/** Queries the database and retrieves all entries in the database table **/
	public Cursor fetchAllRecordings() {		
        return database.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DATE, KEY_LENGTH, KEY_RECORDINGPATH, KEY_SUBJECT, KEY_NOTES}, null, null, null, null, null, null);
    }
	
	
	/** Returns a single recording based on a rowID
	 * 
	 * @param: rowID - the position of the element in the ListView **/
	public Cursor fetchRecording(long rowId) throws SQLException {
        Cursor cursor = database.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DATE, KEY_LENGTH, KEY_RECORDINGPATH, KEY_SUBJECT, KEY_NOTES}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
	
	
	/** Updates a recording based on the information passed to the function. **/
    public boolean updateRecording(long rowId, AudioRecording recording) {
    	Cursor c = fetchAllRecordings();
		c.moveToPosition((int) rowId);
		
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, recording.getName());
        args.put(KEY_DATE, recording.getDate());
        args.put(KEY_LENGTH, recording.getDuration());
        args.put(KEY_RECORDINGPATH, recording.getAudioFilePath());
        args.put(KEY_SUBJECT, recording.getSubject());
        args.put(KEY_NOTES,  recording.getNotes());

        return database.update(DATABASE_TABLE, args, KEY_ROWID + "=" + c.getInt(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_ROWID)), null) > 0;
    }
	
    
	/** This function is called for however many recordings there are. It inflates a new view for a row,
	 * modifies the name, date, and length for each individual recording, and then returns the view for the row. 
	 * 
	 * @param: position - the position of the element and where it will be placed in the ListView
	 * @param: convertView - unneeded parameter required by class
	 * @param: parent - unneeded parameter required by class **/
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

		if (recording.getName().equals("")) {
			recordingName.setText(R.string.untitled);
		} else {
			recordingName.setText(recording.getName());
		}
		recordingDate.setText(recording.getDate());
		recordingLength.setText(recording.getDuration() + "s");

		return rowView;
	}

	/** Since we are using a database to store data, these three methods are unused, yet required by the class. They would normally be 
	 * helper methods to tell the ListView how many rows it needs to inflate, or give it an item or its ID based on its position in the list. **/
	@Override
	public int getCount() { return 0; }
	@Override
	public Object getItem(int position) { return null; }
	@Override
	public long getItemId(int position) { return 0;}

}
