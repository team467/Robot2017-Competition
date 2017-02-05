package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionProcessing {
	private static VisionProcessing instance;
	
	public double targetAngle;
	public double x, y, width, height;
	
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
		targetAngle = SmartDashboard.getNumber("angle", 0.0);
		x = SmartDashboard.getNumber("x", 0.0);
		y = SmartDashboard.getNumber("y", 0.0);
		width = SmartDashboard.getNumber("w", 0.0);
		height = SmartDashboard.getNumber("h", 0.0);
	}
}
