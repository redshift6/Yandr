package com.k7m.yandr;

import android.graphics.Color;

import java.io.Serializable;
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
    private ArrayList<Integer> mColors;	//The Arraylist holding the dice in this instance
    private Integer mResult;              //The color we rolled

    public ColourDice() {
        mColors.add(Color.BLACK);
        mColors.add(Color.RED);
        mColors.add(Color.BLUE);
        mColors.add(Color.GREEN);
        mColors.add(Color.YELLOW);
    }

    public ColourDice(int Multiplier, int Sides, int Modifier, String newName) {
        mModifier = Modifier;
        mSides = Sides;
        mMultiplier = 1;
        mName = newName;
    }
    @Override
    public int getSides() {
        return mSides;
    }

    @Override
    public int getModifier() {
        return mModifier;
    }

    @Override
    public int getMultiplier() {
        return mMultiplier;
    }

    @Override
    public String getResult() {
        return mColors.get(mResult).toString();
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
        Random rand = new Random(mSecond);
        int randNumber;
        for (int i = 0; i< mMultiplier; i++) {
            randNumber = rand.nextInt(mSides)+1;
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
        return null;
    }

    @Override
    public String getTitleAndResult() {
        return null;
    }

    @Override
    public String getTitleAndTotal() {
        return null;
    }
}
