/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;

import org.usfirst.frc.team467.robot.PIDCalibration.WheelPod;

/**
 *
 */
public class UltimateProportionalGainTuner extends BaseTuner implements Tuner {

    private ArrayList<Double> cycleDiff;
    private boolean goingUp;
    private double lastPeak;
    private double lastSpeed;
    private long cycleStartTime;
    private int numberCycles;
    private ArrayList<Long> cycleTimes;
    private boolean positionIsSet;

	/**
	 * @param talon
	 * @param reverseDirection
	 */
	public UltimateProportionalGainTuner(WheelPod wheelPod, boolean findVelocityPID) {
		super(wheelPod, findVelocityPID);
    	cycleDiff = new ArrayList<Double>();
    	cycleTimes = new ArrayList<Long>();
    	System.out.println("Starting autotune stage for ultimate proportional gain.");
    	clear();
    	currentValue = 0.1;
    	previousValue = currentValue;
    	increaseFactor = 1;
    	p(currentValue);
    	if (findVelocityPID) {
        	wheelPod.speedMode();
    	} else {
    		wheelPod.positionMode();
    	}
    	cycleDiff.clear();
    	cycleTimes.clear();
    	count = 0;
    	numberCycles = 0;
    	lastSpeed = 0;
    	lastPeak = 0;
    	goingUp = true;
    	positionIsSet = false;
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

    private double averageCycleDiff() {
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
    	numberCycles = 0;
    	double sumCycleTimes = 0.0;
    	for (Long time : cycleTimes) {
    		sumCycleTimes += (double) time;
	    	numberCycles++;
    	}
    	if (cycleTimes.size() > 0) {
        	return (sumCycleTimes / (double) cycleTimes.size());
    	} else {
    		return 0.0;
    	}
    }

	@Override
	public boolean process() {
    	double speed = wheelPod.readSensor();
//    	System.out.println(speed + " - " + SETPOINT + " = " + wheelPod.error());
    	long time = System.currentTimeMillis();
    	if (count == 0) {
    		cycleTimes.clear();
    		cycleDiff.clear();
        	p(currentValue);
			if (this.findVelocityPID) {
	        	wheelPod.set(setpoint);
			} else {
				if (!positionIsSet) {
		        	set(setpoint);
		        	positionIsSet = true;
				}
			}
        	count++;
    	} else if (count >= HOLD_PERIOD ) {
    		count = 0;
    		if (findVelocityPID) {
    			set(0);
    		} else {
    			wheelPod.zeroPosition();
    			set(0);
        		positionIsSet = false;
    		}
    		int peakIncreaseCount = peakIncreaseCount();
    		int cycleTimeIncreaseCount = cycleTimeIncreaseCount();
    		double averageCycleTime = averageCycleTime();
    		System.out.println(wheelPod.name() + " P: " + currentValue + " Error: " + wheelPod.error() + " Speed: " + speed
    				+ " Ave Diff: " + averageCycleDiff() + " Peaks increasing: " + peakIncreaseCount
    				+ " Ave Cycle Times: " + averageCycleTime + " Times increasing: " + cycleTimeIncreaseCount);
    		if (Math.abs(peakIncreaseCount) < DEFAULT_ALLOWABLE_ERROR
    				&& Math.abs(cycleTimeIncreaseCount) < DEFAULT_ALLOWABLE_ERROR
    				&& numberCycles > 2 && speed > 0) {
    			System.out.println("Cycle times are stable");
    			velocityMaxStableProportionalTerm(currentValue);
    			velocityMaxStableCycleTime(averageCycleTime());
    			wheelPod.percentVoltageBusMode();
    			return true;
    		} else {
    			if (factorDecreaseCount > MAX_FACTOR_DECREASE_COUNT) {
        			System.out.println("Not stable, but enough movement");
        			velocityMaxStableProportionalTerm(currentValue);
        			velocityMaxStableCycleTime(averageCycleTime());
        			wheelPod.percentVoltageBusMode();
        			return true;
    			}
    		}
    		if (peakIncreaseCount > 0 ) {
    			// Getting worse -- unstable
    			decreaseValue();
    		} else {
    			increaseValue();
    		}
    	} else {
			if (this.findVelocityPID) {
	        	set(setpoint);
			} else {
				if (!positionIsSet) {
		        	set(setpoint);
		        	positionIsSet = true;
				}
			}
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
