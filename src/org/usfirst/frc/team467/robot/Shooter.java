package org.usfirst.frc.team467.robot;

import com.ctre.CANTalon;

public class Shooter
{
    private CANTalon motorRight;
    private CANTalon motorLeft;
    
    private int motorSpeed = 1;
    
    public Shooter(int motorChannelRight, int motorChannelLeft, DriverStation2017 driverstation) 
    {
        motorRight = new CANTalon(motorChannelRight);
        motorLeft = new CANTalon(motorChannelLeft);
    }
    
    public void spin() 
    {
        motorRight.set(motorSpeed);
        motorLeft.set(-motorSpeed);
        
        
        //use PID for below
        //TODO: when reaches top speed signal driver
        //TODO: make sure they also spin at the same speed
    }
    
    public void failsafe()
    {
        //spins backwards to dislodge balls
        motorRight.set(-motorSpeed);
        motorLeft.set(motorSpeed);
    }
    
    public void stop()
    {
        motorRight.set(0.0);
        motorLeft.set(0.0);
    }
    

}
