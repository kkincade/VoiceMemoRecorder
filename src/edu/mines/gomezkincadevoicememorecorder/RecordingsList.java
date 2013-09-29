package edu.mines.gomezkincadevoicememorecorder;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;


public class RecordingsList extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
        setContentView(R.layout.recordings_list);
	}
	
	
	public void switchToRecordScreen() {
		Intent myIntent = new Intent(this, MainActivity.class);
		this.startActivity(myIntent);
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

}
