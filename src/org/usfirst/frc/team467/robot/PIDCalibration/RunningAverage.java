/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

/**
 * @author Bryan Duerk
 *
 */
public class RunningAverage
{

    double sum;
    double absoluteSum;
    double[] inputs;
    int position;
    int maxCount;
    int count;

    /**
     *
     */
    public RunningAverage(int maxCount)
    {
        this.maxCount = maxCount;
        inputs = new double[maxCount];
        clear();
    }

    public void clear() {
        for (int i=0; i < inputs.length; i++) {
            inputs[i] = 0.0;
        }
        sum = 0.0;
        position = 0;
        count = 0;
    }

    public void input(double value) {
        sum += value - inputs[position];
        absoluteSum += (value * value) - (inputs[position] * inputs[position]);
        inputs[position] = value;
        position++;
        if (position >= maxCount) {
            position = 0;
        }
        count++;
    }

    public double absoluteAverage(double value) {
        input(value);
        return absoluteAverage();
    }

    public double absoluteAverage() {
        double average = 0.0;
        if (count < maxCount) {
            average =  Math.sqrt(absoluteSum) / count;
        } else {
            average =  Math.sqrt(absoluteSum) / maxCount;
        }
        return average;
    }

    public double average(double value) {
        input(value);
        return average();
    }

    public double average() {
        double average = 0.0;
//        System.out.println("SUM: " + sum + " COUNT: " + count);
        if (count < maxCount) {
            average =  sum / count;
        } else {
            average =  sum / maxCount;
        }
        return average;
    }

}
