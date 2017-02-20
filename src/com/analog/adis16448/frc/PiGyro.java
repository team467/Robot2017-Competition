package com.analog.adis16448.frc;
import edu.wpi.first.wpilibj.networktables.*;

public class PiGyro {
	NetworkTable table;
	
	PiGyro() {
		table = NetworkTable.getTable("Sensors on Pi");
	}
	
	double getAngleX() {
		
	}
	double getAngleZ() {
		
	}
	double getAngleY() {
		
	}
}
