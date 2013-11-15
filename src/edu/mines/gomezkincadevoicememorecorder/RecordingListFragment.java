package edu.mines.gomezkincadevoicememorecorder;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecordingListFragment extends ListFragment {
	private ListView listView;
	OnHeadlineSelectedListener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onArticleSelected(int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        // Create an array adapter for the list view, using the Ipsum headlines array
//        listView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView list = this.getListView();
        list.setDividerHeight(2);
//        int color = Color.parseColor("7CFC00");
//        list.getDivider().setColorFilter(color);

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
//        if (getFragmentManager().findFragmentById(R.id.article_fragment) != null) {
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
//        try {
//            mCallback = (OnHeadlineSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnHeadlineSelectedListener");
//        }
    }

    
}
