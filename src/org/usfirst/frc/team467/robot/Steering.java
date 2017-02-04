package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.*;

/**
 * Class to control steering mechanism on Team467 Robot Uses WPI PID controller
 *
 * There are 2 adjustments that may be necessary in this code to adjust for
 * electronics or mechanical issues.
 *
 * 1. If the steering motors are driving in the wrong direction (due to wiring
 * or gearing changes) - invert the sign of the steering PID - defined in
 * RobotMap.java
 *
 * 2. If the steering sensors are reading in the wrong direction - invert the
 * value read from the sensor by changing the value returned from
 * getSensorValue() to be (RobotMap.STEERING_RANGE -
 * steeringSensor.getAverageValue())
 *
 */
public class Steering {
	private final double LEVELS_PER_ROTATION = 610;

	private static final double MAX_TURNS = 2.0;

	// Sensor used to determine angle
	private AnalogInput steeringSensor;

	// PID Controller object
	private PIDController steeringPID;

	// Steering motor
	private Talon steeringMotor;

	/**
	 * Center point of this steering motor. This is the value read from the
	 * sensor when the wheels are in the normal (zero turn) forward position
	 */
	private double steeringCenter;

	class SteeringPIDSource implements PIDSource {
		public double pidGet() {
			return (getSensorValue());
		}

		public void setPIDSourceType(PIDSourceType pidSource) {
			// Don't set source type
		}

		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}
	}

	/**
	 * Constructor for steering subsystem
	 *
	 * @param pID
	 *            - From the PIDvalues array
	 * @param motor
	 *            - motor channel
	 * @param sensor
	 *            - analog sensor channel
	 * @param center
	 *            - sensor reading when wheels point forward
	 */
	Steering(PID pID, int motor, int sensor, double center) {
		// Make steering motor
		steeringMotor = new Talon(motor);

		// Make steering sensor
		steeringSensor = new AnalogInput(sensor);

		// Set steering center
		steeringCenter = center;

		// Make PID Controller
		steeringPID = new PIDController(pID.p, pID.i, pID.d, new SteeringPIDSource(), steeringMotor);

		// Set PID Controller settings
		steeringPID.setInputRange(0.0, RobotMap.STEERING_RANGE);
		steeringPID.setSetpoint(steeringCenter);
		steeringPID.setContinuous(false);
		steeringPID.enable();
	}

	/**
	 * Enables the PID for the steering.
	 */
	public void enablePID() {
		steeringPID.enable();
	}

	/**
	 * Disables the PID for the steering.
	 */
	public void disablePID() {
		steeringPID.disable();
		steeringMotor.set(0.0);
	}

	/**
	 * @return - setpoint of the PID controller
	 */
	public double getSetPoint() {
		return steeringPID.getSetpoint();
	}

	/**
	 * @return The setPoint in radians
	 */
	public double getSetAngle() {
		return (getSetPoint() * (Math.PI * 2) / LEVELS_PER_ROTATION);
	}

	/**
	 * @return The absolute difference between the setPoint and sensorAngle in
	 *         radians
	 */
	public double getAngleDelta() {
		return Math.abs(getSetPoint() - steeringSensor.getAverageValue()) * (Math.PI * 2) / LEVELS_PER_ROTATION;
	}

	/**
	 * Get the Talon motor of this steering object
	 *
	 * @return
	 */
	public Talon getMotor() {
		return steeringMotor;
	}

	/**
	 * Get the sensor angle. Implements the steering center point to give an
	 * angle accurate to the robot's alignment.
	 *
	 * @return - steering angle
	 */
	public double getSteeringAngle() {
		double sensor = steeringSensor.getAverageValue() - steeringCenter;

		double output = sensor * (Math.PI * 2) / LEVELS_PER_ROTATION;

		if (steeringSensor.getChannel() == RobotMap.FRONT_LEFT_STEERING_SENSOR_CHANNEL) {
			SmartDashboard.putNumber("Front-Left Angle", output);
		} else if (steeringSensor.getChannel() == RobotMap.FRONT_RIGHT_STEERING_SENSOR_CHANNEL) {
			SmartDashboard.putNumber("Front-Right Angle", output);
		} else if (steeringSensor.getChannel() == RobotMap.BACK_LEFT_STEERING_SENSOR_CHANNEL) {
			SmartDashboard.putNumber("Back-Left Angle", output);
		} else if (steeringSensor.getChannel() == RobotMap.BACK_RIGHT_STEERING_SENSOR_CHANNEL) {
			SmartDashboard.putNumber("Back-Right Angle", output);
		}
		return output;
	}

	public static double getMaxTurns() {
		return MAX_TURNS;
	}

	/**
	 * Print steering parameters
	 */
	public void printSteeringParameters() {
		System.out.print("Steering:");
		System.out.print(" P: " + steeringPID.getP());
		System.out.print(" I: " + steeringPID.getI());
		System.out.print(" D: " + steeringPID.getD());
		System.out.print(" M:" + steeringCenter);
		System.out.print(" V:" + steeringSensor.getAverageValue());
		System.out.print(" S: " + steeringPID.getSetpoint());
		System.out.println();
	}

	/**
	 * Get directly the value of the sensor
	 *
	 * @return The sensor value, read from 0 to RobotMap.STEERING_RANGE
	 */
	public double getSensorValue() {
		return steeringSensor.getAverageValue();
	}

	/**
	 * Set angle of front steering. A value of 0.0 corresponds to normally
	 * forward position.
	 *
	 * @param requestedAngle
	 *            - any value between -PI and +PI
	 */
	public void setAngle(final double requestedAngle) {
		// Current angle in full radians (i.e.-6pi to 6pi)
		final double sensorAngle = getSteeringAngle();

		// Translates input angle to closest full rotation to sensor angle
		double outputAngle = requestedAngle;
		while ((outputAngle - sensorAngle) > Math.PI) {
			outputAngle -= Math.PI * 2;
		}
		while ((outputAngle - sensorAngle) < -Math.PI) {
			outputAngle += Math.PI * 2;
		}

		setAbsoluteAngle(outputAngle);
	}

	/**
	 * Set absolute angle of front steering. A value of 0.0 corresponds to
	 * normally forward position with the rotation sensor in the center position
	 * also.
	 *
	 * @param requestedAngle
	 *            - any value between -PI and +PI
	 */
	public void setAbsoluteAngle(double requestedAngle) {
		double outputAngle = requestedAngle;

		// Limit range to -6PI to +6PI
		outputAngle = limitRange(outputAngle);

		// Calculate desired setpoint for PID based on known center position and
		// requested angle
		int setPoint = (int) (steeringCenter + (outputAngle * LEVELS_PER_ROTATION / (Math.PI * 2)));

		steeringPID.setSetpoint(setPoint);
	}

	/**
	 * Limit range to -6PI to +6PI
	 *
	 * @param angle
	 * @return
	 */
	private double limitRange(double angle) {
		while (angle > Math.PI * 2 * MAX_TURNS) {
			angle -= Math.PI * 2;
		}
		while (angle < Math.PI * 2 * -MAX_TURNS) {
			angle += Math.PI * 2;
		}
		return angle;
	}

	/**
	 * Change the center point of this steering motor
	 *
	 * @param center
	 */
	public void setCenter(double center) {
		steeringCenter = center;
	}

}
