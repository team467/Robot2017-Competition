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

/**
 *
 */
public class Drive extends RobotDrive {
	private static final Logger LOGGER = Logger.getLogger(Drive.class);
	// Single instance of this class
	private static Drive instance = null;

	// need CAN Talons for PID modes
	private CANTalon frontLeft;
	private CANTalon backLeft;
	private CANTalon frontRight;
	private CANTalon backRight;

	// Drive control mode
	private TalonControlMode controlMode;

	// Gyroscope
	private Gyrometer gyro;

	private double[] aimingPIDs = { .017, .00022, .19, .32 };
	public PIDController aiming;

	// Steering objects
	public Steering[] steering;

	// Data storage object
	private DataStorage data;
	// Angle to turn at when rotating in place - initialized in constructor
	// takes the arctan of width over length in radians
	// Length is the wide side
	private static final double TURN_IN_PLACE_ANGLE = Math.atan(RobotMap.WHEEL_BASE_LENGTH / RobotMap.WHEEL_BASE_WIDTH);

	// Private constructor
	private Drive(CANTalon frontLeftMotor, CANTalon backLeftMotor, CANTalon frontRightMotor, CANTalon backRightMotor) {
		super(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);

		this.frontLeft = frontLeftMotor;
		initMotor(this.frontLeft);

		this.backLeft = backLeftMotor;
		initMotor(this.backLeft);

		this.frontRight = frontRightMotor;
		initMotor(this.frontRight);

		this.backRight = backRightMotor;
		initMotor(this.backRight);

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
						LOGGER.debug("PID Output=" + output + " error=" + aiming.getError());
						turnDrive(output);
					}
				});
		aiming.setInputRange(-180, 180); // 4 Gyro units per degree
		aiming.setContinuous(); // 0º and 360º are the same point
		aiming.setOutputRange(-1.0, 1.0); // Max Speed in either direction
		aiming.setAbsoluteTolerance(3.0); // 1 degree tolerance
		aiming.setToleranceBuffer(5);
	}

	private void initMotor(CANTalon talon) {
		talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		talon.configEncoderCodesPerRev(RobotMap.WHEELPOD_ENCODER_CODES_PER_REVOLUTION);
		talon.setNominalClosedLoopVoltage(RobotMap.NOMINAL_BATTERY_VOLTAGE);
//		talon.setCloseLoopRampRate(5);
		talon.reverseSensor(true);
	}

	/**
	 * Gets the single instance of this class.
	 *
	 * @return The single instance.
	 */
	public static Drive getInstance() {
		if (instance == null) {
			// First usage - create Drive object
			instance = new Drive(
					new CANTalon(RobotMap.driveMotorChannel[RobotMap.FRONT_LEFT]),
					new CANTalon(RobotMap.driveMotorChannel[RobotMap.BACK_LEFT]),
					new CANTalon(RobotMap.driveMotorChannel[RobotMap.FRONT_RIGHT]),
					new CANTalon(RobotMap.driveMotorChannel[RobotMap.BACK_RIGHT]));
		}
		return instance;
	}
	
	public void setPIDF(double p, double i, double d, double f){
		frontLeft.setPID(p, i, d, f, 128, 2, 1);
		backLeft.setPID(p, i, d, f, 128, 2, 1);
		frontRight.setPID(p, i, d, f, 128, 2, 1);
		backRight.setPID(p, i, d, f, 128, 2, 1);
		
	}


	/**
	 * Sets the motors to drive in speed mode.
	 * 
	 * @return Successful or not
	 */
	public boolean setSpeedMode() {
		if (RobotMap.useSpeedControllers) {
			controlMode = TalonControlMode.Speed;
			initMotorForSpeedMode(frontLeft);
			initMotorForSpeedMode(frontRight);
			initMotorForSpeedMode(backLeft);
			initMotorForSpeedMode(backRight);
			return true;
		} else {
			LOGGER.debug("No Speed Sensors, No Speed Mode");
			return false;
		}
	}

	private void initMotorForSpeedMode(CANTalon talon) {
		talon.changeControlMode(TalonControlMode.Speed);
		talon.setProfile(RobotMap.VELOCITY_PID_PROFILE);
		talon.setAllowableClosedLoopErr(RobotMap.VELOCITY_ALLOWABLE_CLOSED_LOOP_ERROR);
		talon.setNominalClosedLoopVoltage(12);
	}

	/**
	 * Sets the motors to drive in percent of voltage mode. Default for when the speed sensors are not working.
	 */
	public void setPercentVoltageBusMode() {
		controlMode = TalonControlMode.PercentVbus;
		frontLeft.changeControlMode(TalonControlMode.PercentVbus);
		frontRight.changeControlMode(TalonControlMode.PercentVbus);
		backLeft.changeControlMode(TalonControlMode.PercentVbus);
		backRight.changeControlMode(TalonControlMode.PercentVbus);
	}

	/**
	 * Sets the motors to drive in position mode.
	 * 
	 * @return Successful or not
	 */
	public boolean setPositionMode() {
		if (RobotMap.useSpeedControllers) {
			controlMode = TalonControlMode.Position;
	
			initMotorForPositionMode(backLeft);
			initMotorForPositionMode(backRight);
			
			// Front follows back
			initMotorForFollowerMode(backLeft, frontLeft);
			initMotorForFollowerMode(backRight, frontRight);
			return true;
		} else {
			LOGGER.debug("No Speed Sensors, no Position Mode");
			return false;
		}
	}

	private void initMotorForPositionMode(CANTalon talon) {
		talon.changeControlMode(TalonControlMode.Position);
		talon.setProfile(RobotMap.POSITION_PID_PROFILE);
		talon.setAllowableClosedLoopErr(RobotMap.POSITION_ALLOWABLE_CLOSED_LOOP_ERROR);
		talon.setNominalClosedLoopVoltage(8);
		// Zero the position
		talon.setPosition(0);
		LOGGER.debug("Set " + talon.getDeviceID() + " "+ talon.getControlMode());
	}
	
	private void initMotorForFollowerMode(CANTalon master, CANTalon slave) {
		slave.changeControlMode(TalonControlMode.Follower);
		slave.set(master.getDeviceID());
		LOGGER.debug("Set " + slave.getDeviceID() + " Following " + master.getDeviceID());
	}
	
	public void logClosedLoopErrors() {
		LOGGER.debug(
				"closedLoopErr FL=" + frontLeft.getClosedLoopError() +
				" FR=" + frontRight.getClosedLoopError() +
				" BL=" + backLeft.getClosedLoopError() +
				" BR=" + backRight.getClosedLoopError());
	}

	public TalonControlMode getControlMode() {
		return controlMode;
	}

	/**
	 * Takes the drive out of position mode back into its default drive mode.
	 */
	public void setDefaultDriveMode() {
		if (RobotMap.useSpeedControllers) {
			setSpeedMode();
		} else {
			setPercentVoltageBusMode();
		}
		
		stop();
	}

	/**
	 * Drives each of the four wheels at different speeds using invert constants to account for wiring.
	 *
	 * @param frontLeftParam
	 * 			Speed or Distance value for front left wheel
	 * @param frontRightParam
	 * 			Speed or Distance value for front right wheel
	 * @param backLeftParam
	 * 			Speed or Distance value for back left wheel
	 * @param backRightParam
	 * 			Speed or Distance value for back right wheel
	 */
	private void fourWheelDrive(double frontLeftParam, double frontRightParam, double backLeftParam, double backRightParam) {
		// If any of the motors doesn't exist then exit
		if (m_rearLeftMotor == null || m_rearRightMotor == null || m_frontLeftMotor == null || m_frontRightMotor == null) {
			throw new NullPointerException("Null motor provided");
		}
		
		backLeft.set(
				(RobotMap.isDriveMotorInverted[RobotMap.BACK_LEFT] ? -1 : 1) * adjustSpeedOrDistance(backLeftParam, RobotMap.BACK_LEFT));
		backRight.set(
				(RobotMap.isDriveMotorInverted[RobotMap.BACK_RIGHT] ? -1 : 1) * adjustSpeedOrDistance(backRightParam, RobotMap.BACK_RIGHT));
		
		if (getControlMode() == TalonControlMode.Position) {
			frontLeft.set(backLeft.getDeviceID());
			frontRight.set(backRight.getDeviceID());
		} else {
			frontLeft.set(
					(RobotMap.isDriveMotorInverted[RobotMap.FRONT_LEFT] ? -1 : 1) * adjustSpeedOrDistance((frontLeftParam), RobotMap.FRONT_LEFT));
			frontRight.set(
					(RobotMap.isDriveMotorInverted[RobotMap.FRONT_RIGHT] ? -1 : 1) * adjustSpeedOrDistance(frontRightParam, RobotMap.FRONT_RIGHT));
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
	
	public void turnDriveAngle(double rads) {
		// will cause the steering to unwind; on purpose as it is simpler
		this.fourWheelSteer(TURN_IN_PLACE_ANGLE, -TURN_IN_PLACE_ANGLE, -TURN_IN_PLACE_ANGLE, TURN_IN_PLACE_ANGLE);
		//TODO: not sure if I am suppose to call this every time...
		setPositionMode();
		
		/* same as what was in crab
		 * makes sure that wheels are in correct position before moving
		 * check how many wheelpods are in place	`*/
		int numgood = 0;
		for (int i = 0; i < 4; ++i) {
			if (steering[i].getAngleDelta() < Math.PI / 6) {
				numgood++;
			}
		}
		/* if at least 2 wheelpods are in place, proceed as normal*/
		if (numgood >= 2) {
			//distance to travel for the wheelpod
			double rotations = rads * RobotMap.WHEEL_BASE_RADIUS;
			this.fourWheelDrive(rotations, rotations, rotations, rotations);
		}
	
	}
	
	public void setFourWheelSteer(){
		this.fourWheelSteer(TURN_IN_PLACE_ANGLE, -TURN_IN_PLACE_ANGLE, -TURN_IN_PLACE_ANGLE, TURN_IN_PLACE_ANGLE);
	}
	
	public boolean allWheelsTurned(){
		int numgood = 0;
		for (int i = 0; i < 4; ++i) {
			if (steering[i].getAngleDelta() < Math.PI / 6) {
				numgood++;
			}
		}
		if (numgood >= 2) {
			return true;
		}
		return false;
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
		aiming.setSetpoint(angle);
		LOGGER.debug("Turn to Angle: aimAngle=" + angle + " currentAngle=" + gyro.pidGet() + " output=" + aiming.get());
		return aiming.onTarget();
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
	private double adjustSpeedOrDistance(double speedOrDistance, int wheelID) {
		switch (controlMode)
		{
		case Speed:
			speedOrDistance *= RobotMap.MAX_SPEED;
			break;

		case Position:
			speedOrDistance *= 12 / RobotMap.WHEELPOD_CIRCUMFERENCE;
			break;

		default:
			break;
		}
		return (speedOrDistance);
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

		/* check how many wheelpods are in place	`*/
		int numgood = 0;
		for (int i = 0; i < 4; ++i) {
			if (steering[i].getAngleDelta() < Math.PI / 6) {
				numgood++;
			}
		}
		/* if at least 2 wheelpods are in place, proceed as normal*/
		if (numgood >= 2) {
			fourWheelDrive(corrected.speed, corrected.speed, corrected.speed, corrected.speed);
		}

	}

	public boolean isStopped(){
		return false;
	}

	private double getMinimum(double base, double compare) {
		double absVal = Math.abs(compare);
		if(absVal < base) return absVal;
		return base;
	}

	/**
	 * Gets the distance moved for checking drive modes.
	 *
	 * @return the absolute distance moved in feet
	 */
	public double absoluteDistanceMoved() {
		LOGGER.info(" BL: " + backLeft.getPosition() + " BR: " + backRight.getPosition());
		double rotations =  Math.abs(frontLeft.getPosition());
		rotations = getMinimum(rotations, backRight.getPosition());
		rotations = getMinimum(rotations, backLeft.getPosition());

		return rotations * RobotMap.WHEELPOD_CIRCUMFERENCE / 12;
	}
	
	public double getTurnError() {
		double errorInFeet = (backLeft.getError() / 1024) * (RobotMap.WHEELPOD_CIRCUMFERENCE / 12);
		System.out.println("Distances: BL: " + backLeft.getPosition() + " error:" + errorInFeet);
		double rotations =  Math.abs(backLeft.getError());
		rotations = getMinimum(rotations, backLeft.getPosition());

		return Math.abs(errorInFeet);
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
	 * Used for correcting the steering sensors, especially if they are out of range.
	 */
	public void logSteeringValues() {
		LOGGER.debug(
				"FL=" + steering[RobotMap.FRONT_LEFT].getSensorValue() +
				" FR=" + steering[RobotMap.FRONT_RIGHT].getSensorValue() +
				" BL=" + steering[RobotMap.BACK_LEFT].getSensorValue() +
				" BR=" + steering[RobotMap.BACK_RIGHT].getSensorValue());
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
