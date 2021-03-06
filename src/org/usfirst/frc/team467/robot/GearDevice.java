package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;

public class GearDevice {
	private final Logger LOGGER = Logger.getLogger(GearDevice.class);
	private static GearDevice instance = null;
	private Spark spark;
	private PowerDistributionPanel pdp;
	private final int pdpChannel = RobotMap.GEAR_CLIMBER_PDP_CHANNEL;
	private CheckCurrentLimit limitChecker;

	public static GearDevice getInstance() {
		if (instance == null) {
			instance = new GearDevice();
		}
		return instance;
	}

	private GearDevice() {
		spark = new Spark(RobotMap.GEAR_MOTOR);
		pdp = new PowerDistributionPanel();
		limitChecker = new CheckCurrentLimit(3, 3.5);
	}

	public void goDown() {
		final double current = pdp.getCurrent(pdpChannel);
		LOGGER.debug("Going Down: current=" + current);
		if (limitChecker.isOverLimit(current)) {
			LOGGER.warn("*** OVER CURRENT LIMIT ***");
			spark.set(0);
		} else {
			spark.set(1.0);
		}
	}

	public void goUp() {
		final double current = pdp.getCurrent(pdpChannel);
		LOGGER.debug("Going Up: current=" + current);
		if (limitChecker.isOverLimit(current)) {
			LOGGER.warn("*** OVER CURRENT LIMIT ***");
			spark.set(0);
		} else {
			spark.set(-1.0);
		}
	}

}
