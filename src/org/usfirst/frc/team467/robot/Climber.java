package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Spark;

public class Climber {
	  private Spark motorLeft;
	  private Spark motorRight;
	  
	  private double motorSpeed = 0.7;
	  private DriverStation2017 driverstation;
	  
	  //TODO: add current sensor to stop if current is too high
	  //TODO: add touch sensor that light up led when at top?
	  //TODO: current burnout sensor?
	  public Climber(int leftMotorChannel, int rightMotorChannel, DriverStation2017 driverstation)
	  {
		  motorLeft = new Spark(leftMotorChannel);
		  motorRight = new Spark(rightMotorChannel);
		  this.driverstation = driverstation;
 	  }
	  
	  public void stop(){
		  motorLeft.set(0.0);
		  motorRight.set(0.0);
	  }
	  
	  public void climb(){
		  motorLeft.set(motorSpeed);
		  motorRight.set(motorSpeed);
		  
	  }
	  
	  public void descend(){
		  motorLeft.set(-motorSpeed);
		  motorRight.set(-motorSpeed);
		  
	  }
}
