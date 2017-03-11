/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

import java.lang.Math;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 */
public class XBoxJoystick467 {
	private static final int NUM_BUTTONS = 10;
	private Joystick joystick;
	private boolean[] buttons = new boolean[NUM_BUTTONS]; // array of current button states
	private boolean[] prevButtons = new boolean[NUM_BUTTONS]; // array of previous button states used for debouncing
	private double leftStickX = 0.0;
	private double leftStickY = 0.0;
	private double rightStickX = 0.0;
	private double rightStickY = 0.0;
	private double leftTrigger = 0.0;
	private double rightTrigger = 0.0;
	private int pov = 0;

	private static final double DEADZONE = 0.1;

	public static final int BUTTON_A = 1;
	public static final int BUTTON_B = 2;
	public static final int BUTTON_X = 3;
	public static final int BUTTON_Y = 4;
	public static final int BUMPER_LEFT = 5;
	public static final int BUMPER_RIGHT = 6;
	public static final int BUTTON_BACK = 7;
	public static final int BUTTON_START = 8;
	public static final int BUTTON_LEFT = 9;
	public static final int BUTTON_RIGHT = 10;

	private static final int AXIS_LEFT_X = 0;
	private static final int AXIS_LEFT_Y = 1;
	private static final int AXIS_LEFT_TRIGGER = 2;
	private static final int AXIS_RIGHT_TRIGGER = 3;
	private static final int AXIS_RIGHT_X = 4;
	private static final int AXIS_RIGHT_Y = 5;

	/**
	 * Create a new joystick on a given channel
	 *
	 * @param stick
	 */
	public XBoxJoystick467(int stick) {
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
		for (int i = 0; i < NUM_BUTTONS; i++) {
			prevButtons[i] = buttons[i];
			buttons[i] = joystick.getRawButton(i + 1);
		}

		// Read Joystick Axes
		leftStickX = accelerateJoystickInput(joystick.getRawAxis(AXIS_LEFT_X));
		leftStickY = accelerateJoystickInput(joystick.getRawAxis(AXIS_LEFT_Y));

		rightStickX = accelerateJoystickInput(joystick.getRawAxis(AXIS_RIGHT_X));
		rightStickY = accelerateJoystickInput(joystick.getRawAxis(AXIS_RIGHT_Y));

		leftTrigger = accelerateJoystickInput(joystick.getRawAxis(AXIS_LEFT_TRIGGER));
		rightTrigger = accelerateJoystickInput(joystick.getRawAxis(AXIS_RIGHT_TRIGGER));

		pov = joystick.getPOV(0);
	}

	/**
	 * Check if a specific button is being held down. Ignores first button press, but the robot loops too quickly for this to
	 * matter.
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
	 * Gets the X position of the stick. Left to right ranges from -1.0 to 1.0, with 0.0 in the middle. This value is accelerated.
	 *
	 * @return
	 */
	public double getLeftStickX() {
		return leftStickX;
	}

	public double getRightStickX() {
		return rightStickX;
	}

	public double getPOV() {
		return pov;
	}

	/**
	 * Gets the Y position of the stick. Up to down ranges from -1.0 to 1.0, with 0.0 in the middle. This value is accelerated.
	 *
	 * @return
	 */
	public double getLeftStickY() {
		return leftStickY;
	}

	public double getRightStickY() {
		return rightStickY;
	}

	public double getLeftTrigger() {
		return leftTrigger;
	}

	public double getRightTrigger() {
		return rightTrigger;
	}

	/**
	 * Calculate the distance of this stick from the center position.
	 *
	 * @return
	 */
	public double getLeftStickDistance() {
		return Math.sqrt((leftStickX * leftStickX) + (leftStickY * leftStickY));
	}

	public double getRightStickDistance() {
		return Math.sqrt((rightStickX * rightStickX) + (rightStickY * rightStickY));
	}

	private double calculateStickAngle(double stickX, double stickY) {
		if (stickY == 0.0) {
			// In Y deadzone avoid divide by zero error
			return (stickX > 0.0) ? Math.PI / 2 : -Math.PI / 2;
		}

		// Return value in range -PI to PI
		double stickAngle = LookUpTable.getArcTan(stickX / -stickY);

		if (stickY > 0) {
			stickAngle += (stickX > 0) ? Math.PI : -Math.PI;
		}

		return (stickAngle);
	}

	/**
	 * Calculate the angle of this joystick.
	 *
	 * @return Joystick Angle in range -PI to PI
	 */
	public double getLeftStickAngle() {
		return (calculateStickAngle(leftStickX, leftStickY));
	}

	public double getRightStickAngle() {
		return (calculateStickAngle(rightStickX, rightStickY));
	}

	/**
	 * Implement a dead zone for Joystick centering - and a non-linear acceleration as the user moves away from the zero position.
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