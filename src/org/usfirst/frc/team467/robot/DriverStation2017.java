package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.ButtonPanel2017.Buttons;
import org.usfirst.frc.team467.robot.Climber.direction;

public class DriverStation2017 {
	private static DriverStation2017 instance = null;

	Joystick467 driverJoy = null;
	ButtonPanel2017 buttonPanel = null;

	// Mapping of functions to Joystick Buttons for normal operation
	private static int SLOW_BUTTON = Joystick467.TRIGGER;
	private static int TURN_BUTTON = 2;
	private static int TURBO_BUTTON = 7;
	private static int GYRO_RESET_BUTTON = 8;
	private static int UNWIND_BUTTON = 10;
	private static int FIELD_ALIGN_BUTTON = 5;
	private static int VECTOR_DRIVE_BUTTON = 6;
	private static int XB_SPLIT = 4;

	// Mapping of functions to Joystick Buttons for calibration mode
	private static int CALIBRATE_CONFIRM_BUTTON = Joystick467.TRIGGER;
	private static int CALIBRATE_SLOW_BUTTON = 4;
	
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
		driverJoy = new Joystick467(0);
	}
	

	/**
	 * Must be called prior to first button read.
	 */
	public void readInputs() {
		driverJoy.readInputs();
		buttonPanel.readInputs();
	}
	

	/**
	 * Gets joystick instance used by driver.
	 *
	 * @return
	 */
	public Joystick467 getDriveJoystick() {
		return driverJoy;
	}

	/**
	 * Get joystick instance used for calibration.
	 *
	 * @return
	 */
	public Joystick467 getCalibrationJoystick() {
		return driverJoy;
	}

	// All button mappings are accessed through the functions below

	/**
	 * returns the current drive mode. Modes lower in the function will override
	 * those higher up. only 1 mode can be active at any time
	 *
	 * @return currently active drive mode.
	 */
	public DriveMode getDriveMode() {

		DriveMode drivemode = DriveMode.CRAB; // default is regular crab drive
		if (getDriveJoystick().buttonDown(TURN_BUTTON)) {
			drivemode = DriveMode.TURN;
		}
		if (getDriveJoystick().buttonDown(UNWIND_BUTTON)) {
			drivemode = DriveMode.UNWIND;
		}
		if (getDriveJoystick().getPOV() != -1) {
			drivemode = DriveMode.STRAFE;
		}
		if (getDriveJoystick().buttonDown(FIELD_ALIGN_BUTTON)) {
			drivemode = DriveMode.FIELD_ALIGN;
			System.out.println("field align enabled");
		}
		if (getDriveJoystick().buttonDown(VECTOR_DRIVE_BUTTON)) {
			drivemode = DriveMode.VECTOR;
		}
		if (getDriveJoystick().buttonDown(XB_SPLIT)) {
			drivemode = DriveMode.XB_SPLIT;
		}
		return drivemode;
	}

	/**
	 *
	 * @return true if button required to enable slow driving mode are pressed
	 */
	public boolean getSlow() {
		return getDriveJoystick().buttonDown(SLOW_BUTTON);
	}

	/**
	 *
	 * @return true if button required to enable turbo driving mode are pressed
	 */
	public boolean getTurbo() {
		return getDriveJoystick().buttonDown(TURBO_BUTTON);
	}

	// Calibration functions. Calibration is a separate use mode - so the
	// buttons used
	// here can overlap with those used for the regular drive modes

	/**
	 *
	 * @return true if calibration mode selected
	 */
	public boolean getCalibrate() {
		return getDriveJoystick().getFlap();
	}

	public boolean getGyroReset() {
		return driverJoy.buttonDown(GYRO_RESET_BUTTON);
	}

	/**
	 *
	 * @return true if button to confirm calibration selection is pressed
	 */
	public boolean getCalibrateConfirmSelection() {
		return getCalibrationJoystick().buttonDown(CALIBRATE_CONFIRM_BUTTON);
	}

	/**
	 *
	 * @return true if button to enable calibration slow turn mode is pressed
	 */
	public boolean getCalibrateSlowTurn() {
		return getCalibrationJoystick().buttonDown(CALIBRATE_SLOW_BUTTON);
	}
	
	public direction getClimberDirection(){
		if (buttonPanel.buttonDown(Buttons.CLIMBER_UP)){
			return direction.UP;
		}
		else if (buttonPanel.buttonDown(Buttons.CLIMBER_DOWN)){
			return direction.DOWN;
		} else {
			return direction.STOP;
		}
	}
	
	public boolean climberUp(){
		if (getClimberDirection() == direction.UP){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean climberDown(){
		if(getClimberDirection() == direction.DOWN){
			return true;
		}else{
			return false;
		}
	}

}