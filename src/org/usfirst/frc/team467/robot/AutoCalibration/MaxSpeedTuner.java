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

	private boolean isComplete;
	private boolean goingForward;

	/**
	 *
	 */
	public MaxSpeedTuner(WheelPod wheelPod) {
		super(wheelPod, true);
		System.out.println("Starting max speed stage.");
		clear();
		count = 0;
		goingForward = true;
		isComplete = false;
		wheelPod.speedMode();
	}

	@Override
	public boolean process() {
		System.out.println(wheelPod.name() + " " + wheelPod.readSensor() + " " + wheelPod.error());
		if (count < HOLD_PERIOD) {
			if (goingForward) {
				set(MAX_TEST_SPEED);
			} else {
				set(-1 * MAX_TEST_SPEED);
			}
			count++;
		} else {
			if (goingForward) {
				velocityMaxForwardSpeed(Math.abs(readSensor()));
				goingForward = false;
			} else {
				velocityMaxBackwardSpeed(Math.abs(readSensor()));
				isComplete = true;
			}
			count = 0;
			set(0.0);
		}
		if (isComplete) {
			set(0.0);
			wheelPod.percentVoltageBusMode();
		}
		return isComplete;
	}
}
