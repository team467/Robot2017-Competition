package org.usfirst.frc.team467.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import edu.wpi.first.wpilibj.SerialPort;

public class Gyrometer {

	private static ADIS16448_IMU gyro = null;

	private static Gyrometer gyrometer = null;

	private final int BAUD_RATE = 57600;

	private SerialPort sp = null;

	private Gyrometer(int port) {
		try {
			sp = new SerialPort(BAUD_RATE, SerialPort.Port.kUSB);
		} catch (Exception ex) {
			// eaten
		}
	}

	public static ADIS16448_IMU getInstance() {
		if (gyro == null) {
			gyro = new ADIS16448_IMU();
		}
		return gyro;
	}

	public double getAngleX() {
		return gyro.getAngleX();
	}

	public double getAngleY() {
		return gyro.getAngleY();
	}

	public double getAngleZ() {
		return gyro.getAngleZ();
	}

	public void reset() {
		gyro.reset();
	}
}
