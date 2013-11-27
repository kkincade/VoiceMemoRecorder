package edu.mines.gomezkincadevoicememorecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class RecordingListFragment extends ListFragment {
	OnRecordingSelectedListener listener;
	private SimpleCursorAdapter adapter;
	private RecordingsListAdapter databaseHelper;
	private Cursor recordingsCursor;

	/** The container Activity must implement this interface so the fragment can deliver messages **/
	public interface OnRecordingSelectedListener {
		/** Called by HeadlinesFragment when a list item is selected */
		public void onRecordingSelected(int position);
	}
	
	
	/** Resets the adapter to use everything from the database, essentially refreshing the list view **/
	@SuppressWarnings("deprecation")
	public void refreshList() {
		Log.d("RECORDING LIST FRAGMENT", "refreshList()");
		recordingsCursor = databaseHelper.fetchAllRecordings();
		
		// Create an array to specify the fields we want to display in the list (only TITLE)
		String[] from = new String[]{RecordingsListAdapter.KEY_NAME, RecordingsListAdapter.KEY_DATE, RecordingsListAdapter.KEY_LENGTH};

		// Create an array of the widgets we want to set the fields to
		int[] to = new int[]{R.id.recording_name, R.id.recording_date, R.id.recording_length};

		// Now create a simple cursor adapter and set it to display
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.recording_item, recordingsCursor, from, to);
		adapter.notifyDataSetChanged();
		this.setListAdapter(adapter);
	}
	
	
	/** ---------------------------- Overridden methods -------------------------------- **/

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("RECORDING LIST FRAGMENT", "onListItemClick()");
		// Notify the parent activity of selected item
		listener.onRecordingSelected(position);
		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}   

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("RECORDING LIST FRAGMENT", "onCreate()");
		super.onCreate(savedInstanceState);
	}
	

	/** Implements a long click listener to delete voice recordings. **/
	@Override
	public void onStart() {
		Log.d("RECORDING LIST FRAGMENT", "onStart()");
		super.onStart();
		adapter = (SimpleCursorAdapter) this.getListAdapter(); 
		databaseHelper = new RecordingsListAdapter(this.getActivity());
		databaseHelper.open();

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public boolean onItemLongClick(AdapterView<?> adptr, View v, final int position, long id) {
				Log.d("RECORDING LIST FRAGMENT", "onItemLongClick()");
				// Get all of the rows from the database and create the item list
				Cursor c = databaseHelper.fetchAllRecordings();
				getActivity().startManagingCursor(c);

				c.moveToPosition(position);
				String recordingName = c.getString(c.getColumnIndexOrThrow(RecordingsListAdapter.KEY_NAME));

				// Display dialog asking if user wants to delete the voice memo
				new AlertDialog.Builder(getActivity())
				.setTitle("Delete Recording?")
				.setMessage("Are you sure you want to delete " + recordingName + "?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Okay button clicked
						if (whichButton == -1) {
							databaseHelper.deleteRecording(position);
							refreshList();
							return;
						}
					}})
					.setNegativeButton(android.R.string.no, null).show();
				return true;
			}
		});

		// When in two-pane layout, set the listview to highlight the selected list item
		// (We do this during onStart because at the point the listview is available.)
		if (getFragmentManager().findFragmentById(R.id.recording_information_fragment) != null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	
	@Override
	public void onAttach(Activity activity) {
		Log.d("RECORDING LIST FRAGMENT", "onAttach()");
		super.onAttach(activity);

		// This makes sure that the container activity has implemented the callback interface. If not, it throws an exception.
		try {
			listener = (OnRecordingSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}
	
	
	@Override
	public void onResume() {
		Log.d("RECORDING LIST FRAGMENT", "onResume()");
		super.onResume();
		refreshList(); 
	}
}
