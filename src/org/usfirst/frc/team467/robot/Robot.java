/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.AutoCalibration.InitialFeedForwardTuner;
import org.usfirst.frc.team467.robot.AutoCalibration.Tuner;
import org.usfirst.frc.team467.robot.AutoCalibration.UltimateProportionalGainTuner;

import com.ctre.CANTalon;

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

	private Tuner autotuner;

	private CANTalon talons[];

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
//		drive = Drive.getInstance();
//		Calibration.init();

	}

	public void disabledInit() {
	}

	public void disabledPeriodic() {
	}

	public void teleopInit() {
		drive.setSpeedMode();
	}

	boolean isTuningComplete;

	public void testInit() {
	}

	public void testPeriodic() {

	}

	public void autonomousInit() {
		talons = new CANTalon[5];
		talons[0] = null;
		talons[1] = new CANTalon(1);
		talons[2] = new CANTalon(2);
		talons[3] = new CANTalon(3);
		talons[4] = new CANTalon(4);
//		autotuner = new InitialFeedForwardTuner(talons[2], true, true);
		autotuner = new UltimateProportionalGainTuner(talons[2], true, true);
		isTuningComplete = false;
	}

	public void autonomousPeriodic() {
		talons[1].set(0);
//		talons[2].set(0);
		if (!isTuningComplete) {
			isTuningComplete = autotuner.process();
		} else {
			talons[2].set(0);
		}
		talons[3].set(0);
		talons[4].set(0);
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
		DriveMode driveMode = driverstation.getDriveMode();
		switch (driveMode) {
		case UNWIND:
			for (Steering wheelpod : Drive.getInstance().steering) {
				wheelpod.setAbsoluteAngle(0);
			}
			break;

		case TURN:
			drive.turnDrive(-driverstation.getDriveJoystick().getTwist() / 2);
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

		default:
			drive.stop(); // If no drive mode specified, don't drive!
		}
	}
}
