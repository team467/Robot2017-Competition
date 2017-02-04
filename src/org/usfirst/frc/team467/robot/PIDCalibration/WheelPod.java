/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Bryan Duerk
 *
 */
public class WheelPod
{
    public static final double P = 2.16;
    public static final double I = 0.00864;
    public static final double D = 135.0;
    public static final double F = 3.0;
    public static final int ALLOWABLE_CLOSED_LOOP_ERROR = 51;
    public static final double CIRCUMFERENCE = 18.85;

    public static final double TOP_SPEED = 400;

    private double p;
    private double i;
    private double d;
    private double f;
    private double speed;
    private int position;
    private boolean isPosition;

    private AutotunePID tuner;

    CANTalon motor;
    Preferences prefs;
    Pod pod;
    String keyHeader;
    String keyAbr;


    private static final int MAX_ERROR_COUNT = 26;
    RunningAverage averageError;

    /**
     *
     */
    public WheelPod(Pod pod)
    {
        averageError = new RunningAverage(MAX_ERROR_COUNT);
        this.pod = pod;
        prefs = Preferences.getInstance();
        keyHeader = "Pod-" + pod.name + "-PID-";

        if (!prefs.containsKey(keyHeader + "P")) {
            prefs.putDouble(keyHeader + "P", P);
        }
        p = prefs.getDouble(keyHeader + "P", P);
        if (!prefs.containsKey(keyHeader + "I")) {
            prefs.putDouble(keyHeader + "I", I);
        }
        i = prefs.getDouble(keyHeader + "I", I);
        if (!prefs.containsKey(keyHeader + "D")) {
            prefs.putDouble(keyHeader + "D", D);
        }
        d = prefs.getDouble(keyHeader + "D", D);
        if (!prefs.containsKey(keyHeader + "F")) {
            prefs.putDouble(keyHeader + "F", F);
        }
        f = prefs.getDouble(keyHeader + "F", F);
        motor = new CANTalon(pod.id);
        if (pod.isReversed) {
            reverse();
        }
        motor.enable();
        motor.setPID(p, i, d);
        motor.setF(f);
        motor.setAllowableClosedLoopErr(ALLOWABLE_CLOSED_LOOP_ERROR);
        motor.changeControlMode(TalonControlMode.Speed);
        motor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        motor.configEncoderCodesPerRev(256);
        motor.setForwardSoftLimit(11);
        motor.setReverseSoftLimit(-11);
        motor.setPosition(0);
        isPosition = false;
        tuner = new AutotunePID(motor, pod.isReversed, true);

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
     * Distance in feet.
     */
    public void moveDistance(double targetDistance) {
        if (isPosition) {
            motor.changeControlMode(TalonControlMode.Position);
            double positionTicks = targetDistance / WheelPod.CIRCUMFERENCE * 512;
            System.out.println("Position Move: " + positionTicks);
            motor.set(positionTicks);
        }
    }

    public void reverse() {
        motor.reverseSensor(true);
        motor.reverseOutput(true);
    }

    public void setPositionMode() {
        isPosition = true;
        motor.changeControlMode(TalonControlMode.Position);
        motor.setAllowableClosedLoopErr(0);
    }

    public void setSpeedMode() {
        isPosition = false;
        motor.changeControlMode(TalonControlMode.Speed);
        motor.setAllowableClosedLoopErr(ALLOWABLE_CLOSED_LOOP_ERROR);
    }

    public void update()
    {
        double newP = SmartDashboard.getNumber(pod.abr + "-P", p);
        if (newP != p)
        {
            System.out.println("Old P: " + p + " New P: " + newP);
            p = newP;
            prefs.putDouble((keyHeader + "P"), newP);
            motor.setP(newP);
        }
        double newI = SmartDashboard.getNumber(pod.abr + "-I", i);
        if (newI != i)
        {
            System.out.println("Old I: " + i + " New I: " + newI);
            i = newI;
            prefs.putDouble((keyHeader + "I"), newI);
            motor.setI(newI);
        }
        double newD = SmartDashboard.getNumber(pod.abr + "-D", d);
        if (newD != d)
        {
            System.out.println("Old D: " + d + " New D: " + newD);
            d = newD;
            prefs.putDouble((keyHeader + "D"), newD);
            motor.setD(newD);
        }
        double newF = SmartDashboard.getNumber(pod.abr + "-F", f);
        if (newF != f)
        {
            System.out.println("Old F: " + p + " New F: " + newP);
            f = newF;
            prefs.putDouble((keyHeader + "F"), newF);
            motor.setF(newF);
        }
        double newSpeed = SmartDashboard.getNumber("Speed", 0.0);
        if (newSpeed != this.speed)
        {
            System.out.println("Old Speed: " + speed + " New Speed: " + newSpeed);
            this.speed = newSpeed;
        }
        int newPosition = (int) SmartDashboard.getNumber("Position", 0.0);
        if (newPosition != this.speed)
        {
            System.out.println("Old Position: " + speed + " New Position: " + newPosition);
            this.position = newPosition;
        }
        if (isPosition) {
            motor.set(this.position);
        } else {
            motor.set(this.speed);
        }
    }

    public double averageError(double error) {
        double average = this.averageError.average(error);
        SmartDashboard.putNumber(pod.abr + "-AveErr", average);
        return average;
    }

    public double error()
    {
        double error = motor.getError();
        averageError(error);
        SmartDashboard.putNumber(pod.abr + "-Error", error);
        return error;
    }

    private TuneStage tuneStage = TuneStage.INITIAL_FEED_FORWARD;
    private boolean startTune = true;

    public void tune() {
        switch (tuneStage) {
            case INITIAL_FEED_FORWARD:
                if (startTune) {
                    startTune = false;
                    System.out.println("Getting minimum feed forward.");
                    tuner.initInitialFeedForwardStage();
                }
                tuner.setInitialFeedForward();
                if (tuner.isFinished) {
                    tuneStage = TuneStage.NO_TUNING;
//                    tuneStage = TuneStage.MAX_SPEED;
                    startTune = true;
                }
                break;

            case MAX_SPEED:
                if (startTune) {
                    startTune = false;
                    System.out.println("Going to plaid!");
                    tuner.initMaxSpeedStage();
                }
              tuner.maxSpeedStage();
                if (tuner.isFinished) {
                    tuneStage = TuneStage.NO_TUNING;
//                    tuneStage = TuneStage.ULTIMATE_PROPORTIONAL_TERM;
                    startTune = true;
                }
                break;

            case ULTIMATE_PROPORTIONAL_TERM:
                if (startTune) {
                    startTune = false;
                    System.out.println("Finding ultimate proportional gain term.");
                    tuner.initFindUltimateProportionalGainStage();
                }
                tuner.findUltimateProportionalGainStage();
                if (tuner.isFinished) {
                    tuneStage = TuneStage.FEED_FORWARD_CURVE;
                    startTune = true;
                }
                break;

            case FEED_FORWARD_CURVE:
                if (startTune) {
                    startTune = false;
                    System.out.println("Finding feed forward curve.");
                    tuner.initFindFeedForwardCurveStage();
                }
//                tuner.findFeedForwardCurveStage();
                if (tuner.isFinished) {
                    tuneStage = TuneStage.NO_TUNING;
                    startTune = true;
                }
                break;

            default:
                startTune = false;
        }
    }

}
