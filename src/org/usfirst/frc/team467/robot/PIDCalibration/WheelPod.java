/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

import org.usfirst.frc.team467.robot.PIDF;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The WheelPod class is used for manually finding the PID values using the
 * SmartDashboard.
 */
public class WheelPod {

	private static final int VELOCITY_PID_PROFILE = 0;
    private static final int POSITION_PID_PROFILE = 1;

    private static final int ENCODER_CODES_PER_REVOLUTION = 256;

	// The default PID settings if not otherwise specified.
	private static final double DEFAULT_P = 2.16;
	private static final double DEFAULT_I = 0.00864;
	private static final double DEFAULT_D = 135.0;
	private static final double DEFAULT_F = 3.0;

	/**
	 * The top speed for use in speed mode of this wheel pod. The overall robot
	 * max speed is set in the Robot Map.
	 */
	public static final double TOP_SPEED = 300;

	/**
	 * The circumference of the wheels for use in determining distance in
	 * position mode.
	 */
	public static final double CIRCUMFERENCE = 18.85;

	private static final int ALLOWABLE_CLOSED_LOOP_ERROR = 51;
	private static final int MAX_ERROR_COUNT = 26;

	private CANTalon motor;

	// The current PID values
	private double p;
	private double i;
	private double d;
	private double f;

	// Wheel Limit Parameters - Velocity
	private double velocityMaxStableProportionalTerm;
	private double velocityMaxStableCycleTime;
	private double velocityMaxForwardSpeed;
	private double velocityMaxBackwardSpeed;

	// The movement settings.
	private double speed;
	private int position;

	private boolean isPosition;
	private Preferences prefs;
	private Pod pod;
	private String keyHeader;
	private RunningAverage averageError;

	/**
	 * Creates a wheel pod with external specified PID values.
	 *
	 * @param deviceChannel
	 *            the Talon channel identifier
	 * @param pidf
	 *            the speed p, i, d, and f values
	 */
	public WheelPod(int deviceChannel, PIDF pidf) {
		this(Pod.idToPod(deviceChannel), pidf.p, pidf.i, pidf.d, pidf.f);
	}

	/**
	 * Creates a wheel pod with external specified PID values.
	 *
	 * @param pod
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
	public WheelPod(Pod pod, double p, double i, double d, double f) {
		isPosition = false;
		averageError = new RunningAverage(MAX_ERROR_COUNT);
		this.pod = pod;
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		velocityMaxStableProportionalTerm = 0.0;
		velocityMaxStableCycleTime = 0.0;
		velocityMaxForwardSpeed = 0.0;
		velocityMaxBackwardSpeed = 0.0;
		motor = new CANTalon(pod.id);
		setTalonParameters();
		motor.changeControlMode(TalonControlMode.PercentVbus);
		motor.setPID(p, i, d);
		motor.setF(f);

		speed = SmartDashboard.getNumber("Speed", 0.0);
		position = (int) SmartDashboard.getNumber("Position", 0.0);
		SmartDashboard.putNumber(pod.abr + "-P", p);
		SmartDashboard.putNumber(pod.abr + "-I", i);
		SmartDashboard.putNumber(pod.abr + "-D", d);
		SmartDashboard.putNumber(pod.abr + "-F", f);
		SmartDashboard.putNumber("Speed", speed);
		SmartDashboard.putNumber("Position", position);
		SmartDashboard.putNumber(pod.abr + "-Error", 0.0);
		SmartDashboard.putNumber(pod.abr + "-ID", pod.id);
	}

	/**
	 * Creates a wheel pod with the PID values in the WPILib preferences. If
	 * there are no values in the preferences matching the wheel pod identifier,
	 * then it will use the default values.
	 *
	 * @param pod
	 *            the wheel pod identifier
	 */
	public WheelPod(Pod pod) {
		isPosition = false;
		this.pod = pod;
		p = DEFAULT_P;
		i = DEFAULT_I;
		d = DEFAULT_D;
		f = DEFAULT_F;
		velocityMaxStableProportionalTerm = 0.0;
		velocityMaxStableCycleTime = 0.0;
		velocityMaxForwardSpeed = 0.0;
		velocityMaxBackwardSpeed = 0.0;
		motor = new CANTalon(pod.id);
		loadFromPreferences();
		setTalonParameters();
		motor.changeControlMode(TalonControlMode.PercentVbus);
	}

    public void clear() {
    	motor.clearStickyFaults();
    	motor.clearMotionProfileTrajectories();
    	motor.ClearIaccum();
    }

    private void setTalonParameters() {
    	motor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
//    	motor.configEncoderCodesPerRev(ENCODER_CODES_PER_REVOLUTION);
//    	motor.setAllowableClosedLoopErr(ALLOWABLE_CLOSED_LOOP_ERROR);
//    	motor.setPosition(0);
//    	motor.setEncPosition(0);
//    	motor.enableBrakeMode(true);
    }

	public void pi(double p, double i) {
		motor.setP(p);
		motor.setI(i);
	}

	public void pid(double p, double i, double d) {
		motor.setP(p);
		motor.setI(i);
		motor.setD(d);
	}

	public void pidf(double p, double i, double d, double f) {
		motor.setP(p);
		motor.setI(i);
		motor.setD(d);
		motor.setF(f);
	}

	public void p(double p) {
		motor.setF(p);
	}

	public void i(double i) {
		motor.setF(i);
	}

	public void d(double d) {
		motor.setF(d);
	}

	public void f(double f) {
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

	/*
	 * Loads the preferences from the WPILib preferences file. If there is no
	 * entry, it will set it to the default. If you tune using the
	 * SmartDashboard, it will save to preferences.
	 */
	public void loadFromPreferences() {
		prefs = Preferences.getInstance();
		keyHeader = "Pod-" + pod.name + "-PID-";
		if (!prefs.containsKey(keyHeader + "P")) {
			prefs.putDouble(keyHeader + "P", p);
		}
		p = prefs.getDouble(keyHeader + "P", p);
		if (!prefs.containsKey(keyHeader + "I")) {
			prefs.putDouble(keyHeader + "I", i);
		}
		i = prefs.getDouble(keyHeader + "I", i);
		if (!prefs.containsKey(keyHeader + "D")) {
			prefs.putDouble(keyHeader + "D", d);
		}
		d = prefs.getDouble(keyHeader + "D", d);
		if (!prefs.containsKey(keyHeader + "F")) {
			prefs.putDouble(keyHeader + "F", f);
		}
		f = prefs.getDouble(keyHeader + "F", f);
	}

	public void saveToPreferences() {
		prefs = Preferences.getInstance();
		keyHeader = "Pod-" + pod.name + "-PID-";
		if (motor.getControlMode() == TalonControlMode.Speed) {
			prefs.putDouble(keyHeader + "Velocity-P", p);
			prefs.putDouble(keyHeader + "Velocity-I", i);
			prefs.putDouble(keyHeader + "Velocity-D", d);
			prefs.putDouble(keyHeader + "Velocity-F", f);
		} else {
			prefs.putDouble(keyHeader + "Position-P", p);
			prefs.putDouble(keyHeader + "Position-I", i);
			prefs.putDouble(keyHeader + "Position-D", d);
			prefs.putDouble(keyHeader + "Position-F", f);
		}
		prefs.putDouble(keyHeader + "MaxForwardSpeed", velocityMaxForwardSpeed);
		prefs.putDouble(keyHeader + "MaxBackwardSpeed", velocityMaxBackwardSpeed);
		prefs.putDouble(keyHeader + "VelocityMaxStableProportionalTerm", velocityMaxStableProportionalTerm);
		prefs.putDouble(keyHeader + "VelocityMaxStableCycleTime", velocityMaxStableCycleTime);
	}

	/**
	 * Moves a distance if in position mode.
	 *
	 * @param targetDistance
	 *            the target distance in feet.
	 */
	public void moveDistance(double targetDistance) {
		if (isPosition) {
			motor.changeControlMode(TalonControlMode.Position);
			double positionTicks = targetDistance / WheelPod.CIRCUMFERENCE * 512;
			System.out.println("Position Move: " + positionTicks);
			motor.set(positionTicks);
		}
	}

	/*
	 * Reverses the sensor and motor output if the wheel pod is on the side.
	 */
	public void reverse() {
		motor.reverseSensor(true);
		motor.reverseOutput(true);
	}

	public boolean checkReversed() {
		if (pod.isReversed) {
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
//		motor.setPosition(0);
//		motor.setEncPosition(0);
	}

	public void zeroPosition() {
		motor.setPosition(0);
		motor.setEncPosition(0);
	}

	/**
	 * Sets the Talon into speed mode.
	 */
	public void speedMode() {
		isPosition = false;
		motor.setProfile(VELOCITY_PID_PROFILE);
		motor.changeControlMode(TalonControlMode.Speed);
		motor.configEncoderCodesPerRev(ENCODER_CODES_PER_REVOLUTION);
		motor.setAllowableClosedLoopErr(ALLOWABLE_CLOSED_LOOP_ERROR);
	}

	public void set(double setpoint) {
		if (this.isPosition) {
			System.out.println("Setting to " + setpoint);
			motor.set(setpoint);
			System.out.println(isPosition + " " + motor.getControlMode() + " " + motor.getPosition() + " " + motor.getSetpoint());
		} else {
			motor.set(setpoint);
		}
	}

	public double readSensor() {
		double reading = motor.getEncVelocity();
		if (isPosition) {
			reading =  motor.getPosition();
			System.out.println(name() + ": " + motor.getControlMode() + " SET: " + motor.getSetpoint()
			+ " ERR: " + motor.getError() + " ENC: " + motor.getEncPosition()
			+ " READ: " + reading);
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
		return pod.name;
	}

	/**
	 * Checks the SmartDashboard for updated PID values, sets them, and then
	 * sets the speed or position setting depending on the current mode. This
	 * should be called from a periodic method in the Robot class.
	 *
	 * @return current error
	 */
	public double updateValues() {
		double newP = SmartDashboard.getNumber(pod.abr + "-P", p);
		if (newP != p) {
			System.out.println("Old P: " + p + " New P: " + newP);
			p = newP;
			prefs.putDouble((keyHeader + "P"), newP);
			motor.setP(newP);
		}
		double newI = SmartDashboard.getNumber(pod.abr + "-I", i);
		if (newI != i) {
			System.out.println("Old I: " + i + " New I: " + newI);
			i = newI;
			prefs.putDouble((keyHeader + "I"), newI);
			motor.setI(newI);
		}
		double newD = SmartDashboard.getNumber(pod.abr + "-D", d);
		if (newD != d) {
			System.out.println("Old D: " + d + " New D: " + newD);
			d = newD;
			prefs.putDouble((keyHeader + "D"), newD);
			motor.setD(newD);
		}
		double newF = SmartDashboard.getNumber(pod.abr + "-F", f);
		if (newF != f) {
			System.out.println("Old F: " + p + " New F: " + newP);
			f = newF;
			prefs.putDouble((keyHeader + "F"), newF);
			motor.setF(newF);
		}
		double newSpeed = SmartDashboard.getNumber("Speed", 0.0);
		if (newSpeed != this.speed) {
			System.out.println("Old Speed: " + speed + " New Speed: " + newSpeed);
			this.speed = newSpeed;
		}
		int newPosition = (int) SmartDashboard.getNumber("Position", 0.0);
		if (newPosition != this.speed) {
			System.out.println("Old Position: " + speed + " New Position: " + newPosition);
			this.position = newPosition;
		}
		if (isPosition) {
			motor.set(this.position);
		} else {
			motor.set(this.speed);
		}
		return error();
	}

	/*
	 * Gets the running average error.
	 *
	 * @return the average error
	 */
	public double averageError(double error) {
		double average = this.averageError.average(error);
		SmartDashboard.putNumber(pod.abr + "-AveErr", average);
		return average;
	}

	/**
	 * Gets the error value from the motor controller.
	 *
	 * @return the current error
	 */
	public double error() {
		double error = motor.getError();
		averageError(error);
		SmartDashboard.putNumber(pod.abr + "-Error", error);
		return error;
	}

}
