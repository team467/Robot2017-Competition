/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Bryan Duerk
 *
 */
public class WheelPod extends Subsystem
{
    public static final String SUBSYSTEM = "WheelPod";
    public static final double P = 2.16;
    public static final double I = 0.00864;
    public static final double D = 135.0;
    public static final double F = 3.0;

    public static final double TOP_SPEED = 400;

    private double p;
    private double i;
    private double d;
    private double f;
    private double speed;

    CANTalon motor;

    /**
     *
     */
    public WheelPod(Pod pod)
    {
        motor = new CANTalon(pod.id);
        p = P;
        i = I;
        d = D;
        f = F;
        motor.enable();
        motor.setPID(p, i, d);
        motor.setF(f);
        speed = 0.0;
        motor.changeControlMode(TalonControlMode.Speed);
        SmartDashboard.putNumber("p", p);
        SmartDashboard.putNumber("i", i);
        SmartDashboard.putNumber("d", d);
        SmartDashboard.putNumber("f", f);
        SmartDashboard.putNumber("Speed", speed);
        SmartDashboard.putNumber("Error", 0.0);
        SmartDashboard.putNumber("Device ID", pod.id);
    }

    /*
     * LiveWindow keys
     * p
     * Type
     * d
     * f
     * Mode
     * Value
     * Enabled
     * i
     */
    public void update()
    {
        double newP = SmartDashboard.getNumber("p", p);
        if (newP != p)
        {
            System.out.println("Old P: " + p + " New P: " + newP);
            p = newP;
            motor.setP(newP);
        }
        double newI = SmartDashboard.getNumber("i", i);
        if (newI != i)
        {
            System.out.println("Old I: " + i + " New I: " + newI);
            i = newI;
            motor.setI(newI);
        }
        double newD = SmartDashboard.getNumber("d", d);
        if (newD != d)
        {
            System.out.println("Old D: " + d + " New D: " + newD);
            d = newD;
            motor.setD(newD);
        }
        double newF = SmartDashboard.getNumber("f", f);
        if (newF != f)
        {
            System.out.println("Old F: " + p + " New F: " + newP);
            f = newF;
            motor.setF(newF);
        }
        double newSpeed = SmartDashboard.getNumber("Speed", 0.0);
        if (newSpeed != this.speed)
        {
            System.out.println("Old Speed: " + speed + " New Speed: " + newSpeed);
            this.speed = newSpeed;
        }
        motor.set(this.speed);
    }

    public double error()
    {
        double error = motor.getError();
        SmartDashboard.putNumber("Error", error);
        return error;
    }

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub

    }

}
