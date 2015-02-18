package com.k7m.yandr;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

/**
 * @author Andy
 */
public class D20Dice implements Serializable, SimpleDice {
    private String mName = null;
    private String mTitle;
    private Integer mModifier;			//The numerical modifier to add to the total result
    private Integer mSides;				//The number of sides a dice/set of dice have
    private Integer mMultiplier;		//The number of dice in this instance
    private ArrayList<Integer> mResult;	//The vector holding the dice in this instance

    /**
     * Create a die/dice instance with <b>Multiplier</b> dice, each with <b>Sides</b> sides and adding a <b>Modifier</b> modifier.
     * @param Multiplier The number of dice to simulate
     * @param Sides The number of sides each dice will have
     * @param Modifier The positive number to add to the total
     */
    public D20Dice(int Multiplier, int Sides, int Modifier) {
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
     * @param newName The name of the dice.
     */
    public D20Dice(int Multiplier, int Sides, int Modifier, String newName) {
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
    public Integer getSides() {
        return mSides;
    }
    /**
     *
     * @return The modifier to the total dice sum
     */
    public Integer getModifier() {
        return mModifier;
    }
    /**
     *
     * @return The number of dice in this instance
     */
    public Integer getMultiplier() {
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
    public String getRepresentativeTitle() {
        if ((mName != null) && (mName.length() >= 1)) {
            return mName;
        } else return mTitle;
    }
    public String getTitleAndResult() {
        StringBuilder value = new StringBuilder();
        value.append(mTitle);
        value.append(" = ");
        for (int i = 0; i< mMultiplier; i++) {
            value.append("(");
            value.append(mResult.get(i));
            value.append(")");
        }
        if (mModifier > 0) {
            value.append("+");
            value.append(mModifier);
        }
        return value.toString();
    }
    public String getTitleAndTotal() {
        return mTitle + " = " + getTotal();
    }
    /**
     * @return The total of the raw rolls, summed
     */
    public String getResult() {
        return String.valueOf(total());
    }
    /**
     * @return The total of the rolls + the modifier
     */
    public String getTotal() {
        return String.valueOf(total() + mModifier);
    }
    /**
     * Run this to create our Result Vector, and fill it with 0 values to begin with.
     */
    private void resultArrayInit() {
        mResult = new ArrayList<Integer> ();
        for (int i = 0; i< mMultiplier; i++) {
            mResult.add(0);
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
        Calendar c = Calendar.getInstance();
        int mSecond = c.get(Calendar.MILLISECOND);
        byte[] ba = ByteBuffer.allocate(4).putInt(mSecond).array();
        Random rand = new SecureRandom(ba);
        int randNumber;
        for (int i = 0; i< mMultiplier; i++) {
            randNumber = rand.nextInt(mSides)+1;
            mResult.set(i,randNumber);
        }
        sortResults();
    }
    public void sortResults() {
        Collections.sort(mResult);
    }
    private Integer total() {
        Integer total = 0;
        for (int i = 0; i< mMultiplier; i++) {
            total = total + mResult.get(i);
        }
        return total;
    }
    /**
     * Returns the String representation of this object
     * in the format AdB+C=($value)($value)......
     */
    public String toString() {
        String value;
        if ((!mName.equals("")) && (mName != null)) {
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
}
