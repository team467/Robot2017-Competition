package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Spark;

public class BallIntake {
	private static BallIntake ballintake = null;
	private static Spark spark;

	public static BallIntake getInstance() {
		if (ballintake == null) {
			ballintake = new BallIntake();
		}
		return ballintake;
	}

	private BallIntake() {
		spark = new Spark(RobotMap.INTAKE_MOTOR);
	}

	public void startIntake() {
		spark.set(1.0);
	}

	public void reverse() {
		// reverse should be slower than intake
		spark.set(-1.0);
	}

	public void stop() {
		spark.set(0.0);
	}

}
