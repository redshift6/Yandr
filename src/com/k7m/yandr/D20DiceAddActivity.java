package com.k7m.yandr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Andy on 19/04/2014.
 */
public class D20DiceAddActivity extends BasicDiceActivity {

    // App variables
    private NumberPicker numpic1,numpic2,numpic3;
    //private CheckBox mNameDice;
    private EditText editText;

    // Dice_Edit_Dialog Number_Picker max and min values
    //overridden in code, ignore MinDiceSides and MaxDiceSides
    private final static int MinDiceMulti = 1;
    private final static int MaxDiceMulti = 9;
    private final static int MinDiceMod = 0;
    private final static int MaxDiceMod = 100;
    // Dice Arrays for Dice sides and text representations
    private String[] mDiceSides;
    private int[] mDiceSidesInt;

    private int edit_position;
    private int mFunction;

    private Boolean screenLock;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.diceeditactivitylayout);
        mDiceSides = getResources().getStringArray(R.array.dice_sides_string_array);
        mDiceSidesInt = getResources().getIntArray(R.array.dice_sides_integer_array);

        numpic1 = (NumberPicker) findViewById(R.id.multipicker);
        numpic1.setMaxValue(MaxDiceMulti);
        numpic1.setMinValue(MinDiceMulti);
        numpic2 = (NumberPicker) findViewById(R.id.sidespicker);
        numpic2.setDisplayedValues(mDiceSides);
        numpic2.setMaxValue(mDiceSides.length-1);
        numpic2.setMinValue(0);
        numpic3 = (NumberPicker) findViewById(R.id.modpicker);
        numpic3.setMaxValue(MaxDiceMod);
        numpic3.setMinValue(MinDiceMod);
        // Suppress soft keyboard from the beginning
        // it was infuriatingly popping up and down whenever user tried to edit multiplier
        numpic1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numpic2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numpic3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        editText = (EditText) findViewById(R.id.nameDiceText);
        editText.setText("");

        //check intent for edit functionality to read in the dice to be edited
        Intent intent = getIntent();
        if (intent.hasExtra(TASK)) {
            mFunction = intent.getIntExtra(TASK, 1);
            if (mFunction == EDIT_TASK) {
                edit_position = intent.getIntExtra(EDIT_POSITION, 0);
                String name = intent.getStringExtra(NEW_DICE_NAME);
                editText.setText(name);
                numpic1.setValue(intent.getIntExtra(NEW_DICE_MULTI, 1));
                int sides = intent.getIntExtra(NEW_DICE_SIDES, 0);
                for (int i = 0; i<mDiceSidesInt.length; i++) {
                    if (sides == mDiceSidesInt[i]) {
                        numpic2.setValue(i);
                    }
                }
                numpic3.setValue(intent.getIntExtra(NEW_DICE_MOD, 0));
            }
        }
    }

    public void addDice() {
        Intent intent = new Intent();
        if (editText.getText().toString() != null) {
            intent.putExtra(NEW_DICE_NAME, editText.getText().toString());
        } else intent.putExtra(NEW_DICE_NAME, "");
        intent.putExtra(NEW_DICE_MULTI, numpic1.getValue());
        intent.putExtra(NEW_DICE_SIDES, mDiceSidesInt[numpic2.getValue()]);
        intent.putExtra(NEW_DICE_MOD, numpic3.getValue());
        setResult(RESULT_OK, intent);
        if (mFunction == EDIT_TASK) {
            intent.putExtra(TASK, EDIT_TASK);
            intent.putExtra(EDIT_POSITION, edit_position);
        }
        finish();
    }
}
