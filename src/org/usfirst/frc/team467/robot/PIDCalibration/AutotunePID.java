/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Bryan Duerk
 *
 * The process for autotuning PID:
 *   1. Determine the max speed of the motor by applying 100% voltage and reading the speed from the Talon SRX.
 *      a. Repeat for reverse.
 *   1. Set top and bottom set points.
 *   2. Increase the Feed Forward until the average error is at a minimum.
 *   3. Moving between top and bottom set points
 *     a. Increase the P value slowly until the system oscillates. At each increase, double the previous.
 *        If it oscillates, go back to the last good value and increase at half until P is dialed in.
 *
 */
public class AutotunePID
{
    public static final int ENCODER_CODES_PER_REVOLUTION = 256;
    public static final int UPPER_SETPOINT = 150;
    public static final int LOWER_SETPOINT = 50;

    public static final int VELOCITY_PID_PROFILE = 0;
    public static final int POSITION_PID_PROFILE = 1;

    public static final double DEFAULT_ALLOWABLE_ERROR = 20.0;
    public static final double ALLOWABLE_CYCLE_TIME_ERROR = 0.5;

    boolean isFinished;

    CANTalon talon;
    boolean isTalonReversed;

    private double maxForwardSpeed;
    private double maxBackwardSpeed;
    private double maxOverallSpeed;
    private double initialFeedForward;

    private double Ku;
    private double Kp;
    private double Ki;
    private double Kd;

    private double currentValue;
    private double previousValue;
    private double increaseFactor;
    private int currentSetpoint;
    private double allowableError;

    private boolean isFindingVelocityPID;

    private PIDTuningCycleCharacteristics characteristics;


    /**
     *
     */
    public AutotunePID(CANTalon talon, boolean reverseDirection, boolean isFindingVelocityPID)
    {
        isFinished = true;
        if (reverseDirection) {
            talon.reverseOutput(true);
            talon.reverseSensor(true);
        }
        isTalonReversed = reverseDirection;
        this.talon = talon;
        if (isFindingVelocityPID) {
            talon.setProfile(VELOCITY_PID_PROFILE);
        } else {
            talon.setProfile(POSITION_PID_PROFILE);
        }
        Ku = 0.0;
        Kp = 0.0;
        Ki = 0.0;
        Kd = 0.0;
        talon.setPID(Kp, Ki, Kd);
        talon.setF(0);
        maxForwardSpeed = 0;
        maxBackwardSpeed = 0;
        maxOverallSpeed = 0;
        initialFeedForward = 0.0;
        currentValue = 0.0;
        previousValue = 0.0;
        increaseFactor = 2.0;
        allowableError = DEFAULT_ALLOWABLE_ERROR;
        currentSetpoint = UPPER_SETPOINT;
        this.isFindingVelocityPID = isFindingVelocityPID;
        characteristics = new PIDTuningCycleCharacteristics();
    }

    private void clear() {
        talon.ClearIaccum();
        talon.clearIAccum();
        talon.clearMotionProfileTrajectories();
        talon.clearStickyFaults();
        talon.setPIDSourceType(PIDSourceType.kRate);
        talon.changeControlMode(TalonControlMode.Speed);
        talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        talon.configEncoderCodesPerRev(ENCODER_CODES_PER_REVOLUTION);
        talon.setForwardSoftLimit(0);
        talon.setReverseSoftLimit(0);
        talon.setPosition(0);
        talon.setEncPosition(0);
        talon.enableBrakeMode(true);
        talon.enable();
        talon.enableControl();
        characteristics.clear();
    }

    public double maxSpeed() throws InterruptedException {
        if (isFinished) {
            isFinished = false;
            clear();
            talon.set(500);
            System.out.println("Interim: " + talon.getSpeed() + " " + talon.getAnalogInVelocity()
            + " " + talon.getEncVelocity());
            Thread.sleep(2000);
            System.out.println("Interim 2: " + talon.getSpeed() + " " + talon.getAnalogInVelocity()
            + " " + talon.getEncVelocity());
            maxForwardSpeed = talon.getSpeed();
            talon.set(-500);
            Thread.sleep(2000);
            maxBackwardSpeed = talon.getSpeed();
            talon.set(0.0);
            if (maxForwardSpeed < maxBackwardSpeed) {
                maxOverallSpeed = maxForwardSpeed;
            } else {
                maxOverallSpeed = maxBackwardSpeed;
            }
            System.out.println("Max Forward Speed: " + maxForwardSpeed);
            System.out.println("Max Backward Speed: " + maxBackwardSpeed);
            System.out.println("Max Overall Speed: " + maxOverallSpeed);
        }
        return maxOverallSpeed;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void startTuneStage() {
        isFinished = true;
    }

    private double previousAverage;
    private int count;
    private RunningAverage error;
    boolean increaseFeedForward;

    public double setInitialFeedForward() {
        if (isFinished) {
            count = 0;
            error = new RunningAverage(20);
            currentValue = 0.1;
            increaseFeedForward = true;
        }
        int target = (LOWER_SETPOINT + UPPER_SETPOINT) / 2;
        talon.setF(currentValue);
        talon.set(target);
        if (count < 20) {
            error.average(talon.getError());
            count++;
        }
        double average = error.average();
        previousAverage = error.average();
        while (previousAverage > allowableError) {
            if (increaseFeedForward) {
                increaseValue();
            } else {
                decreaseValue();
            }
            talon.setF(currentValue);
            average = error.average(talon.getError());
            SmartDashboard.putNumber("Average Error", average);
            SmartDashboard.putNumber("Feed Forward", currentValue);
            if (average > previousAverage) {
                increaseFeedForward = false;
            } else {
                increaseFeedForward = true;
                previousAverage = average;
            }
        }
        initialFeedForward = currentValue;
        System.out.println("Initial feed forward set to " + initialFeedForward);
        return initialFeedForward;
    }

    public double findUltimateGain() {
        clear();
        currentValue = 0.01;
        talon.setP(currentValue);
        currentSetpoint = UPPER_SETPOINT;
        // Preload the Talon averages
        talon.setP(currentValue);
        talon.set(currentSetpoint);
        for (int i = 0; i < 100; i++) {
            if (isFindingVelocityPID) {
                characteristics.add(talon.getSpeed(), talon.getError());
            } else {
                characteristics.add(talon.getPosition(), talon.getError());
            }
        }
        while (characteristics.cycleTimeDividedByAverageCycleTime() > ALLOWABLE_CYCLE_TIME_ERROR) {
            alternateSetpoint();
            talon.set(currentSetpoint);
            double previousError = characteristics.shiftedAverageError();
            while (characteristics.shiftedAverageError() > previousError) {
                SmartDashboard.putNumber("Ultimate Proportional Term", currentValue);
                talon.setP(currentValue);
                increaseValue();
                characteristics.add(talon.getSpeed(), talon.getError());
            }
            decreaseValue();
            talon.setP(currentValue);
        }
        Ku = currentValue;
        System.out.println("Ultimate Proportional Term is " + Ku);
        return Ku;
    }

    private double increaseValue() {
        previousValue = currentValue;
        currentValue *= increaseFactor;
        return currentValue;
    }

    private double decreaseValue() {
        increaseFactor /= 2;
        currentValue = previousValue * increaseFactor;
        return currentValue;
    }

    private int alternateSetpoint() {
        if (currentSetpoint == LOWER_SETPOINT) {
            currentSetpoint = UPPER_SETPOINT;
        } else {
            currentSetpoint = LOWER_SETPOINT;
        }
        return currentSetpoint;
    }

}
