/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;

import org.usfirst.frc.team467.robot.WheelPod;

/**
 *
 */
public class UltimateProportionalGainTuner extends BaseTuner implements Tuner {

	private ArrayList<Long> cycleDiff;
	private boolean goingUp;
	private long lastPeak;
	private long lastReading;
	private long cycleStartTime;
	private int numberCycles;
	private ArrayList<Long> cycleTimes;

	/**
	 * @param talon
	 * @param reverseDirection
	 */
	public UltimateProportionalGainTuner(WheelPod wheelPod) {
		super(wheelPod, true);
		cycleDiff = new ArrayList<Long>();
		cycleTimes = new ArrayList<Long>();
		System.out.println("Starting autotune stage for ultimate proportional gain.");
		clear();
		currentValue = 1;
		previousValue = currentValue;
		increaseFactor = 1;
		pid(currentValue, 0.0, 0.0);
		wheelPod.speedMode();
		cycleDiff.clear();
		cycleTimes.clear();
		count = 0;
		numberCycles = 0;
		lastReading = 0;
		lastPeak = 0;
		goingUp = true;
	}

	private int peakIncreaseCount() {
		int increaseCount = 0;
		long previousPeak = 0;
		for (int i = 0; i < cycleDiff.size(); i++) {
			long peak = cycleDiff.get(i);
			if (i != 0) {
				if ((peak - previousPeak) > 2) {
					increaseCount++;
				} else if ((peak - previousPeak) < -2) {
					increaseCount--;
				}
			}
			previousPeak = cycleDiff.get(i);
		}
		return increaseCount;
	}

	private long averageCycleDiff() {
		double sumPeaks = 0;
		for (long peak : cycleDiff) {
			sumPeaks += (double) peak;
		}
		if (cycleDiff.size() > 0) {
			return Math.round(sumPeaks / (double) cycleDiff.size());
		} else {
			return 0;
		}
	}

	private int cycleTimeIncreaseCount() {
		int increaseCount = 0;
		long previousCycleTime = 0;
		for (int i = 0; i < cycleTimes.size(); i++) {
			long cycleTime = cycleTimes.get(i);
			if (i != 0) {
				if ((cycleTime - previousCycleTime) > 2) {
					increaseCount++;
				} else if ((cycleTime - previousCycleTime) < -2) {
					increaseCount--;
				}
			}
			previousCycleTime = cycleTimes.get(i);
		}
		return increaseCount;
	}

	private long averageCycleTime() {
		numberCycles = 0;
		double sumCycleTimes = 0.0;
		for (Long time : cycleTimes) {
			sumCycleTimes += (double) time;
			numberCycles++;
		}
		if (cycleTimes.size() > 0) {
			return Math.round(sumCycleTimes / (double) cycleTimes.size());
		} else {
			return 0;
		}
	}

	@Override
	public boolean process() {
		long reading = Math.round(wheelPod.readSensor());
		long time = System.currentTimeMillis();
		if (count == 0) {
			cycleTimes.clear();
			cycleDiff.clear();
			p(currentValue);
			wheelPod.set(setpoint);
			count++;
		} else if (count >= HOLD_PERIOD) {
			count = 0;
			set(0);
			int peakIncreaseCount = peakIncreaseCount();
			int cycleTimeIncreaseCount = cycleTimeIncreaseCount();
			long averageCycleTime = averageCycleTime();
			System.out.println(wheelPod.name() + " P: " + currentValue + " Error: " + wheelPod.error() + " Reading: " + reading
					+ " Ave Diff: " + averageCycleDiff() + " Peaks increasing: " + peakIncreaseCount + " Ave Cycle Times: "
					+ averageCycleTime + " Times increasing: " + cycleTimeIncreaseCount + " Number of Cycles: " + numberCycles);
			if (peakIncreaseCount >= 0 && Math.abs(peakIncreaseCount) < DEFAULT_ALLOWABLE_ERROR && Math.abs(cycleTimeIncreaseCount) < DEFAULT_ALLOWABLE_ERROR
					&& numberCycles > 2 && reading > 0) {
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
			if (peakIncreaseCount > DEFAULT_ALLOWABLE_ERROR) {
				// Getting worse -- unstable
				decreaseValue();
			} else {
				increaseValue();
			}
		} else {
			set(setpoint);
			if (goingUp) {
				if (reading < lastReading) {
					// Found peek
					lastPeak = reading;
					cycleStartTime = System.currentTimeMillis();
					// System.out.println("Peak " + speed);
					goingUp = false;
				}
			} else { // Going down
				if (reading > lastReading) {
					// Found trough
					// System.out.println("Trough " + speed + " Diff: " +
					// (lastPeak - speed) + " Cycle Time: " + (time -
					// cycleStartTime));
					cycleDiff.add(lastPeak - reading);
					this.cycleTimes.add(time - cycleStartTime);
					goingUp = true;
				}
			}
			count++;
		}
		lastReading = reading;
		return false;
	}

}