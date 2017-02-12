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
	private Joystick467 stick;
	private XBJoystick xbstick;
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
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		// Initialize logging framework.

		// Make robot objects
		driverstation = DriverStation2015.getInstance();
		drive = Drive.getInstance();
		drive.setSpeedMode();
		Calibration.init();

		/*
		 * Ignore Warning - Shashvat Will cause the initial computation of the
		 * look up table
		 */
		LookUpTable table = LookUpTable.getInstance();

		cam = CameraStream.getInstance();
		vision = VisionProcessing.getInstance();
		gyro = Gyrometer.getInstance();
		imu = gyro.getIMU();
		imu.calibrate();
		imu.reset();

		stick = new Joystick467(0);
		xbstick = new XBJoystick(0);

		SmartDashboard.putString("DB/String 0", "1.0");
		SmartDashboard.putString("DB/String 1", "0.0");
		SmartDashboard.putString("DB/String 2", "0.0");
		SmartDashboard.putString("DB/String 3", "0.0");
	}

	public void disabledInit() {
	}

	public void disabledPeriodic() {
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
		imu.reset();
		double p = Double.parseDouble(SmartDashboard.getString("DB/String 0", "2.0"));
		double i = Double.parseDouble(SmartDashboard.getString("DB/String 1", "0.0"));
		double d = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0"));
		double f = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0.0"));
		drive.aiming.setPID(p, i, d, f);
	}

	public void teleopInit() {
		imu.reset();
		imu.calibrate();
	}

	public void testInit() {
	}

	public void testPeriodic() {
	}

	public void autonomousPeriodic() {
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

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		double gyroAngle = gyro.pidGet();
		SmartDashboard.putNumber("gyro", imu.getAngleZ() / 4);
		SmartDashboard.putString("DB/String 4", String.valueOf(gyroAngle));
		
		drive.aiming.reset();
//		System.out.println("-------Teleop Periodic-------");
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
		drive.aiming.reset();

		DriveMode driveMode = driverstation.getDriveMode();
//		System.out.println("Update Drive: drivemode=" + driveMode.name());
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
//          case XB_SPLIT:
//        	drive.xbSplit(driverstation.getDriveJoystick().getStickAngle(),
//        				-driverstation.getRightDriveJoystick().getTurn() / 2,
//        				driverstation.getDriveJoystick().getStickDistance());
//        	break;
		case FIELD_ALIGN:
			drive.fieldAlignDrive(driverstation.getDriveJoystick().getStickAngle(),
						    driverstation.getDriveJoystick().getStickDistance());
			break;

		case VECTOR:
			drive.setSpeedMode();
			//drive.vectorDrive(driverstation.getDriveJoystick().getStickX(), driverstation.getDriveJoystick().getStickY(),
			//		driverstation.getDriveJoystick().getTwist());
			drive.vectorDrive(driverstation.getDriveJoystick().getStickAngle(),
				    driverstation.getDriveJoystick().getStickDistance(), driverstation.getDriveJoystick().getTwist());
			break;
		default:
			drive.stop(); // If no drive mode specified, don't drive!
        }
    }
}