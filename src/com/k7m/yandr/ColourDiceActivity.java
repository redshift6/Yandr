package com.k7m.yandr;

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

import java.util.ArrayList;

/**
 * Created by Andy on 19/04/2014.
 */
public class ColourDiceActivity extends BasicDiceActivity {

    // App variables
    private ColourAdapter mColourAdapterTotal, mColourAdapterChosen;
    //private CheckBox mNameDice;
    private EditText mEditText;

    // Dice Arrays for Dice colours and text representations
    private String[] mDiceColours;
    private ArrayList<String> mDiceChosen;
    ListView mListViewTotal, mListViewChosen;
    private int edit_position;
    private int mFunction;

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
        mEditText = (EditText) findViewById(R.id.nameDiceText);
        mEditText.setText("");

        //check intent for edit functionality to read in the dice to be edited
        Intent intent = getIntent();
        if (intent.hasExtra(TASK)) {
            mFunction = intent.getIntExtra(TASK, 1);
            if (mFunction == EDIT_TASK) {
                edit_position = intent.getIntExtra(EDIT_POSITION, 0);
                String name = intent.getStringExtra(NEW_DICE_NAME);
                mEditText.setText(name);
                String[] editDice = intent.getStringArrayExtra(NEW_DICE_COLOURS);
                for (int i =0; i< editDice.length; i++) {
                    mDiceChosen.add(editDice[i]);
                }
            }
        }
    }
    public void addDice() {
        Intent intent = new Intent();
        if (mDiceChosen.size() > 1) { //there should be at least 2 colours to choose from when rolling
            if (mEditText.getText().toString() != null) {
                intent.putExtra(NEW_DICE_NAME, mEditText.getText().toString());
            } else intent.putExtra(NEW_DICE_NAME, "");
            String[] colours = new String[mDiceChosen.size()];
            colours = mDiceChosen.toArray(colours);
            intent.putExtra(NEW_DICE_COLOURS, colours);
            setResult(RESULT_OK, intent);
            if (mFunction == EDIT_TASK) {
                intent.putExtra(TASK, EDIT_TASK);
                intent.putExtra(EDIT_POSITION, edit_position);
            }
        }// otherwise we don't sent the OK signal, or add a dice.
        //regardless, we finish here.
        finish();
    }
}
