package org.usfirst.frc.team467.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;

public class Gyrometer {

	private static ADIS16448_IMU gyro = null;

	private Gyrometer(int port) {
	}

	public static ADIS16448_IMU getInstance() {
		if (gyro == null) {
			gyro = new ADIS16448_IMU();
		}
		return gyro;
	}
}
