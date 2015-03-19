package com.k7m.yandr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.util.ArrayList;

/**
 * Created by Andy on 19/04/2014.
 * TODO: this is the ColourDice adder.
 */
public class ColourDiceAddActivity extends BasicDiceAddActivity {

    private static int ADD_COLOURDICE_ACTIVITY = 21;
    private static int EDIT_COLOURDICE_ACTIVITY = 22;
    private static int ADD_TASK = 1;
    private static int EDIT_TASK = 2;

    private static String NEW_DICE_NAME = "NEW_DICE_NAME";
    private static String NEW_DICE_COLOURS = "NEW_DICE_COLOURS";
    private static String NEW_DICE_MULTI = "NEW_DICE_MULTI";
    private static String NEW_DICE_SIDES = "NEW_DICE_SIDES";
    private static String NEW_DICE_MOD = "NEW_DICE_MOD";
    private static String TASK = "TASK";
    // App variables
    private Context mContext;
    private ColourAdapter mColourAdapterTotal, mColourAdapterChosen;
    //private CheckBox mNameDice;
    private EditText editText;

    // Dice Arrays for Dice colours and text representations
    private String[] mDiceColours;
    private ArrayList<String> mDiceChosen;
    ListView mListViewTotal, mListViewChosen;

    SharedPreferences sharedPref;
    private Boolean muteLock;
    private Boolean screenLock;
    private Boolean sumTotals;
    private Boolean canVibrate;
    private Boolean vibrate;
    private Boolean rotate;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.coloureditactivitylayout);
        mDiceColours = getResources().getStringArray(R.array.dice_colours_string_array);
        mDiceChosen = new ArrayList<String> ();



        mListViewTotal = (ListView)findViewById(R.id.available_colour_picker);
        mListViewChosen = (ListView)findViewById(R.id.chosen_colour_picker);

        mColourAdapterTotal = new ColourAdapter(mContext, mDiceColours);
        mColourAdapterChosen = new ColourAdapter(mContext, mDiceChosen);

        mListViewTotal.setAdapter(mColourAdapterTotal);
        mListViewChosen.setAdapter(mColourAdapterChosen);

        AdapterView.OnItemClickListener addItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //check that the colour doesn't already exist in out chosen pool
                if (!mDiceChosen.contains(mDiceColours[position])) {
                    //if it doesn't, we can add it.
                    mDiceChosen.add(mDiceColours[position]);
                    mColourAdapterChosen.notifyDataSetChanged();
                }
            }
        };

        AdapterView.OnItemClickListener removeItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // remove the object(colour) fromthe arraylist.
                mDiceChosen.remove(position);
                mColourAdapterChosen.notifyDataSetChanged();
            }
        };
        mListViewTotal.setOnItemClickListener(addItemClickListener);
        mListViewChosen.setOnItemClickListener(removeItemClickListener);
        editText = (EditText) findViewById(R.id.nameDiceText);
        editText.setText("");
        int func;
        Intent intent = getIntent();
        if (intent.hasExtra(TASK)) {
            func = intent.getIntExtra(TASK, 1);
        }


    }
    public void addDice() {
        Intent intent = new Intent();
        if (mDiceChosen.size() > 1) { //there should be at least 2 colours to choose from when rolling
            if (editText.getText().toString() != null) {
                intent.putExtra(NEW_DICE_NAME, editText.getText().toString());
            } else intent.putExtra(NEW_DICE_NAME, "");
            String[] colours = new String[mDiceChosen.size()];
            colours = mDiceChosen.toArray(colours);
            intent.putExtra(NEW_DICE_COLOURS, colours);
            setResult(RESULT_OK, intent);
        }// otherwise we don't sent the OK signal, or add a dice.
        //regardless, we finish here.
        //TODO: consider a dialog preventing exit by this method unless a valid die is created.
        finish();
    }

    private void loadAndApplyPreferences() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        rotate = sharedPref.getBoolean("pref_rotate", false);
        if (rotate) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndApplyPreferences();
    }
}
