package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Joystick;

public interface RightJoystick467
{
    /**
     * Returns the jaw joystick object inside Joystick467
     * 
     * @return
     */
	   Joystick getJoystick();

	    /**
	     * Read all inputs from the underlying joystick object.
	     */
	    void readInputs();

	    /**
	     * Gets the Y position of the stick. Up to down ranges from -1.0 to 1.0,
	     * with 0.0 in the middle. This value is accelerated.
	     * 
	     * @return
	     */
	    
	    double getTurn();

		double getSpeed();
    
}
