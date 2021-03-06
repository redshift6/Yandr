package com.k7m.yandr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

/*TODO: 
 * YetANotherDiceRoller(YANDR)(Yet Another Native Dice Roller)('yandere' or 'yandir')
 * TODO: code cleanup, especially in the settings page.
 * TODO: investigate GridLayout
 */
public class rollingTable extends Activity implements SensorEventListener {
    // Menu variables
    private static int ADD_D20DICE_ACTIVITY = 11;
    private static int EDIT_D20DICE_ACTIVITY = 12;
    private static int ADD_COLOURDICE_ACTIVITY = 21;
    private static int EDIT_COLOURDICE_ACTIVITY = 22;
    private static int ADD_TASK = 1;
    private static int EDIT_TASK = 2;

    private static int DICE_ROLLING_SOURCE = -2;
    private static int VIBRATE_DURATION = 200;

    //Context menu item IDs
    private final int EDIT_ID = 4;
    private final int DELETE_ID = 5;
    private final int REROLL_ID = 6;
    private final int CLONE_ID = 7;

    //New dice activity IDs
    private static String NEW_DICE_NAME = "NEW_DICE_NAME";
    private static String NEW_DICE_MULTI = "NEW_DICE_MULTI";
    private static String NEW_DICE_SIDES = "NEW_DICE_SIDES";
    private static String NEW_DICE_MOD = "NEW_DICE_MOD";
    private static String NEW_DICE_COLOURS = "NEW_DICE_COLOURS";
    private static String TASK = "TASK";
    private static String EDIT_POSITION = "EDIT_POSITION";

    //Dice save file name
    private static String FILENAME = "YANDRARRAY.dat";

    // DiceArray
    private ArrayList<SimpleDice> DiceList;

    // Display item variables
    private GridView DiceTable;
    // App variables
    private Context mContext;
    private DiceAdapter mDiceAdapter;
    private CheckBox mShakeLock;
    private boolean mRollLock;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    // Motion sensitivity variables
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;
    private final float NOISE = (float) 2.0; //consider as a setting
    private float mCummDelta = 0;
    public long mCummulativeTrigger = 20; //consider as a setting

    // Dice Arrays for Dice sides and text representations
    private String[] mDiceSides;
    private Integer[] mDiceSidesInt;

    // Sound variables
    private MediaPlayer mPlayer;

    // Options/Settings variables
    SharedPreferences sharedPref;
    private Boolean muteLock;
    private Boolean screenLock;
    private Boolean sumTotals;
    private Boolean canVibrate;
    private Boolean vibrate;
    private Boolean rotate;
    //add static variables to add options to the app

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        ReadSave();
        setContentView(R.layout.rtablemain);
        DiceTable = (GridView)findViewById(R.id.DiceView);
        attachAdapter();
        registerForContextMenu(DiceTable);
        //set default values on first load
        PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
        // Load preferences
        loadAndApplyPreferences();
        initAudio();
        mShakeLock = (CheckBox) findViewById(R.id.switch1);
        mShakeLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRollLock = isChecked;
            }
        });
        mShakeLock.setChecked(true);
        checkAndAttachSensors();
        DiceTable.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                diceViewDialog(position);
            }
        });
        mDiceSides = getResources().getStringArray(R.array.dice_sides_string_array);
        mDiceSidesInt = getIntegerArray(getResources().getIntArray(R.array.dice_sides_integer_array));

    }

    private Integer[] getIntegerArray(int[] baseArray) {
        Integer[] result = new Integer[baseArray.length];
        for (int i = 0; i< baseArray.length; i++) {
            result[i] = Integer.valueOf(baseArray[i]);
        }
        return result;
    }

    private void loadAndApplyPreferences() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
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
     * @param position The position in the DiceList to access the position of the view on the screen
     */
    public void diceViewDialog(int position) {
        SimpleDice dice = DiceList.get(position);
        Dialog dialog;
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.viewdicedialog);
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        TextView text1 = (TextView) dialog.findViewById(R.id.text1);
        TextView text2 = (TextView) dialog.findViewById(R.id.text2);
        TextView text3 = (TextView) dialog.findViewById(R.id.text3);
        if (dice instanceof D20Dice) {
            dialog.setTitle(dice.getRepresentativeTitle());
            image.setImageResource(mDiceAdapter.getDiceIconFromPosition(position));
            text1.setText(dice.getTitle());
            text2.setText(dice.getTitleAndResult());
            text3.setText(dice.getTitleAndTotal());
        } else if (dice instanceof ColourDice) {
            dialog.setTitle(dice.getName());
            if (dice.getResult() != null) {
                image.setImageDrawable(new ColorDrawable(Color.parseColor(dice.getResult())));
                text2.setText(dice.getTitleAndResult());
            } else image.setImageResource(R.drawable.dttr);
        }

        dialog.show();
    }

    public void cloneDice(int position){
        SimpleDice dice = DiceList.get(position);
        SimpleDice dice2 = dice.clone();
        DiceList.add(position+1, dice2);
        mDiceAdapter.notifyDataSetChanged();

    }
    public void generateRandom(int i) {
        //if i is < 0, do all dice rolls here, if = 0, do specified dice rolls at the dice class level
        // Use an i of -1 to generate all random values in one place from one rand seed
        if (!DiceList.isEmpty()) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (i==-1){
                Random rand = new Random();
                int randNumber;
                for (SimpleDice dice : DiceList) {
                    for (int j=0; j<dice.getMultiplier(); j++){
                        randNumber = rand.nextInt(dice.getSides())+1;
                        dice.setResult(j, randNumber);
                    }
                    dice.sortResults();
                }
            }
            // Use an i of =0 to delegate all randoming to each individual dice instance
            if (i==-2){
                for (SimpleDice dice : DiceList) {
                    dice.roll();
                }
            }
            if (i>=0){
                DiceList.get(i).roll();
            }
            mDiceAdapter.notifyDataSetChanged();
            playAudio();
            // Vibrate for VIBRATE_DURATION milliseconds
            if (vibrate && canVibrate) v.vibrate(VIBRATE_DURATION);
        }
    }
    /**
     * Add the menu options
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rollingtablemenulayout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_clear_screen:
                clearScreen();
                return true;
            case R.id.menu_add_dice:
                callComplexDiceAddScreen(ADD_D20DICE_ACTIVITY, 0);
                return true;
            case R.id.menu_add_ttr:
                callComplexDiceAddScreen(ADD_COLOURDICE_ACTIVITY, 0);
                return true;
            case R.id.menu_roll_dice:
                generateRandom(DICE_ROLLING_SOURCE);
                return true;
            case R.id.menu_activity_add_dice:
                callComplexDiceAddScreen(ADD_D20DICE_ACTIVITY, 0);
                return true;
            case R.id.about_id:
                callAboutScreen();
                return true;
            case R.id.preferences:
                callSettingsScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_D20DICE_ACTIVITY) {
                SimpleDice dice = new D20Dice(
                        data.getIntExtra(NEW_DICE_MULTI, 1),
                        data.getIntExtra(NEW_DICE_SIDES, 1),
                        data.getIntExtra(NEW_DICE_MOD, 1),
                        data.getStringExtra(NEW_DICE_NAME));
                addDice(dice);
            } else if (requestCode == EDIT_D20DICE_ACTIVITY) {
                SimpleDice dice = new D20Dice(
                        data.getIntExtra(NEW_DICE_MULTI, 1),
                        data.getIntExtra(NEW_DICE_SIDES, 1),
                        data.getIntExtra(NEW_DICE_MOD, 1),
                        data.getStringExtra(NEW_DICE_NAME));
                int position = data.getIntExtra(EDIT_POSITION, 0);
                DiceList.set(position, dice);
            } else if (requestCode == ADD_COLOURDICE_ACTIVITY) {
                String name = data.getStringExtra(NEW_DICE_NAME);
                String[] colours = data.getStringArrayExtra(NEW_DICE_COLOURS);
                SimpleDice dice = new ColourDice(name, colours);
                addDice(dice);
            } else if (requestCode == EDIT_COLOURDICE_ACTIVITY) {
                String name = data.getStringExtra(NEW_DICE_NAME);
                String[] colours = data.getStringArrayExtra(NEW_DICE_COLOURS);
                SimpleDice dice = new ColourDice(name, colours);
                int position = data.getIntExtra(EDIT_POSITION, 0);
                DiceList.set(position, dice);
            }
        }
        //we are here presumably because the result code was not OK
        //we don't care, since we don't NEED anything in that case.
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
                SimpleDice editDice = DiceList.get(position);
                if (editDice instanceof D20Dice) {
                    callComplexDiceAddScreen(EDIT_D20DICE_ACTIVITY, position);
                } else if (editDice instanceof ColourDice) {
                    callComplexDiceAddScreen(EDIT_COLOURDICE_ACTIVITY, position);
                }
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
    public void callAboutScreen() {
        Intent intent = new Intent(mContext, AboutActivity.class);
        startActivity(intent);
    }
    public void addDice(SimpleDice dice) {
        DiceList.add(dice);
        mDiceAdapter.notifyDataSetChanged();
    }
    public void callComplexDiceAddScreen(Integer func, int position) {
        Intent intent = null;
        if (func.equals(ADD_D20DICE_ACTIVITY)) {
            intent = new Intent(mContext, D20DiceAddActivity.class);
        }
        if (func.equals(EDIT_D20DICE_ACTIVITY)) {
            if (position >=0) {
                intent = new Intent(mContext, D20DiceAddActivity.class);
                intent.putExtra(TASK, EDIT_TASK);
                intent.putExtra(EDIT_POSITION, position);
                D20Dice d20Dice = (D20Dice)DiceList.get(position);
                intent.putExtra(NEW_DICE_NAME, d20Dice.getName());
                intent.putExtra(NEW_DICE_MULTI, d20Dice.getMultiplier());
                intent.putExtra(NEW_DICE_MOD, d20Dice.getModifier());
                intent.putExtra(NEW_DICE_SIDES, d20Dice.getSides());
            }
        }
        if (func.equals(ADD_COLOURDICE_ACTIVITY)) {
            intent = new Intent(mContext, ColourDiceActivity.class);
        }
        if (func.equals(EDIT_COLOURDICE_ACTIVITY)) {
            if (position >=0) {
                intent = new Intent(mContext, ColourDiceActivity.class);
                intent.putExtra(TASK, EDIT_TASK);
                intent.putExtra(EDIT_POSITION, position);
                ColourDice colourDice = (ColourDice)DiceList.get(position);
                intent.putExtra(NEW_DICE_NAME, colourDice.getName());
                intent.putExtra(NEW_DICE_COLOURS, colourDice.getColours());
            }
        }
        startActivityForResult(intent, func);
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
                    generateRandom(DICE_ROLLING_SOURCE);
                    mShakeLock.setChecked(true);
                }
                mCummDelta = 0;
            }
        }
    }

    /**
     * Attach / reset the adapter and attach the new one after a configuration change
     * such as a screen rotation.
     */
    private void attachAdapter() {
        mDiceAdapter = new DiceAdapter(this, DiceList);
        DiceTable.setAdapter(mDiceAdapter);
    }

    /**
     * Check for and attach if able any sensors needed by the activity
     * namely the accelerometer
     */
    private void checkAndAttachSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            canVibrate = true;
        }
        else {
            canVibrate = false;
        }

    }
    private void SaveDiceArray() {
        FileOutputStream fos;
        ObjectOutputStream out;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fos);
            out.writeObject(DiceList);
            out.close();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void ReadSave() {
        FileInputStream fis;
        ObjectInputStream in;
        try {
            fis = openFileInput(FILENAME);
            in = new ObjectInputStream(fis);
            DiceList = (ArrayList<SimpleDice>) in.readObject();
            in.close();
            fis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (DiceList == null || DiceList.isEmpty()) {
            DiceList = new ArrayList<SimpleDice>();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SaveDiceArray();
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ReadSave();
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
        attachAdapter();
        loadAndApplyPreferences();
        checkAndAttachSensors();

    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        //keep this empty
    }
}