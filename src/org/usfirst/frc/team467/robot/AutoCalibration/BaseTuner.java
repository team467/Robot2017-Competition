/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.PID;
import org.usfirst.frc.team467.robot.PIDCalibration.WheelPod;

/**
 *
 */
public abstract class BaseTuner implements Tuner {

    public static final int ENCODER_CODES_PER_REVOLUTION = 256;
    protected static final double VELOCITY_SETPOINT = 100;
    protected static final double POSITION_SETPOINT = 5;

    protected static final double DEFAULT_ALLOWABLE_ERROR = 2.0;
    protected static final double ALLOWABLE_CYCLE_TIME_ERROR = 0.5;

    protected static final int HOLD_PERIOD = 600;

    protected double currentValue;
    protected double previousValue;
    protected double increaseFactor;

    private boolean isTalonPID;
    protected WheelPod wheelPod;
    protected int count;
    protected boolean findVelocityPID;

    protected PID pid;

    protected double setpoint;

    /**
	 *
	 */
	public BaseTuner(WheelPod wheelPod, boolean findVelocityPID) {
		isTalonPID = true;
        this.findVelocityPID = findVelocityPID;
        if (findVelocityPID) {
            setpoint = VELOCITY_SETPOINT;
        } else {
            setpoint = POSITION_SETPOINT;

        }
        this.wheelPod = wheelPod;
        wheelPod.clear();
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
			wheelPod.pid(p, i, d);
		}
	}

	protected void pidf(double p, double i, double d, double f) {
		if (isTalonPID) {
			wheelPod.pidf(p, i, d, f);
		}
	}

    protected double increaseValue() {
        previousValue = currentValue;
        currentValue += increaseFactor;
        System.out.println("Increased to " + currentValue);
        return currentValue;
    }

    protected double decreaseValue() {
        increaseFactor /= 2.0;
        currentValue = previousValue + increaseFactor;
        System.out.println("Decreased to " + currentValue);
        return currentValue;
    }

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.AutoCalibration.Tuner#process()
	 */
	@Override
	public abstract boolean process();

}
