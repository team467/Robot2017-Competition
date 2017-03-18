package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.Spark;

public class GearDevice {
	private static final Logger LOGGER = Logger.getLogger(GearDevice.class);
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
		LOGGER.debug("Going Down");
		spark.set(0.5);
	}

	public void goUp() {
		LOGGER.debug("Going Up");
		spark.set(-0.5);
	}

}
