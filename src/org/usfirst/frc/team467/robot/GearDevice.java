package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.ButtonPanel2017.Buttons;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.PIDController;

public class GearDevice {
	private static GearDevice instance = null;
	private static Spark spark;
	private static AnalogPotentiometer sensor;
	private static PIDController controller;
	ButtonPanel2017 buttonPanel;

	// TODO: no idea if angles are actually correct

	// = 90 + 16.29 (calculated amount)
	private static double SCOOP_ANGLE = 91.0;
	private static double CARRY_ANGLE = 30;

	public enum Mode {
		CARRY, SCOOP, GET, PLACE;
	}

	private static Mode mode = Mode.CARRY;

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

	public void updateMode() {
		if (buttonPanel.buttonDown(Buttons.GEAR_UP)) {
			mode = Mode.CARRY;
		} else if (buttonPanel.buttonDown(Buttons.GEAR_SCOOP)) {
			if (mode == Mode.SCOOP) {
				mode = Mode.CARRY;
			} else {
				mode = Mode.SCOOP;
			}
		} else if (buttonPanel.buttonDown(Buttons.GEAR_GET)) {
			if (mode == Mode.GET) {
				mode = Mode.CARRY;
			} else {
				mode = Mode.GET;
			}
		} else if (buttonPanel.buttonDown(Buttons.GEAR_PLACE)) {
			if (mode == Mode.PLACE) {
				mode = Mode.CARRY;
			} else {
				mode = Mode.PLACE;
			}
		}

	}

	public void update() {
		updateMode();
		switch (mode) {
		case SCOOP:
			scoop();
			break;
		case GET:
			getGear();
			break;
		case PLACE:
			placeGear();
			break;
		case CARRY:
    		goUp();
    		break;
    	default:
    		goUp();
    		break;
    	}
    	
    }
    
}

