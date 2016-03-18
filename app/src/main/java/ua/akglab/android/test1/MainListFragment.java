package ua.akglab.android.test1;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainListFragment extends Fragment  implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainListFragment";

    private boolean mGotInsert;
    private RecyclerView mRecyclerView;
    private MainListAdapter mMainListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[][] mMainListDataset;
    private MainListFragment mMainListFragment;

    // newInstance constructor for creating fragment with arguments
    public static MainListFragment newInstance(int page, String title) {
        MainListFragment fragment = new MainListFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(0);
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).setMainListFragment(this);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mMainListAdapter = new MainListAdapter(mMainListDataset, getActivity().getResources());
        mRecyclerView.setAdapter(mMainListAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        return view;
    }

    public void updateDataset(boolean gotInsert) {

        if (gotInsert) mGotInsert = true;
        else mGotInsert = false;
        getLoaderManager().restartLoader(0, null, this);

        mMainListDataset = new String[][] {};

    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StaffTable.COLUMN_ID, StaffTable.COLUMN_FIRSTNAME, StaffTable.COLUMN_SECONDNAME,
                StaffTable.COLUMN_BIRTHYEAR, StaffTable.COLUMN_BIRTHPLACE, StaffTable.COLUMN_POSITION
        };
        CursorLoader cursorLoader = new CursorLoader(this.getActivity(),
                StaffContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Resources res = getActivity().getResources();
        String type1 = res.getString(R.string.year_legend_1);
        String type2 = res.getString(R.string.year_legend_2);
        String type3 = res.getString(R.string.year_legend_3);

        mMainListDataset = new String[cursor.getCount()][6];
        cursor.moveToFirst();
        int index = -1;
        while(!cursor.isAfterLast()) {
            index++;

            mMainListDataset[index][0] = String.valueOf(cursor.getLong(0));
            mMainListDataset[index][1] = cursor.getString(1);
            mMainListDataset[index][2] = cursor.getString(2);
            mMainListDataset[index][3] = cursor.getString(3);
            mMainListDataset[index][4] = cursor.getString(4);
            mMainListDataset[index][5] = cursor.getString(5);

            cursor.moveToNext();
        }
        cursor.close();

        mMainListAdapter.updateAdapterData(mMainListDataset, mGotInsert, mRecyclerView);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        mMainListDataset = new String[][] {};
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {

        mMainListDataset = new String[][] {};

        mGotInsert = false;
        getLoaderManager().initLoader(0, null, this);

    }
}
