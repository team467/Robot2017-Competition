/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usfirst.frc.team467.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 *
 */
public class Drive extends RobotDrive {
	// Single instance of this class
	private static Drive instance = null;

	// WheelPods for setting modes
	private static WheelPod frontleft;
	private static WheelPod backleft;
	private static WheelPod frontright;
	private static WheelPod backright;

	// Drive control mode
	private TalonControlMode controlMode;

	// Gyroscope
	private Gyrometer gyro;

	private double[] aimingPIDs = { .018, 0.0, 0.06, 0.0 };
	public PIDController aiming;

	// Steering objects
	public Steering[] steering;

	// Data storage object
	private DataStorage data;
	// Angle to turn at when rotating in place - initialized in constructor
	// takes the arctan of width over length in radians
	// Length is the wide side
	private static final double TURN_IN_PLACE_ANGLE = Math.atan(RobotMap.length / RobotMap.width);

	// Private constructor
	private Drive(CANTalon frontLeftMotor, CANTalon backLeftMotor, CANTalon frontRightMotor, CANTalon backRightMotor) {
		super(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);

		// Set default control mode to percentage of voltage bus.
		controlMode = TalonControlMode.PercentVbus;

		// Make objects
		data = DataStorage.getInstance();
		gyro = Gyrometer.getInstance();

		// Make steering array
		steering = new Steering[4];

		// Make all steering objects
		for (int i = 0; i < steering.length; i++) {
			// Read all steering values from saved robot data(Format = (<data
			// key>, <backup value>))
			double steeringCenter = data.getDouble(RobotMap.STEERING_KEYS[i], RobotMap.STEERING_RANGE / 2);

			// Create Steering Object
			steering[i] = new Steering(RobotMap.steeringMotorType[i], RobotMap.PIDvalues[i], RobotMap.steeringMotorChannel[i], RobotMap.steeringSensorChannel[i],
					steeringCenter);
		}

		aiming = new PIDController(aimingPIDs[0], aimingPIDs[1], aimingPIDs[2], aimingPIDs[3], Gyrometer.getInstance(),
				(output) -> {
					if (aiming.isEnabled()) {
						System.out.println("PID Output=" + output);
						turnDrive(output);
					}
				});
		aiming.setInputRange(0, 360); // 4 Gyro units per degree
		aiming.setContinuous(); // 0º and 360º are the same point
		aiming.setOutputRange(-1.0, 1.0); // Max Speed in either direction
		aiming.setAbsoluteTolerance(1.0); // 1 degree tolerance
	}

	/**
	 * Gets the single instance of this class.
	 *
	 * @return The single instance.
	 */
	public static Drive getInstance() {
		if (instance == null) {
			// First usage - create Drive object
			frontleft = new WheelPod(RobotMap.FRONT_LEFT);
			backleft = new WheelPod(RobotMap.BACK_LEFT);
			frontright = new WheelPod(RobotMap.FRONT_RIGHT);
			backright = new WheelPod(RobotMap.BACK_RIGHT);

			instance = new Drive(frontleft.motor(), backleft.motor(), frontright.motor(), backright.motor());
		}
		return instance;
	}

	public void setSpeedMode() {
		controlMode = TalonControlMode.Speed;
		frontleft.speedMode();
		frontright.speedMode();
		backright.speedMode();
		backleft.speedMode();
	}

	public void setPercentVoltageBusMode() {
		controlMode = TalonControlMode.PercentVbus;
		frontleft.percentVoltageBusMode();
		frontright.percentVoltageBusMode();
		backright.percentVoltageBusMode();
		backleft.percentVoltageBusMode();
	}

	/**
	 * Drives each of the four wheels at different speeds using invert constants to account for wiring.
	 *
	 * @param frontLeftSpeed
	 * @param frontRightSpeed
	 * @param backLeftSpeed
	 * @param backRightSpeed
	 */
	private void fourWheelDrive(double frontLeftSpeed, double frontRightSpeed, double backLeftSpeed, double backRightSpeed) {
		// If any of the motors doesn't exist then exit
		if (m_rearLeftMotor == null || m_rearRightMotor == null || m_frontLeftMotor == null || m_frontRightMotor == null) {
			throw new NullPointerException("Null motor provided");
		}

		switch (controlMode) {
		case Speed:
			m_frontLeftMotor.set((RobotMap.isDriveMotorInverted[RobotMap.FRONT_LEFT] ? -1 : 1)
					* limitSpeed(frontLeftSpeed, RobotMap.FRONT_LEFT) * RobotMap.MAX_SPEED);
			m_frontRightMotor.set((RobotMap.isDriveMotorInverted[RobotMap.FRONT_RIGHT] ? -1 : 1)
					* limitSpeed(frontRightSpeed, RobotMap.FRONT_RIGHT) * RobotMap.MAX_SPEED);
			m_rearLeftMotor.set((RobotMap.isDriveMotorInverted[RobotMap.BACK_LEFT] ? -1 : 1)
					* limitSpeed(backLeftSpeed, RobotMap.BACK_LEFT) * RobotMap.MAX_SPEED);
			m_rearRightMotor.set((RobotMap.isDriveMotorInverted[RobotMap.BACK_RIGHT] ? -1 : 1)
					* limitSpeed(backRightSpeed, RobotMap.BACK_RIGHT) * RobotMap.MAX_SPEED);
			break;
		case Voltage:
		case PercentVbus:
		default:
			// System.out.println(frontLeftSpeed);
			m_frontLeftMotor.set((RobotMap.isDriveMotorInverted[RobotMap.FRONT_LEFT] ? -1 : 1)
					* limitSpeed((frontLeftSpeed), RobotMap.FRONT_LEFT));
			m_frontRightMotor.set((RobotMap.isDriveMotorInverted[RobotMap.FRONT_RIGHT] ? -1 : 1)
					* limitSpeed(frontRightSpeed, RobotMap.FRONT_RIGHT));
			m_rearLeftMotor.set(
					(RobotMap.isDriveMotorInverted[RobotMap.BACK_LEFT] ? -1 : 1) * limitSpeed(backLeftSpeed, RobotMap.BACK_LEFT));
			m_rearRightMotor.set((RobotMap.isDriveMotorInverted[RobotMap.BACK_RIGHT] ? -1 : 1)
					* limitSpeed(backRightSpeed, RobotMap.BACK_RIGHT));
		}

		if (m_safetyHelper != null) {
			m_safetyHelper.feed();
		}
	}

	/**
	 * @param frontLeft
	 * @param frontRight
	 * @param backLeft
	 * @param backRight
	 */
	private void fourWheelSteer(double frontLeft, double frontRight, double backLeft, double backRight) {
		// set the angles to steer
		steering[RobotMap.FRONT_LEFT].setAngle(frontLeft);
		steering[RobotMap.FRONT_RIGHT].setAngle(frontRight);
		steering[RobotMap.BACK_LEFT].setAngle(backLeft);
		steering[RobotMap.BACK_RIGHT].setAngle(backRight);
	}

	/**
	 * Set angles in "turn in place" position Wrap around will check whether the closest angle is facing forward or backward
	 *
	 * Front Left- / \ - Front Right<br>
	 * Back Left - \ / - Back Right
	 *
	 * @param speed
	 */
	public void turnDrive(double speed) {
		WheelCorrection frontLeft = wrapAroundCorrect(RobotMap.FRONT_LEFT, TURN_IN_PLACE_ANGLE, speed);
		WheelCorrection frontRight = wrapAroundCorrect(RobotMap.FRONT_RIGHT, -TURN_IN_PLACE_ANGLE, -speed);
		WheelCorrection backLeft = wrapAroundCorrect(RobotMap.BACK_LEFT, -TURN_IN_PLACE_ANGLE, speed);
		WheelCorrection backRight = wrapAroundCorrect(RobotMap.BACK_RIGHT, TURN_IN_PLACE_ANGLE, -speed);

		this.fourWheelSteer(frontLeft.angle, frontRight.angle, backLeft.angle, backRight.angle);
		this.fourWheelDrive(frontLeft.speed, frontRight.speed, backLeft.speed, backRight.speed);
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
		aiming.enable();
		aiming.setSetpoint(angle); // 4 gyro units per degree
		System.out.println("Turn to Angle: angle=" + angle);
		System.out.println("Turn to Angle: output=" + aiming.get());
		return aiming.onTarget();
	}

	/**
	 * Limit the rate at which the robot can change speed once driving fast. This is to prevent causing mechanical damage - or
	 * tipping the robot through stopping too quickly.
	 *
	 * @param speed
	 *            desired speed for robot
	 * @return returns rate-limited speed
	 */
	private double limitSpeed(double speed, int wheelID) {
		// TODO - do we need to introduce any rate limiting this year?
		return (speed);
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
		WheelCorrection corrected = wrapAroundCorrect(RobotMap.BACK_RIGHT, angle, speed);
		fourWheelSteer(corrected.angle, corrected.angle, corrected.angle, corrected.angle);
		fourWheelDrive(corrected.speed, corrected.speed, corrected.speed, corrected.speed);
	}

	/**
	 * Vector drive
	 *
	 * @param driveAngle
	 *            the angle you want the robot to drive, taken from the angle of the joystick this is passed in in radians
	 * @param speed
	 *            the speed you want the robot to go, taken from the distance the joystick travels
	 * @param turnSpeed
	 *            the speed that the robot should turn at takes a value between -1 and 1
	 */
	public void vectorDrive(double driveAngle, double speed, double turnSpeed) {
		if ((speed == 0) && (turnSpeed == 0)) {
			stop();
			return;
		}
		// Derive angle of wheels for field aligned
		double angleDiff = driveAngle - gyro.getRobotAngleRadians();

		// vector component of the field aligned part of the motion
		Vector faVector = new Vector(LookUpTable.getSin(angleDiff) * speed, LookUpTable.getCos(angleDiff) * speed);

		// Only need to do math for first turn vector - can use symmetry to generate the rest
		Vector flTurn = new Vector(LookUpTable.getSin(TURN_IN_PLACE_ANGLE) * turnSpeed,
				LookUpTable.getCos(TURN_IN_PLACE_ANGLE) * turnSpeed);

		Vector frTurn = new Vector(flTurn.getX(), -flTurn.getY());
		Vector blTurn = new Vector(-flTurn.getX(), flTurn.getY());
		Vector brTurn = new Vector(-flTurn.getX(), -flTurn.getY());

		// add the field aligned and turn vectors

		Vector FL = faVector.Add(flTurn);
		Vector FR = faVector.Add(frTurn);
		Vector BL = faVector.Add(blTurn);
		Vector BR = faVector.Add(brTurn);

		// Figure out corrected angles & speeds for each wheel
		// Note - correction calculates shortest distance to drive to required angle and will
		// flip direction by 180 and speed by -1 if that is shorter
		WheelCorrection flCorrected = wrapAroundCorrect(RobotMap.FRONT_LEFT, FL.getAngle(), FL.getMagnitude());
		WheelCorrection frCorrected = wrapAroundCorrect(RobotMap.FRONT_RIGHT, FR.getAngle(), FR.getMagnitude());
		WheelCorrection blCorrected = wrapAroundCorrect(RobotMap.BACK_LEFT, BL.getAngle(), BL.getMagnitude());
		WheelCorrection brCorrected = wrapAroundCorrect(RobotMap.BACK_RIGHT, BR.getAngle(), BR.getMagnitude());

		// if some speed is > 1, divide correspondingly to have max speed = 1
		double maximumSpd = Math.max(Math.max(Math.abs(brCorrected.speed), Math.abs(blCorrected.speed)),
				Math.max(Math.abs(frCorrected.speed), Math.abs(flCorrected.speed)));
		if (maximumSpd > 1.0) {
			flCorrected.speed /= maximumSpd;
			frCorrected.speed /= maximumSpd;
			blCorrected.speed /= maximumSpd;
			brCorrected.speed /= maximumSpd;
		}

		// drive wheelpods
		fourWheelSteer(flCorrected.angle, frCorrected.angle, blCorrected.angle, brCorrected.angle);
		fourWheelDrive(flCorrected.speed, frCorrected.speed, blCorrected.speed, brCorrected.speed);
	}

	/**
	 * Individually controls a specific steering motor
	 *
	 * @param angle
	 *            Angle to drive to
	 * @param speed
	 *            Speed to drive at
	 * @param steeringId
	 *            Id of steering motor to drive
	 */
	public void individualSteeringDrive(double angle, int steeringId) {
		// Set steering angle
		steering[steeringId].setAngle(angle);
	}

	/**
	 * Does not drive drive motors and keeps steering angle at previous position.
	 */
	public void stop() {
		this.fourWheelDrive(0, 0, 0, 0);// no drive for you!
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
		double frontLeftSpeed = 0.0;
		double frontRightSpeed = 0.0;
		double rearLeftSpeed = 0.0;
		double rearRightSpeed = 0.0;

		switch (steeringId) {
		case RobotMap.FRONT_LEFT:
			frontLeftSpeed = 1.0;
			break;
		case RobotMap.FRONT_RIGHT:
			frontRightSpeed = speed * 1.0;
			break;
		case RobotMap.BACK_LEFT:
			rearLeftSpeed = speed * 1.0;
			break;
		case RobotMap.BACK_RIGHT:
			rearRightSpeed = speed * 1.0;
			break;
		}

		fourWheelDrive(frontLeftSpeed, frontRightSpeed, rearLeftSpeed, rearRightSpeed);
	}

	/**
	 * Function to determine the wrapped around difference from the joystick angle to the steering angle.
	 *
	 * @param value1
	 *            - The first angle to check against
	 * @param value2
	 *            - The second angle to check against
	 * @return The normalized wrap around difference
	 */
	static double wrapAroundDifference(double value1, double value2) {
		double diff = Math.abs(value1 - value2) % (2 * Math.PI);
		while (diff > Math.PI) {
			diff = (2.0 * Math.PI) - diff;
		}
		return diff;
	}

	/**
	 * Only used for steering
	 *
	 * @param steeringIndex
	 *            - which wheel pod
	 * @param targetAngle
	 *            - in radians
	 * @param targetSpeed
	 * @return corrected
	 */
	private WheelCorrection wrapAroundCorrect(int steeringIndex, double targetAngle, double targetSpeed) {
		WheelCorrection corrected = new WheelCorrection(targetAngle, targetSpeed);

		double normalizedSteeringAngle = steering[steeringIndex].getSteeringAngle() % (Math.PI * 2);
		if (wrapAroundDifference(normalizedSteeringAngle, targetAngle) > Math.PI / 2) {
			// shortest path to desired angle is to reverse speed and adjust
			// angle -PI
			corrected.speed *= -1;

			corrected.angle -= Math.PI;
		}
		return corrected;
	}

	/**
	 * Set the steering center to a new value
	 *
	 * @param steeringMotor
	 *            The id of the steering motor (0 = FL, 1 = FR, 2 = BL, 3 = BR)
	 * @param value
	 *            The new center value
	 */
	public void setSteeringCenter(int steeringMotor, double value) {
		steering[steeringMotor].setCenter(value);
	}

	/**
	 * Get the steering angle of the corresponding steering motor
	 *
	 * @param steeringId
	 *            The id of the steering motor
	 * @return
	 */
	public double getSteeringAngle(int steeringMotor) {
		return steering[steeringMotor].getSensorValue();
	}

	/**
	 * Get the normalized steering angle of the corresponding steering motor
	 *
	 * @param steeringId
	 *            The id of the steering motor
	 * @return
	 */
	public double getNormalizedSteeringAngle(int steeringMotor) {
		return steering[steeringMotor].getSteeringAngle();
	}

}

/* Simple class to use as a simple C style struct for a speed and angle */
class WheelCorrection {
	public double speed;
	public double angle;

	public WheelCorrection(double angleIn, double speedIn) {
		angle = angleIn;
		speed = speedIn;
	}

	@Override
	public String toString() {
		return "WheelCorrection [speed=" + speed + ", angle=" + angle + "]";
	}
}
