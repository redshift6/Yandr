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
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Andy on 19/04/2014.
 */
public class D20DiceAddActivity extends Activity {

    private static int ADD_D20DICE_ACTIVITY = 1;
    private static String NEW_DICE_NAME = "NEW_DICE_NAME";
    private static String NEW_DICE_MULTI = "NEW_DICE_MULTI";
    private static String NEW_DICE_SIDES = "NEW_DICE_SIDES";
    private static String NEW_DICE_MOD = "NEW_DICE_MOD";
    // App variables
    private Context mContext;
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

    private Boolean screenLock;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }
    public void addDice(View view) {
        Intent intent = new Intent();
        if (editText.getText().toString() != null) {
            intent.putExtra(NEW_DICE_NAME, editText.getText().toString());
        } else intent.putExtra(NEW_DICE_NAME, "");
        intent.putExtra(NEW_DICE_MULTI, numpic1.getValue());
        intent.putExtra(NEW_DICE_SIDES, mDiceSidesInt[numpic2.getValue()]);
        intent.putExtra(NEW_DICE_MOD, numpic3.getValue());
        setResult(RESULT_OK, intent);
        finish();
    }
}
