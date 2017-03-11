/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.usfirst.frc.team467.robot.WheelPod;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 */
public class PositionTuner extends BaseTuner implements Tuner {

	private static final int NUMBER_CYCLES = 6;

	private Queue<Double> cycleTimes;
	private double lastCycleTime;
	private double sumOfCycleTimes;

	private Queue<Double> peaks;
	private double lastPeak;
	private double sumOfPeaks;
	private double lastAverageOfPeaks;

	private Timer timer;
	private boolean movingToPosition;
	private double lastPosition;

	/**
	 * @param talon
	 * @param reverseDirection
	 */
	public PositionTuner(WheelPod wheelPod) {
		super(wheelPod, false);
		System.out.println("Starting autotune stage for position.");
		clear();
		currentValue = 0.01;
		previousValue = currentValue;
		increaseFactor = 1;
		pid(currentValue, 0.0, 0.0);
		wheelPod.positionMode();
		movingToPosition = false;
		timer = new Timer();
		timer.reset();
		sumOfPeaks = 0;
		lastAverageOfPeaks = 0;
	}

	@Override
	public boolean process() {
		boolean isComplete = false;

		if (!movingToPosition) {

			double averageOfPeaks = sumOfPeaks / NUMBER_CYCLES;
			double differenceInPeaks = 0;
			for (double peak : (Double[]) peaks.toArray()) {
				differenceInPeaks += Math.abs(peak - averageOfPeaks);
			}
			System.out.println("Average Overshoot:" + averageOfPeaks);

			if (differenceInPeaks < DEFAULT_ALLOWABLE_ERROR) {
				System.out.println("Done");
				double ultimateCycleTime = sumOfCycleTimes / NUMBER_CYCLES;
				System.out.println("Pu: " + currentValue + " Tu: " + ultimateCycleTime);
				double p = currentValue * 0.2;
				double i = ultimateCycleTime / 20 / 2;
				double d = ultimateCycleTime / 20 / 3;
				wheelPod.motor().setPID(p, i, d);
				System.out.println("P: " + p +  " I: " + i + " D: " + d);
				isComplete = true;
				return isComplete;
			}


			if (averageOfPeaks < lastAverageOfPeaks) {
				increaseValue();
			} else {
				decreaseValue();
			}
			lastAverageOfPeaks = averageOfPeaks;

			movingToPosition = true;
			timer.start();
			wheelPod.positionMode();
			count = 0;
			lastPosition = 0;
			lastCycleTime = 0;
			lastPeak = 0;

			cycleTimes = new ConcurrentLinkedQueue<Double>();
			peaks = new ConcurrentLinkedQueue<Double>();
			for (int i = 0; i < NUMBER_CYCLES; i++) {
				peaks.add(0.0);
				cycleTimes.add(0.0);
			}
			sumOfPeaks = 0;
			sumOfCycleTimes = 0;

			p(currentValue);
			set(POSITION_SETPOINT);
		}

		double position = wheelPod.motor().getPosition();
		double time = Timer.getFPGATimestamp();
		double cycleTime = time - lastCycleTime;
		double distanceFromLastPeak = Math.abs(lastPeak - position);
		if (timer.hasPeriodPassed(HOLD_PERIOD / 100)) {
			movingToPosition = false;
			timer.stop();
			timer.reset();
			set(0);
		}

		if ((lastPosition > 0 && position < 0) || (lastPosition < 0 && position > 0)) {
			count++;
			if (count > 2) {
				cycleTimes.add(cycleTime);
				sumOfCycleTimes += cycleTime - cycleTimes.remove();
				peaks.add(distanceFromLastPeak);
				sumOfPeaks += distanceFromLastPeak - peaks.remove();
			}
			lastCycleTime = time;
			lastPeak = position;
		}

		lastPosition = position;
		return isComplete;
	}
}