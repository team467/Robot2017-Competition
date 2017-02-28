package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.Ultrasonic;

public class Ultrasonic467 {
	private static Ultrasonic467 instance;
	private Ultrasonic ultra;
	
	private Ultrasonic467(int channel) {
		ultra = new Ultrasonic(2, channel);
	}

	public static Ultrasonic467 getInstance() {
		if (instance == null) {
			instance = new Ultrasonic467(RobotMap.ULTRASONIC_SENSOR);
		}
		return instance;
	}
	
	public double getDistance() {
		return ultra.getRangeInches();
	}
	
	public Ultrasonic getSensor() {
		return ultra;
	}
	
	@Override
	public String toString() {
		return "Ultrasonic467 [distance=" + getDistance() + "]";
	}
}
