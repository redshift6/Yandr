package com.k7m.yandr;

/**
 * Created by aso on 23-12-2014.
 */
public interface DiceTable {
    public void addDice(SimpleDice newDice);
    public void replaceDice(SimpleDice oldDice, SimpleDice newDice);
    public void roll();
}
