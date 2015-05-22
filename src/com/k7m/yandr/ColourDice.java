package com.k7m.yandr;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by aso on 09-12-2014.
 */
public class ColourDice implements SimpleDice, Serializable {

    private String mName = null;
    private String mTitle;
    private Integer mModifier;			//The numerical modifier to add to the total result
    private Integer mSides;				//The number of sides a dice/set of dice have
    private Integer mMultiplier;		//The number of dice in this instance
    private ArrayList<String> mColours;	//The Arraylist holding the dice in this instance
    private Integer mResult;              //The color we rolled

    public ColourDice() {
        mMultiplier = 1;
        mSides = 5;
        mResult = null;
        mColours = new ArrayList<String>();
        mColours.add("BLACK");
        mColours.add("RED");
        mColours.add("BLUE");
        mColours.add("GREEN");
        mColours.add("YELLOW");
    }

    public ColourDice(int Multiplier, int Sides, int Modifier, String newName) {
        mModifier = Modifier;
        mSides = Sides;
        mMultiplier = Multiplier;
        mName = newName;
    }
    public ColourDice(String name, String[] colours) {
        mName = name;
        mColours = new ArrayList<String>();
        mSides = colours.length;
        for (int i = 0; i< colours.length; i++) {
            mColours.add(colours[i]);
        }
    }
    public ColourDice(String name, ArrayList<String> colours) {
        mName = name;
        mColours = colours;
        mSides = mColours.size();
    }
    @Override
    public Integer getSides() {
        return mSides;
    }

    public String[] getColours() {
        String[] colours = new String[mSides];
        colours = mColours.toArray(colours);
        return colours;
    }
    @Override
    public Integer getModifier() {
        return mModifier;
    }

    @Override
    public Integer getMultiplier() {
        return mMultiplier;
    }

    @Override
    public String getResult() {
        if (mResult != null) {
            return mColours.get(mResult).toString();
        }
        return null;
    }

    @Override
    public String getTotal() {
        return mColours.get(mResult).toString();
    }

    @Override
    public void setSides(int sides) {

    }

    @Override
    public void setModifier(int modifier) {

    }

    @Override
    public void setMultiplier(int multiplier) {

    }

    @Override
    public void setResult(int position, int result) {

    }

    @Override
    public void roll() {
        Calendar c = Calendar.getInstance();
        int mSecond = c.get(Calendar.MILLISECOND);
        byte[] ba = ByteBuffer.allocate(4).putInt(mSecond).array();
        Random rand = new SecureRandom(ba);
        int randNumber;
        for (int i = 0; i< mSides; i++) {
            randNumber = rand.nextInt(mSides);
            mResult = randNumber;
        }
        sortResults();
    }

    @Override
    public void sortResults() {

    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getRepresentativeTitle() {
        if (mResult != null) {
            return mColours.get(mResult);
        }
        return "";

    }

    @Override
    public String getTitleAndResult() {
        if (mResult != null) {
            return "    " + mColours.get(mResult) + " should start.";
        }
        return "";
    }

    @Override
    public String getTitleAndTotal() {
        return null;
    }

    @Override
    public SimpleDice clone() {
        return new ColourDice(this.getName(), this.getColours());
    }
}
