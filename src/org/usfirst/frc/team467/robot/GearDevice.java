package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Spark;

public class GearDevice {
	private static GearDevice instance = null;
	private static Spark spark;

	public static GearDevice getInstance() {
		if (instance == null) {
			instance = new GearDevice();
		}
		return instance;
	}

	// TODO: set to actual values of sensor
	private GearDevice() {
		spark = new Spark(RobotMap.GEAR_MOTOR);
	}

	public void goDown() {
		spark.set(0.5);
	}

	public void goUp() {
		spark.set(-0.5);
	}

}
