package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.ButtonPanel2017.Buttons;

import org.usfirst.frc.team467.robot.GamePieceDirection;

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

	/**
	 *
	 * @return true if button to enable calibration slow turn mode is pressed
	 */

	/*--GAME PIECE CODE--*/
	// Currently all code just has one for each action that needs to be held down
	// Can change based on preferences

	// Which direction should climber go?
	public GamePieceDirection getClimberDirection() {
		if (buttonPanel.buttonDown(Buttons.CLIMBER_UP)) {
			return GamePieceDirection.UP;
		} else if (buttonPanel.buttonDown(Buttons.CLIMBER_DOWN)) {
			return GamePieceDirection.DOWN;
		} else {
			return GamePieceDirection.STOP;
		}
	}

	// is climber climbing?
	public boolean climberUp() {
		if (getClimberDirection() == GamePieceDirection.UP) {
			return true;
		} else {
			return false;
		}
	}

	// is climber descending?
	public boolean climberDown() {
		if (getClimberDirection() == GamePieceDirection.DOWN) {
			return true;
		} else {
			return false;
		}
	}

	// get the direction the shooter will spin in
	public GamePieceDirection getShooterDirection() {
		if (buttonPanel.buttonDown(Buttons.SHOOTER_SPIN)) {
			return GamePieceDirection.SPIN;
		}
		if (buttonPanel.buttonDown(Buttons.SHOOTER_FAILSAFE)) {
			return GamePieceDirection.FAILSAFE;
		} else {
			return GamePieceDirection.STOP;
		}
	}

	// is the shooter spinning to shoot balls?
	public boolean shooterSpinning() {
		if (getShooterDirection() == GamePieceDirection.SPIN) {
			return true;
		} else {
			return false;
		}
	}

	// is the shooter spinning backwards?
	public boolean shooterFailSafe() {
		if (getShooterDirection() == GamePieceDirection.FAILSAFE) {
			return true;
		} else {
			return false;
		}
	}

	// Direction of intake
	public GamePieceDirection intakeDirection() {
		if (buttonPanel.buttonDown(Buttons.INTAKE_IN)) {
			return GamePieceDirection.IN;
		}
		if (buttonPanel.buttonDown(Buttons.INTAKE_OUT)) {
			return GamePieceDirection.OUT;
		} else {
			return GamePieceDirection.STOP;
		}
	}

	// is intake intaking?
	public boolean intakeIn() {
		if (intakeDirection() == GamePieceDirection.IN) {
			return true;
		} else {
			return false;
		}
	}

	// is intake outputing?
	public boolean intakeOut() {
		if (intakeDirection() == GamePieceDirection.OUT) {
			return true;
		} else {
			return false;
		}
	}

	// basic agitator code
	public GamePieceDirection agitatorDirection() {
		if (buttonPanel.buttonDown(Buttons.AGITATOR_SHOOT)) {
			return GamePieceDirection.SHOOT;
		}
		if (buttonPanel.buttonDown(Buttons.AGITATOR_REVERSE)) {
			return GamePieceDirection.REVERSE;
		} else {
			return GamePieceDirection.STOP;
		}
	}

	// is agitator pushing balls?
	public boolean agitatorShoot() {
		if (agitatorDirection() == GamePieceDirection.SHOOT) {
			return true;
		} else {
			return false;
		}
	}

	// is agitator running backwards?
	public boolean agitatorReverse() {
		if (agitatorDirection() == GamePieceDirection.REVERSE) {
			return true;
		} else {
			return false;
		}
	}
}