package org.usfirst.frc.team467.robot;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class Gyrometer {
	
	private static Gyrometer gyrometer = null;
	
	AnalogGyro gyro = new AnalogGyro(0);

	public Gyrometer(){
		AnalogGyro gyro = new AnalogGyro(0);
	}
	public static Gyrometer getInstance(){
		if (gyrometer == null ){
			gyrometer = new Gyrometer();
		}
		return gyrometer;
	}
	public double getAngle(){
		return gyro.getAngle();
	}
	public void reset(){
		gyro.reset();
	}
}
