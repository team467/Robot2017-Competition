package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.usfirst.frc.team467.robot.PIDCalibration.RunningAverage;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PIDTuningCycleCharacteristics
{
    private static int NO_TIME_YET = -1;
    private static double NO_ERROR_YET = Double.MAX_VALUE;
    private static int CYCLES_TO_AVERAGE = 8;


    // PID Output Characteristics
    public LinkedHashMap<Long, Double> history;
    public double sumOfErrors;
    public RunningAverage averageError;
    public RunningAverage shiftedAverageError;
    public double maxOvershoot;
    public ArrayList<Double> overshoots;
    public RunningAverage averageOvershoot;

    public long lastCycleTime;
    public ArrayList<Long> cycleTimes;
    public RunningAverage averageCycleTime;
    public long timeToSteadyState;

    // Internal meta-computation
    private boolean isFirstMove;
    private boolean isGoingUp;
    private long cycleTimeStart;
    private long previousTime;
    private double previousError;
    private double previousState;


    public PIDTuningCycleCharacteristics()
    {
        overshoots = new ArrayList<Double>();
        cycleTimes = new ArrayList<Long>();
        history = new LinkedHashMap<Long, Double>();
        averageError = new RunningAverage(CYCLES_TO_AVERAGE);
        shiftedAverageError = new RunningAverage(CYCLES_TO_AVERAGE);
        averageOvershoot = new RunningAverage(CYCLES_TO_AVERAGE);
        averageCycleTime = new RunningAverage(CYCLES_TO_AVERAGE);
    }

    public void clear() {
        maxOvershoot = 0.0;
        overshoots.clear();
        averageOvershoot.clear();
        sumOfErrors = 0.0;
        averageError.clear();
        shiftedAverageError.clear();
        lastCycleTime = 0;
        cycleTimes.clear();
        averageCycleTime.clear();
        timeToSteadyState = 0;
        history.clear();
        isFirstMove = true;
        isGoingUp = true;
        cycleTimeStart = 0;
        previousTime = NO_TIME_YET;
        previousError = NO_ERROR_YET;
        previousState = 0.0;
    }

    public void goingUp() {
        isGoingUp = true;
    }

    public void goingDown() {
        isGoingUp = false;
    }

    private void recordOvershoot(long time, double error) {
        double overshoot = Math.abs(error);
        overshoots.add(overshoot);
        long cycleTime = 0;
        if (!isFirstMove) {
            cycleTime = time-cycleTimeStart;
            SmartDashboard.putNumber("Cycle Time", cycleTime);
            cycleTimes.add(cycleTime);
            SmartDashboard.putNumber("Average Error", averageError.average(error));
            shiftedAverageError.average(error - averageError.average());
            SmartDashboard.putNumber("Average Overshoot", averageOvershoot.average(overshoot));
            SmartDashboard.putNumber("Average Cycle Time", averageCycleTime.average(cycleTime));
        }
        lastCycleTime = cycleTime;
        cycleTimeStart = previousTime;
        isFirstMove = false;
    }

    public void add(double state, double error) {
        long time = System.nanoTime();
        history.put(time, error);
        if (previousTime != NO_TIME_YET && previousError != NO_ERROR_YET) {
//            long timeDiff = (time - previousTime);
//            double errorDiff = (error - previousError);
            double stateDiff = (state - previousState);
            double absoluteError = Math.abs(error);
            if (absoluteError > maxOvershoot) {
                maxOvershoot = absoluteError;
            }
            if ((isGoingUp && stateDiff <= 0) || (!isGoingUp && stateDiff >= 0)) {
                    recordOvershoot(previousTime, previousError);
            }
        }
        previousTime = time;
        previousError = error;
        previousState = state;
    }

    public double cycleTimeDividedByAverageCycleTime() {
        if (cycleTimes.size() >= CYCLES_TO_AVERAGE) {
            return ((double) lastCycleTime / averageCycleTime.absoluteAverage());
        } else {
            return Double.MAX_VALUE;
        }

    }

    public double shiftedAverageError() {
        return shiftedAverageError.average();
    }

}
