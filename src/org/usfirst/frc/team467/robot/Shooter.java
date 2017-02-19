package org.usfirst.frc.team467.robot;

import com.ctre.CANTalon;

public class Shooter {
	private static Shooter instance = null;
	
    private static CANTalon motorRight;
    private static CANTalon motorLeft;
    
    public static Shooter getInstance() {
    	if (instance == null) {
    		instance = new Shooter();
    	}
    	return instance;
    }
    
    private int motorSpeed = 1;
    
    private Shooter() {
        motorRight = new CANTalon(RobotMap.SHOOTER_MOTOR_1);
        motorLeft = new CANTalon(RobotMap.SHOOTER_MOTOR_2);
    }
    
    public void shoot() {
        motorRight.set(motorSpeed);
        motorLeft.set(-motorSpeed);
        
        
        //use PID for below
        //TODO: when reaches top speed signal driver
        //TODO: make sure they also spin at the same speed
    }
    
    public void failsafe() {
        //spins backwards to dislodge balls
        motorRight.set(-motorSpeed);
        motorLeft.set(motorSpeed);
    }
    
    public void stop() {
        motorRight.set(0.0);
        motorLeft.set(0.0);
    }
    
}
