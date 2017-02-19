package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Spark;

public class Climber {
	private static Climber climber = null;

	private static Spark[] sparks;

	public static Climber getInstance() {
		if (climber == null) {
			climber = new Climber();

		}
		return climber;
	}

	private Climber() {
		sparks = new Spark[2];
		System.out.println("Warning: update climber-spark motor values if needed");
		sparks[0] = new Spark(RobotMap.CLIMBER_MOTOR_CHANNELS[0]);
		sparks[1] = new Spark(RobotMap.CLIMBER_MOTOR_CHANNELS[1]);
	}

	public static void update(boolean goForward) {
		if (goForward) {
			for (Spark s : sparks) {
				s.set(1.0);
			}
		}

		else {
			for (Spark s : sparks) {
				s.set(0.0);
			}
		}
		antiBurnoutSafetyPrecautions();
	}

	// TODO: add anti burnout protection
	private static void antiBurnoutSafetyPrecautions() {
		System.out.println("will change later if necessary");
		return;
	}
}
