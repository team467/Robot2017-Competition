package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.Talon;

public class Shooter {
	Talon talon;
    DriverStation2015 driverstation;
    private static Shooter shooter = null;
    
    double speed;
    
	private Shooter(){
		talon = new Talon(4);
		driverstation = DriverStation2015.getInstance();
		speed = 0.0;
		talon.set(speed);
	}
	
	public static Shooter getInstance(){
		if (shooter == null){
			shooter = new Shooter();
		}
		return shooter;
	}
		
	public void increaseSpeed(){
		speed += 0.1;
		if (speed > 1)
			speed = 1;
	}
	public void decreaseSpeed(){
		speed -= 0.1;
		if (speed < 0)
			speed = 0;
	}
	public void shoot(boolean tf){
		if(tf){
			talon.set(speed);
		}
		else{
			talon.set(0.0);
		}
		System.out.println(speed);
	}
	

}
