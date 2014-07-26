package com.k7m.yandr;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

/*TODO: 
 * YetANotherDiceRoller(YANDR)(Yet Another Native Dice Roller)('yandere' or 'yandir')
 * TODO: Complete the DiceAddActivity, with all the additional options it will use
 * TODO: make all user interface elements a fragment
 * TODO: code cleanup, especially in the settings page.
 * TODO: check for any other needed functionality(names and titles)
 */
public class rollingTable extends Activity implements SensorEventListener {
    // Menu variables
    private static int ADD_DICE_ACTIVITY = 1;

    private final int EDIT_ID = 4;
    private final int DELETE_ID = 5;
    private final int REROLL_ID = 6;
    private final int CLONE_ID = 7;
    private static String NEW_DICE_NAME = "NEW_DICE_NAME";
    private static String NEW_DICE_MULTI = "NEW_DICE_MULTI";
    private static String NEW_DICE_SIDES = "NEW_DICE_SIDES";
    private static String NEW_DICE_MOD = "NEW_DICE_MOD";

    // DiceArray
    private ArrayList<Dice> DiceList;
    private final String ArraySaveTag = "Array";

    // Display item variables
    private GridView DiceTable;
    // App variables
    private Context mContext;
    private DiceAdapter mDiceAdapter;
    private NumberPicker mNumpic1,mNumpic2,mNumpic3;
    private CheckBox mShakeLock;
    private boolean mRollLock;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private CheckBox mGroupDice;

    // Motion sensitivity variables
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private final float NOISE = (float) 2.0; //consider as a setting
    private float mCummDelta = 0;
    public long mCummulativeTrigger = 20; //consider as a setting

    // Dice_Edit_Dialog Number_Picker max and min values
    private final static int MinDiceMulti = 1;
    private final static int MaxDiceMulti = 9;
    private final static int MinDiceMod = 0;
    private final static int MaxDiceMod = 100;
    // Dice Arrays for Dice sides and text representations
    private String[] mDiceSides;
    private int[] mDiceSidesInt;

    // Sound variables
    private MediaPlayer mPlayer;

    // Options/Settings variables
    SharedPreferences sharedPref;
    private Boolean muteLock;
    private Boolean screenLock;
    private Boolean sumTotals;
    private Boolean vibrate;
    private Boolean rotate;
    //add static variables to add options to the app

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        DiceList = new ArrayList<Dice>();
        setContentView(R.layout.rtablemain);
        DiceTable = (GridView)findViewById(R.id.DiceView);

        mDiceAdapter = new DiceAdapter(this, DiceList);

        DiceTable.setAdapter(mDiceAdapter);
        registerForContextMenu(DiceTable);
        //set default values on first load
        PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
        // Load preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        loadPreferences();
        initAudio();

        mShakeLock = (CheckBox) findViewById(R.id.switch1);
        mShakeLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRollLock = true;
                } else {
                    mRollLock = false;
                }
            }
        });
        mShakeLock.setChecked(true);
        //TODO add try cases here in case a device does not have an accelerometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        DiceTable.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                diceViewDialog(position);
            }
        });
        mDiceSides = getResources().getStringArray(R.array.dice_sides_string_array);
        mDiceSidesInt = getResources().getIntArray(R.array.dice_sides_integer_array);
    }
    private void loadPreferences() {
        muteLock = sharedPref.getBoolean("pref_mute", false);
        screenLock = sharedPref.getBoolean("pref_force_wake", false);
        DiceTable.setKeepScreenOn(screenLock);
        sumTotals = sharedPref.getBoolean("pref_sum_totals", false);
        mDiceAdapter.setSumTotals(sumTotals);
        mDiceAdapter.notifyDataSetChanged();
        vibrate = sharedPref.getBoolean("pref_still", true);
        rotate = sharedPref.getBoolean("pref_rotate", false);
        if (rotate) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }
    /**
     * Create the MediaPlayer and load the  sound resource
     */
    public void initAudio() {
        // Used with permission under the Creative Commons Attribution 3.0 license
        // Sound file downloaded from:
        // http://soundbible.com/182-Shake-And-Roll-Dice.html
        mPlayer = MediaPlayer.create(mContext,  R.raw.roll);
    }
    /**
     * Play the sound file for dice rolling
     * Will not play if the mute option is set
     */
    public void playAudio() {
        if ((mPlayer != null) && !(muteLock)) {
            mPlayer.seekTo(0);
            mPlayer.start();
        }
    }
    /**
     * Release and null the MediaPlayer resources
     */
    public void endAudio() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    /**
     * Show a dialog that tells us what the dice was set to, what it rolled, and the total
     * @param position The position in the DiceVector to access the position of the view on the screen
     */
    public void diceViewDialog(int position) {
        Dice dice = DiceList.get(position);
        Dialog dialog;
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.viewdicedialog);
        dialog.setTitle(dice.getTitle() + "=" + dice.getTotal());
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(mDiceAdapter.getDiceIconFromPosition(position));
        TextView text1 = (TextView) dialog.findViewById(R.id.text1);
        text1.setText(dice.getName());
        TextView text2 = (TextView) dialog.findViewById(R.id.text2);
        text2.setText(dice.toString());
        TextView text3 = (TextView) dialog.findViewById(R.id.text3);
        text3.setText(dice.getTitle() + " = " + dice.getResult() + " + " + dice.getModifier() + " = " + dice.getTotal());
        dialog.show();
    }
    public void aboutYANDR() {
        Dialog dialog;
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.aboutyandr);
        String title = new String(getString(R.string.about) + " " + getString(R.string.app_name));
        dialog.setTitle(title);
        final Button soundBtn = (Button) dialog.findViewById(R.id.atributelink);
        soundBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.soundurl)));
                startActivity(browserIntent);
            }
        });
        dialog.show();
    }
    /**
     * Add a dice by showing a number picker dialog, allow the user to set the values they want, and then add it to the ArrayList.
     */
    public void addDice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.diceeditdialog2, (ViewGroup) findViewById(R.id.layout_root));
        mNumpic1 = (NumberPicker) layout.findViewById(R.id.multipicker);
        mNumpic1.setMaxValue(MaxDiceMulti);
        mNumpic1.setMinValue(MinDiceMulti);
        mNumpic2 = (NumberPicker) layout.findViewById(R.id.sidespicker);
        mNumpic2.setDisplayedValues(mDiceSides);
        mNumpic2.setMaxValue(mDiceSides.length-1);
        mNumpic2.setMinValue(0);
        mNumpic3 = (NumberPicker) layout.findViewById(R.id.modpicker);
        mNumpic3.setMaxValue(MaxDiceMod);
        mNumpic3.setMinValue(MinDiceMod);
        // Suppress soft keyboard from the beginning
        // it was infuriatingly popping up and down whenever user tried to edit multiplier
        mNumpic1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumpic2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumpic3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mGroupDice = (CheckBox) layout.findViewById(R.id.groupDice);
        mGroupDice.setChecked(false);
        builder.setMessage(R.string.dice_add_dialog_string).setCancelable(true).setPositiveButton(R.string.prompt_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Dice dice = new Dice();
                if (mGroupDice.isChecked() == true)  {
                    dice = new Dice(mNumpic1.getValue(), mDiceSidesInt[mNumpic2.getValue()], mNumpic3.getValue());
                    DiceList.add(dice);
                }
                if (mGroupDice.isChecked() == false) {
                    for (int i = 0;i<mNumpic1.getValue();i++){
                        dice = new Dice(1, mDiceSidesInt[mNumpic2.getValue()], mNumpic3.getValue());
                        DiceList.add(dice);
                    }
                }
                mDiceAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton(R.string.prompt_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setView(layout);
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void editDice(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.diceeditdialog, (ViewGroup) findViewById(R.id.layout_root));
        final Dice currentDice = (Dice)mDiceAdapter.getItem(position);
        mNumpic1 = (NumberPicker) layout.findViewById(R.id.multipicker);
        mNumpic1.setMaxValue(MaxDiceMulti);
        mNumpic1.setMinValue(MinDiceMulti);
        mNumpic2 = (NumberPicker) layout.findViewById(R.id.sidespicker);
        mNumpic2.setDisplayedValues(mDiceSides);
        mNumpic2.setMaxValue(mDiceSides.length-1);
        mNumpic2.setMinValue(0);
        mNumpic3 = (NumberPicker) layout.findViewById(R.id.modpicker);
        mNumpic3.setMaxValue(MaxDiceMod);
        mNumpic3.setMinValue(MinDiceMod);
        mNumpic1.setValue(currentDice.getMultiplier());
        for (int i = 0; i<mDiceSidesInt.length; i++) {
            if (currentDice.getSides() == mDiceSidesInt[i]) {
                mNumpic2.setValue(i);
            }
        }
        mNumpic3.setValue(currentDice.getModifier());
        mNumpic1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumpic2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mNumpic3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        builder.setMessage(R.string.dice_edit_dialog_string).setCancelable(true).setPositiveButton(R.string.prompt_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mNumpic1.getValue() != currentDice.getMultiplier()) {
                    DiceList.get(position).setMultiplier(mNumpic1.getValue());
                }
                if (mDiceSidesInt[mNumpic2.getValue()] != currentDice.getSides()) {
                    DiceList.get(position).setSides(mDiceSidesInt[mNumpic2.getValue()]);
                }
                DiceList.get(position).setModifier(mNumpic3.getValue());
                mDiceAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton(R.string.prompt_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setView(layout);
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void cloneDice(int position){
        Dice dice = DiceList.get(position);
        Dice dice2 = new Dice(dice.getMultiplier(), dice.getSides(), dice.getModifier());
        DiceList.add(position+1, dice2);
        mDiceAdapter.notifyDataSetChanged();

    }
    public void generateRandom(int i) {
        //if i is less than 0, do all dice rolls here, if >=0, do specified dice rolls at the dice class level
        // Use an i of -1 to generate all random values in one place from one rand seed
        if (!DiceList.isEmpty()) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (i<0){
                Random rand = new Random();
                int randNumber;
                for (Dice dice : DiceList) {
                    for (int j=0; j<dice.getMultiplier(); j++){
                        randNumber = rand.nextInt(dice.getSides())+1;
                        dice.setResult(j, randNumber);
                    }
                    dice.sortResults();
                }
            }
            // Use an i of >=0 to delegate all randoming to each individual dice instance
            if (i>=0){
                Dice dice = DiceList.get(i);
                dice.roll();
            }
            mDiceAdapter.notifyDataSetChanged();
            playAudio();
            // Vibrate for 200 milliseconds
            if (vibrate) v.vibrate(200);
        }
    }
    /**
     * Add the menu options
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menulayout, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_clear_screen:
                clearScreen();
                return true;
            case R.id.menu_add_dice:
                addDice();
                return true;
            case R.id.menu_roll_dice:
                generateRandom(-1);
                return true;
            /*case R.id.menu_activity_add_dice:
                Intent intent = new Intent(mContext, DiceAddActivity.class);
                startActivityForResult(intent, ADD_DICE_ACTIVITY);
                return true;*/ 
            case R.id.about_id:
                aboutYANDR();
                return true;
            case R.id.preferences:
                callSettingsScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_DICE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Dice dice = new Dice(
                        data.getIntExtra(NEW_DICE_MULTI, 1),
                        data.getIntExtra(NEW_DICE_SIDES, 1),
                        data.getIntExtra(NEW_DICE_MOD, 1),
                        data.getStringExtra(NEW_DICE_NAME));
                DiceList.add(dice);
                mDiceAdapter.notifyDataSetChanged();
            }
        }
    }
    public void clearScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.prompt_clear_table).setCancelable(true).setPositiveButton(R.string.prompt_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DiceList.clear();
                mDiceAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton(R.string.prompt_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void removeDice(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.prompt_remove_dice).setCancelable(true).setPositiveButton(R.string.prompt_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DiceList.remove(position);
                mDiceAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton(R.string.prompt_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EDIT_ID, 0, R.string.edit_dice);
        menu.add(0, DELETE_ID, 0, R.string.remove_dice);
        menu.add(0, REROLL_ID, 0, R.string.reroll_dice);
        menu.add(0, CLONE_ID, 0, R.string.clone_dice);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        switch(item.getItemId()) {
            case DELETE_ID:
                removeDice(position);
                break;
            case EDIT_ID:
                editDice(position);
                break;
            case REROLL_ID:
                generateRandom(position);
                break;
            case CLONE_ID:
                cloneDice(position);
                break;
        }
        return super.onContextItemSelected(item);
    }
    public void callSettingsScreen() {
        Intent intent = new Intent(mContext, SettingsActivity.class);
        startActivity(intent);
    }
    public void addDice(Dice dice) {
        DiceList.add(dice);
        mDiceAdapter.notifyDataSetChanged();
    }
    public void onSensorChanged(SensorEvent event) {
        // Get instance of Vibrator from current Context
        float deltaX, deltaY, deltaZ;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) { //Initialise everything we need in here
            mLastX = x; //last measured X value
            mLastY = y; //last measured Y value
            mLastZ = z; //last measured Z value
            deltaX = deltaY = deltaZ = 0; //change of X/Y/Z is all 0
            mInitialized = true;
        } else { //everything is initialised, so we do some math
            deltaX = Math.abs(mLastX - x); //the change(delta) in the last measured x and current x values
            deltaY = Math.abs(mLastY - y); //the change(delta) in the last measured y and current y values
            deltaZ = Math.abs(mLastZ - z); //the change(delta) in the last measured z and current z values
            if (deltaX < NOISE) deltaX = (float)0.0; //if the change is less than the NOISE, reset deltaX(change of x)
            if (deltaY < NOISE) deltaY = (float)0.0; //if the change is less than the NOISE, reset deltaY(change of y)
            if (deltaZ < NOISE) deltaZ = (float)0.0; //if the change is less than the NOISE, reset deltaZ(change of z)
            mLastX = x; //last measured X value
            mLastY = y; //last measured Y value
            mLastZ = z; //last measured Z value
            float TotalDelta = deltaX + deltaY + deltaZ; //get the total change of all 3 axes
            mCummDelta = mCummDelta + TotalDelta; //add to our additive change total
            if (mCummDelta > mCummulativeTrigger) {
                if ((!mRollLock) && !(DiceList.isEmpty())) {
                    generateRandom(-1);
                    mShakeLock.setChecked(true);
                }
                mCummDelta = 0;
            }
        }
    }
    private String SaveDiceArray() {
        StringBuilder sb = new StringBuilder();
        for (Dice dice : DiceList) {
            sb.append(dice.getSave()+"|");
        }
        return sb.toString();
    }
    private void ReadSave(String save) {
        Integer length;
        String diceString;
        String multi, sides, mod;
        //parse the string for dice information
        while (save.length() != 0){
            length = save.indexOf("|");
            diceString = save.substring(0, length);
            //parse the individual values to make a dice
            multi = diceString.substring(0, diceString.indexOf("d"));
            diceString = diceString.substring(diceString.indexOf("d")+1);
            sides = diceString.substring(0, diceString.indexOf("+"));
            diceString = diceString.substring(diceString.indexOf("+")+1);
            mod = diceString.substring(0, diceString.indexOf(":"));
            diceString = diceString.substring(diceString.indexOf(":")+1);
            //diceString contains only our results now, add them to the dice.
            Dice dice = new Dice(Integer.parseInt(multi), Integer.parseInt(sides),Integer.parseInt(mod));
            for (int j = 0; j<Integer.parseInt(multi); j++) {
                Integer distance;
                distance = diceString.indexOf(":");
                dice.setResult(j, Integer.parseInt(diceString.substring(0,distance)));
                diceString = diceString.substring(distance+1);
            }
            DiceList.add(dice);
            save = save.substring(length+1);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String save = SaveDiceArray();
        outState.putString(ArraySaveTag, save);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String save = savedInstanceState.getString(ArraySaveTag);
        ReadSave(save);
    }
    @Override
    protected void onPause() {
        super.onPause();
        endAudio();
        mSensorManager.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        initAudio();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        loadPreferences();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        //keep this empty
    }
}