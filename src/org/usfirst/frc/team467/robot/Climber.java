package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.ButtonPanel2017.Buttons;

import edu.wpi.first.wpilibj.Spark;

public class Climber {
	private static Climber instance;
	private static Spark motorLeft;
	private static Spark motorRight;
	private static ButtonPanel2017 buttonPanel;

	private static double motorSpeed = 0.7;

	public static Climber getInstance() {
		if (instance == null) {
			instance = new Climber();
		}
		return instance;
	}

	// TODO: add current sensor to stop if current is too high
	// TODO: add touch sensor that light up led when at top?
	// TODO: current burnout sensor?
	public Climber() {
		motorLeft = new Spark(RobotMap.CLIMBER_MOTOR_1);
		motorRight = new Spark(RobotMap.CLIMBER_MOTOR_2);
		buttonPanel = DriverStation2017.getInstance().getButtonPanel();
	}

	private void stop() {
		motorLeft.set(0.0);
		motorRight.set(0.0);
	}

	private void climb() {
		motorLeft.set(motorSpeed);
		motorRight.set(motorSpeed);
	}

	private void descend() {
		motorLeft.set(-motorSpeed);
		motorRight.set(-motorSpeed);
	}

	public void update() {
		if (buttonPanel.buttonDown(Buttons.CLIMBER_UP)) {
			climb();
		} else if (buttonPanel.buttonDown(Buttons.CLIMBER_DOWN)) {
			descend();
		} else {
			stop();
		}
	}
	  
}
