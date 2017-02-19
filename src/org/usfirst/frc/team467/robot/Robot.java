/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team467.robot.Autonomous.Process;
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
	private Process autonomous;

	private CameraStream cam;
	private VisionProcessing vision;
	private Gyrometer gyro;
	private ADIS16448_IMU imu;

	int session;

	/**
	 * Time in milliseconds
	 */
	double time;

	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	public void robotInit() {
		// Initialize logging framework
		Logging.init();

		// Make robot objects
		driverstation = DriverStation2017.getInstance();
		drive = Drive.getInstance();
		// drive.setSpeedMode();
		drive.setPercentVoltageBusMode();
		Calibration.init();
		gyro = Gyrometer.getInstance();
		imu = gyro.getIMU();
		imu.calibrate();
		imu.reset();

		LookUpTable.init();

		cam = CameraStream.getInstance();
		vision = VisionProcessing.getInstance();
		gyro = Gyrometer.getInstance();
		imu = gyro.getIMU();
		imu.calibrate();
		imu.reset();

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
		SmartDashboard.putData("IMU", imu);

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
		autonomous = Actions.getBasicProcess();
		autonomous.reset();
	}

	public void teleopInit() {
		imu.reset();
		driverstation.readInputs();
	}

	public void testInit() {
		imu.reset();
		double p = Double.parseDouble(SmartDashboard.getString("DB/String 0", "2.0"));
		double i = Double.parseDouble(SmartDashboard.getString("DB/String 1", "0.0"));
		double d = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0"));
		double f = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0.0"));
		drive.aiming.setPID(p, i, d, f);
	}

	public void testPeriodic() {
		double gyroAngle = gyro.pidGet();
		SmartDashboard.putNumber("gyro", imu.getAngleZ() / 4);
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
		autonomous.run();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		double gyroAngle = gyro.pidGet();
		SmartDashboard.putNumber("gyro", imu.getAngleZ() / 4);
		SmartDashboard.putString("DB/String 4", String.valueOf(gyroAngle));

		drive.aiming.reset();
		// System.out.println("-------Teleop Periodic-------");
		// Read driverstation inputs
		driverstation.readInputs();

		if (driverstation.getGyroReset()) {
			imu.reset();
		}
		if (driverstation.getCalibrate()) {
			// Calibrate Mode
			Calibration.updateCalibrate();
		} else {
			// Drive Mode
			updateDrive();
		}
	}

	/**
	 * called once per iteration to perform any necessary updates to the drive system.
	 */
	private void updateDrive() {
		drive.aiming.reset();
		DriveMode driveMode = driverstation.getDriveMode();
		
		if (!Actions.getBasicProcess().isComplete()) {
			Actions.getBasicProcess().run();
			return; // Takes over until completion
		} else if (driveMode == DriveMode.AUTONOMOUS) {
			Actions.getExampleProcess().reset();
		}

		switch (driveMode) {
		case AIM:
			Actions.aimProcess(vision.targetAngle).run();
			break;

		case VECTOR:
			double driveSpeed = driverstation.getDriveJoystick().getLeftStickDistance();
			double turnSpeed = driverstation.getDriveJoystick().getRightStickDistance();
			drive.vectorDrive(driverstation.getDriveJoystick().getLeftStickAngle(), // Field aligned direction
					driveSpeed, // Robot speed
					turnSpeed * driverstation.getVectorTurnDirection()); // Robot turn speed
			break;

		case CRAB:
			if (driverstation.getDriveJoystick().getLeftStickDistance() < MIN_DRIVE_SPEED) {
				// Don't start driving until commanded speed greater than mininum
				drive.stop();
			} else {
				drive.crabDrive(driverstation.getDriveJoystick().getLeftStickAngle(), // Robot aligned direction
						driverstation.getDriveJoystick().getLeftStickDistance()); // Robot speed
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