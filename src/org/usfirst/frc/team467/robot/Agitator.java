package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Relay;

public class Agitator {
	private static Agitator instance = null;
    private static Relay motor;
    
    public static Agitator getInstance() {
    	if (instance == null) {
    		instance = new Agitator();
    	}
    	return instance;
    }
    private Agitator() {
        motor = new Relay(RobotMap.AGITATOR_MOTOR);
    }
    
    public void shoot() {
        motor.setDirection(Relay.Direction.kForward);
        motor.set(Relay.Value.kOn);
    }
    public void reverse() {
        motor.setDirection(Relay.Direction.kReverse);
        motor.set(Relay.Value.kOn);
    }
    public void stop() {
        motor.set(Relay.Value.kOff);
    }

}
