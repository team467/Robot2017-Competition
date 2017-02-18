/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.PID;
import org.usfirst.frc.team467.robot.WheelPod;

/**
 *
 */
public abstract class BaseTuner implements Tuner {

	public static final int ENCODER_CODES_PER_REVOLUTION = 256;
	protected static final double VELOCITY_SETPOINT = 100;
	protected static final double POSITION_SETPOINT = 1;

	protected static final double DEFAULT_ALLOWABLE_ERROR = 1.0;
	protected static final double ALLOWABLE_CYCLE_TIME_ERROR = 0.5;

	protected static final int HOLD_PERIOD = 600;

	protected static final int MAX_FACTOR_DECREASE_COUNT = 4;

	protected double currentValue;
	protected double previousValue;
	protected double increaseFactor;

	private boolean isTalonPID;
	protected WheelPod wheelPod;
	protected int count;
	protected boolean findVelocityPID;

	protected PID pid;

	protected double setpoint;

	protected int factorDecreaseCount;

	/**
	 *
	 */
	public BaseTuner(WheelPod wheelPod, boolean findVelocityPID) {
		isTalonPID = true;
		this.findVelocityPID = findVelocityPID;
		if (findVelocityPID) {
			setpoint = VELOCITY_SETPOINT;
			wheelPod.speedMode();
		} else {
			setpoint = POSITION_SETPOINT;
			wheelPod.positionMode();
		}
		this.wheelPod = wheelPod;
		wheelPod.clear();
		wheelPod.checkReversed();
		factorDecreaseCount = 0;
	}

	protected void set(double currentSetpoint) {
		if (isTalonPID) {
			wheelPod.set(currentSetpoint);
		}
	}

	protected void clear() {
		if (isTalonPID) {
			wheelPod.clear();
		}
	}

	protected double readSensor() {
		double reading = Double.NaN;
		if (isTalonPID) {
			return wheelPod.readSensor();
		}
		return reading;
	}

	protected void p(double p) {
		if (isTalonPID) {
			wheelPod.p(p);
		}
	}

	protected void f(double f) {
		if (isTalonPID) {
			wheelPod.f(f);
		}
	}

	protected void pid(double p, double i, double d) {
		if (isTalonPID) {
			wheelPod.p(p);
			wheelPod.i(i);
			wheelPod.d(d);
		}
	}

	protected void pidf(double p, double i, double d, double f) {
		if (isTalonPID) {
			wheelPod.p(p);
			wheelPod.i(i);
			wheelPod.d(d);
			wheelPod.f(f);
		}
	}

	protected void feedForward(double feedForward) {
		System.out.println("Initial feed forward set to " + feedForward);
		if (isTalonPID) {
			wheelPod.f(feedForward);
			wheelPod.saveToPreferences();
		}
	}

	protected void velocityMaxStableProportionalTerm(double velocityMaxStableProportionalTerm) {
		if (isTalonPID) {
			wheelPod.velocityMaxStableProportionalTerm(velocityMaxStableProportionalTerm);
			wheelPod.saveToPreferences();
		}
	}

	protected void velocityMaxStableCycleTime(double velocityMaxStableCycleTime) {
		if (isTalonPID) {
			wheelPod.velocityMaxStableCycleTime(velocityMaxStableCycleTime);
			wheelPod.saveToPreferences();
		}
	}

	protected void velocityMaxForwardSpeed(double velocityMaxForwardSpeed) {
		System.out.println("Max Forward Speed: " + velocityMaxForwardSpeed);
		if (isTalonPID) {
			wheelPod.velocityMaxForwardSpeed(velocityMaxForwardSpeed);
			wheelPod.saveToPreferences();
		}
	}

	protected void velocityMaxBackwardSpeed(double velocityMaxBackwardSpeed) {
		System.out.println("Max Backward Speed: " + velocityMaxBackwardSpeed);
		if (isTalonPID) {
			wheelPod.velocityMaxBackwardSpeed(velocityMaxBackwardSpeed);
			wheelPod.saveToPreferences();
		}
	}

	protected double increaseValue() {
		previousValue = currentValue;
		currentValue += increaseFactor;
		// System.out.println("Increased to " + currentValue);
		factorDecreaseCount--;
		return currentValue;
	}

	protected double decreaseValue() {
		increaseFactor /= 10.0;
		currentValue = previousValue + increaseFactor;
		// System.out.println("Decreased to " + currentValue);
		factorDecreaseCount++;
		return currentValue;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.usfirst.frc.team467.robot.AutoCalibration.Tuner#process()
	 */
	@Override
	public abstract boolean process();

}