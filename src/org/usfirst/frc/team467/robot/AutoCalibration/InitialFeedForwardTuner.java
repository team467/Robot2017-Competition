/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;

import org.usfirst.frc.team467.robot.WheelPod;

/**
 *
 */
public class InitialFeedForwardTuner extends BaseTuner implements Tuner {

	private ArrayList<Long> errors;
	private long lastAverageError;
	private long FEED_FORWARD_ALLOWABLE_ERROR = 5;

	/**
	 * @param talon
	 * @param reverseDirection
	 */
	public InitialFeedForwardTuner(WheelPod wheelPod, boolean findVelocityPID) {
		super(wheelPod, findVelocityPID);
		System.out.println("Initializing initial feed forward stage.");
		clear();
		currentValue = 0.1;
		increaseFactor = 1;
		lastAverageError = Long.MAX_VALUE;
		if (findVelocityPID) {
			wheelPod.speedMode();
		} else {
			wheelPod.positionMode();
		}
		errors = new ArrayList<Long>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.usfirst.frc.team467.robot.AutoCalibration.BaseTuner#process()
	 */
	@Override
	public boolean process() {
		wheelPod.readSensor(); // TODO: Remove when all works
		long error = Math.round(wheelPod.error());
		if (count == 0) {
			errors.clear();
			f(currentValue);
			set(setpoint);
			count++;
		} else if (count >= HOLD_PERIOD) {
			count = 0;
			set(0);
			long averageError = averageError();
			System.out.println(wheelPod.name() + " Feed Forward: " + currentValue + " Error: " + wheelPod.error()
					+ " Average Error: " + averageError + " Last Error: " + lastAverageError);
			if (Math.abs(averageError) > Math.abs(lastAverageError)) {
				// Getting worse -- unstable
				decreaseValue();
			} else {
				increaseValue();
			}
			lastAverageError = averageError;
			if ((Math.abs(averageError()) < FEED_FORWARD_ALLOWABLE_ERROR) || (factorDecreaseCount > MAX_FACTOR_DECREASE_COUNT)) {
				feedForward(currentValue);
				wheelPod.percentVoltageBusMode();
				return true;
			}
		} else {
			set(setpoint);
			errors.add(error);
			count++;
		}
		return false;
	}

	private long averageError() {
		double sumErrors = 0.0;
		for (long error : errors) {
			sumErrors += (double) error;
		}
		if (errors.size() > 0) {
			return Math.round((sumErrors / (double) errors.size()));
		} else {
			return 0;
		}
	}

}