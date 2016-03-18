package ua.akglab.android.test1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by alexandr on 3/17/16.
 */
public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder> {
    private static final String TAG = "MainListAdapter";
    private static String[][] mDataSet;
    private int mCurrYear;
    private String mType1;
    private String mType2;
    private String mType3;


    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView1;
        private final TextView textView2;
        private final TextView textView3;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    String id = mDataSet[pos][0];
                    //Log.d(TAG, "Element " + position + " clicked.");
                    //Toast.makeText(v.getContext().getApplicationContext(), "Item "+pos+", id="+id,
                    //        Toast.LENGTH_SHORT).show();

                    String firstname = mDataSet[pos][1];
                    String secondname = mDataSet[pos][2];
                    String year = mDataSet[pos][3];
                    String place = mDataSet[pos][4];
                    String position = mDataSet[pos][5];

                    broadcastClickEvent(pos,
                            Long.valueOf(id), firstname, secondname, year, place, position,
                            v.getContext().getApplicationContext());
                }
            });
            textView1 = (TextView) v.findViewById(R.id.textView1);
            textView2 = (TextView) v.findViewById(R.id.textView2);
            textView3 = (TextView) v.findViewById(R.id.textView3);
        }

        public TextView getTextView1() {
            return textView1;
        }
        public TextView getTextView2() {
            return textView2;
        }
        public TextView getTextView3() {
            return textView3;
        }
    }


    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public MainListAdapter(String[][] dataSet, Resources res) {
        mDataSet = dataSet;
        mCurrYear = Calendar.getInstance().get(Calendar.YEAR);
        mType1 = res.getString(R.string.year_legend_1);
        mType2 = res.getString(R.string.year_legend_2);
        mType3 = res.getString(R.string.year_legend_3);
    }

    public void updateAdapterData(String[][] dataSet, boolean gotInsert, RecyclerView recyclerView) {
        int oldCount = mDataSet.length;
        mDataSet = dataSet;
        int pos = oldCount;
        if (gotInsert) this.notifyItemInserted(oldCount);
        else {
            pos = MainActivity.getSelectedListItem();
            this.notifyItemChanged(pos);
        }
        recyclerView.scrollToPosition(pos);

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_list_row_item, viewGroup, false);

        return new ViewHolder(v);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        String tmpStr = mDataSet[position][1];
        if (tmpStr.length()>0) tmpStr = " " + tmpStr.substring(0,1) + ".";
        String tmpStr2 = mDataSet[position][2] + tmpStr;
        viewHolder.getTextView1().setText(tmpStr2);

        tmpStr = mDataSet[position][3];
        int tmpInt = mCurrYear - Integer.valueOf(tmpStr);
        int tmpInt2 = tmpInt % 10;

        // TODO: take locale into account
        if (tmpInt2==1) tmpStr = mType1;
        else if (tmpInt2<5) tmpStr = mType2;
        else tmpStr = mType3;

        tmpStr2 = String.valueOf(tmpInt) + " " + tmpStr;
        viewHolder.getTextView2().setText(tmpStr2);

        viewHolder.getTextView3().setText(mDataSet[position][4]);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    private static void broadcastClickEvent(int pos,
               long id, String firstname, String secondname, String year, String place, String position,
                                            Context context) {
        Intent intent = new Intent(MainActivity.LIST_ADAPTER_EVENT);
        intent.putExtra("item", pos);
        intent.putExtra("id", id);
        intent.putExtra("firstname", firstname);
        intent.putExtra("secondname", secondname);
        intent.putExtra("year", year);
        intent.putExtra("place", place);
        intent.putExtra("position", position);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
