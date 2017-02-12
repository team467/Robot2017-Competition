package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriverStation2015
{    
    private static DriverStation2015 driverstation2015 = null;

    //Instantiated this in main class instead of inside of getDriveMode    
    // Mapping of functions to Joystick Buttons for normal operation
	private static int FIELD_ALIGN_BUTTON = 5;
	private static int VECTOR_DRIVE_BUTTON = 6;
	private static int XB_SPLIT = 4;
	// Mapping of functions to Joystick Buttons for calibration mode
    private static int SLOW_BUTTON = Joystick467.TRIGGER;
    private static int TURN_BUTTON = 2;
    private static int TURBO_BUTTON = 7;
    private static int GYRO_RESET_BUTTON = 8;
    // Mapping of functions to Joystick Buttons for calibration mode
    private static int CALIBRATE_CONFIRM_BUTTON = Joystick467.TRIGGER;
    private static int CALIBRATE_SLOW_BUTTON = 4;
    private static int UNWIND_BUTTON = 10;
       
    MainJoystick467 driverJoy1 = null;
    RightJoystick467 driverJoy2 = null;
    String stickType;

    public boolean split = false;
    
    Joysticks sticks = null;
    enum Speed 
    {
        SLOW, FAST
    }

    /**
     * Singleton instance of the object.
     * 
     * @return
     */
    public static DriverStation2015 getInstance()
    {
        if (driverstation2015 == null)
        {
            driverstation2015 = new DriverStation2015();
        }
        return driverstation2015;
    }

    /**
     * Private constructor
     */
    public DriverStation2015()
    {
        makeJoysticks();
    }

    private void makeJoysticks(){
    	String newStickType = SmartDashboard.getString("DB/String 0", "XBSplit"); //Assume xbsplit
    	if (newStickType.isEmpty())
    	{
    		newStickType = "xbsplit";
    		}
    	System.out.println(newStickType);{
    	if (newStickType.equals(stickType))
    	{
    		return;
    		}
    	stickType = newStickType.toUpperCase();
    	String stickTypeDescription;
    	switch (stickType)
    	{
    	case "LT1":
            driverJoy1 = new Joystick467(0);
            driverJoy2 = null;
            stickTypeDescription = "Logitech 1-stick";
            split = false;
            break;
    	 case "XBSPLIT":
             driverJoy1 = new XBJoystick(0);
             driverJoy2 = new XBJoystickRight(0);
             split = true;
             stickTypeDescription = "XBox split-stick";
             break;
        default:
           stickTypeDescription = "Invalid(XBSplit)";
            driverJoy1 = new XBJoystick(0);
            driverJoy2 = new XBJoystickRight(0);
            split = true;
            stickTypeDescription = "XBox split-stick";
            break;
    }
    SmartDashboard.putString("DB/String 5", "Stick type " + stickTypeDescription);
    }
    }
 
    /**
     * Must be called prior to first button read.
     */
    
    public void readInputs()
    {
        makeJoysticks();
        
        driverJoy1.readInputs();
        if (driverJoy2 != null)
        {
            driverJoy2.readInputs();
        }
    }

    /**
     * Gets joystick instance used by driver.
     *
     * @return
     */
    public MainJoystick467 getDriveJoystick()
    {
        return driverJoy1;
    }
    /**
     * Get joystick instance used for calibration.
     *
     * @return
     */
    public MainJoystick467 getCalibrationJoystick()
    {
        return driverJoy1;
    }

    public RightJoystick467 getRightDriveJoystick()
    {
        return driverJoy2;
    }
    // All button mappings are accessed through the functions below

    /**
     * returns the current drive mode. Modes lower in the function will override
     * those higher up. only 1 mode can be active at any time
     * 
     * @return currently active drive mode.
     */
    public DriveMode getDriveMode()
    {
    	//Instantiated outside of this class and in main class
    	//crab drive is default. if else, it is strafe.
    	//return the chosen drivemode
       DriveMode drivemode = DriveMode.CRAB;  // default is regular crab drive        
        if (getDriveJoystick().buttonDown(TURN_BUTTON))
        {
            drivemode = DriveMode.TURN;
        } 
        if (getDriveJoystick().buttonDown(UNWIND_BUTTON))
        {
            drivemode = DriveMode.UNWIND;
        }
        if(getDriveJoystick().getPOV() != -1)
        {
        	drivemode = DriveMode.STRAFE;
        }
        if(getDriveJoystick().buttonPressed(FIELD_ALIGN_BUTTON));
        {
        	drivemode = DriveMode.FIELD_ALIGN;
        }
        if(getDriveJoystick().buttonPressed(XB_SPLIT));
        {
        	drivemode = DriveMode.XB_SPLIT;
        }
		return drivemode;
    }

    /**
     * 
     * @return true if button required to enable slow driving mode are pressed
     */
    public boolean getSlow()
    {
        return getDriveJoystick().buttonDown(SLOW_BUTTON);
    }

    /**
     * 
     * @return true if button required to enable turbo driving mode are pressed
     */
    public boolean getTurbo()
    {
        return getDriveJoystick().buttonDown(TURBO_BUTTON);
    }

    // Calibration functions. Calibration is a separate use mode - so the buttons used
    // here can overlap with those used for the regular drive modes

    /**
     * 
     * @return true if calibration mode selected
     */
    public boolean getCalibrate()
    {
        return getDriveJoystick().getFlap();
    }
    
    public boolean getGyroReset()
    {
        return driverJoy1.buttonDown(GYRO_RESET_BUTTON);
    }

    /**
     * 
     * @return true if button to confirm calibration selection is pressed
     */
    public boolean getCalibrateConfirmSelection()
    {
        return getCalibrationJoystick().buttonDown(CALIBRATE_CONFIRM_BUTTON);
    }

    /**
     * 
     * @return true if button to enable calibration slow turn mode is pressed
     */
    public boolean getCalibrateSlowTurn()
    {
        return getCalibrationJoystick().buttonDown(CALIBRATE_SLOW_BUTTON);
    }
}
