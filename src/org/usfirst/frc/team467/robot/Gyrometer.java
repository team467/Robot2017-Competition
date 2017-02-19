package org.usfirst.frc.team467.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class Gyrometer implements PIDSource {

	private ADIS16448_IMU imu = null;
	private static Gyrometer instance;

	private Gyrometer() {
		imu = new ADIS16448_IMU();
	}

	public static Gyrometer getInstance() {
		if (instance == null) {
			instance = new Gyrometer();
		}
		return instance;
	}
	
	public void reset() {
		imu.reset();
	}
	
	public void calibrate() {
		imu.calibrate();
	}

	public double getRobotAngleRadians() {
		if (RobotMap.ROBOT_2015) {
			return getAngleZRadians();
		} else {
			return getAngleYRadians();
		}
	}

	public double getRobotAngleDegrees() {
		if (RobotMap.ROBOT_2015) {
			return getAngleZDegrees();
		} else {
			return getAngleYDegrees();
		}
	}

	// base gyro returns values in degrees - 1440 degrees per rotation
	public double getAngleZRadians() {
		return imu.getAngleZ() * Math.PI / 720;
	}

	// base gyro returns values in degrees - 1440 degrees per rotation
	public double getAngleZDegrees() {
		return imu.getAngleZ() / 4;
	}

	public double getAngleXRadians() {
		return -imu.getAngleX() * Math.PI / 720;
	}

	public double getAngleXDegrees() {
		return imu.getAngleX() / 4;
	}

	public double getAngleYRadians() {
		return -imu.getAngleY() * Math.PI / 720;
	}

	public double getAngleYDegrees() {
		return imu.getAngleY() / 4;
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
		double angle = imu.getAngleZ() / 4;
		while (angle > 180) {
			angle -= 360;
		}
		while (angle < -180) {
			angle += 360;
		}
		if (angle == 0) {
			imu.reset();
		}
		return angle;
	}

}
