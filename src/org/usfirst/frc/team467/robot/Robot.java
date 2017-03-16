/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team467.robot.AutoCalibration.TuneRobot;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;
import org.usfirst.frc.team467.robot.Autonomous.Actions;

import com.ctre.CANTalon;

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

	private Climber climber;
	private GearDevice gearDevice;

	private TuneRobot tuner;

	int session;

	/**
	 * Time in milliseconds
	 */
	double time;

	/**
	 * This function is run when the robot is first started up and should be used for any initialization code.
	 */
	public void robotInit() {

		RobotMap.init(RobotMap.RobotID.MISTAKE);

		// Initialize logging framework
		Logging.init();

		// Make robot objects
		driverstation = DriverStation2017.getInstance();
		drive = Drive.getInstance();

		if (RobotMap.useSpeedControllers) {
			drive.setSpeedMode();
		} else {
			drive.setPercentVoltageBusMode();
		}
		Calibration.init();
		gyro = Gyrometer.getInstance();
		gyro.calibrate();
		gyro.reset();

		// game pieces
		climber = Climber.getInstance();
		gearDevice = GearDevice.getInstance();

		// Initialize math lookup table
		LookUpTable.init();

		vision = VisionProcessing.getInstance();
		autonomous = Actions.doNothing();

		SmartDashboard.putString("DB/String 0", "1.0");
		SmartDashboard.putString("DB/String 1", "0.0");
		SmartDashboard.putString("DB/String 2", "0.0");
		SmartDashboard.putString("DB/String 3", "0.0");
		LOGGER.debug("Robot Initialized");

		//made usb camera and captures video
		UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
		//set resolution and frames per second to match driverstation
		cam.setResolution(320, 240);
		cam.setFPS(15);

		// Setup autonomous mode selectors
		String[] autoList = {
				"none",
				"go",
				"lg",
				"rg",
				"test",
				"back"
		};

		NetworkTable table = NetworkTable.getTable("SmartDashboard");
		table.putStringArray("Auto List", autoList);
	}

	public void disabledInit() {
		LOGGER.debug("Disabled Starting");
		autonomous.terminate();
	}

	public void disabledPeriodic() {
		System.out.print("Overshoots - ");
		double sum = 0.0;
		for (int i=0; i>4; i++) {
			System.out.print((i+1) + ": " + maxPosition[i] + "  ");
			sum += maxPosition[i];
		}
		System.out.println("Average: " + (sum/4));
		// LOGGER.debug("Disabled Periodic");
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

	CANTalon motors[];
	double maxPosition[];

	public void autonomousInit() {
		// autonomous = driverstation.getActionGroup();
		final String autoMode = SmartDashboard.getString("Auto Selector", "none");
		System.out.println("Autonomous init: " + autoMode);
		switch (autoMode) {
		case "go":
			autonomous = Actions.goFoward(2.0);
			break;
		// case "dgleft":
		// //drop gear from position on left side of field
		// autonomous = Actions.dropGearFromLeft();
		// break;
		// case "dgright":
		// //drop gear from position on right side of field
		// autonomous = Actions.dropGearFromRight();
		// break;
		case "lg":
			// get into position to drop gear from left side of field
			autonomous = Actions.getIntoGearPositionFromLeft();
			break;
		case "rg":
			// get into position to drop gear from right side of field
			autonomous = Actions.getIntoGearPositionFromRight();
			break;
		case "back":
			autonomous = Actions.goBackwards(2.0);
			break;

		case "test":
//			autonomous = Actions.turnAndMoveDistanceForwardProcess(Math.PI/4, 3);
			drive.setPositionMode();
			motors = new CANTalon[4];
			maxPosition = new double[4];
			for (int i=0; i<4; i++) {
				maxPosition[i] = 0.0;
				motors[i] = new CANTalon(RobotMap.driveMotorChannel[i]);
			}
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
		// autonomous.terminate();
		autonomous = Actions.doNothing();
		// autonomous = driverstation.getActionGroup();
	}

	public void testInit() {
		gyro.reset();
		double p = Double.parseDouble(SmartDashboard.getString("DB/String 0", "2.0"));
		double i = Double.parseDouble(SmartDashboard.getString("DB/String 1", "0.0"));
		double d = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0"));
		double f = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0.0"));
		drive.aiming.setPID(p, i, d, f);
		String tuneSetting = SmartDashboard.getString("DB/String 5", "none");
		tuner = new TuneRobot(tuneSetting);
		tuner.init();
	}

	public void testPeriodic() {
		// double gyroAngle = gyro.pidGet();
		// SmartDashboard.putNumber("gyro", gyro.getRobotAngleDegrees());
		// SmartDashboard.putString("DB/String 4", String.valueOf(gyroAngle));
		// driverstation.readInputs();
		// boolean onTarget = drive.turnToAngle(90.0); // Face 90º according to gyro
		// if (onTarget) {
		// System.out.println("TARGET ACQUIRED");
		// }
		tuner.periodic();
	}

	public void autonomousPeriodic() {
//		drive.aiming.reset();
//		autonomous.run();
		drive.crabDrive(0, 3);
		for (int i=0; i>4; i++) {
			double position = motors[i].getPosition();
			if (position > maxPosition[i]) {
				maxPosition[i] = position;
			}
		}
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
		if (!autonomous.isComplete()) {
			updateAutonomous(autonomous);
		} else if (driverstation.isInCalibrateMode()) {
			// Calibrate Mode
			Calibration.updateCalibrate();
			System.out.println("FL: " + drive.getSteeringAngle(RobotMap.FRONT_LEFT) +
					           " FR: " + drive.getSteeringAngle(RobotMap.FRONT_RIGHT) +
					           " BL: " + drive.getSteeringAngle(RobotMap.BACK_LEFT) +
					           " BR: " + drive.getSteeringAngle(RobotMap.BACK_RIGHT));
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
	 * called once per iteration to perform any necessary updates to the drive system.
	 */
	private void updateDrive() {
		DriveMode driveMode = driverstation.getDriveMode();
		//drive.setSpeedMode();

		// If not in any drive mode that aims
		if (driveMode != DriveMode.AIM) {
			drive.aiming.disable();
		}

		switch (driveMode) {
		case AIM:
			LOGGER.debug("AIM DRIVE-CAN_SEE_TWO=" + vision.canSeeTwo());
			if (vision.canSeeTwo()) {
				LOGGER.debug("AIM DRIVE: TURNING");
				drive.turnToAngle(vision.getTargetAngle());
			}
			break;

		case VECTOR:
			double turnSpeed = driverstation.getDriveJoystick().getRightStickDistance() * 0.5;
			// @formatter:off
			drive.vectorDrive(driverstation.getDriveJoystick().getLeftStickAngle(), // Field aligned direction
					driverstation.getDriveJoystick().getLeftStickDistance(),        // Robot speed
					turnSpeed * driverstation.getVectorTurnDirection());            // Robot turn speed
			// @formatter:on
			break;

		case CRAB:
			if (driverstation.getDriveJoystick().getLeftStickDistance() < MIN_DRIVE_SPEED) {
				// Don't start driving until commanded speed greater than minimum
				drive.stop();
			} else {
				// @formatter:off
				drive.crabDrive(driverstation.getDriveJoystick().getLeftStickAngle(), // Robot aligned direction
						driverstation.getDriveJoystick().getLeftStickDistance());     // Robot speed
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

			// Only respond to 0, 90, 180 and 270 degree positions from POV input
			if (povAngleDeg % 90 == 0) {
				drive.crabDrive(povAngleDeg * (Math.PI / 180), 0.4);
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
	}

}