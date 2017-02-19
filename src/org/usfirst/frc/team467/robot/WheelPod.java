/**
 *
 */
package org.usfirst.frc.team467.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The WheelPod class is used for manually finding the PID values using the SmartDashboard.
 */
public class WheelPod {

	// Used for storing the velocity numbers in the Talon SRX
	private static final int VELOCITY_PID_PROFILE = 0;
	private static final int POSITION_PID_PROFILE = 1;

	/**
	 * The top speed for use in speed mode of this wheel pod. The overall robot max speed is set in the Robot Map.
	 */
	public static final double TOP_SPEED = 300;

	// Allowable closed loop error is 20%
	private static final int ALLOWABLE_CLOSED_LOOP_ERROR = 51;

	private CANTalon motor;
	private int id;

	// Wheel Limit Parameters - Velocity
	private double velocityMaxStableProportionalTerm;
	private double velocityMaxStableCycleTime;
	private double velocityMaxForwardSpeed;
	private double velocityMaxBackwardSpeed;

	private boolean isPosition;
	private Preferences prefs;
	private String keyHeader;

	// 2015 Robot current PID values
	// new PID(0.50, 0.0036, 18.00, 2.35), // Front Left PID values
	// new PID(1.35, 0.0027, 168.75, 1.90), // Front Right PID values
	// new PID(1.35, 0.0020, 168.75, 2.00), // Back Left PID values
	// new PID(1.35, 0.0027, 168.75, 2.00), // Back Right PID values

	/**
	 * Creates a wheel pod with external specified PID values.
	 *
	 * @param id
	 *            the wheel pod identifier
	 * @param p
	 *            the proportional input
	 * @param i
	 *            the integral input
	 * @param d
	 *            the the derivative input
	 * @param f
	 *            the feed forward input
	 */
	public WheelPod(int id, double p, double i, double d, double f) {
		this(id);
		motor.setPID(p, i, d);
		motor.setF(f);
		initSmartDashboard();
	}

	/**
	 * Creates a wheel pod with the PID values in the WPILib preferences. If there are no values in the preferences matching the
	 * wheel pod identifier, then it will use the default values.
	 *
	 * @param pod
	 *            the wheel pod identifier
	 */
	public WheelPod(int id) {
		this.id = id;
		isPosition = false;
		prefs = Preferences.getInstance();
		velocityMaxStableProportionalTerm = 0.0;
		velocityMaxStableCycleTime = 0.0;
		velocityMaxForwardSpeed = 0.0;
		velocityMaxBackwardSpeed = 0.0;
		motor = new CANTalon(RobotMap.DRIVING_MOTOR_CHANNELS[id]);
		setTalonParameters();
		motor.changeControlMode(TalonControlMode.PercentVbus);
		initSmartDashboard();
	}

	private void initSmartDashboard() {
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-P", motor.getP());
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-I", motor.getI());
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-D", motor.getD());
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-F", motor.getF());
		SmartDashboard.putNumber("Setpoint", 0.0);
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-Error", 0.0);
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-ID", id);
	}

	public void clear() {
		motor.clearStickyFaults();
		motor.clearMotionProfileTrajectories();
		motor.ClearIaccum();
	}

	private void setTalonParameters() {
		keyHeader = RobotMap.STEERING_KEYS[id] + "-Drive-PID-";
		motor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		motor.configEncoderCodesPerRev(RobotMap.WHEELPOD_ENCODER_CODES_PER_REVOLUTION);
		// motor.setNominalClosedLoopVoltage(RobotMap.NOMINAL_BATTERY_VOLTAGE);
		motor.enableBrakeMode(true);
	}

	public void p(double p) {
		System.out.println("Old P: " + motor.getP() + " New P: " + p);
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-P", p);
		prefs.putDouble(keyHeader() + "P", p);
		motor.setP(p);
	}

	public void i(double i) {

		System.out.println("Old I: " + motor.getI() + " New I: " + i);
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-I", i);
		prefs.putDouble(keyHeader() + "I", i);
		motor.setI(i);
	}

	public void d(double d) {
		System.out.println("Old D: " + motor.getD() + " New D: " + d);
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-D", d);
		prefs.putDouble(keyHeader() + "D", d);
		motor.setD(d);
	}

	public void f(double f) {
		System.out.println("Old F: " + motor.getF() + " New F: " + f);
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-F", f);
		prefs.putDouble(keyHeader() + "F", f);
		motor.setF(f);
	}

	public String pidfValueString() {
		String values = "";
		values += "P = " + motor.getP() + " ";
		values += "I = " + motor.getI() + " ";
		values += "D = " + motor.getD() + " ";
		values += "F = " + motor.getF() + " ";
		return values;
	}

	public void velocityMaxStableProportionalTerm(double velocityMaxStableProportionalTerm) {
		this.velocityMaxStableProportionalTerm = velocityMaxStableProportionalTerm;
	}

	public void velocityMaxStableCycleTime(double velocityMaxStableCycleTime) {
		this.velocityMaxStableCycleTime = velocityMaxStableCycleTime;
	}

	public void velocityMaxForwardSpeed(double velocityMaxForwardSpeed) {
		this.velocityMaxForwardSpeed = velocityMaxForwardSpeed;
	}

	public void velocityMaxBackwardSpeed(double velocityMaxBackwardSpeed) {
		this.velocityMaxBackwardSpeed = velocityMaxBackwardSpeed;
	}

	/**
	 * Loads the preferences from the WPILib preferences file. If there is no entry, it will set it to the default. If you tune
	 * using the SmartDashboard, it will save to preferences.
	 */
	private void loadFromPreferences(String header) {
		prefs = Preferences.getInstance();
		double p = prefs.getDouble(header + "P", motor.getP());
		double i = prefs.getDouble(header + "I", motor.getI());
		double d = prefs.getDouble(header + "D", motor.getD());
		double f = prefs.getDouble(header + "F", motor.getF());
		motor.setPID(p, i, d);
		motor.setF(f);
	}

	/**
	 * Loads the preferences from the WPILib preferences file. If there is no entry, it will set it to the default. If you tune
	 * using the SmartDashboard, it will save to preferences.
	 */
	public void loadFromPreferences() {
		prefs = Preferences.getInstance();
		motor.setProfile(POSITION_PID_PROFILE);
		loadFromPreferences(keyHeader + "Position-");
		motor.setProfile(VELOCITY_PID_PROFILE);
		loadFromPreferences(keyHeader + "Velocity-");
	}

	public void saveToPreferences() {
		prefs = Preferences.getInstance();
		if (!isPosition) {
			prefs.putDouble(keyHeader() + "MaxForwardSpeed", velocityMaxForwardSpeed);
			prefs.putDouble(keyHeader() + "MaxBackwardSpeed", velocityMaxBackwardSpeed);
			prefs.putDouble(keyHeader() + "VelocityMaxStableProportionalTerm", velocityMaxStableProportionalTerm);
			prefs.putDouble(keyHeader() + "VelocityMaxStableCycleTime", velocityMaxStableCycleTime);
		}
		prefs.putDouble(keyHeader() + "P", motor.getP());
		prefs.putDouble(keyHeader() + "I", motor.getI());
		prefs.putDouble(keyHeader() + "D", motor.getD());
		prefs.putDouble(keyHeader() + "F", motor.getF());
	}

	/**
	 * Moves a distance if in position mode.
	 *
	 * @param targetDistance
	 *            the target distance in feet.
	 */
	public void moveDistance(double targetDistance) {
		if (!isPosition) {
			positionMode();
		}
		zeroPosition();
		double revolutions = targetDistance * 12 / RobotMap.WHEELPOD_CIRCUMFERENCE;
		motor.set(revolutions);
	}

	/*
	 * Reverses the sensor and motor output if the wheel pod is on the side.
	 */
	public void reverse() {
		motor.reverseSensor(true);
		motor.reverseOutput(true);
	}

	public boolean checkReversed() {
		if (RobotMap.IS_DRIVE_MOTOR_INVERTED[id]) {
			motor.reverseOutput(true);
			motor.reverseSensor(true);
			return true;
		}
		return false;
	}

	public boolean checkSensor() {
		boolean sensorWorking = false;
		motor.set(100);
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (motor.getControlMode() == TalonControlMode.Speed) {
			if ((motor.getSpeed() != 0) && motor.getError() != 0) {
				sensorWorking = true;
			}
		} else if (motor.getControlMode() == TalonControlMode.Position) {
			if ((motor.getPosition() != 0) && motor.getError() != 0) {
				sensorWorking = true;
			}
		}
		motor.set(0);
		if (!sensorWorking) {
			System.out.println(name() + " sensors are not working!");
		} else {
			System.out.println(name() + " sensors working!");
		}
		return sensorWorking;
	}

	/**
	 * Changes the control mode to percentage of voltage bus.
	 */
	public void percentVoltageBusMode() {
		motor.changeControlMode(TalonControlMode.PercentVbus);
	}

	/**
	 * Sets the Talon into position mode.
	 */
	public void positionMode() {
		isPosition = true;
		motor.setProfile(POSITION_PID_PROFILE);
		motor.changeControlMode(TalonControlMode.Position);
		motor.setPosition(0);
	}

	public void zeroPosition() {
		motor.setPosition(0);
	}

	/**
	 * Sets the Talon into speed mode.
	 */
	public void speedMode() {
		isPosition = false;
		motor.setProfile(VELOCITY_PID_PROFILE);
		motor.changeControlMode(TalonControlMode.Speed);
		motor.setAllowableClosedLoopErr(ALLOWABLE_CLOSED_LOOP_ERROR);
	}

	public void set(double setpoint) {
		motor.set(setpoint);
	}

	public String keyHeader() {
		if (isPosition) {
			return keyHeader + "Position-";
		} else {
			return keyHeader + "Velocity-";
		}
	}

	public double readSensor() {
		double reading = Double.NaN;
		if (isPosition) {
			reading = motor.getPosition();
		} else {
			reading = motor.getSpeed();
		}
		return reading;
	}

	/**
	 * Gets the motor controller.
	 *
	 * @return the motor controller
	 */
	public CANTalon motor() {
		return motor;
	}

	public String name() {
		return RobotMap.STEERING_KEYS[id];
	}

	/**
	 * Checks the SmartDashboard for updated PID values, sets them, and then sets the speed or position setting depending on the
	 * current mode. This should be called from a periodic method in the Robot class.
	 *
	 * @return current error
	 */
	public double manualPIDTuning() {

		double oldP = motor.getP();
		double p = SmartDashboard.getNumber(RobotMap.STEERING_KEYS[id] + "-P", oldP);
		if (p != oldP) {
			p(p);
		}

		double oldI = motor.getI();
		double i = SmartDashboard.getNumber(RobotMap.STEERING_KEYS[id] + "-I", oldI);
		if (i != oldI) {
			i(i);
		}

		double oldD = motor.getD();
		double d = SmartDashboard.getNumber(RobotMap.STEERING_KEYS[id] + "-D", oldD);
		if (d != oldD) {
			d(d);
		}

		double oldF = motor.getF();
		double f = SmartDashboard.getNumber(RobotMap.STEERING_KEYS[id] + "-F", oldF);
		if (f != oldF) {
			f(f);
		}

		double oldSetpoint = motor.getSetpoint();
		double setpoint = SmartDashboard.getNumber("Setpoint", oldSetpoint);
		if (setpoint != oldSetpoint) {
			System.out.println("Old set point: " + oldSetpoint + " New set point: " + setpoint);
		}
		motor.set(setpoint);

		return error();
	}

	/**
	 * Gets the error value from the motor controller.
	 *
	 * @return the current error
	 */
	public double error() {
		double error;
		if (this.isPosition) {
			error = motor.getError() / RobotMap.WHEELPOD_ENCODER_CODES_PER_REVOLUTION;
		} else {
			error = motor.getError();
		}
		SmartDashboard.putNumber(RobotMap.STEERING_KEYS[id] + "-Error", error);
		return error;
	}

}