package org.usfirst.frc.team467.robot;

import com.analog.adis16448.frc.PiGyro;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/*
 *  Simple wrapper class around a gyro. This is implemented as a singleton
 */
public class Gyrometer implements PIDSource {

	private PiGyro imu = null;
	private static Gyrometer instance;

	/*
	 * private constructor (singleton pattern)
	 */
	private Gyrometer() {
		imu = new PiGyro();
	}

	/**
	 * Returns a single instance of the gyro object.
	 */
	public static Gyrometer getInstance() {
		if (instance == null) {
			instance = new Gyrometer();
		}
		return instance;
	}

	/*
	 * Reset gyro
	 */
	public void reset() {
		imu.reset();
	}

	/*
	 * Calibrate gyro
	 */
	public void calibrate() {
		imu.calibrate();
	}

	/**
	 * Returns the angle of the robot orientation in Radians. Robot is assumed to be pointing forward at 0.0. Clockwise rotation is
	 * positive, counter clockwise rotation is negative
	 *
	 * @return the robot angle
	 */
	public double getRobotAngleRadians() {
		if (RobotMap.robotID == RobotMap.RobotID.ROBOT2015) {
			return getAngleZRadians();
		} else {
			//no idea what
			System.out.println("HILOO");
			return getAngleZRadians();
		}
	}

	/**
	 * Returns the angle of the robot orientation in Degrees. Robot is assumed to be pointing forward at 0.0. Clockwise rotation is
	 * positive, counter clockwise rotation is negative
	 *
	 * @return the robot angle
	 */
	public double getRobotAngleDegrees() {
		if (RobotMap.robotID == RobotMap.RobotID.ROBOT2015) {
			return getAngleZDegrees();
		} else {
			return getAngleYDegrees();
		}
	}

	/**
	 * Returns the Z angle of the gyro in Radians. Note, the IMU returns 1440 degrees per rotation.
	 *
	 * @return the gyro angle
	 */
	public double getAngleZRadians() {
		return imu.getAngleZ() * Math.PI / 720;
	}

	/**
	 * Returns the Z angle of the gyro in Degrees. Note, the IMU returns 1440 degrees per rotation.
	 *
	 * @return the gyro angle
	 */
	public double getAngleZDegrees() {
		return imu.getAngleZ() / 4;
	}

	/**
	 * Returns the X angle of the gyro in Radians. Note, the IMU returns 1440 degrees per rotation.
	 *
	 * @return the gyro angle
	 */
	public double getAngleXRadians() {
		return -imu.getAngleX() * Math.PI / 720;
	}

	/**
	 * Returns the X angle of the gyro in Degrees. Note, the IMU returns 1440 degrees per rotation.
	 *
	 * @return the gyro angle
	 */
	public double getAngleXDegrees() {
		return imu.getAngleX() / 4;
	}

	/**
	 * Returns the Y angle of the gyro in Radians. Note, the IMU returns 1440 degrees per rotation.
	 *
	 * @return the gyro angle
	 */
	public double getAngleYRadians() {
		return -imu.getAngleY() * Math.PI / 720;
	}

	/**
	 * Returns the Y angle of the gyro in Degrees. Note, the IMU returns 1440 degrees per rotation.
	 *
	 * @return the gyro angle
	 */
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
