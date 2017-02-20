/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.apache.log4j.Logger;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, as described in the
 * IterativeRobot documentation. If you change the name of this class or the package after creating this project, you must also
 * update the manifest file in the resource directory.
 */

public class Robot extends IterativeRobot {
	private static final double MIN_DRIVE_SPEED = RobotMap.MIN_DRIVE_SPEED;
	private static final Logger LOGGER = Logger.getLogger(Robot.class);

	// Robot objects
	private DriverStation2017 driverstation;
	private Drive drive;
	private ActionGroup autonomous;

	private VisionProcessing vision;
	private Gyrometer gyro;
	private Ultrasonic ultra;

	int session;

	/**
	 * Time in milliseconds
	 */
	double time;

	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	public void robotInit() {
		
		RobotMap.init(RobotMap.RobotID.ROBOT2015);
		
		// Initialize logging framework
		Logging.init();

		// Make robot objects
		driverstation = DriverStation2017.getInstance();
		drive = Drive.getInstance();
		// drive.setSpeedMode();
		drive.setPercentVoltageBusMode();
		Calibration.init();
		gyro = Gyrometer.getInstance();
		gyro.calibrate();
		gyro.reset();

		// Initialize math lookup table
		LookUpTable.init();

		vision = VisionProcessing.getInstance();
		ultra = new Ultrasonic(0, 1);
		gyro = Gyrometer.getInstance();

		SmartDashboard.putString("DB/String 0", "1.0");
		SmartDashboard.putString("DB/String 1", "0.0");
		SmartDashboard.putString("DB/String 2", "0.0");
		SmartDashboard.putString("DB/String 3", "0.0");
		LOGGER.debug("Robot Initialized");
	}

	public void disabledInit() {
		LOGGER.debug("Disabled Starting");
	}

	public void disabledPeriodic() {
		// LOGGER.debug("Disabled Periodic");
		SmartDashboard.putData("Ultrasonic", ultra);
		double gyroAngle = gyro.pidGet();
		SmartDashboard.putNumber("gyro", gyroAngle);
		SmartDashboard.putString("DB/String 4", String.valueOf(gyroAngle));
		double p = Double.parseDouble(SmartDashboard.getString("DB/String 0", "2.0"));
		double i = Double.parseDouble(SmartDashboard.getString("DB/String 1", "0.0"));
		double d = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0"));
		double f = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0.0"));
		drive.aiming.setPID(p, i, d, f);
		vision.update();
	}

	public void autonomousInit() {
		System.out.println("Autonomous reset");
		autonomous = driverstation.getAutonomous();
		autonomous.reset();
	}

	public void teleopInit() {
		gyro.reset();
		driverstation.readInputs();
		driverstation.getAutonomous().terminate();
	}

	public void testInit() {
		gyro.reset();
		double p = Double.parseDouble(SmartDashboard.getString("DB/String 0", "2.0"));
		double i = Double.parseDouble(SmartDashboard.getString("DB/String 1", "0.0"));
		double d = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0"));
		double f = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0.0"));
		drive.aiming.setPID(p, i, d, f);
	}

	public void testPeriodic() {
		double gyroAngle = gyro.pidGet();
		SmartDashboard.putNumber("gyro", gyro.getRobotAngleDegrees());
		SmartDashboard.putString("DB/String 4", String.valueOf(gyroAngle));
		vision.update();
		driverstation.readInputs();
		// double driveAngle = (vision.targetAngle - gyroAngle) * Math.PI / 180;
		// drive.crabDrive(driveAngle, 0.0);
		boolean onTarget = drive.turnToAngle(90.0); // Face 2º according to gyro
		if (onTarget) {
			System.out.println("TARGET ACQUIRED");
		}
	}

	public void autonomousPeriodic() {
		updateAutonomous();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		SmartDashboard.putString("DB/String 4", String.valueOf(gyro.pidGet()));

		drive.aiming.reset();

		// Read driverstation inputs
		driverstation.readInputs();

		if (driverstation.getGyroReset()) {
			gyro.reset();
		}
		
		if (driverstation.getCalibrate()) {
			// Calibrate Mode
			Calibration.updateCalibrate();
		} else if (!driverstation.getAutonomous().isComplete()) {
			updateAutonomous();
		} else if (driverstation.getStartAuto()) {
			LOGGER.debug("AUTONOMOUS");
			driverstation.getAutonomous().reset();
		} else {
			// Drive Mode
			updateDrive();
		}
	}

	public void updateAutonomous() {
		ActionGroup auto = driverstation.getAutonomous();
		driverstation.readInputs();
		LOGGER.debug("getTerminateAuto=" + driverstation.getTerminateAuto());
		if (driverstation.getTerminateAuto()) {
			auto.terminate();
		} else {
			auto.run();
		}
	}

	/**
	 * called once per iteration to perform any necessary updates to the drive system.
	 */
	private void updateDrive() {

		DriveMode driveMode = driverstation.getDriveMode();

		switch (driveMode) {
		case AIM:
			Actions.aimProcess(vision.targetAngle).run();
			break;

		case VECTOR:
			double turnSpeed = driverstation.getDriveJoystick().getRightStickDistance() * 0.5;
			// @formatter:off
			drive.vectorDrive(
					driverstation.getDriveJoystick().getLeftStickAngle(),     // Field aligned direction
					driverstation.getDriveJoystick().getLeftStickDistance(),  // Robot speed
					turnSpeed * driverstation.getVectorTurnDirection());      // Robot turn speed
			// @formatter:on
			break;

		case CRAB:
			if (driverstation.getDriveJoystick().getLeftStickDistance() < MIN_DRIVE_SPEED) {
				// Don't start driving until commanded speed greater than minimum
				drive.stop();
			} else {
				// @formatter:off
				drive.crabDrive(
						driverstation.getDriveJoystick().getLeftStickAngle(),     // Robot aligned direction
						driverstation.getDriveJoystick().getLeftStickDistance()); // Robot speed
				// @formatter:on
			}
			break;

		case UNWIND:
			for (Steering wheelpod : Drive.getInstance().steering) {
				wheelpod.setAbsoluteAngle(0);
			}
			break;

		default:
			drive.stop();
			break;
		}
	}
}