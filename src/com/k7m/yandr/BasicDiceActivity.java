package com.k7m.yandr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by Andy on 19/04/2014.
 */
public abstract class BasicDiceActivity extends Activity {

    static int ADD_D20DICE_ACTIVITY = 11;
    static int EDIT_D20DICE_ACTIVITY = 12;
    static int ADD_COLOURDICE_ACTIVITY = 21;
    static int EDIT_COLOURDICE_ACTIVITY = 22;
    static int ADD_TASK = 1;
    static int EDIT_TASK = 2;

    //New dice activity IDs
    static String NEW_DICE_NAME = "NEW_DICE_NAME";
    static String NEW_DICE_MULTI = "NEW_DICE_MULTI";
    static String NEW_DICE_SIDES = "NEW_DICE_SIDES";
    static String NEW_DICE_MOD = "NEW_DICE_MOD";
    static String NEW_DICE_COLOURS = "NEW_DICE_COLOURS";
    static String TASK = "TASK";
    static String EDIT_POSITION = "EDIT_POSITION";
    /**
     * Add the menu options
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addmenulayout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_add_dice:
                addDice();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    abstract void addDice();
}
