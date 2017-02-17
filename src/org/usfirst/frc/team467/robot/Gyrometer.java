package org.usfirst.frc.team467.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class Gyrometer implements PIDSource {

	private ADIS16448_IMU gyro = null;
	private static Gyrometer instance;

	private Gyrometer() {
		gyro = new ADIS16448_IMU();
	}

	public static Gyrometer getInstance() {
		if (instance == null) {
			instance = new Gyrometer();
		}
		return instance;
	}
	
	public ADIS16448_IMU getIMU()
	{
		return gyro;
	}

	// base gyro returns values in degrees - 1440 degrees per rotation
	public double getAngleZRadians()
	{
		return gyro.getAngleZ() * Math.PI / 720;
	}
	
	// base gyro returns values in degrees - 1440 degrees per rotation
	public double getAngleZDegrees()
	{
		return gyro.getAngleZ() / 4;
	}
	
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		// Sorry I'm just displacement for now :P
		
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return PIDSourceType.kDisplacement;
	}

	@Override
	public double pidGet() {
		double angle = gyro.getAngleZ() / 4;
		while (angle > 180) {
			angle -= 360;
		}
		while (angle < -180) {
			angle += 360;
		}
		if (angle == 0)
		{
			gyro.reset();
		}
		return angle;
	}

}

