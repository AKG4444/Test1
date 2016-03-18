package ua.akglab.android.test1;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String LIST_ADAPTER_EVENT = "list_adapter_event";

    private MainListFragment mMainListFragment;
    private ListItemFragment mListItemFragment;

    private FragmentPagerAdapter mAdapterViewPager;
    private ViewPager mViewPager;
    private FloatingActionButton mFab;
    private Handler mHandler;
    private static int mCurrentPage;
    private static int mSelectedListItem;
    private static long mRecordId;
    private static String[] mRecordFields;
    private static final String FAB_VISIBILE = "fab_visible";


    public void setMainListFragment(MainListFragment fragment) {
        mMainListFragment = fragment;
    }

    public void setListItemFragment(ListItemFragment fragment) {
        mListItemFragment = fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentPage==1) {
            mFab.setVisibility(View.INVISIBLE);
            mViewPager.setCurrentItem(0);

        } else super.onBackPressed();
    }

    public void updateFabIcon() {
        if (mCurrentPage==0) {
            mFab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_add_white_24dp));
        } else {
            mFab.setImageDrawable(getResources().getDrawable(R.mipmap.ic_done_white_24dp));
        }
    }

    public void setFabVisibility(boolean visible) {
        if (visible) mFab.setVisibility(View.VISIBLE);
        else mFab.setVisibility(View.INVISIBLE);
    }

    public static class PagerAdapter extends FragmentPagerAdapter {

        private static int itemsAmount = 1;
        private static ViewPager viewPager;

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return itemsAmount;
        }

        public static void setCount(int value) {
            itemsAmount = value;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return MainListFragment.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return ListItemFragment.newInstance(1, "Page # 2");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    public static void setSelectedListItem(int item) {
        mSelectedListItem = item;
    }

    public static int getSelectedListItem() {
        return mSelectedListItem;
    }

    public static void setRecordId(long id) {
        mRecordId = id;
    }

    public static long getRecordId() {
        return mRecordId;
    }

    public static void setRecordFields(ArrayList<String> fieldsList) {
        mRecordFields = fieldsList.toArray(new String[fieldsList.size()]);
    }

    public static String[] getRecordFileds() {
        return mRecordFields;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentPage = mViewPager.getCurrentItem();

        if (mCurrentPage==0) {
            setTitle(getResources().getString(R.string.main_list_title));
            mFab.setVisibility(View.VISIBLE);
        } else {
            boolean visible = getPreferences(MODE_PRIVATE).getBoolean(FAB_VISIBILE, true);
            if (visible)mFab.setVisibility(View.VISIBLE);
            else mFab.setVisibility(View.INVISIBLE);
        }

        //mViewPager.setCurrentItem(0);

        updateFabIcon();

        mHandler = new Handler();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(mEventsListener, new IntentFilter(LIST_ADAPTER_EVENT));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mEventsListener);

        if (mCurrentPage==1) {
            boolean visible = mFab.getVisibility()==View.VISIBLE;
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putBoolean(FAB_VISIBILE, visible);
            editor.commit();
        }

        // getLoaderManager().destroyLoader(0);
    }

    private void init() {
        mCurrentPage = 0;
        mSelectedListItem = -1;

        mFab = (FloatingActionButton)  findViewById(R.id.my_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);

                //updateFabIcon();

                PagerAdapter.setCount(2);
                mViewPager.getAdapter().notifyDataSetChanged();
                if (mCurrentPage==0) {
                    MainActivity.setSelectedListItem(-1);
                    MainActivity.this.setTitle(getResources().getString(R.string.new_item_title));
                    mViewPager.setCurrentItem(1);
                    mListItemFragment.initUI();
                } else {
                    String[] data = mListItemFragment.getData();
                    //String str = data[0]+"_"+data[1]+"_"+data[2]+"_"+data[3]+"_"+data[4];
                    //Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

                    ContentValues values = new ContentValues();
                    values.put(StaffTable.COLUMN_FIRSTNAME, data[0]);
                    values.put(StaffTable.COLUMN_SECONDNAME, data[1]);
                    values.put(StaffTable.COLUMN_BIRTHYEAR, data[2]);
                    values.put(StaffTable.COLUMN_BIRTHPLACE, data[3]);
                    values.put(StaffTable.COLUMN_POSITION, data[4]);

                    if (MainActivity.getSelectedListItem()==-1) {
                        // insert
                        getContentResolver().insert(StaffContentProvider.CONTENT_URI, values);
                        mMainListFragment.updateDataset(true);

                    } else {
                        // update
                        long id = MainActivity.getRecordId();
                        Uri staffUri = Uri.parse(StaffContentProvider.CONTENT_URI + "/" + id);
                        getContentResolver().update(staffUri, values, null, null);
                        mMainListFragment.updateDataset(false);

                    }

                    MainActivity.this.setTitle(getResources().getString(R.string.main_list_title));
                    mViewPager.setCurrentItem(0);
                }

            }
        });

        mViewPager = (ViewPager) findViewById(R.id.vpPager);
        mAdapterViewPager = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapterViewPager);

        // Attach the page change listener inside the activity
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(MainActivity.this,
                //        "Selected page position: " + position, Toast.LENGTH_SHORT).show();
                mCurrentPage = position;
                updateFabIcon();
                if (mCurrentPage==0) {
                    mFab.setVisibility(View.VISIBLE);
                    MainActivity.this.setTitle(getResources().getString(R.string.main_list_title));
                    PagerAdapter.setCount(1);
                } else {
                    if (MainActivity.getSelectedListItem()!=-1) mFab.setVisibility(View.VISIBLE);
                    PagerAdapter.setCount(2);
                }
                mViewPager.getAdapter().notifyDataSetChanged();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
    }

    private BroadcastReceiver mEventsListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LIST_ADAPTER_EVENT)) {
                final int pos = intent.getIntExtra("item", -1);
                final long id = intent.getLongExtra("id", 0L);
                final String firstname = intent.getStringExtra("firstname");
                final String secondname = intent.getStringExtra("secondname");
                final String year = intent.getStringExtra("year");
                final String place = intent.getStringExtra("place");
                final String position = intent.getStringExtra("position");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (pos >= 0) {
                            MainActivity.setSelectedListItem(pos);
                            MainActivity.setRecordId(id);
                            ArrayList<String> list = new ArrayList<String>();
                            list.add(firstname);
                            list.add(secondname);
                            list.add(year);
                            list.add(place);
                            list.add(position);
                            MainActivity.setRecordFields(list);
                            MainActivity.this.setTitle(getResources().getString(R.string.edit_item_title));
                            updateFabIcon();
                            PagerAdapter.setCount(2);
                            mViewPager.getAdapter().notifyDataSetChanged();
                            mViewPager.setCurrentItem(1);
                            mListItemFragment.initUI();
                        }
                    }
                });
            }
            return;
        }
    };

}
