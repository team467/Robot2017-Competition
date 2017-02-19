package org.usfirst.frc.team467.robot;
import org.usfirst.frc.team467.robot.PID;

/**
 *
 */
public class RobotMap {
	// Global robot constants

	public static final boolean ROBOT_2015 = true;  // set to true for 2015 chassis
	
	// The maximum revolutions per minute (RPM) of a wheel when in speed control mode.
	public static final double MAX_SPEED = 300.0;

	//
	// Array IDs
	//

	// Steering motor ids in array (DO NOT ALTER)
	public static final int FRONT_LEFT = 0;
	public static final int BACK_LEFT = 1;
	public static final int BACK_RIGHT = 2;
	public static final int FRONT_RIGHT = 3;

	//
	// PWM IDs
	//
	
	// Steering motor channels - Sparks, roboRio
	public static final int FRONT_LEFT_STEERING_MOTOR_CHANNEL = 0;	//Spark #1 PWM	
	public static final int BACK_LEFT_STEERING_MOTOR_CHANNEL = 1;	//Spark #2 PWM
	public static final int BACK_RIGHT_STEERING_MOTOR_CHANNEL = 2;	//Spark #3 PWM
	public static final int FRONT_RIGHT_STEERING_MOTOR_CHANNEL = 3;	//Spark #4 PWMs

	//
	// CAN IDs
	//

	// Drive motors - TalonSRX
	public static final int FRONT_LEFT_MOTOR_CHANNEL = 1;	//TalonSRX #3
	public static final int BACK_LEFT_MOTOR_CHANNEL = 2;	//TalonSRX #1
	public static final int BACK_RIGHT_MOTOR_CHANNEL = 3;	//TalonSRX #2
	public static final int FRONT_RIGHT_MOTOR_CHANNEL = 4;	//TalonSRX #4

	// Game pieces motor channels
	public static final int AGITATOR_MOTOR = 11;	//Spike #1
    public static final int SHOOTER_MOTOR_1 = 12;	//TalonSRX #5
    public static final int SHOOTER_MOTOR_2 = 13;	//TalonSRX #6
    public static final int CLIMBER_MOTOR_1 = 14;	//Spark #5
    public static final int CLIMBER_MOTOR_2 = 15;	//SPark #6
    public static final int GEAR_MOTOR = 16;
    public static final int INTAKE_MOTOR = 17;
 

	// @formatter:off
	public static final int[] DRIVING_MOTOR_CHANNELS = { 
			FRONT_LEFT_MOTOR_CHANNEL, 
			FRONT_RIGHT_MOTOR_CHANNEL,
			BACK_LEFT_MOTOR_CHANNEL, 
			BACK_RIGHT_MOTOR_CHANNEL 
			};

	// Invert the drive motors to allow for wiring.
	public static final boolean FRONT_LEFT_DRIVE_INVERT = false;
	public static final boolean FRONT_RIGHT_DRIVE_INVERT = true;
	public static final boolean BACK_LEFT_DRIVE_INVERT = false;
	public static final boolean BACK_RIGHT_DRIVE_INVERT = true;

	public static final boolean[] IS_DRIVE_MOTOR_INVERTED = { 
			FRONT_LEFT_DRIVE_INVERT, 
			FRONT_RIGHT_DRIVE_INVERT,
			BACK_LEFT_DRIVE_INVERT, 
			BACK_RIGHT_DRIVE_INVERT 
			};
	// @formatter:on

	//
	// Digital Inputs
	//

	//
	// Analog Inputs
	//

    // Steering sensors - roboRio
    public static final int FRONT_LEFT_STEERING_SENSOR_CHANNEL = 0;	//Aln2 Steering sensor #3	
    public static final int BACK_LEFT_STEERING_SENSOR_CHANNEL = 1;	//Aln0 Steering sensor #1
    public static final int BACK_RIGHT_STEERING_SENSOR_CHANNEL = 2;	//Aln1 Steering sensor #2
    public static final int FRONT_RIGHT_STEERING_SENSOR_CHANNEL = 3;//Aln3 Steering sensor #4
		
	//
	// Robot Dimensions
	//

	// Length is front to back, Width side to side
	public static final double LENGTH = 31.5; // inches btw the wheels
	public static final double WIDTH = 18.5; // inches btw the wheels

	// Steering motor constant array
	// @formatter:off
	public static final int[] STEERING_MOTOR_CHANNELS = { 
			RobotMap.FRONT_LEFT_STEERING_MOTOR_CHANNEL,
			RobotMap.FRONT_RIGHT_STEERING_MOTOR_CHANNEL, 
			RobotMap.BACK_LEFT_STEERING_MOTOR_CHANNEL,
			RobotMap.BACK_RIGHT_STEERING_MOTOR_CHANNEL 
			};

	// Steering sensor constant array
	public static final int[] STEERING_SENSOR_CHANNELS = { 
			RobotMap.FRONT_LEFT_STEERING_SENSOR_CHANNEL,
			RobotMap.FRONT_RIGHT_STEERING_SENSOR_CHANNEL, 
			RobotMap.BACK_LEFT_STEERING_SENSOR_CHANNEL,
			RobotMap.BACK_RIGHT_STEERING_SENSOR_CHANNEL 
			};

	// Data keys (names used when saving centers to robot)
	public static final String[] STEERING_KEYS = new String[] { 
			"FrontLeft", 
			"FrontRight", 
			"BackLeft", 
			"BackRight" 
			};
	// @formatter:on
	public static final int GEAR_SENSOR = 5;

	/**
	 * Number of increments on the steering sensor (12-bit A/D)
	 */
	public static final double STEERING_RANGE = 3662;

	// PID array
	// @formatter:off
	public static final PID[] PIDvalues = { 
			new PID(-0.013, 0.0, 0.0), // Front Left PID values
			new PID(-0.013, 0.0, 0.0), // Front Right PID values
			new PID(-0.013, 0.0, 0.0), // Back Left PID values
			new PID(-0.015, 0.0, 0.0), // Back Right PID values
	};

	public static final PID[] SpeedPIDFvalues = { 
			new PID(0.50, 0.0036, 18.00, 2.35),  // Front Left PID values
			new PID(1.35, 0.0027, 168.75, 1.90), // Front Right PID values
			new PID(1.35, 0.0020, 168.75, 2.00), // Back Left PID values
			new PID(1.35, 0.0027, 168.75, 2.00), // Back Right PID values
	};
	// @formatter:on

	public static final PID GEAR_PID = new PID(0.1, 0.01, 0.001, 0);

	// TODO: need actual motor channels
	public static final int[] CLIMBER_MOTOR_CHANNELS = { 6, 7 };
	// TODO: need actual motor channels
	public static final int BALL_INTAKE_MOTOR_CHANNEL = 8;
	// TODO: need actual motor channels
	public static final int GEAR_DEVICE_MOTOR_CHANNEL = 9;
	// TODO: need actual input device channel
	public static final int GEAR_SENSOR_CHANNEL = 5;

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
