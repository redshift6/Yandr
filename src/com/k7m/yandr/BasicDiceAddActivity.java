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
public abstract class BasicDiceAddActivity extends Activity {

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
