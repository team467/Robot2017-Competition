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
	private static double SCOOP_ANGLE = 106.29;
	private static double GET_GEAR_ANGLE = 30;

	public enum Mode {
		UP, SCOOP, GET, PLACE;
	}

	private static Mode mode = Mode.UP;

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
		controller.setSetpoint(0);
	}

	public void getGear() {
		controller.setSetpoint(GET_GEAR_ANGLE);
	}

	public void placeGear() {
		// too complex for now
	}

	public void updateMode() {
		if (buttonPanel.buttonDown(Buttons.GEAR_UP)) {
			mode = Mode.UP;
		} else if (buttonPanel.buttonDown(Buttons.GEAR_SCOOP)) {
			if (mode == Mode.SCOOP) {
				mode = Mode.UP;
			} else {
				mode = Mode.SCOOP;
			}
		} else if (buttonPanel.buttonDown(Buttons.GEAR_GET)) {
			if (mode == Mode.GET) {
				mode = Mode.UP;
			} else {
				mode = Mode.GET;
			}
		} else if (buttonPanel.buttonDown(Buttons.GEAR_PLACE)) {
			if (mode == Mode.PLACE) {
				mode = Mode.UP;
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
		case UP:
    		goUp();
    		break;
    	default:
    		goUp();
    		break;
    	}
    	
    }
    
}

