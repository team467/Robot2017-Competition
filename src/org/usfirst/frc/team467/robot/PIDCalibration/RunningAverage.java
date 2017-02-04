/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

/**
 * Utility class for specifying a running average to reduce noise when examining
 * errors or other values.
 */
public class RunningAverage
{

    double sum;
    double absoluteSum;
    double[] terms;
    int positionOfOldestTerm;
    int windowSize;
    int count;

    /**
     * Creates a running average with the specified window size.
     *
     * @param the
     *            window to average over
     */
    public RunningAverage(int windowSize)
    {
        this.windowSize = windowSize;
        terms = new double[windowSize];
        clear();
    }

    /**
     * Clears the average terms
     */
    public void clear()
    {
        for (int i = 0; i < terms.length; i++)
        {
            terms[i] = 0.0;
        }
        sum = 0.0;
        positionOfOldestTerm = 0;
        count = 0;
    }

    /**
     * Add a term to the running average. It will remove the oldest term from the average
     * at the same time.
     *
     * @param term
     *            the new term for the average
     */
    public void input(double term)
    {
        sum += term - terms[positionOfOldestTerm];
        absoluteSum += (term * term) - (terms[positionOfOldestTerm] * terms[positionOfOldestTerm]);
        terms[positionOfOldestTerm] = term;
        positionOfOldestTerm++;
        if (positionOfOldestTerm >= windowSize)
        {
            positionOfOldestTerm = 0;
        }
        count++;
    }

    /**
     * The absolute average takes the absolute value of terms before adding it to
     * the running average.
     *
     * @param term
     *            the new term for the absolute average
     * @return the current absolute average
     */
    public double absoluteAverage(double term)
    {
        input(term);
        return absoluteAverage();
    }

    /**
     * The absolute average takes the absolute value of terms before adding it to
     * the running average.
     *
     * @return the current absolute average
     */
    public double absoluteAverage()
    {
        double average = 0.0;
        if (count < windowSize)
        {
            average = Math.sqrt(absoluteSum) / count;
        }
        else
        {
            average = Math.sqrt(absoluteSum) / windowSize;
        }
        return average;
    }

    /**
     * Calculates an average over a running window of time.
     *
     * @param term
     *            a term to add to the running average
     * @return the current running average
     */
    public double average(double term)
    {
        input(term);
        return average();
    }

    /**
     * Calculates an average over a running window of time.
     *
     * @return the current running average
     */
    public double average()
    {
        double average = 0.0;
        if (count < windowSize)
        {
            average = sum / count;
        }
        else
        {
            average = sum / windowSize;
        }
        return average;
    }

}
