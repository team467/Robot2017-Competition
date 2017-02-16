package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Spark;

public class Climber {
	  private Spark motor;
	  
	  private double motorSpeed = 0.7;
	  private DriverStation2017 driverstation;
	  
	  //TODO: add current sensor to stop if current is too high
	  //TODO: add touch sensor that light up led when at top?
	  //TODO: current burnout sensor?
	  public Climber(int motorChannel, DriverStation2017 driverstation)
	  {
		  motor = new Spark(motorChannel);
		  this.driverstation = driverstation;
 	  }
	  
	  public void stop(){
		  motor.set(0.0);
	  }
	  
	  public void climb(){
		  motor.set(motorSpeed);
	  }
	  
	  public void descend(){
		  motor.set(-motorSpeed);
	  }
}
