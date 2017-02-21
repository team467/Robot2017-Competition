/**
 *
 */
package org.usfirst.frc.team467.robot;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Utility class for checking current limits over a number of periodic cycles.
 * This is required to avoid stopping a motor based on the initial current spike at motor start.
 */
public class CheckCurrentLimit {

	private Queue<Double> currentReadings;
	private int numberOfReadings;
	private double sumOfCurrents;
	private double limit;

	/**
	 * Sets up a running average for the specified number of readings.
	 *
	 * @param numberOfReadings  the number of readings for the running average
	 * @param limit  the max allowable current
	 */
	public CheckCurrentLimit(int numberOfReadings, double limit) {
		this.numberOfReadings = numberOfReadings;
		this.limit = limit;
		currentReadings = new ConcurrentLinkedQueue<Double>();
		for (int i = 0; i < numberOfReadings; i++) {
			currentReadings.add(0.0);
		}
		sumOfCurrents = 0.0;

	}

	/**
	 * Averages the current readings over the specified period and checks to see if it is over the limit.
	 * This class should be called every periodic cycle where a current limited motor is used.
	 *
	 * @param current  the latest current sensor reading
	 * @return true if over the current limit
	 */
	public boolean isOverLimit(double current) {
		boolean isOverLimit = false;
		currentReadings.add(current);
		sumOfCurrents += current - currentReadings.remove();
		if ((sumOfCurrents / (double) numberOfReadings) > limit) {
			isOverLimit = true;
		}
		return isOverLimit;
	}

}
