/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot
{
    private static final double MIN_DRIVE_SPEED = 0.1;

    // Robot objects
    private DriverStation2015 driverstation;
    private Drive drive;
    private Joystick467 stick;
    private XBJoystick xbstick;
    private ADIS16448_IMU gyro;

    int session;

    /**
     * Time in milliseconds
     */
    double time;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {
        // Initialize logging framework.

        // Make robot objects
        driverstation = DriverStation2015.getInstance();
        drive = Drive.getInstance();
        Calibration.init();
        gyro = Gyrometer.getInstance();
        LookUpTable table = LookUpTable.getInstance();
        
        stick = new Joystick467(0);
        xbstick = new XBJoystick(0);
    }

    public void disabledInit()
    {
    }

    public void disabledPeriodic()
    {
    	SmartDashboard.putData("IMU", gyro);
//    	System.out.println("x:" + gyro.getAngleX() + "y:" + gyro.getAngleY() + "z:" + gyro.getAngleZ());
//    	gyro.reset();
    }

    public void autonomousInit()
    {
    	gyro.reset();
    	gyro.calibrate();
    }

    public void teleopInit()
    {
    	gyro.reset();
    	gyro.calibrate();
    }

    public void testInit()
    {
    }

    public void testPeriodic()
    {
    }

    public void autonomousPeriodic()
    {
        driverstation.readInputs();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic(){
    	// Read driverstation inputs
    	driverstation.readInputs();
    	        
      
        if(driverstation.getGyroReset())
        {
        	gyro.reset();
        }

        if (driverstation.getCalibrate())
        {
            // Calibrate Mode
            Calibration.updateCalibrate();
        }
        else
        {
            // Drive Mode
            updateDrive();
        }
    }

    /**
     * called once per iteration to perform any necessary updates to the drive
     * system.
     */
    private void updateDrive()
    {
    	DriveMode driveMode = driverstation.getDriveMode();
        switch (driveMode)
        {
            case UNWIND:
                for (Steering wheelpod : Drive.getInstance().steering)
                {
                    wheelpod.setAbsoluteAngle(0);
                }
                break;

            case TURN:
                drive.turnDrive(-driverstation.getDriveJoystick().getTwist()/2);
                break;

            case CRAB:
                if (driverstation.getDriveJoystick().getStickDistance() < MIN_DRIVE_SPEED)
                {
                    // Don't start driving until commanded speed greater than minimum
                    drive.stop();
                }
                else
                {
                    drive.crabDrive(driverstation.getDriveJoystick().getStickAngle(),
                    				driverstation.getDriveJoystick().getStickDistance());
                }
                break;
            case STRAFE:
            	if (driverstation.getDriveJoystick().getStickDistance() < MIN_DRIVE_SPEED)
                {
                    // Don't start driving until commanded speed greater than minimum
                    drive.stop();
                }
                else
                {
                	drive.strafeDrive(driverstation.getDriveJoystick().getPOV());

                }
               	break;
//            case XB_SPLIT:
//            	drive.xbSplit(driverstation.getDriveJoystick().getStickAngle(),
//            				-driverstation.getRightDriveJoystick().getTurn() / 2,
//            				driverstation.getDriveJoystick().getStickDistance());
//            	break;
//            case FIELD_ALIGN:
//            	//angle Z is taken from the ADIS 16448 gyrometer
//            	drive.fieldAlignDrive(driverstation.getDriveJoystick().getStickAngle(),
//            			driverstation.getDriveJoystick().getStickDistance());
            case FIELD_ALIGN:
            	//angle Z is taken from the ADIS 16448 gyrometer
            	drive.fieldAlignDrive(gyro.getAngleZ(), driverstation.getDriveJoystick().getStickAngle(),
            			driverstation.getDriveJoystick().getStickDistance());
            	System.out.println("WHYY");
            	break;
            case VECTOR:
    			drive.setSpeedMode();
    			//drive.vectorDrive(driverstation.getDriveJoystick().getStickX(), driverstation.getDriveJoystick().getStickY(),
    			//		driverstation.getDriveJoystick().getTwist());
    			drive.vectorDrive(driverstation.getDriveJoystick().getStickAngle(),
    				    driverstation.getDriveJoystick().getStickDistance(), driverstation.getDriveJoystick().getTwist());
    			break;
		default:
			 if (driverstation.getDriveJoystick().getStickDistance() < MIN_DRIVE_SPEED)
             {
                 // Don't start driving until commanded speed greater than minimum
                 drive.stop();
             }
             else
             {
                 drive.crabDrive(driverstation.getDriveJoystick().getStickAngle(),
                 				driverstation.getDriveJoystick().getStickDistance());
             }
			break;

        }
    }
}