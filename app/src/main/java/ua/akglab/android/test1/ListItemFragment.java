package ua.akglab.android.test1;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.Calendar;

/**
 * Created by alexandr on 3/17/16.
 */
public class ListItemFragment extends Fragment {
    private static final String TAG = "ListItemFragment";

    private boolean mFirstnameOK;
    private boolean mSecondnameOK;

    private EditText mFirstname;
    private EditText mSecondname;
    private NumberPicker mNp;
    private Spinner mSpinner1;
    private Spinner mSpinner2;

    private String[] mNpValues;
    private String[] mListItemDataset;
    private View mView;


    // newInstance constructor for creating fragment with arguments
    public static ListItemFragment newInstance(int page, String title) {
        ListItemFragment fragment = new ListItemFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onDestroyView() {
        hideKeyboard();
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).setListItemFragment(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_secondary, container, false);

        mFirstnameOK = false;
        mSecondnameOK = false;

        initUI();

        return mView;
    }

    public void initUI() {
        Log.v(TAG, "initUI");

        int yearItem = 0;
        String placeDb = "";
        String firstnameDb = "";
        String secondnameDb = "";
        String positionDb = "";

        View view = mView;

        int selectedItem = MainActivity.getSelectedListItem();

        if (selectedItem!=-1) {
            // edit profile
            String[] fields = MainActivity.getRecordFileds();
            yearItem = Integer.valueOf(fields[2]);
            placeDb = fields[3];
            firstnameDb = fields[0];
            secondnameDb = fields[1];
            positionDb = fields[4];
        }

        mNp = (NumberPicker) view.findViewById(R.id.birthyear_picker);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int max = year - 17;
        int min = max - 90;
        int range = max - min + 1;
        mNpValues = new String[range];
        for(int i=0;i<mNpValues.length;i++){
            mNpValues[i]=Integer.toString(min+i);
        }
        mNp.setWrapSelectorWheel(false);
        mNp.setMaxValue(mNpValues.length - 1);
        mNp.setMinValue(0);
        mNp.setDisplayedValues(mNpValues);
        if (selectedItem==-1) mNp.setValue(mNpValues.length - 2);
        else {
            int selectedYearIndex = yearItem - min;
            mNp.setValue(selectedYearIndex);
        }

        mSpinner1 = (Spinner) view.findViewById(R.id.birthplace_edit);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.places_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinner1.setAdapter(adapter1);
        if (selectedItem==-1) mSpinner1.setSelection(0);
        else mSpinner1.setSelection(getSpinnerSelectedItem(R.array.places_array, placeDb));

        mSpinner2 = (Spinner) view.findViewById(R.id.position_edit);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.positions_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner2.setAdapter(adapter2);
        if (selectedItem==-1) mSpinner2.setSelection(0);
        else mSpinner2.setSelection(getSpinnerSelectedItem(R.array.positions_array,positionDb ));

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.scrollable_body);
        ll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        mFirstname = (EditText) view.findViewById(R.id.firstname_edit);
        mFirstname.setText(firstnameDb);
        mFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mFirstname.getText().toString().trim();
                if (text.length() > 0) {
                    mFirstnameOK = true;
                    if (mSecondnameOK) ((MainActivity) getActivity()).setFabVisibility(true);
                } else {
                    mFirstnameOK = false;
                    ((MainActivity) getActivity()).setFabVisibility(false);
                }

            }
        });

        mSecondname = (EditText) view.findViewById(R.id.secondname_edit);
        mSecondname.setText(secondnameDb);
        mSecondname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mSecondname.getText().toString().trim();
                if (text.length() > 0) {
                    mSecondnameOK = true;
                    if (mFirstnameOK) ((MainActivity) getActivity()).setFabVisibility(true);
                } else {
                    mSecondnameOK = false;
                    ((MainActivity) getActivity()).setFabVisibility(false);
                }

            }
        });

        hideKeyboard();

    }

    public String[] getData() {
        String[] data = new String[5];
        data[0] = mFirstname.getText().toString();
        data[1] = mSecondname.getText().toString();
        data[2] = mNpValues[mNp.getValue()];
        data[3] = mSpinner1.getSelectedItem().toString();
        data[4] = mSpinner2.getSelectedItem().toString();

        return  data;
    }

    private int getSpinnerSelectedItem(int id, String value) {
        int item = 0;
        if (value.length()==0) return item;
        String[] mEntries = getActivity().getResources().getStringArray(id);
        for (int i=0; i<mEntries.length; i++) {
            if (value.equals((mEntries[i]))) {
                item = i;
                break;
            }
        }
        return item;
    }

    private void hideKeyboard() {
        if (getActivity()!=null && getActivity().getWindow()!=null && getActivity().getWindow().getCurrentFocus()!=null) {
            if (android.os.Build.VERSION.SDK_INT < 11) {
                ((InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(),0);
            } else {
                ((InputMethodManager) getActivity().getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
