/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.apache.log4j.Logger;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
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
	private Ultrasonic467 ultra;

	private Climber climber;
	private BallIntake intake;
	private BallLoader loader;
	private GearDevice gearDevice;
	private Shooter shooter;

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

		// game pieces
		climber = Climber.getInstance();
		intake = BallIntake.getInstance();
		loader = BallLoader.getInstance();
		gearDevice = GearDevice.getInstance();
		shooter = Shooter.getInstance();

		// Initialize math lookup table
		LookUpTable.init();

		vision = VisionProcessing.getInstance();
		ultra = Ultrasonic467.getInstance();
		gyro = Gyrometer.getInstance();
		autonomous = driverstation.getActionGroup();
		LOGGER.debug("Robot Initialized");
		
	}
	
	public void robotPeriodic() {
		vision.update();
	}

	public void disabledInit() {
		LOGGER.debug("Disabled Starting");
		drive.aiming.disable();
		autonomous.terminate();
	}

	public void disabledPeriodic() {
//		vision.logDistanceValues();
		// LOGGER.debug("Disabled Periodic");
		SmartDashboard.putData("Ultrasonic", ultra.getSensor());
		SmartDashboard.putString("DB/String 4", String.valueOf(gyro.pidGet()));
//		drive.logSteeringValues();
		}

	public void autonomousInit() {
//		autonomous = driverstation.getActionGroup();
		final String autoMode = SmartDashboard.getString("Auto Selector","none");
		System.out.println("Autonomous init: " + autoMode);
		switch (autoMode) {
//		case "dgleft":
//			//drop gear from position on left side of field
//			autonomous = Actions.dropGearFromLeft();
//			break;
//		case "dgright":
//			//drop gear from position on right side of field
//			autonomous = Actions.dropGearFromRight();
//			break;
		case "lg":
			//get into position to drop gear from left side of field
			autonomous = Actions.getIntoGearPositionFromLeft();
			break;
		case "rg":
			//get into position to drop gear from right side of field
			autonomous = Actions.getIntoGearPositionFromRight();
			break;
		default:
			autonomous = Actions.doNothing();
			break;
		}
		autonomous.enable();
	}

	public void teleopInit() {
		gyro.reset();
		driverstation.readInputs();
		autonomous.terminate();
//		autonomous = Actions.doNothing();
		autonomous = driverstation.getActionGroup();
	}

	public void testInit() {
		double p = Double.parseDouble(SmartDashboard.getString("DB/String 0", ".025"));
		double i = Double.parseDouble(SmartDashboard.getString("DB/String 1", "0.0"));
		double d = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.1"));
		double f = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0.0"));
		drive.aiming.setPID(p, i, d, f);
	}

	public void testPeriodic() {
		double gyroAngle = gyro.pidGet();
		SmartDashboard.putNumber("gyro", gyro.getRobotAngleDegrees());
		SmartDashboard.putString("DB/String 4", String.valueOf(gyroAngle));
		driverstation.readInputs();
		boolean onTarget = drive.turnToAngle(90.0); // Face 90º according to gyro
		if (onTarget) {
			System.out.println("TARGET ACQUIRED");
		}

	}

	public void autonomousPeriodic() {
		drive.aiming.reset();
		autonomous.run();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
//		vision.logDistanceValues();
		LOGGER.info("angle aimTo=" + vision.getTargetAngle() + ", gyro=" + gyro.getRobotAngleDegrees());
		SmartDashboard.putString("DB/String 4", String.valueOf(gyro.pidGet()));
//		drive.logSteeringValues();
		// Read driverstation inputs
		driverstation.readInputs();

		if (driverstation.getGyroReset()) {
			gyro.reset();
		}
		
		if (!autonomous.isComplete()) {
			updateAutonomous(autonomous);
		} else if (driverstation.isInCalibrateMode()) {
			// Calibrate Mode
			Calibration.updateCalibrate();
			System.out.println(" FL: " + drive.getSteeringAngle(0) + " BL: " + drive.getSteeringAngle(1) + " BR: " + drive.getSteeringAngle(2) + " FR: " + drive.getSteeringAngle(3));
		} else {
			autonomous = driverstation.getActionGroup();
			// Drive Mode
			updateDrive();
			updateNavigation();
		}
	}

	/**
	 * @param auto
	 *            - ActionGroup to run
	 */
	private void updateAutonomous(ActionGroup auto) {
		driverstation.readInputs();
		LOGGER.debug("getTerminateAuto=" + driverstation.getTerminateAuto());
		if (driverstation.getTerminateAuto()) {
			auto.terminate();
		} else {
			auto.run();
		}
}

	/**
	 * called once per iteration to perform any necessary updates to the drive
	 * system.
	 */
	private void updateDrive() {
		DriveMode driveMode = driverstation.getDriveMode();
		
		if (driveMode != DriveMode.FACE_ANGLE && driveMode != DriveMode.AIM) {
			drive.aiming.disable();
		}

		switch (driveMode) {
//		case AIM:
//			LOGGER.debug("AIM DRIVE-CAN_SEE_TWO=" + vision.canSeeTwo());
//			if (vision.canSeeTwo()) {
//				LOGGER.debug("AIM DRIVE: TURNING");
//				drive.turnToAngle(vision.getTargetAngle());
//			}
//			break;
		case FACE_ANGLE:
			drive.turnToAngle(driverstation.driverJoy.getJoystick().getPOV(0));
			break;
			
		case VECTOR:
			double turnSpeed = driverstation.getDriveJoystick().getRightStickDistance() * 0.5;
			// @formatter:off
			drive.vectorDrive(driverstation.getDriveJoystick().getLeftStickAngle(), // Field
																					// aligned
																					// direction
					driverstation.getDriveJoystick().getLeftStickDistance(), // Robot
																				// speed
					turnSpeed * driverstation.getVectorTurnDirection()); // Robot
																			// turn
																			// speed
			// @formatter:on
			break;

		case CRAB:
			if (driverstation.getDriveJoystick().getLeftStickDistance() < MIN_DRIVE_SPEED) {
				// Don't start driving until commanded speed greater than
				// minimum
				drive.stop();
			} else {
				// @formatter:off
				drive.crabDrive(driverstation.getDriveJoystick().getLeftStickAngle(), // Robot
																						// aligned
																						// direction
						driverstation.getDriveJoystick().getLeftStickDistance()); // Robot
																					// speed
				// @formatter:on
			}
			break;

		case UNWIND:
			for (Steering wheelpod : Drive.getInstance().steering) {
				wheelpod.setAbsoluteAngle(0);
			}
			break;
			
		case CRAB_SLOW:
			double povAngleDeg = driverstation.getDriveJoystick().getPOV();
			//Timer.delay(0.25);

			if(povAngleDeg % 90 == 0) {
				drive.crabDrive(povAngleDeg * (Math.PI / 180), 0.4);
				System.out.println("pov angle:" + driverstation.getDriveJoystick().getPOV());
			} else {
				drive.stop();
			}
			break;

		default:
			drive.stop();
			break;
		} 
	}

	private void updateNavigation() {
		if (driverstation.isShooting()) {
			shooter.shoot();
			loader.load();
		} else if (driverstation.isShootingReverse()) {
			shooter.reverse();
			loader.reverse();
		} else {
			shooter.stop();
			loader.stop();
		}

		if (driverstation.isClimbing()) {
			climber.climb();
		} else if (driverstation.isClimbingSlow()) {
			climber.slow();
		} else {
			climber.stop();
		}

		if (driverstation.isGearDown()) {
			gearDevice.goDown();
		} else {
			gearDevice.goUp();
		}

		if (driverstation.isIntaking()) {
			intake.startIntake();
		} else if (driverstation.isIntakingReverse()) {
			intake.reverse();
		} else {
			intake.stop();
		}
	}

}