package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.Spark;

public class BallIntake {
	private static  BallIntake ballintake = null;
	private static Spark spark;
	
	public static BallIntake getInstance() {
		if (ballintake == null) {
			ballintake = new BallIntake();
		}
		return ballintake;
	}
	
	private BallIntake() {
		spark = new Spark(RobotMap.BALL_INTAKE_MOTOR_CHANNEL);
	}
	
	public static void startIntake() {
		spark.set(1.0);
	}
	
	public static void reverse() {
		//reverse should be slower than intake
		spark.set(-0.1);
	}
	
}
