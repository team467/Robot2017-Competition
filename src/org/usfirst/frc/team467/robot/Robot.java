/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	private static final double MIN_DRIVE_SPEED = 0.1;

	// Robot objects
	private DriverStation2015 driverstation;
	private Drive drive;

	int session;

	/**
	 * Time in milliseconds
	 */
	double time;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		// Initialize logging framework.

		// Make robot objects
		driverstation = DriverStation2015.getInstance();
		drive = Drive.getInstance();
		Calibration.init();

		/* Ignore Warning - Shashvat
		   Will cause the initial computation of the look up table */
		LookUpTable table = LookUpTable.getInstance();

	}

	public void disabledInit() {
	}

	public void disabledPeriodic() {
	}

	public void autonomousInit() {
	}

	public void teleopInit() {
		driverstation.getDriveJoystick().setXbox();
	}

	public void testInit() {
	}

	public void testPeriodic() {
	}

	public void autonomousPeriodic() {
		driverstation.readInputs();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		// Read driverstation inputs
		driverstation.readInputs();

		if (driverstation.getCalibrate()) {
			// Calibrate Mode
			Calibration.updateCalibrate();
		} else {
			// Drive Mode
			updateDrive();
		}
	}

	/**
	 * called once per iteration to perform any necessary updates to the drive
	 * system.
	 */
	private void updateDrive() {
		drive.setSpeedMode();

		DriveMode driveMode = driverstation.getDriveMode();
		switch (driveMode) {
		case UNWIND:
			for (Steering wheelpod : Drive.getInstance().steering) {
				wheelpod.setAbsoluteAngle(0);
			}
			break;

		case TURN:
			if (driverstation.getDriveJoystick().getXbox()){
				drive.turnDrive(-driverstation.getDriveJoystick().getTurnStickX() / 2);
			}
			else{
				drive.turnDrive(-driverstation.getDriveJoystick().getTwist() / 2);
			}
			break;

		case CRAB:
			if (driverstation.getDriveJoystick().getStickDistance() < MIN_DRIVE_SPEED) {
				// Don't start driving until commanded speed greater than
				// minimum
				drive.stop();
			} else {
				drive.crabDrive(driverstation.getDriveJoystick().getStickAngle(),
							    driverstation.getDriveJoystick().getStickDistance());
			}
			break;

		case STRAFE:
			drive.strafeDrive(driverstation.getDriveJoystick().getPOV());
			break;

		case FIELD_ALIGN:
			drive.fieldAlignDrive(driverstation.getDriveJoystick().getStickAngle(),
						    driverstation.getDriveJoystick().getStickDistance());
			break;

		case VECTOR:
			if (driverstation.getDriveJoystick().getXbox()){
				drive.vectorDrive(driverstation.getDriveJoystick().getStickAngle(),
					    driverstation.getDriveJoystick().getStickDistance(), driverstation.getDriveJoystick().getTurnStickX());
			}
			else {
				drive.vectorDrive(driverstation.getDriveJoystick().getStickAngle(),
				    driverstation.getDriveJoystick().getStickDistance(), driverstation.getDriveJoystick().getTwist());
			}
			break;
//		case XB_SPLIT:
//			if (driverstation.getDriveJoystick().getXbox()){
//	        	drive.xbSplit(driverstation.getDriveJoystick().getStickAngle(),
//	    				driverstation.getDriveJoystick().getStickDistance(),
//	    				-driverstation.getDriveJoystick().getTurnStickX() / 2);
//			}
//			else {
//	        	drive.xbSplit(driverstation.getDriveJoystick().getStickAngle(),
//	    				driverstation.getDriveJoystick().getStickDistance(),
//	    				-driverstation.getDriveJoystick().getTwist() / 2);
//			}
//			break;

		default:
			drive.stop(); // If no drive mode specified, don't drive!
		}
	}
}