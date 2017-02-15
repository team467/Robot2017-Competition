package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.PIDController;

public class GearDevice {
	private static GearDevice geardevice = null;
	private static Spark spark;
	private static AnalogPotentiometer sensor;
	private static PIDController controller;
	
	public static GearDevice getInstance() {
		if (geardevice == null) {
			geardevice = new GearDevice();
		}
		return geardevice;
	}
	
	//TODO: set to actual values of sensor
	private GearDevice() {
		spark = new Spark(RobotMap.GEAR_DEVICE_MOTOR_CHANNEL);
		// MUST CHANGE
		sensor = new AnalogPotentiometer(RobotMap.GEAR_SENSOR_CHANNEL, 360, 30);

	}
}
