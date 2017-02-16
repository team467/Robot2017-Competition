package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Spark;

public class Intake
{
    private Spark motor;
    
    int motorSpeed = 1;
    
    public Intake(int motorChannel)
    {
        motor = new Spark(motorChannel);
    }
    
    public void in()
    {
        motor.set(motorSpeed);
    }
    
    public void out()
    {
        motor.set(-motorSpeed);
    }
    
    public void stop()
    {
        motor.set(0.0);
    }

}
