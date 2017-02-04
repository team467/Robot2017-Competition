/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import com.ctre.CANTalon;

/**
 * @author Bryan Duerk
 *
 */
public class ATune
{

    private CANTalon talon;
    private boolean isMax;
    private boolean isMin;
    private double setpoint;
    private double noiseBand;
    private int controlType;
    private boolean running;
    private long peak1;
    private long peak2;
    private long lastTime;
    private int sampleTime;
    private int lookBack;
    private int peakType;
    private double[] lastInputs;
    private double[] peaks;
    private int peakCount;
    private boolean justchanged;
    private boolean justevaled;
    private int initCount;
    private double absMax;
    private double absMin;
    private double step;
    private double outputStart;
    private double Ku;
    private double Pu;

    /**
     *
     */
    public ATune(CANTalon talon)
    {
        this.talon = talon;
        lastInputs = new double[100];
        peaks = new double[10];
        controlType = 0; // default to PI
        noiseBand = 0.5;
        running = false;
        step = 30;
        setLookbackSec(10);
        lastTime = System.currentTimeMillis();
    }

    public void cancel()
    {
        running = false;
    }

    public boolean runtime()
    {
        justevaled = false;
        if (peakCount > 9 && running)
        {
            running = false;
            finishUp();
            return true;
        }
        long now = System.currentTimeMillis();

        if ((now - lastTime) < (long) sampleTime)
            return false;
        lastTime = now;
        double refVal = talon.getSpeed();
        justevaled = true;
        if (!running)
        { // initialize working variables the first time around
            peakType = 0;
            peakCount = 0;
            justchanged = false;
            absMax = refVal;
            absMin = refVal;
            setpoint = refVal;
            running = true;
            initCount = 0;
            outputStart = talon.getSetpoint();
            talon.set(outputStart + step);
        }
        else
        {
            if (refVal > absMax)
                absMax = refVal;
            if (refVal < absMin)
                absMin = refVal;
        }

        // oscillate the output base on the input's relation to the setpoint

        if (refVal > setpoint + noiseBand)
            talon.set(outputStart - step);
        else if (refVal < setpoint - noiseBand)
            talon.set(outputStart + step);

        // boolean isMax=true, isMin=true;
        isMax = true;
        isMin = true;
        // id peaks
        for (int i = lookBack - 1; i >= 0; i--)
        {
            double val = lastInputs[i];
            if (isMax)
                isMax = refVal > val;
            if (isMin)
                isMin = refVal < val;
            lastInputs[i + 1] = lastInputs[i];
        }
        lastInputs[0] = refVal;
        if (lookBack < 9)
        {  // we don't want to trust the maxes or mins until the inputs array has been filled
            initCount++;
            return false;
        }

        if (isMax)
        {
            if (peakType == 0)
                peakType = 1;
            if (peakType == -1)
            {
                peakType = 1;
                justchanged = true;
                peak2 = peak1;
            }
            peak1 = now;
            peaks[peakCount] = refVal;

        }
        else if (isMin)
        {
            if (peakType == 0)
                peakType = -1;
            if (peakType == 1)
            {
                peakType = -1;
                peakCount++;
                justchanged = true;
            }

            if (peakCount < 10)
                peaks[peakCount] = refVal;
        }

        if (justchanged && peakCount > 2)
        { // we've transitioned. check if we can autotune based on the last peaks
            double avgSeparation = (Math.abs(peaks[peakCount - 1] - peaks[peakCount - 2])
                    + Math.abs(peaks[peakCount - 2] - peaks[peakCount - 3])) / 2;
            if (avgSeparation < 0.05 * (absMax - absMin))
            {
                finishUp();
                running = false;
                return true;

            }
        }
        justchanged = false;
        return false;
    }

    public void finishUp()
    {
        talon.set(outputStart);
        // we can generate tuning parameters!
        Ku = 4 * step / ((absMax - absMin) * 3.14159);
        Pu = (double) (peak1 - peak2) / 1000;
    }

    public double getKp()
    {
        return controlType == 1 ? 0.6 * Ku : 0.4 * Ku;
    }

    public double getKi()
    {
        return controlType == 1 ? 1.2 * Ku / Pu : 0.48 * Ku / Pu;  // Ki = Kc/Ti
    }

    public double getKd()
    {
        return controlType == 1 ? 0.075 * Ku * Pu : 0;  // Kd = Kc * Td
    }

    public void setOutputStep(double step)
    {
        this.step = step;
    }

    public double getOutputStep()
    {
        return step;
    }

    public void setControlType(int type) // 0=PI, 1=PID
    {
        controlType = type;
    }

    public int getControlType()
    {
        return controlType;
    }

    public void setNoiseBand(double band)
    {
        noiseBand = band;
    }

    public double getNoiseBand()
    {
        return noiseBand;
    }

    public void setLookbackSec(int value)
    {
        if (value < 1)
            value = 1;

        if (value < 25)
        {
            lookBack = value * 4;
            sampleTime = 250;
        }
        else
        {
            lookBack = 100;
            sampleTime = value * 10;
        }
    }

    public int getLookbackSec()
    {
        return lookBack * sampleTime / 1000;
    }

}
