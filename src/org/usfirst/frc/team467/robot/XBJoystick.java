package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Joystick;

public class XBJoystick implements MainJoystick467{
	private Joystick joystick;
    private boolean[] buttons = new boolean[10];     // array of current button states
    private boolean[] prevButtons = new boolean[10]; // array of previous button states, involved in edge detection.
    private double stickX = 0.0;
    private double stickY = 0.0;
    private double triggerL = 0.0;
    private double triggerR = 0.0;
    private int pov = 0;
    
    public static final int TRIGGER = 1;
    private static final double DEADZONE = 0.1;

    private static final int AXIS_LX = 0;
    private static final int AXIS_LY = 1;
    private static final int TRIGGER_LEFT = 2;
    private static final int TRIGGER_RIGHT = 3;
    private static final int POV_INDEX = 0;

    /**
     * Create a new joystick on a given channel
     *
     * @param stick
     */
    public XBJoystick(int stick)
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
    @Override
    public void readInputs()
    {
        // read all buttons
        for (int i = 0; i < 10; i++)
        {
            prevButtons[i] = buttons[i];
            buttons[i] = joystick.getRawButton(i + 1);
        }

        // Read Joystick Axes
        stickY = accelerateJoystickInput(joystick.getRawAxis(AXIS_LY));
        stickX = accelerateJoystickInput(joystick.getRawAxis(AXIS_LX));
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
    public double getTriggerR()
    {
    	return triggerR;
    }
    public double getTriggerL(){
    	return triggerL;
    }
    
    /**
     * 
     * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
     */
    @Override
    public int getPOV()
    {
        return pov;
    }
    /**
     * Calculate the distance of this stick from the center position.
     *
     * @return
     */
    @Override
    public double getStickDistance()
    {
        return Math.sqrt((stickX * stickX) + (stickY * stickY));
    }
    
    @Override
    public boolean isInDeadzone()
    {
        return (Math.abs(stickX) < DEADZONE) && (Math.abs(stickY) < DEADZONE);
    }
    
    /**
     * Calculate the angle of this joystick.
     *
     * @return Joystick Angle in range -PI to PI
     */
    
    @Override
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

	@Override
	public boolean getFlap() {
		return false;
	}

	@Override
	public double getTwist() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTurn() {
		return stickX;
	}

	@Override
	public double getSpeed() {
		return stickY;
	}
}
