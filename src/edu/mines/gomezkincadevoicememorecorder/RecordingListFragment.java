package edu.mines.gomezkincadevoicememorecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ListView;

public class RecordingListFragment extends ListFragment {
	OnRecordingSelectedListener mCallback;
	SimpleCursorAdapter adapter;

	// The container Activity must implement this interface so the frag can deliver messages
	public interface OnRecordingSelectedListener {
		/** Called by HeadlinesFragment when a list item is selected */
		public void onRecordingSelected(int position);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		adapter = (SimpleCursorAdapter) this.getListAdapter(); 

		getListView().setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				new AlertDialog.Builder(getActivity())
				.setTitle("Delete Recording?")
				.setMessage("Are you sure you want to delete?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Okay button clicked
						if (whichButton == -1) {
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
		super.onAttach(activity);

		// This makes sure that the container activity has implemented the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnRecordingSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("POSITION", Integer.toString(position));
		// Notify the parent activity of selected item
		mCallback.onRecordingSelected(position);

		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}    
}
