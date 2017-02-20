package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;

public class GearDevice {
	private static GearDevice instance = null;
	private static Spark spark;
	private static PIDSource sensor;
	private static PIDController controller;
	ButtonPanel2017 buttonPanel;

	// TODO: no idea if angles are actually correct

	// = 90 + 16.29 (calculated amount)
	private static double SCOOP_ANGLE = 91.0;
	private static double CARRY_ANGLE = 30;

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
		sensor = new GearPIDSource();
		controller = new PIDController(RobotMap.GEAR_PID.p, RobotMap.GEAR_PID.d, RobotMap.GEAR_PID.f, sensor, spark);
		buttonPanel = DriverStation2017.getInstance().getButtonPanel();
	}

	public void scoop() {
		controller.setSetpoint(SCOOP_ANGLE);
	}

	public void goUp() {
		controller.setSetpoint(CARRY_ANGLE);
	}

	public void getGear() {
		controller.setSetpoint(0);
	}

	public void placeGear() {
		controller.setSetpoint(SCOOP_ANGLE);
	}

}
