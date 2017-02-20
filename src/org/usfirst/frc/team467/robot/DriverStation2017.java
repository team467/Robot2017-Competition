package org.usfirst.frc.team467.robot;

public class DriverStation2017 {
	private static DriverStation2017 instance = null;

	XBoxJoystick467 driverJoy = null;
	ButtonPanel2017 buttonPanel = null;

	// Mapping of functions to Joystick Buttons for normal operation

	private static int GYRO_RESET_BUTTON = XBoxJoystick467.BUTTON_Y;
	private static int UNWIND_BUTTON = XBoxJoystick467.BUTTON_B;
	private static int CALIBRATE_BUTTON = XBoxJoystick467.BUTTON_X;
	private static int CRAB_DRIVE = XBoxJoystick467.BUTTON_A;

	// Mapping of functions to Joystick Buttons for calibration mode
	private static int CALIBRATE_CONFIRM_BUTTON = XBoxJoystick467.BUMPER_RIGHT;

	enum Speed {
		SLOW, FAST
	}

	/**
	 * Singleton instance of the object.
	 *
	 * @return
	 */
	public static DriverStation2017 getInstance() {
		if (instance == null) {
			instance = new DriverStation2017();
		}
		return instance;
	}

	/**
	 * Private constructor
	 */
	private DriverStation2017() {
		driverJoy = new XBoxJoystick467(0);
		// buttonPanel = new ButtonPanel2017(1);
	}

	/**
	 * Must be called prior to first button read.
	 */
	public void readInputs() {
		driverJoy.readInputs();
	}

	/**
	 * Gets joystick instance used by driver.
	 *
	 * @return
	 */
	public XBoxJoystick467 getDriveJoystick() {
		return driverJoy;
	}

	public ButtonPanel2017 getButtonPanel() {
		return buttonPanel;
	}

	/**
	 * Get joystick instance used for calibration.
	 *
	 * @return
	 */
	public XBoxJoystick467 getCalibrationJoystick() {
		return driverJoy;
	}

	// All button mappings are accessed through the functions below

	/**
	 * returns the current drive mode. Modes lower in the function will override those higher up. only 1 mode can be active at any
	 * time
	 *
	 * @return currently active drive mode.
	 */
	public DriveMode getDriveMode() {
		DriveMode drivemode = DriveMode.VECTOR; // default drive mode for xbox

		// UNWIND takes greatest priority
		if (getDriveJoystick().buttonDown(UNWIND_BUTTON)) {
			drivemode = DriveMode.UNWIND;
		} else if (getDriveJoystick().buttonDown(CRAB_DRIVE)) {
			drivemode = DriveMode.CRAB;
		}
		return drivemode;
	}

	// return +1 for right, -1 for left
	public double getVectorTurnDirection() {

		double turnDirection = 0.0;

		if (driverJoy.getRightStickX() < 0) {
			turnDirection = -1.0;
		} else if (driverJoy.getRightStickX() > 0) {
			turnDirection = 1.0;
		}
		return turnDirection;
	}

	// Calibration functions. Calibration is a separate use mode - so the
	// buttons used
	// here can overlap with those used for the regular drive modes

	/**
	 *
	 * @return true if calibration mode selected
	 */
	public boolean getCalibrate() {
		return driverJoy.buttonDown(CALIBRATE_BUTTON);
	}

	public boolean getGyroReset() {
		return driverJoy.buttonDown(GYRO_RESET_BUTTON);
	}

	/**
	 * @return true if button to confirm calibration selection is pressed
	 */
	public boolean getCalibrateConfirmSelection() {
		return getCalibrationJoystick().buttonDown(CALIBRATE_CONFIRM_BUTTON);
	}
}