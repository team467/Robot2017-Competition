package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Joystick;

public class XBJoystick {
	private Joystick joystick;
	private boolean[] buttons = new boolean[12]; // array of current button
													// states
	private boolean[] prevButtons = new boolean[12]; // array of previous button
														// states, involved in
														// edge detection.
	private double stickLX = 0.0;
	private double stickLY = 0.0;
	private double stickRX = 0.0;
	private double stickRY = 0.0;
	private double triggerL = 0.0;
	private double triggerR = 0.0;
	private int pov = 0;

	public static final int TRIGGER = 1;
	private static final double DEADZONE = 0.1;

	private static final int AXIS_LX = 0;
	private static final int AXIS_LY = 1;
	private static final int AXIS_RX = 4;
	private static final int AXIS_RY = 5;
	private static final int TRIGGER_LEFT = 2;
	private static final int TRIGGER_RIGHT = 3;
	private static final int POV_INDEX = 0;

	/**
	 * Create a new joystick on a given channel
	 *
	 * @param stick
	 */
	public XBJoystick(int stick) {
		joystick = new Joystick(stick);
	}

	/**
	 * Returns the raw joystick object inside Joystick467
	 * 
	 * @return
	 */
	public Joystick getJoystick() {
		return joystick;
	}

	/**
	 * Read all inputs from the underlying joystick object.
	 */
	public void readInputs() {
		// read all buttons
		for (int i = 0; i < 12; i++) {
			prevButtons[i] = buttons[i];
			buttons[i] = joystick.getRawButton(i + 1);
		}

		// Read Joystick Axes
		stickLY = accelerateJoystickInput(joystick.getRawAxis(AXIS_LY));
		stickLX = accelerateJoystickInput(joystick.getRawAxis(AXIS_LX));
		stickRY = accelerateJoystickInput(joystick.getRawAxis(AXIS_RY));
		stickRX = accelerateJoystickInput(joystick.getRawAxis(AXIS_RX));
		triggerL = accelerateJoystickInput(joystick.getRawAxis(TRIGGER_LEFT));
		triggerR = accelerateJoystickInput(joystick.getRawAxis(TRIGGER_RIGHT));
		pov = joystick.getPOV(POV_INDEX);

	}

	/**
	 * Check if a specific button is being held down. Ignores first button
	 * press, but the robot loops too quickly for this to matter.
	 *
	 * @param button
	 * @return
	 */
	public boolean buttonDown(int button) {
		return buttons[(button) - 1];
	}

	/**
	 * Check if a specific button has just been pressed. (Ignores holding.)
	 *
	 * @param button
	 * @return
	 */
	public boolean buttonPressed(int button) {
		return buttons[button - 1] && !prevButtons[button - 1];
	}

	/**
	 * Check if a specific button has just been released.
	 *
	 * @param button
	 * @return
	 */
	public boolean buttonReleased(int button) {
		return !buttons[button - 1] && prevButtons[button - 1];
	}

	/**
	 * Gets the X position of the stick. Left to right ranges from -1.0 to 1.0,
	 * with 0.0 in the middle. This value is accelerated.
	 * 
	 * @return
	 */
	public double getStickLX() {
		return stickLX;
	}

	/**
	 * Gets the Y position of the stick. Up to down ranges from -1.0 to 1.0,
	 * with 0.0 in the middle. This value is accelerated.
	 * 
	 * @return
	 */
	public double getStickLY() {
		return stickLY;
	}

	public double getStickRX() {
		return stickRX;
	}

	public double getStickRY() {
		return stickRY;
	}

	public double getTriggerR() {
		return triggerR;
	}

	public double getTriggerL() {
		return triggerL;
	}

	/**
	 * 
	 * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
	 */
	public int getPOV() {
		return pov;
	}

	/**
	 * Calculate the distance of this stick from the center position.
	 *
	 * @return
	 */
	public double getLeftStickDistance() {
		return Math.sqrt((stickLX * stickLX) + (stickLY * stickLY));
	}

	public double getRightStickDistance() {
		return Math.sqrt((stickRX * stickRX) + (stickRY * stickRY));
	}

	public boolean isLeftInDeadzone() {
		return (Math.abs(stickLX) < DEADZONE) && (Math.abs(stickLY) < DEADZONE);
	}

	public boolean isRightInDeadzone() {
		return (Math.abs(stickRX) < DEADZONE) && (Math.abs(stickRY) < DEADZONE);
	}

	/**
	 * Calculate the angle of this joystick.
	 *
	 * @return Joystick Angle in range -PI to PI
	 */
	public double getStickAngle() {
		// This shouldn't be necessary, deadzone filtering should already
		// be performed - however it doesn't hurt to make sure.
		if (isLeftInDeadzone()) {
			return 0.0;
		}

		if (stickLY == 0.0) {
			// In Y deadzone avoid divide by zero error
			return (stickLX > 0.0) ? Math.PI / 2 : -Math.PI / 2;
		}

		// Return value in range -PI to PI
		double stickAngle = Math.atan(stickLX / -stickLY);

		if (stickLY > 0) {
			stickAngle += (stickLX > 0) ? Math.PI : -Math.PI;
		}

		return (stickAngle);
	}

	public double getRStickAngle() {
		if (isRightInDeadzone()) {
			return 0.0;
		}

		if (stickRY == 0.0) {
			// In Y deadzone avoid divide by zero error
			return (stickRX > 0.0) ? Math.PI / 2 : -Math.PI / 2;
		}

		double stickAngle = Math.atan(stickRX / -stickRY);

		if (stickRY > 0) {
			stickAngle += (stickRX > 0) ? Math.PI : -Math.PI;
		}

		return (stickAngle);
	}

	/**
	 * Implement a dead zone for Joystick centering - and a non-linear
	 * acceleration as the user moves away from the zero position.
	 *
	 * @param input
	 * @return processed input
	 */
	private double accelerateJoystickInput(double input) {
		// Ensure that there is a dead zone around zero
		if (Math.abs(input) < DEADZONE) {
			return 0.0;
		}
		// Simply square the input to provide acceleration
		// ensuring that the sign of the input is preserved
		return (input * Math.abs(input));
	}
}
