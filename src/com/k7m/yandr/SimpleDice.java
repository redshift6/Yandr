package com.k7m.yandr;

/**
 * Created by Andy on 09-12-2014.
 */
public interface SimpleDice {

    /**
     * @return The number of sides in this dice instance
     */
    public Integer getSides();

    /**
     * @return The modifier to the total dice sum
     */
    public Integer getModifier();

    /**
     * @return The number of dice in this instance
     */
    public Integer getMultiplier();

    /**
     * @return The total of the raw rolls, summed
     */
    public String getResult();

    /**
     * Returns the final single total value of this dice instance
     * @return The total of the rolls + the modifier
     */
    public String getTotal();

    public void setSides(int sides);

    public void setModifier(int modifier);

    public void setMultiplier(int multiplier);

    /**
     * Mutator for manual result manipulation, in case we want to use an outside function to randomise for us
     * @param position The position to set the value of in this dice instance
     * @param result The value to set at the position in this dice instance
     */
    public void setResult(int position, int result);

    /**
     * Roll the dice, and assign each die in the group a result.
     * We don't care how the dice calculates new values, we just ask it to perform a 'roll' of all its representations.
     */
    public void roll();

    public void sortResults();

    /**
     * Returns the String representation of the object's name
     * @return The name of this dice instance
     */
    public String getName();

    public String getTitle();

    public String getRepresentativeTitle();

    public String getTitleAndResult();

    public String getTitleAndTotal();

    public SimpleDice clone();
}
