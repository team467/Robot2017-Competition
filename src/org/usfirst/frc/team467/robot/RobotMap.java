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
	 * The maximum revolutions per minute (RPM) of a wheel when in speed control
	 * mode.
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

	public static final PIDF[] SpeedPIDFvalues = {
			new PIDF(0.50, 0.0036, 18.00, 2.35), // Front Left PID values
			new PIDF(1.35, 0.0027, 168.75, 1.90), // Front Right PID values
			new PIDF(1.35, 0.0020, 168.75, 2.00), // Back Left PID values
			new PIDF(1.35, 0.0027, 168.75, 2.00), // Back Right PID values
	};
	
	/* distance from the center of the robot to the wheelpod in inches */
	public static final double wheelpodRadius = 18.29;
	/* diameter of wheelpod in inches */
	public static final double wheeldiameter = 6;
	
	/* multiplier for vector drive */
	public static final double vectorDriveAngularVelocityMultiplier = 0.5;
	public static final double vectorDriveVelocityMultiplier = 44.42;

}
