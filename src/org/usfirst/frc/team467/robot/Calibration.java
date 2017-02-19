/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

/**
 * This class contains only static variables and functions, and simply acts as a container for all the calibration code.
 */
public class Calibration {
	// Creates objects

	private static Drive drive;
	private static DataStorage data;

	// Incremented angle used for calibrating wheels
	private static double calibrationAngle = 0.0;

	/**
	 * Initialize calibration code
	 */
	public static void init() {
		// makes the objects
		drive = Drive.getInstance();
		data = DataStorage.getInstance();
	}

	/**
	 * Updates steering calibration
	 *
	 * @param motorId
	 *            The id of the motor to calibrate
	 */
	public static void updateSteeringCalibrate(int motorId) {
		// not a valid wheel ID
		if (motorId < 0) {
			drive.stop();
			return;
		}

		calibrationAngle = getCalibrationAngle(calibrationAngle);

		if (calibrationAngle > Math.PI) {
			calibrationAngle -= 2.0 * Math.PI;
		}
		if (calibrationAngle < -Math.PI) {
			calibrationAngle += 2.0 * Math.PI;
		}

		// Drive specified steering motor with no speed to allow only steering
		drive.individualSteeringDrive(calibrationAngle, motorId);

		// Write and set new center if trigger is pressed
		if (DriverStation2017.getInstance().getCalibrateConfirmSelection()) {
			double currentAngle = drive.getSteeringAngle(motorId);

			// Write data to robot
			data.putDouble(RobotMap.STEERING_KEYS[motorId], currentAngle);

			// Set new steering center
			drive.setSteeringCenter(motorId, currentAngle);

			// Reset calibration angle
			calibrationAngle = 0.0;
		}
	}

	/**
	 * Gets the wheel selected by the stick.
	 *
	 * @param prevSelectedWheel
	 *            previously selected wheel
	 * @return int val of which wheel to select
	 */
	private static int getWheelStick(int prevSelectedWheel) {
		XBoxJoystick467 joystick = DriverStation2017.getInstance().getCalibrationJoystick();
		double stickAngle = joystick.getLeftStickAngle();

		// Select motor being calibrated
		if (joystick.getLeftStickDistance() > 0.5) {
			if (stickAngle < 0) {
				if (stickAngle < -Math.PI / 2) {
					return RobotMap.BACK_LEFT;
				} else {
					return RobotMap.FRONT_LEFT;
				}
			} else {
				if (stickAngle > 0) {
					if (stickAngle > Math.PI / 2) {
						return RobotMap.BACK_RIGHT;
					} else {
						return RobotMap.FRONT_RIGHT;
					}
				}
			}
		}
		// No new selected, return previous wheel selected
		return prevSelectedWheel;
	}

	/**
	 * Gets the angle to set the calibrating wheel.
	 *
	 * @param prevCalibrationAngle
	 *            previous angle to update
	 * @return angle for setting the angle
	 */
	private static double getCalibrationAngle(double prevCalibrationAngle) {

		// Drive motor based on turn angle
		prevCalibrationAngle += (DriverStation2017.getInstance().getCalibrationJoystick().getRightStickX() / 100.0);
		return prevCalibrationAngle;
	}

	// This is a static variable to define the wheel being calibrated.
	private static int calibrateWheelSelect = 0;

	/**
	 * Update steering calibration control
	 */
	public static void updateCalibrate() {
		calibrateWheelSelect = getWheelStick(calibrateWheelSelect);
		updateSteeringCalibrate(calibrateWheelSelect);
	}

}
