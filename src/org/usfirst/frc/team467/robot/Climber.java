package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Spark;

public class Climber {
<<<<<<< HEAD
	  private static Climber instance;
	  private static Spark motorLeft;
	  private static Spark motorRight;
	  
	  private static double motorSpeed = 0.7;
	  
	  public static Climber getInstance() {
		  if (instance == null) {
			  instance = new Climber();
		  }
		  return instance;
	  }
	  
	  //TODO: add current sensor to stop if current is too high
	  //TODO: add touch sensor that light up led when at top?
	  //TODO: current burnout sensor?
	  public Climber()
	  {
		  motorLeft = new Spark(RobotMap.CLIMBER_MOTOR_1);
		  motorRight = new Spark(RobotMap.CLIMBER_MOTOR_2);
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
