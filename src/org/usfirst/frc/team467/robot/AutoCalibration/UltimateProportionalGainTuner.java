/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;

import com.ctre.CANTalon;

/**
 *
 */
public class UltimateProportionalGainTuner extends BaseTuner implements Tuner {


    private double Ku;
    private double Tu;
    private boolean isFindingVelocityPID;
    private ArrayList<Double> cycleDiff;
    private boolean goingUp;
    private double lastPeak;
    private double lastSpeed;
    private long cycleStartTime;
    private ArrayList<Long> cycleTimes;

	/**
	 * @param talon
	 * @param reverseDirection
	 */
	public UltimateProportionalGainTuner(CANTalon talon, boolean reverseDirection, boolean isFindingVelocityPID) {
		super(talon, reverseDirection);
        if (isFindingVelocityPID) {
            talon.setProfile(VELOCITY_PID_PROFILE);
        } else {
            talon.setProfile(POSITION_PID_PROFILE);
        }
        this.isFindingVelocityPID = isFindingVelocityPID;
    	cycleDiff = new ArrayList<Double>();
    	cycleTimes = new ArrayList<Long>();
    	System.out.println("Starting autotune stage for ultimate proportional gain.");
    	clear();
        Ku = 0.0;
        Tu = 0.0;
    	currentValue = 0.1;
    	previousValue = 0.1;
    	increaseFactor = 1;
    	talon.setPID(0.0, 0.0, 0.0);
    	talon.setF(4);
    	Ku = 0;
    	Tu = 0;
    	cycleDiff.clear();
    	cycleTimes.clear();
    	count = 0;
    	lastSpeed = 0;
    	lastPeak = 0;
    	goingUp = true;
	}

    private int peakIncreaseCount() {
    	int increaseCount = 0;
    	double previousPeak = 0;
    	for (int i = 0; i < cycleDiff.size(); i++) {
    		double peak = cycleDiff.get(i);
    		if (i != 0) {
    			if (peak > previousPeak) {
    				increaseCount++;
    			} else if (peak < previousPeak) {
    				increaseCount--;
    			}
    		}
    		previousPeak = cycleDiff.get(i);
    	}
    	return increaseCount;
    }

    private double averaageCycleDiff() {
    	double sumPeaks = 0.0;
    	for (double peak : cycleDiff) {
    		sumPeaks += peak;
    	}
    	if (cycleDiff.size() > 0) {
        	return (sumPeaks / (double) cycleDiff.size());
    	} else {
    		return 0.0;
    	}
    }

    private int cycleTimeIncreaseCount() {
    	int increaseCount = 0;
    	double previousCycleTime = 0;
    	for (int i = 0; i < cycleTimes.size(); i++) {
    		double cycleTime = cycleTimes.get(i);
    		if (i != 0) {
    			if (cycleTime > previousCycleTime) {
    				increaseCount++;
    			} else if (cycleTime < previousCycleTime) {
    				increaseCount--;
    			}
    		}
    		previousCycleTime = cycleTimes.get(i);
    	}
    	return increaseCount;
    }

    private double averageCycleTime() {
    	double sumCycleTimes = 0.0;
    	for (Long time : cycleTimes) {
    		sumCycleTimes += (double) time;
    	}
    	if (cycleTimes.size() > 0) {
        	return (sumCycleTimes / (double) cycleTimes.size());
    	} else {
    		return 0.0;
    	}
    }

	@Override
	public boolean process() {
    	double speed = talon.getSpeed();
    	long time = System.currentTimeMillis();
    	if (count == 0) {
    		cycleTimes.clear();
    		cycleDiff.clear();
        	talon.setP(currentValue);
        	talon.set(SETPOINT);
        	count++;
    	} else if (count >= HOLD_PERIOD ) {
    		count = 0;
    		talon.set(0);
    		int peakIncreaseCount = peakIncreaseCount();
    		int cycleTimeIncreaseCount = cycleTimeIncreaseCount();
    		double averageCycleTime = averageCycleTime();
    		System.out.println("P: " + currentValue + " Error: " + talon.getError()
    				+ " Ave Peaks: " + averaageCycleDiff() + " Peaks increasing: " + peakIncreaseCount
    				+ " Ave Cycle Times: " + averageCycleTime + " Times increasing: " + cycleTimeIncreaseCount);
    		if (Math.abs(cycleTimeIncreaseCount) <= DEFAULT_ALLOWABLE_ERROR && averageCycleTime > 0.0) {
    			System.out.println("Cycle times are stable");
//    			Ku = currentValue;
//    			Tu = averageCycleTime();
//    			return true;
    		}
    		if (peakIncreaseCount > DEFAULT_ALLOWABLE_ERROR) {
    			// Getting worse -- unstable
    			decreaseValue();
    		} else {
    			increaseValue();
    		}
    	} else {
        	talon.set(SETPOINT);
    		if (goingUp) {
    			if (speed  < lastSpeed) {
    				// Found peek
    				lastPeak = speed;
    				cycleStartTime = System.currentTimeMillis();
//    	        	System.out.println("Peak " + speed);
    				goingUp = false;
    			}
    		} else { // Going down
       			if (speed > lastSpeed) {
    				// Found trough
//       	        	System.out.println("Trough " + speed + " Diff: " + (lastPeak - speed) + " Cycle Time: " + (time - cycleStartTime));
       				cycleDiff.add(lastPeak - speed);
       				this.cycleTimes.add(time - cycleStartTime);
    				goingUp = true;
    			}
    		}
    		count++;
    	}
		lastSpeed = speed;
    	return false;
    }

}
