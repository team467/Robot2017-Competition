package org.usfirst.frc.team467.robot;

/**
 *
 */
public class RobotMap {
	public enum RobotID {
		ROBOT2015, ROBOT2017A, ROBOT2017B
	};

	// Steering motor ids in array (DO NOT ALTER)
	public static final int FRONT_LEFT = 0;
	public static final int BACK_LEFT = 1;
	public static final int BACK_RIGHT = 2;
	public static final int FRONT_RIGHT = 3;

	// Initialize robot map. Returns false if robot ID not defined
	public static void init(RobotID id) {

		// Initialize robot map based on robot ID;
		
		PIDvalues = new PID[4];
		speedPIDFvalues = new PID[4];
		
		switch (id) {
		case ROBOT2015:
			robotID = RobotID.ROBOT2015;
			steeringMotorChannel = new int[] { 0, 1, 2, 3 };
			steeringMotorType = new Steering.PWMType[] { Steering.PWMType.TALON, Steering.PWMType.TALON, Steering.PWMType.TALON,
					Steering.PWMType.TALON };
			steeringSensorChannel = new int[] { 0, 1, 2, 3 };
			driveMotorChannel = new int[] { 2, 1, 3, 4 };
			isDriveMotorInverted = new boolean[] { false, true, false, true };
			length = 31.5; // front to back - in inches
			width = 18.5; // side to side in inches
			PIDvalues[FRONT_LEFT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[FRONT_RIGHT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[BACK_LEFT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[BACK_RIGHT] = new PID(-0.015, 0.0, 0.0);
			speedPIDFvalues[FRONT_LEFT] = new PID(0.50, 0.0036, 18.00, 2.35);
			speedPIDFvalues[FRONT_RIGHT] = new PID(1.35, 0.0027, 168.75, 1.90);
			speedPIDFvalues[BACK_LEFT] = new PID(1.35, 0.0020, 168.75, 2.00);
			speedPIDFvalues[BACK_RIGHT] = new PID(1.35, 0.0027, 168.75, 2.00);
			break;

		case ROBOT2017A:
			robotID = RobotID.ROBOT2017A;
			steeringMotorChannel = new int[] { 0, 3, 2, 1 };
			steeringMotorType = new Steering.PWMType[] { Steering.PWMType.SPARK, Steering.PWMType.SPARK, Steering.PWMType.SPARK,
					Steering.PWMType.SPARK };
			steeringSensorChannel = new int[] { 0, 3, 2, 1 };
			driveMotorChannel = new int[] { 1, 4, 3, 2 };
			isDriveMotorInverted = new boolean[] { false, true, false, true };
			length = 18.5; // front to back - in inches
			width = 22.5; // side to side in inches
			PIDvalues[FRONT_LEFT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[FRONT_RIGHT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[BACK_LEFT] = new PID(-0.013, 0.0, 0.0);
			PIDvalues[BACK_RIGHT] = new PID(-0.015, 0.0, 0.0);
			speedPIDFvalues[FRONT_LEFT] = new PID(0.50, 0.0036, 18.00, 2.35);
			speedPIDFvalues[FRONT_RIGHT] = new PID(1.35, 0.0027, 168.75, 1.90);
			speedPIDFvalues[BACK_LEFT] = new PID(1.35, 0.0020, 168.75, 2.00);
			speedPIDFvalues[BACK_RIGHT] = new PID(1.35, 0.0027, 168.75, 2.00);
			hasGear = true;
			hasClimber = true;
			hasBallIntake = true;
			hasShooter = true;
			break;

		default:
			System.out.println("Robot ID not defined");
			break;
		}
	}

	// Global robot constants

	public static RobotID robotID;

	// The maximum revolutions per minute (RPM) of a wheel when in speed control mode.
	public static final double MAX_SPEED = 300.0;

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

	// Game pieces motor channels

	// @formatter:off

	// Data keys (names used when saving centers to robot)
	public static final String[] STEERING_KEYS = new String[] { 
			"FrontLeft", 
			"FrontRight",
			"BackLeft",
			"BackRight"
	};
	// @formatter:on

	// Number of increments on the steering sensor (12-bit A/D)
	public static final double STEERING_RANGE = 4095;

	// PID array
	public static PID[] PIDvalues;
	public static PID[] speedPIDFvalues;

	// Game Pieces

	// Gear
	public static boolean hasGear = false; // default to no gear mechanism
	public static final PID GEAR_PID = new PID(0.1, 0.01, 0.001, 0);
	// TODO: need actual motor channels
	public static final int GEAR_DEVICE_MOTOR_CHANNEL = 9;
	// TODO: need actual input device channel
	public static final int GEAR_SENSOR_CHANNEL = 5;
	// TODO confirm channel for these pieces
	public static final int GEAR_MOTOR = 16;
	public static final int GEAR_SENSOR = 5;

	// Climber
	public static boolean hasClimber = false; // default to no climber
	// TODO: need actual motor channels
	// TODO - test climber
	public static final int CLIMBER_MOTOR_1 = 5;
	public static final int CLIMBER_MOTOR_2 = 6;
	public static final int[] CLIMBER_MOTOR_CHANNELS = { 6, 7 };

	// Ball Intake
	public static boolean hasBallIntake = false;
	// TODO: need actual motor channels
	public static final int BALL_INTAKE_MOTOR_CHANNEL = 8;
	public static final int INTAKE_MOTOR = 17;

	// Shooter
	public static boolean hasShooter = false;
	// TODO - test shooter motor
	public static final int SHOOTER_MOTOR_1 = 5;
	public static final int SHOOTER_MOTOR_2 = 6;
	public static final int AGITATOR_MOTOR = 11;

	// The number of encoder ticks per one revolution of the wheel. This is used for correctly determining RPM and position.
	public static final int WHEELPOD_ENCODER_CODES_PER_REVOLUTION = 256;

	/**
	 * Used to ensure that all Talon SRX outputs are relative to a fixed value. If the available voltage is below the nominal and a
	 * value about that is requested, the output will be 100%.
	 */
	public static final double NOMINAL_BATTERY_VOLTAGE = 12.0;

	// The circumference of the wheels for use in determining distance in position mode
	public static final double WHEELPOD_CIRCUMFERENCE = 18.85;

	public static final double MIN_DRIVE_SPEED = 0.1;
}
