package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.ButtonPanel2017.Buttons;

import edu.wpi.first.wpilibj.Relay;

public class Agitator {
	private static Agitator instance = null;
	private static Relay motor;
	private static ButtonPanel2017 buttonPanel;

	public static Agitator getInstance() {
		if (instance == null) {
			instance = new Agitator();
		}
		return instance;
	}

	private Agitator() {
		motor = new Relay(RobotMap.AGITATOR_MOTOR);
		buttonPanel = DriverStation2017.getInstance().getButtonPanel();
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

	public void update() {
		if (buttonPanel.buttonDown(Buttons.SHOOTER_SPIN)) {
			shoot();
		} else if (buttonPanel.buttonDown(Buttons.SHOOTER_FAILSAFE)) {
			reverse();
		} else {
			stop();
		}
	}
}
