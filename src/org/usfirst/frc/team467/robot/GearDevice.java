package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.PIDController;

public class GearDevice {
	private static GearDevice instance = null;
	private static Spark spark;
	private static AnalogPotentiometer sensor;
	private static PIDController controller;

	// TODO: no idea if angles are actually correct

	private static double SCOOP_ANGLE = 91.0;

	public static GearDevice getInstance() {
		if (instance == null) {
			instance = new GearDevice();
		}
		return instance;
	}

	// TODO: set to actual values of sensor
	private GearDevice() {
		spark = new Spark(RobotMap.GEAR_MOTOR);
		// MUST CHANGE
		sensor = new AnalogPotentiometer(RobotMap.GEAR_SENSOR, 360, 30);
		controller = new PIDController(RobotMap.GEAR_PID.p, RobotMap.GEAR_PID.d, RobotMap.GEAR_PID.f, sensor, spark);
	}

	public void goDown() {
		controller.setSetpoint(SCOOP_ANGLE);
	}

	public void goUp() {
		controller.setSetpoint(0);
	}

}
