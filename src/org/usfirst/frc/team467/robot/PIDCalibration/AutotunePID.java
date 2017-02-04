/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

import java.util.ArrayList;

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

    public static final double DEFAULT_ALLOWABLE_ERROR = 2.0;
    public static final double ALLOWABLE_CYCLE_TIME_ERROR = 0.5;

    boolean isFinished;

    CANTalon talon;
    boolean isTalonReversed;

    private double maxForwardSpeed;
    private double maxBackwardSpeed;
    private double maxOverallSpeed;
    private double initialFeedForward;

    private double Ku;
    private double Tu;
    private double Kp;
    private double Ki;
    private double Kd;

    private double currentValue;
    private double previousValue;
    private double increaseFactor;
    private int currentSetpoint;
    private double allowableError;

    // Meta data for calculating values
    private boolean isFindingVelocityPID;
    private long stageStartTime;
    private double previousAverage;
    private int count;
    private RunningAverage error;
    boolean increaseFeedForward;
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
        Tu = 0.0;
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
        talon.configEncoderCodesPerRev(256);
        talon.setAllowableClosedLoopErr(0);
        talon.setForwardSoftLimit(0);
        talon.setReverseSoftLimit(0);
        talon.setPosition(0);
        talon.setEncPosition(0);
        talon.enableBrakeMode(true);
        talon.enable();
        talon.enableControl();
        characteristics.clear();
    }

    public void computePID(TuningRule rule) {
        switch (rule) {

            case ZIEGLER_NICHOLS_P_STANDARD:
                Kp = 0.5 * Ku;
                Ki = 0.0;
                Kd = 0.0;
                break;

            case ZIEGLER_NICHOLS_PI_STANDARD:
                Kp = 0.45 * Ku;
                Ki = 0.833333 * Tu;
                Kd = 0.0;
                break;

            case ZIEGLER_NICHOLS_PID_STANDARD:
                Kp = 0.6 * Ku;
                Ki = 0.5 * Tu;
                Kd = 0.125 * Tu;
                break;

            case PESSEN_INTEGRAL_RULE:
                Kp = 0.7 * Ku;
                Ki = 0.4 * Tu;
                Kd = 0.15 * Tu;
                break;

            case ZIEGLER_NICHOLS_PID_SOME_OVERSHOOT:
                Kp = 0.333333 * Ku;
                Ki = 0.5 * Tu;
                Kd = 0.333333 * Tu;
                break;

            case ZIEGLER_NICHOLS_PID_NO_OVERSHOOT:
                Kp = 0.2 * Ku;
                Ki = 0.5 * Tu;
                Kd = 0.333333 * Tu;
                break;

            default:
                Kp = 0.0;
                Ki = 0.0;
                Kd = 0.0;
        }
    }

    private double increaseValue() {
        previousValue = currentValue;
        currentValue *= increaseFactor;
        System.out.println("Increased to " + currentValue);
        return currentValue;
    }

    private double decreaseValue() {
        increaseFactor /= 2.0;
        currentValue = previousValue * increaseFactor;
        System.out.println("Decreased to " + currentValue);
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

    public boolean isFinished() {
        return isFinished;
    }

    public void initMaxSpeedStage() {
        System.out.println("Starting max speed stage.");
        clear();
        talon.changeControlMode(TalonControlMode.PercentVbus);
        isFinished = false;
        stageStartTime = -1;
        maxForwardSpeed = 0.0;
        maxBackwardSpeed = 0.0;
        maxOverallSpeed = 0.0;
        talon.setPID(2.16, 0.00864, 135.0);
        talon.setF(3);
    }

    public double maxSpeedStage() {
        if (!isFinished) {
            if (stageStartTime == -1) {
                stageStartTime = System.currentTimeMillis();
            }
            if ((System.currentTimeMillis() - stageStartTime) < 2000) {
                talon.set(1);
                maxForwardSpeed = talon.getSpeed();
            } else if ((System.currentTimeMillis() - stageStartTime) < 4000) {
                talon.set(-1);
                maxBackwardSpeed = talon.getSpeed();
            } else {
                talon.set(0.0);
                isFinished = true;
                if (maxForwardSpeed < maxBackwardSpeed) {
                    maxOverallSpeed = Math.abs(maxForwardSpeed);
                } else {
                    maxOverallSpeed = Math.abs(maxBackwardSpeed);
                }
                System.out.println("Max Forward Speed: " + maxForwardSpeed);
                System.out.println("Max Backward Speed: " + maxBackwardSpeed);
                System.out.println("Max Overall Speed: " + maxOverallSpeed);
            }
        }
        return maxOverallSpeed;
    }

    public void initInitialFeedForwardStage() {
        System.out.println("Initializing initial feed forward stage.");
        clear();
        isFinished = false;
        count = 0;
        error = new RunningAverage(10);
        currentValue = 3;
        increaseFactor = 2;
        increaseFeedForward = true;
        talon.setPID(3,0,0);
        talon.set((LOWER_SETPOINT + UPPER_SETPOINT) / 2);
        previousError = 100;
    }

    private double previousError;

    public double setInitialFeedForward() {
        talon.setF(currentValue);
//        error.average(talon.getError());
        if (count < 10) {
            count++;
//            previousError = Math.abs(talon.getError());
//            previousAverage = error.average(talon.getError());
        } else {
            double currentError = Math.abs(talon.getError());
            System.out.println("ERROR: " + talon.getError() + " E':" + currentError
                    + " AE:" + allowableError + " PE:" + previousError
                    + " F:" + talon.getF() + " F':" + currentValue
                    + " S:" + talon.getSpeed()
                    + " S':" + talon.getSetpoint());
            count = 0;
//            double average = error.average(talon.getError());
//            SmartDashboard.putNumber("Average Error", average);
//            if (previousAverage > allowableError) {
              if (previousError > allowableError) {
                if (increaseFeedForward) {
                    increaseValue();
                } else {
                    decreaseValue();
                }
            } else {
                initialFeedForward = currentValue;
                System.out.println("Initial feed forward set to " + initialFeedForward);
                SmartDashboard.putNumber("Feed Forward", currentValue);
                isFinished = true;
            }
//            System.out.println("Average Error: " + average + " previous: " + previousAverage);
//              if (average > previousAverage) {
            if (currentError > previousError) {
                increaseFeedForward = false;
            } else {
                increaseFeedForward = true;
//                previousAverage = average;
                previousError = currentError;
            }
            SmartDashboard.putNumber("Feed Forward", currentValue);
        }
        return initialFeedForward;
    }

    public void initFindUltimateProportionalGainStage() {
        clear();
        isFinished = false;
        currentValue = 0.01;
        currentSetpoint = UPPER_SETPOINT;
        count = 0;
        Ku = 0.0;
        Tu = 0.0;
        previousAverage = 0.0;
    }

    /**
     *
     * @return the ultimate proportional gain if isFinished = true, otherwise 0.0
     */
    public double findUltimateProportionalGainStage() {
        talon.setP(currentValue);
        talon.set(currentSetpoint);
        // Preload the Talon averages
        if (count < 10) {
            if (isFindingVelocityPID) {
                characteristics.add(talon.getSpeed(), talon.getError());
            } else {
                characteristics.add(talon.getPosition(), talon.getError());
            }
            count++;
            previousAverage = characteristics.shiftedAverageError();
        } else {
            if (characteristics.cycleTimeDividedByAverageCycleTime() > ALLOWABLE_CYCLE_TIME_ERROR) {
                alternateSetpoint();
                if (characteristics.shiftedAverageError() > previousAverage) {
                    SmartDashboard.putNumber("Ultimate Proportional Term", currentValue);
                    increaseValue();
                    characteristics.add(talon.getSpeed(), talon.getError());
                } else {
                    decreaseValue();
                }
                previousAverage = characteristics.shiftedAverageError();
            } else {
                isFinished = true;
                Ku = currentValue;
                System.out.println("Ultimate Proportional Term is " + Ku);
            }
        }
        return Ku;
    }

    private ArrayList<Double> feedForwardCurve;
    private int target;

    public void initFindFeedForwardCurveStage() {
        clear();
        isFinished = false;
        feedForwardCurve = new ArrayList<Double>();
        target = 1;
    }

    public void findFeedForwardCurveStage() {
        if (target > (int) maxOverallSpeed) {
            isFinished = true;
        } else {
            target++;
        }

    }

}
