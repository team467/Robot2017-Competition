/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 */
public class RobotMap {
	// Global robot constants
	/**
	 * The maximum revolutions per minute (RPM) of a wheel when in speed control mode.
	 */
	public static final double MAX_SPEED = 300.0;

	//
	// Array IDs
	//

	// Steering motor ids in array (DO NOT ALTER)
	public static final int FRONT_LEFT = 0;
	public static final int FRONT_RIGHT = 1;
	public static final int BACK_LEFT = 2;
	public static final int BACK_RIGHT = 3;

	//
	// PWM IDs
	//

	// Steering motors - Talon, roboRio
	public static final int FRONT_LEFT_STEERING_MOTOR_CHANNEL = 0;
	public static final int FRONT_RIGHT_STEERING_MOTOR_CHANNEL = 1;
	public static final int BACK_LEFT_STEERING_MOTOR_CHANNEL = 2;
	public static final int BACK_RIGHT_STEERING_MOTOR_CHANNEL = 3;

	//
	// CAN IDs
	//

	// Drive motors - CAN, CANTalons
	public static final int FRONT_RIGHT_MOTOR_CHANNEL = 1;
	public static final int FRONT_LEFT_MOTOR_CHANNEL = 2;
	public static final int BACK_LEFT_MOTOR_CHANNEL = 3;
	public static final int BACK_RIGHT_MOTOR_CHANNEL = 4;

	public static final int[] DRIVING_MOTOR_CHANNELS = { FRONT_LEFT_MOTOR_CHANNEL, FRONT_RIGHT_MOTOR_CHANNEL,
			BACK_LEFT_MOTOR_CHANNEL, BACK_RIGHT_MOTOR_CHANNEL };

	// Invert the drive motors to allow for wiring.
	public static final boolean FRONT_LEFT_DRIVE_INVERT = false;
	public static final boolean FRONT_RIGHT_DRIVE_INVERT = true;
	public static final boolean BACK_LEFT_DRIVE_INVERT = false;
	public static final boolean BACK_RIGHT_DRIVE_INVERT = true;

	public static final boolean[] IS_DRIVE_MOTOR_INVERTED = { FRONT_LEFT_DRIVE_INVERT, FRONT_RIGHT_DRIVE_INVERT,
			BACK_LEFT_DRIVE_INVERT, BACK_RIGHT_DRIVE_INVERT };

	//
	// Digital Inputs
	//

	//
	// Analog Inputs
	//

	// Steering sensors - roboRio
	public static final int FRONT_LEFT_STEERING_SENSOR_CHANNEL = 0;
	public static final int FRONT_RIGHT_STEERING_SENSOR_CHANNEL = 1;
	public static final int BACK_LEFT_STEERING_SENSOR_CHANNEL = 2;
	public static final int BACK_RIGHT_STEERING_SENSOR_CHANNEL = 3;

	//
	// Robot Dimensions
	//

	// Length is front to back, Width side to side
	// Measured on 2015 robot
	public static final double LENGTH = 31.5; // inches btw the wheels
	public static final double WIDTH = 18.5; // inches btw the wheels

	// Steering motor constant array
	public static final int[] STEERING_MOTOR_CHANNELS = { RobotMap.FRONT_LEFT_STEERING_MOTOR_CHANNEL,
			RobotMap.FRONT_RIGHT_STEERING_MOTOR_CHANNEL, RobotMap.BACK_LEFT_STEERING_MOTOR_CHANNEL,
			RobotMap.BACK_RIGHT_STEERING_MOTOR_CHANNEL };

	// Steering sensor constant array
	public static final int[] STEERING_SENSOR_CHANNELS = { RobotMap.FRONT_LEFT_STEERING_SENSOR_CHANNEL,
			RobotMap.FRONT_RIGHT_STEERING_SENSOR_CHANNEL, RobotMap.BACK_LEFT_STEERING_SENSOR_CHANNEL,
			RobotMap.BACK_RIGHT_STEERING_SENSOR_CHANNEL };

	// Data keys (names used when saving centers to robot)
	public static final String[] STEERING_KEYS = new String[] { "FrontLeft", "FrontRight", "BackLeft", "BackRight" };

	/**
	 * Number of increments on the steering sensor (12-bit A/D)
	 */
	public static final double STEERING_RANGE = 4095;

	// PID array
	public static final PID[] PIDvalues = { new PID(-0.013, 0.0, 0.0), // Front
																		// Left
																		// PID
																		// values
			new PID(-0.013, 0.0, 0.0), // Front Right PID values
			new PID(-0.013, 0.0, 0.0), // Back Left PID values
			new PID(-0.015, 0.0, 0.0), // Back Right PID values
	};

	public static final PID[] SpeedPIDFvalues = { new PID(0.50, 0.0036, 18.00, 2.35), // Front
																						// Left
																						// PID
																						// values
			new PID(1.35, 0.0027, 168.75, 1.90), // Front Right PID values
			new PID(1.35, 0.0020, 168.75, 2.00), // Back Left PID values
			new PID(1.35, 0.0027, 168.75, 2.00), // Back Right PID values
	};

	public static final PID GearDevicePID = new PID(0.1, 0.01, 0.001, 0);

	// TODO: need actual motor channels
	public static final int[] CLIMBER_MOTOR_CHANNELS = { 6, 7 };
	// TODO: need actual motor channels
	public static final int BALL_INTAKE_MOTOR_CHANNEL = 8;
	// TODO: need actual motor channels
	public static final int GEAR_DEVICE_MOTOR_CHANNEL = 9;
	// TODO: need actual input device channel
	public static final int GEAR_SENSOR_CHANNEL = 5;

	/* distance from the center of the robot to the wheelpod in inches */
	public static final double wheelpodRadius = 18.29;
	/* diameter of wheelpod in inches */
	public static final double wheeldiameter = 6;

	/**
	 * The number of encoder ticks per one revolution of the wheel. This is used for correctly determining RPM and position.
	 */
	public static final int WHEELPOD_ENCODER_CODES_PER_REVOLUTION = 256;

	/**
	 * Used to ensure that all Talon SRX outputs are relative to a fixed value.
	 * If the available voltage is below the nominal and a value about that is
	 * requested, the output will be 100%.
	 */
	public static final double NOMINAL_BATTERY_VOLTAGE = 12.0;

	/**
	 * The circumference of the wheels for use in determining distance in
	 * position mode.
	 */
	public static final double WHEELPOD_CIRCUMFERENCE = 18.85;

	public static final double MIN_DRIVE_SPEED = 0.1;
}
