package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionProcessing {
	private static final Logger LOGGER = Logger.getLogger(VisionProcessing.class);
	private static VisionProcessing instance;

	private double targetAngle, x, y, width, height = 0.0;
	private boolean seeTwo = false;

	private VisionProcessing() {
		update();
	}

	public static VisionProcessing getInstance() {
		if (instance == null) {
			instance = new VisionProcessing();
		}
		return instance;
	}

	public void update() {
		seeTwo = SmartDashboard.getBoolean("seeTwo", false);
		targetAngle = SmartDashboard.getNumber("angle", 0.0);
		x = SmartDashboard.getNumber("x", 0.0);
		y = SmartDashboard.getNumber("y", 0.0);
		width = SmartDashboard.getNumber("w", 0.0);
		height = SmartDashboard.getNumber("h", 0.0);
		
//		LOGGER.debug("Can see two contours = " + String.valueOf(seeTwo));
	}
	
	public double getTargetAngle() {
		return targetAngle;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public boolean canSeeTwo() {
		return seeTwo;
	}
}
