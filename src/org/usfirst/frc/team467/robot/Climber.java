package org.usfirst.frc.team467.robot;

import com.ctre.CANTalon;

public class Climber {
	  private CANTalon motor;
	  
	  private double motorSpeed = 0.7;
	  private DriverStation2017 driverstation;
	  
	  //TODO: add current sensor to stop if current is too high
	  //TODO: add touch sensor that light up led when at top?
	  //TODO: current burnout sensor?
	  public Climber(int motorChannel, DriverStation2017 driverstation)
	  {
		  motor = new CANTalon(motorChannel);
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
	  
	  enum direction{
		  UP, DOWN, STOP
	  }
}
