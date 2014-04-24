package com.k7m.yandr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author Andy
 * TODO: play with the mName and mTitle fields, decide how they work.
 */
public class Dice {
    private String mName = null;
    private String mTitle;
    private Integer mModifier;			//The numerical modifier to add to the total result
    private Integer mSides;				//The number of sides a dice/set of dice have
    private Integer mMultiplier;		//The number of dice in this instance
    private ArrayList<Integer> mResult;	//The vector holding the dice in this instance
    /**
     * Create a 1d1+0
     */
    public Dice() {
        mModifier = 0;
        mSides = 1;
        mMultiplier = 1;
        setTitle();
        resultArrayInit();
    }
    /**
     * Create a 1dX +0
     * @param Sides The number of sides this die has
     */
    public Dice(int Sides) {
        mModifier = 0;
        mSides = Sides;
        mMultiplier = 1;
        setTitle();
        resultArrayInit();
    }
    /**
     * Create a die/dice instance with <b>Multiplier</b> dice, each with <b>Sides</b> sides and adding a <b>Modifier</b> modifier.
     * @param Multiplier The number of dice to simulate
     * @param Sides The number of sides each dice will have
     * @param Modifier The positive number to add to the total
     */
    public Dice(int Multiplier, int Sides, int Modifier) {
        mModifier = Modifier;
        mSides = Sides;
        mMultiplier = Multiplier;
        setTitle();
        resultArrayInit();
    }
    /**
     * Create a die/dice instance with <b>Multiplier</b> dice, each with <b>Sides</b> sides and adding a <b>Modifier</b> modifier with <b>Name</b>
     * @param Multiplier The number of dice to simulate
     * @param Sides The number of sides each dice will have
     * @param Modifier The positive number to add to the total
     * @param newName
     */
    public Dice(int Multiplier, int Sides, int Modifier, String newName) {
        mModifier = Modifier;
        mSides = Sides;
        mMultiplier = Multiplier;
        mName = newName;
        setTitle();
        resultArrayInit();
    }
    /**
     *
     * @return The number of sides in this dice instance
     */
    public int getSides() {
        return mSides;
    }
    /**
     *
     * @return The modifier to the total dice sum
     */
    public int getModifier() {
        return mModifier;
    }
    /**
     *
     * @return The number of dice in this instance
     */
    public int getMultiplier() {
        return mMultiplier;
    }
    /**
     * Returns the String representation of the object's name
     */
    public String getName() {
        return (mName == null) ? "" : mName;
    }
    public String getTitle() {
        return mTitle;
    }
    /**
     * @param position Position of the value in the result vector we want the value of
     * @return the value @ position
     */
    public int getResultAtPosition(int position) {
        return mResult.get(position);
    }
    /**
     * @return The total of the raw rolls, summed
     */
    public int getResult() {
        Integer total = 0;
        for (int i = 0; i< mMultiplier; i++) {
            total = total + mResult.get(i);
        }
        return total;
    }
    /**
     * @return The total of the rolls + the modifier
     */
    public int getTotal() {
        return getResult() + mModifier;
    }
    /**
     *
     * @return An ArrayList containing the results of the dice
     */
    public ArrayList<Integer> getResultList() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < mMultiplier; i++) {
            list.add(getResultAtPosition(i));
        }
        return mResult;
    }
    /**
     *
     * @return A sorted ArrayList containing the results of the dice, from lowest to highest
     */
    public ArrayList<Integer> getSortedResultList() {
        ArrayList<Integer> sortedList = new ArrayList<Integer>();
        sortedList = getResultList();
        Collections.sort(getResultList());
        return sortedList;
    }
    public String getSave() {
        StringBuilder save = new StringBuilder();
        save.append(mMultiplier.toString() + "d" +mSides.toString() + "+" +mModifier.toString());
        for (int i = 0; i < mMultiplier; i++) {
            save.append(":"+mResult.get(i).toString());
        }
        return save.toString() +":";
    }
    /**
     * Run this to create our Result Vector, and fill it with 0 values to begin with.
     */
    private void resultArrayInit() {
        mResult = new ArrayList<Integer> ();
        for (int i = 0; i< mMultiplier; i++) {
            mResult.add(Integer.valueOf(0));
        }
    }
    public void setSides(int sides) {
        mSides = sides;
        resultArrayInit();
        setTitle();
    }
    public void setModifier(int modifier) {
        mModifier = modifier;
        setTitle();
    }
    public void setMultiplier(int multiplier) {
        mMultiplier = multiplier;
        resultArrayInit();
        setTitle();
    }

    public void setName(String newName) {
        mName = newName;
    }
    private void setTitle() {
        mTitle = mMultiplier + "d" + mSides;
        if (mModifier >0) {
            mTitle += "+" + mModifier;
        }
    }
    /**
     * Mutator for manual result manipulation, in case we want to use an outside function to randomise for us
     * @param position The position to set the value of
     * @param result The value to set at the position
     */
    public void setResult(int position, int result) {
        mResult.set(position, result);
    }
    /**
     * Roll the dice, and assign each die in the group a result.
     */
    public void roll() {
        Random rand = new Random();
        int randNumber;
        for (int i = 0; i< mMultiplier; i++) {
            randNumber = rand.nextInt(mSides)+1;
            mResult.set(i,randNumber);
        }
        sortResults();
    }
    public void sortResults() {
        ArrayList<Integer> sortedList = new ArrayList<Integer>();
        sortedList = getResultList();
        Collections.sort(getResultList());
        mResult = sortedList;
    }
    /**
     * Returns the String representation of this object
     * in the format AdB+C=($value)($value)......
     */
    public String toString() {
        String value;
        if ((mName != "") && (mName != null)) {
            value = getName() + " = ";
        } else value = getTitle() + " = ";
        for (Integer i : mResult) {
            value+= i + ",";
        }
        if (value.endsWith(",")) {
            value =value.substring(0,value.length()-1);
        }
        return value;
    }
    /**
     * Have the dice class draw the view depending on it's contents, settings, preferences etc..
     * The adapter would simply call the dice to get it's own preferable graphical representation
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        //Consider filling in if this becomes a thing.
        return convertView;
    }
}
