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
public class Joystick467
{
    private Joystick joystick;
    private boolean[] buttons = new boolean[12];     // array of current button states
    private boolean[] prevButtons = new boolean[12]; // array of previous button states, involved in edge detection.
    private double stickX = 0.0;
    private double stickY = 0.0;
    private int pov = 0;
    private double twist = 0.0;
    private boolean flap = false;

    public static final int TRIGGER = 1;
    private static final double DEADZONE = 0.1;

    private static final int AXIS_X = 0;
    private static final int AXIS_Y = 1;
    private static final int TWIST_AXIS = 2;
    private static final int FLAP_AXIS = 3;
    private static final int POV_INDEX = 0;

    /**
     * Create a new joystick on a given channel
     *
     * @param stick
     */
    public Joystick467(int stick)
    {
        joystick = new Joystick(stick);
    }

    /**
     * Returns the raw joystick object inside Joystick467
     * 
     * @return
     */
    public Joystick getJoystick()
    {
        return joystick;
    }

    /**
     * Read all inputs from the underlying joystick object.
     */
    public void readInputs()
    {
        // read all buttons
        for (int i = 0; i < 12; i++)
        {
            prevButtons[i] = buttons[i];
            buttons[i] = joystick.getRawButton(i + 1);
        }

        // Read Joystick Axes
        flap = joystick.getRawAxis(FLAP_AXIS) < 0.0;
        stickY = accelerateJoystickInput(joystick.getRawAxis(AXIS_Y));
        stickX = accelerateJoystickInput(joystick.getRawAxis(AXIS_X));
        twist = accelerateJoystickInput(joystick.getRawAxis(TWIST_AXIS));
        pov = joystick.getPOV(POV_INDEX);
    }

    /**
     * Check if a specific button is being held down. Ignores first button
     * press, but the robot loops too quickly for this to matter.
     *
     * @param button
     * @return
     */
    public boolean buttonDown(int button)
    {
        return buttons[(button) - 1];
    }

    /**
     * Check if a specific button has just been pressed. (Ignores holding.)
     *
     * @param button
     * @return
     */
    public boolean buttonPressed(int button)
    {
        return buttons[button - 1] && !prevButtons[button - 1];
    }

    /**
     * Check if a specific button has just been released.
     *
     * @param button
     * @return
     */
    public boolean buttonReleased(int button)
    {
        return !buttons[button - 1] && prevButtons[button - 1];
    }

    /**
     * Gets the X position of the stick. Left to right ranges from -1.0 to 1.0,
     * with 0.0 in the middle. This value is accelerated.
     * 
     * @return
     */
    public double getStickX()
    {
        return stickX;
    }

    /**
     * Gets the Y position of the stick. Up to down ranges from -1.0 to 1.0,
     * with 0.0 in the middle. This value is accelerated.
     * 
     * @return
     */
    public double getStickY()
    {
        return stickY;
    }

    /**
     * 
     * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
     */
    public int getPOV()
    {
        return pov;
    }

    /**
     * 
     * 
     * @return
     */
    public boolean getFlap()
    {
        return flap;
    }

    public double getTwist()
    {
        return twist;
    }

    /**
     * Calculate the distance of this stick from the center position.
     *
     * @return
     */
    public double getStickDistance()
    {
        return Math.sqrt((stickX * stickX) + (stickY * stickY));
    }

    public boolean isInDeadzone()
    {
        return (Math.abs(stickX) < DEADZONE) && (Math.abs(stickY) < DEADZONE);
    }

    /**
     * Calculate the angle of this joystick.
     *
     * @return Joystick Angle in range -PI to PI
     */
    public double getStickAngle()
    {
        // This shouldn't be necessary, deadzone filtering should already
        // be performed - however it doesn't hurt to make sure.
        if (isInDeadzone())
        {
            return 0.0;
        }

        if (stickY == 0.0)
        {
            // In Y deadzone avoid divide by zero error
            return (stickX > 0.0) ? Math.PI / 2 : -Math.PI / 2;
        }

        // Return value in range -PI to PI
        double stickAngle = Math.atan(stickX / -stickY);

        if (stickY > 0)
        {
            stickAngle += (stickX > 0) ? Math.PI : -Math.PI;
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
    private double accelerateJoystickInput(double input)
    {
        // Ensure that there is a dead zone around zero
        if (Math.abs(input) < DEADZONE)
        {
            return 0.0;
        }
        // Simply square the input to provide acceleration
        // ensuring that the sign of the input is preserved
        return (input * Math.abs(input));
    }
   
}
