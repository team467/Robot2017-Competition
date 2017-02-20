package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Relay;

public class BallLoader {
	private static BallLoader instance = null;
	private static Relay motor;

	public static BallLoader getInstance() {
		if (instance == null) {
			instance = new BallLoader();
		}
		return instance;
	}

	private BallLoader() {
		motor = new Relay(RobotMap.AGITATOR_MOTOR);
	}

	public void load() {
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
