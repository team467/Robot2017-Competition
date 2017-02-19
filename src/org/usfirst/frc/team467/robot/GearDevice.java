package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.PIDController;

public class GearDevice {
    private static GearDevice instance = null;
    private static Spark spark;
    private static AnalogPotentiometer sensor;
    private static PIDController controller;
    ButtonPanel2017 buttonPanel;
    
    //TODO: no idea if angles are actually correct
    
    //= 90 + 16.29 (calculated amount)
    private static double SCOOP_ANGLE = 106.29;
    private static double GET_GEAR_ANGLE = 30;
    
    public enum Mode {
    	UP,
    	SCOOP,
    	GET,
    	PLACE;
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
        //too complex for now
    }
    
    private void updateMode() {
    	
    }
    
}

