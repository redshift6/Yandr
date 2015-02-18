package com.k7m.yandr;

import android.graphics.Color;

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
    private ArrayList<String> mColors;	//The Arraylist holding the dice in this instance
    private Integer mResult;              //The color we rolled

    public ColourDice() {
        mMultiplier = 1;
        mSides = 5;
        mResult = null;
        mColors = new ArrayList<String>();
        mColors.add("BLACK");
        mColors.add("RED");
        mColors.add("BLUE");
        mColors.add("GREEN");
        mColors.add("YELLOW");
    }

    public ColourDice(int Multiplier, int Sides, int Modifier, String newName) {
        mModifier = Modifier;
        mSides = Sides;
        mMultiplier = Multiplier;
        mName = newName;
    }
    @Override
    public Integer getSides() {
        return mSides;
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
            return mColors.get(mResult).toString();
        }
        return null;
    }

    @Override
    public String getTotal() {
        return mColors.get(mResult).toString();
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
        for (int i = 0; i< mMultiplier; i++) {
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
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getRepresentativeTitle() {
        if (mResult != null) {
            return mColors.get(mResult);
        }
        return "";

    }

    @Override
    public String getTitleAndResult() {
        if (mResult != null) {
            return "    " + mColors.get(mResult) + " should start.";
        }
        return "";
    }

    @Override
    public String getTitleAndTotal() {
        return null;
    }
}
