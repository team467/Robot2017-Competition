/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Drive extends RobotDrive {
	// TODO: DEfine logger


	// Single instance of this class
	private static Drive instance = null;

	// Data storage object
	private DataStorage data;

	// Private constructor
	private Drive() {
		super(1,2); // Need to specify the motor channels.

		// TODO: Define and initialize motors.

		// Make objects
		data = DataStorage.getInstance();

	}
	
	private void initMotor(CANTalon talon) {
		// TODO: Set the default Talon parameters
	}

	/**
	 * Gets the single instance of this class.
	 *
	 * @return The single instance.
	 */
	public static Drive getInstance() {
		// TODO: Update if constructure changes
		if (instance == null) {
			// First usage - create Drive object
			instance = new Drive();
		}
		return instance;
	}

	public void setPIDF(double p, double i, double d, double f){
		// TODO: Set the PIDF of the talons. Assumes the same values for all motors
	}

	/**
	 * Sets the motors to drive in speed mode.
	 *
	 * @return Successful or not
	 */
	public boolean setSpeedMode() {
		// TODO: Set the motors in speed control mode. Check RobotMap to see if it is enabled for this robot.
		return false; // Update
	}

	/**
	 * Sets the motors to drive in percent of voltage mode. Default for when the speed sensors are not working.
	 */
	public boolean setPercentVoltageBusMode() {
		// TODO: Set motors for percent voltage bus mode
		return true;
	}

	/**
	 * Sets the motors to drive in position mode.
	 *
	 * @return Successful or not
	 */
	public boolean setPositionMode() {
		// TODO: Set motors for percent voltage bus mode Check RobotMap to see if it is enabled for this robot.
		return false;
	}

	private void initMotorForFollowerMode(CANTalon master, CANTalon slave) {
		// TODO: Slave motors to a single master
	}

	public void logClosedLoopErrors() {
		// TODO Log closed loop errors in the Speed/Position control loop
	}

	public TalonControlMode getControlMode() {
		// TODO: Update to return the currently used control mode
		return TalonControlMode.PercentVbus;
	}

	/**
	 * Takes the drive out of position mode back into its default drive mode.
	 */
	public void setDefaultDriveMode() {
		// TODO: Check the Robot Map and set the system to use the speed controllers if enabled there
	}

	/**
	 * Drives each of the four wheels at different speeds using invert constants to account for wiring.
	 *
	 * @param left
	 * 			Speed or Distance value for left wheels
	 * @param right
	 * 			Speed or Distance value for right wheels
	 */
	private void go(double left, double right) {
		// TODO: Check to make sure all motors exist. If not throw a null pointer exception

		//TODO: Set the speeds

		if (m_safetyHelper != null) {
			m_safetyHelper.feed();
		}
	}

	private TalonControlMode controlMode = TalonControlMode.PercentVbus;
	
	/**
	 * 
	 * @param speed		the forward speed or distance
	 * @param angle		turn angle in radians
	 */
	public void goWithAngle(double speedOrDistance, double angle) {
		double right = speedOrDistance;
		double left = speedOrDistance;
		double arcLength = 0.0;

		// double inchesPerMilisecond = RobotMap.MAX_SPEED * RobotMap.WHEELPOD_CIRCUMFERENCE / 60 / 1000;
		// Wheel base width = 22.5
		// Wheel circumference = 19.74
		// at 480 RPM, 3.16 inches per 20 ms cycle
		// Max turn = Pi * (RobotMap.WHEEL_BASE_WIDTH / 2) ~ 35.325	inches
		
		if (controlMode == TalonControlMode.Position) {
			
			double radius = RobotMap.WHEEL_BASE_WIDTH / 2;  // in inches			
			if (angle > Math.PI) {
				arcLength = (angle - 2 * Math.PI) * radius;
			} else {
				arcLength = angle * radius;
			}
			
			// In position mode, only reduce the one side so that position is set to the farthest it will go.
			if (speedOrDistance != 0) {
				if (arcLength > 0) {
					// right back
					right -= (2*arcLength);
				} else {
					// Left back, but arcLength is negative, so add
					left += (2*arcLength);
				}
			} else {
				// Exception is if the distance is set to 0, turn in place
				left += arcLength;
				right -= arcLength;
			}
		} else { // Speed or percent of voltage mode
			
			// The arc length is divided by the circumference of the robot to ensure the turn value is between -1.0 and 1.0.
			if (angle > Math.PI) {
				arcLength = (angle / Math.PI - 2);
			} else {
				arcLength = angle / Math.PI;
			}
			
			left += arcLength;
			right -= arcLength;

			// In speed or postion mode, the setting cannot be above 1.0 or below -1.0
			if (left > 1.0) {
				right -= (left - 1.0);
				left = 1.0;
				if (right < -1.0) {
					right = -1.0;
				}
			} else if (right > 1.0) {
				left -= (right - 1.0);
				right = 1.0;
				if (left < -1.0) {
					left = -1.0;
				}
			} else if (left < -1.0) {
				right -= (left + 1.0);
				left = -1.0;
				if (right > 1.0) {
					right = 1.0;
				}
			} else if (right < -1.0) {
				left -= (right + 1.0);
				right = -1.0;
				if (left > 1.0) {
					left = 1.0;
				}
			}
		}
				
		go(left,right);
	}

	public void turnByPosition(double degrees) {
		// TODO: Turns in place to the specified angle from center using position mode
	}

	/**
	 * Turns to specified angle according to gyro
	 *
	 * @param angle
	 *            in degrees
	 *
	 * @return True when pointing at the angle
	 */
	public boolean turnToAngle(double angle) {
		//TODO: Uses the gyro to determine the angle, correcting until it points the correct direction
		return false; // Put in test to determine if on target
	}

	/**
	 * Single point of entry for any speed adjustments made to the robot. This can be used for:
	 * - limiting rate of acceleration or deceleration
	 * - adjusting speed parameter based on Talon control mode (Speed, Position etc.)
	 *
	 * @param speedOrDistance
	 *            input speed or distance for robot
	 *            	speed will be in range -1.0 to 1.0
	 *              distance is measured in feet
	 * @return returns adjusted speed
	 */
	private double adjustSpeedOrDistance(double speedOrDistance) {
		// TODO: Check the control mode to do the right adjustment
		// TODO: Adjust speed value based on the robot's max speed setting
		// TODO: Adjust position value based on the circumference of the wheel
		return 0;
	}

	public void zeroPosition() {
		// TODO: Zero the encoder and current position so that next position move is relative to current position
	}

	/**
	 * Gets the error value from the motor controller.
	 *
	 * @return the current error
	 */
	public double error() {
		// TODO: Get the error from the motor sensor. If in position mode, change into a distance measurement based onte the number of codes per revolution
		return 0;
	}

	public boolean checkSensor() {
		// TODO: Check the sensors to make sure they are reading the values specified. For example if I set the speed at 100, the value return from get speed should be 100
		// Need separate checks for speed and position.

		// All some time for the motors to get up to speed
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Moves a distance if in position mode.
	 *
	 * @param distance
	 *            the target distance in feet.
	 * @return boolean true if move is complete
	 */
	public void moveDistance(double distance) {
		// TODO: CHecks to see if the robot is in postion mode, and if so, converts the distance to revolutions and moves
	}

	public boolean isAtDistance() {
		// TODO: Checks to see if the robot is at the desired postion, plus or minus the allowed error
		return true;
	}



	/**
	 * Crab Drive
	 *
	 * @param angle
	 *            value corresponding to the field direction to move in
	 * @param speed
	 *            Speed to drive at
	 */
	public void crabDrive(double angle, double speed) {
		// TODO: Adjust the drive based on the angle

	}

	public boolean isStopped(){
		// TODO: Check to see if the robot is stopped
		return false;
	}

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	public double absoluteDistanceMoved() {
		// TODO: returns the amount of distance moved based on the the position of the talon sensors nad the wheel circumerence
		return 0;
	}

	public double getTurnError() {
		// TODO Get the absolute error when turning
		return 0;
	}

	/**
	 * Does not drive drive motors and keeps steering angle at previous position.
	 */
	public void stop() {
		//TODO: Stop all motors 
	}

	/**
	 * Individually controls a specific driving motor
	 *
	 * @param speed
	 *            Speed to drive at
	 * @param steeringId
	 *            Id of driving motor to drive
	 */
	public void individualWheelDrive(double speed, int steeringId) {
		// TODO: Call drive with a speed only for the specified motor, with all other motors stopped.
	}

}
