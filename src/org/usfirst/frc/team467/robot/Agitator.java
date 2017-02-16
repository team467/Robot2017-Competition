package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Relay;

public class Agitator
{
    private Relay motor;
    
    public Agitator(int channelNumber)
    {
        motor = new Relay(channelNumber);
    }
    
    public void shoot()
    {
        motor.setDirection(Relay.Direction.kForward);
        motor.set(Relay.Value.kOn);
    }
    public void reverse()
    {
        motor.setDirection(Relay.Direction.kReverse);
        motor.set(Relay.Value.kOn);
    }
    public void stop()
    {
        motor.set(Relay.Value.kOff);
    }

}
