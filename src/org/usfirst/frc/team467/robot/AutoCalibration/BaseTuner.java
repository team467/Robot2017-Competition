/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.PID;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PIDSourceType;

/**
 *
 */
public abstract class BaseTuner implements Tuner {

	protected static final int VELOCITY_PID_PROFILE = 0;
    protected static final int POSITION_PID_PROFILE = 1;

    public static final int ENCODER_CODES_PER_REVOLUTION = 256;
    protected static final double SETPOINT = 100;

    protected static final double DEFAULT_ALLOWABLE_ERROR = 2.0;
    protected static final double ALLOWABLE_CYCLE_TIME_ERROR = 0.5;

    protected static final int HOLD_PERIOD = 600;

    protected double currentValue;
    protected double previousValue;
    protected double increaseFactor;

    protected CANTalon talon;
    protected int count;
    protected boolean findVelocityPID;

    protected PID pid;

    /**
	 *
	 */
	public BaseTuner(CANTalon talon, boolean reverseDirection, boolean findVelocityPID) {
        if (reverseDirection) {
            talon.reverseOutput(true);
            talon.reverseSensor(true);
        }
        if (findVelocityPID) {
            talon.setProfile(VELOCITY_PID_PROFILE);
        } else {
            talon.setProfile(POSITION_PID_PROFILE);
        }
        this.findVelocityPID = findVelocityPID;
        this.talon = talon;
        pid(0,0,0,0);
        clear();
	}

	protected void pid(double p, double i, double d, double f) {
		pid = new PID(p, i, d, f);
		talon.setPID(p, i, d);
		talon.setF(f);
	}

	public PID pid() {
		return pid;
	}

    protected void clear() {
    	if (findVelocityPID) {
            talon.changeControlMode(TalonControlMode.Speed);
    	} else {
            talon.changeControlMode(TalonControlMode.Position);
    	}
    	talon.setPIDSourceType(PIDSourceType.kRate);
        talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        talon.configEncoderCodesPerRev(ENCODER_CODES_PER_REVOLUTION);
        talon.ClearIaccum();
        talon.setAllowableClosedLoopErr(0);
        talon.setForwardSoftLimit(0);
        talon.setReverseSoftLimit(0);
        talon.setPosition(0);
        talon.setEncPosition(0);
        talon.enableBrakeMode(true);
        count = 0;
    }

    protected double increaseValue() {
        previousValue = currentValue;
        currentValue += increaseFactor;
        System.out.println("Increased to " + currentValue);
        return currentValue;
    }

    protected double decreaseValue() {
        increaseFactor /= 10.0;
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
