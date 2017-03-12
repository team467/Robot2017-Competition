package org.usfirst.frc.team467.robot;

/**
 *
 */
public class RobotMap {
	public enum RobotID {
		ROBOT2015, MIRACLE, MISTAKE
	};

	// Steering motor ids in array (DO NOT ALTER)
	public static final int FRONT_LEFT = 0;
	public static final int FRONT_RIGHT = 1;
	public static final int BACK_LEFT = 2;
	public static final int BACK_RIGHT = 3;

	// Initialize robot map. Returns false if robot ID not defined
	public static void init(RobotID id) {

		// Initialize robot map based on robot ID;

		PIDvalues = new PID[4];
		speedPIDFvalues = new PID[4];

		switch (id) {
		case ROBOT2015:
			robotID = RobotID.ROBOT2015;
            useSpeedControllers = false;
			steeringMotorChannel = new int[] { 0, 1, 2, 3 };
			steeringMotorType = new Steering.PWMType[] { Steering.PWMType.TALON, Steering.PWMType.TALON,
					Steering.PWMType.TALON, Steering.PWMType.TALON };
			steeringSensorChannel = new int[] { 0, 1, 2, 3 };
			driveMotorChannel = new int[] { 2, 1, 3, 4 };
			isDriveMotorInverted = new boolean[] { false, true, false, true };
			length = 31.5; // front to back - in inches
			width = 18.5; // side to side in inches
			CamToCenterWidthInches = 4.5; // TODO Get measurement for other robots
			CamToCenterLengthInches = 17; // TODO Get measurement for other robots
			MAX_SPEED = 300.0;
			PIDvalues[FRONT_LEFT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[FRONT_RIGHT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[BACK_LEFT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[BACK_RIGHT] = new PID(-0.015, 0.0, 0.0);
			speedPIDFvalues[FRONT_LEFT] = new PID(0.50, 0.0036, 18.00, 2.35);
			speedPIDFvalues[FRONT_RIGHT] = new PID(1.35, 0.0027, 168.75, 1.90);
			speedPIDFvalues[BACK_LEFT] = new PID(1.35, 0.0020, 168.75, 2.00);
			speedPIDFvalues[BACK_RIGHT] = new PID(1.35, 0.0027, 168.75, 2.00);
			break;

		case MIRACLE:
			robotID = RobotID.MIRACLE;
            useSpeedControllers = false;
			steeringMotorChannel = new int[] { 2, 1, 3, 0 };
			steeringMotorType = new Steering.PWMType[] { Steering.PWMType.TALON, Steering.PWMType.TALON,
					Steering.PWMType.TALON, Steering.PWMType.TALON };
			steeringSensorChannel = new int[] { 2, 1, 3, 0 };
			driveMotorChannel = new int[] { 3, 2, 4, 1 };
			isDriveMotorInverted = new boolean[] { false, true, false, true };
			length = 18.5; // front to back - in inches
			width = 22.5; // side to side in inches
			MAX_SPEED = 450.0;
			PIDvalues[FRONT_LEFT] = new PID(0.013, 0.0, 0.0);
			PIDvalues[FRONT_RIGHT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[BACK_LEFT] = new PID(0.013, 0.0, 0.0);
			PIDvalues[BACK_RIGHT] = new PID(0.015, 0.0, 0.0);;
			speedPIDFvalues[FRONT_LEFT] = new PID(0.50, 0.0036, 18.00, 2.35);
			speedPIDFvalues[FRONT_RIGHT] = new PID(1.35, 0.0027, 168.75, 1.90);
			speedPIDFvalues[BACK_LEFT] = new PID(1.35, 0.0020, 168.75, 2.00);
			speedPIDFvalues[BACK_RIGHT] = new PID(1.35, 0.0027, 168.75, 2.00);
			break;
			
		case MISTAKE:
            robotID = id;
            useSpeedControllers = true;
            steeringMotorChannel = new int[] { 2, 1, 3, 0 };
            steeringMotorType = new Steering.PWMType[] { Steering.PWMType.TALON, Steering.PWMType.TALON,
                    Steering.PWMType.TALON, Steering.PWMType.TALON };
            steeringSensorChannel = new int[] { 2, 1, 3, 0 };
            driveMotorChannel = new int[] { 3, 2, 4, 1 };
            isDriveMotorInverted = new boolean[] { false, true, false, true };
            length = 18.5; // front to back - in inches
            width = 22.5; // side to side in inches
            MAX_SPEED = 450.0;
            PIDvalues[FRONT_LEFT] = new PID(-0.013, 0.0, 0.0);
            PIDvalues[FRONT_RIGHT] = new PID(-0.013, 0.0, 0.0);
            PIDvalues[BACK_LEFT] = new PID(-0.013, 0.0, 0.0);
            PIDvalues[BACK_RIGHT] = new PID(-0.015, 0.0, 0.0);
            speedPIDFvalues[FRONT_LEFT] = new PID(0.50, 0.0036, 18.00, 2.35);
            speedPIDFvalues[FRONT_RIGHT] = new PID(1.35, 0.0027, 168.75, 1.90);
            speedPIDFvalues[BACK_LEFT] = new PID(1.35, 0.0020, 168.75, 2.00);
            speedPIDFvalues[BACK_RIGHT] = new PID(1.35, 0.0027, 168.75, 2.00);
            break;
		default:
			System.out.println("Robot ID not defined");
			break;
		}
	}

	// Global robot constants

	public static RobotID robotID;
	
	public static boolean useSpeedControllers;

	// Set to true to use LSM9DS1 IMU on Raspberry Pi
	// Set to false to use the local ADIS16448 IMU on the Robo Rio
	public static final boolean useRemoteImu = false;

	// The maximum revolutions per minute (RPM) of a wheel when in speed control
	// mode.
	public static double MAX_SPEED;

	// PWM Outputs
	public static int[] steeringMotorChannel;
	public static Steering.PWMType[] steeringMotorType;

	// CAN Outputs
	public static int[] driveMotorChannel;

	// Analog inputs
	public static int[] steeringSensorChannel;

	// for each wheel identify if the driving direction needs to be flipped
	public static boolean[] isDriveMotorInverted;

	// Robot Dimensions
	public static double length;
	public static double width;
	public static double CamToCenterWidthInches;
	public static double CamToCenterLengthInches;

	// Game pieces motor channels

	// @formatter:off

	// Data keys (names used when saving centers to robot)
	public static final String[] STEERING_KEYS = new String[] { "FrontLeft", "FrontRight", "BackLeft", "BackRight" };
	// @formatter:on

	// Number of increments on the steering sensor (12-bit A/D)
	public static final double STEERING_RANGE = 4095;

	// PID array
	public static PID[] PIDvalues;
	public static PID[] speedPIDFvalues;

	// Game Pieces

	// Climber
	public static final int CLIMBER_MOTOR_1 = 5;
	public static final int CLIMBER_MOTOR_2 = 4;
	
	// Gear
	public static final int GEAR_MOTOR = 7;

	// The number of encoder ticks per one revolution of the wheel. This is used
	// for correctly determining RPM and position.
	public static final int WHEELPOD_ENCODER_CODES_PER_REVOLUTION = 256;

	/**
	 * Used to ensure that all Talon SRX outputs are relative to a fixed value.
	 * If the available voltage is below the nominal and a value about that is
	 * requested, the output will be 100%.
	 */
	public static final double NOMINAL_BATTERY_VOLTAGE = 12.0;

	// The circumference of the wheels for use in determining distance in
	// position mode
	public static final double WHEELPOD_CIRCUMFERENCE = 18.85;

	public static final double MIN_DRIVE_SPEED = 0.1;
}