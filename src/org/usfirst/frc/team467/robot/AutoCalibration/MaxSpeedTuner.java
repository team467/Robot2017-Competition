/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.PIDCalibration.WheelPod;

/**
 *
 */
public class MaxSpeedTuner extends BaseTuner implements Tuner {

	private static final double MAX_TEST_SPEED = 600;

	private double maxForwardSpeed;
	private double maxBackwardSpeed;
	private double maxOverallSpeed;
	private boolean goingForward;

	/**
	 *
	 */
	public MaxSpeedTuner(WheelPod wheelPod) {
		super(wheelPod, true);
		System.out.println("Starting max speed stage.");
		clear();
		maxForwardSpeed = 0;
		maxBackwardSpeed = 0;
		maxOverallSpeed = 0;
		count = 0;
		goingForward = true;
		maxForwardSpeed = 0.0;
		maxBackwardSpeed = 0.0;
		maxOverallSpeed = 0.0;
		pid(2.16, 0.00864, 135.0, 3.0);
	}

	@Override
	public boolean process() {
		if (count < HOLD_PERIOD) {
			if (goingForward) {
				set(MAX_TEST_SPEED);
			} else {
				set(-1 * MAX_TEST_SPEED);
			}
			count++;
		} else {
			if (goingForward) {
				this.maxForwardSpeed = Math.abs(readSensor());
				goingForward = false;
			} else {
				this.maxBackwardSpeed = Math.abs(readSensor());
			}
			count = 0;
			set(0.0);
		}
		set(0.0);
		if (maxForwardSpeed < maxBackwardSpeed) {
			maxOverallSpeed = Math.abs(maxForwardSpeed);
		} else {
			maxOverallSpeed = Math.abs(maxBackwardSpeed);
		}
		System.out.println("Max Forward Speed: " + maxForwardSpeed);
		System.out.println("Max Backward Speed: " + maxBackwardSpeed);
		System.out.println("Max Overall Speed: " + maxOverallSpeed);
		return true;
	}
}
