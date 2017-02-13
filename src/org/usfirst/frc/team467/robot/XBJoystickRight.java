package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Joystick;

public class XBJoystickRight implements RightJoystick467
{
    private Joystick joystick;
    private double stickX = 0.0;
    private double stickY = 0.0;
    private static final int AXIS_X = 4;
    private static final int AXIS_Y = 5;
    private static final double DEADZONE = 0.1;
    
    public XBJoystickRight(int stick)
    {
        joystick = new Joystick(stick);
    }
    
    @Override
    public Joystick getJoystick()
    {
        return joystick;
    }

    @Override
    public void readInputs()
    {
        stickY = accelerateJoystickInput(joystick.getRawAxis(AXIS_Y));
        stickX = accelerateJoystickInput(joystick.getRawAxis(AXIS_X));
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
//        return input * Math.abs(input);
      return input;
    }
    
    public double getStickY(){
    	return stickY;
    }
    
    public double getStickX(){
        return stickX;
    }
    
    public double getStickDistance()
    {
        return Math.sqrt((stickX * stickX) + (stickY * stickY));
    }
    
    public boolean isInDeadzone()
    {
        return (Math.abs(stickX) < DEADZONE) && (Math.abs(stickY) < DEADZONE);
    }
    
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

	@Override
	public double getTurn() {
		return stickX;
	}

	@Override
	public double getSpeed() {
		return stickY;
	}

	@Override
	public double getXAxis() {
		return stickX;
	}
    
}
