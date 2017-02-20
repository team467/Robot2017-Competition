package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.networktables.*;

public class GearPIDSource implements Potentiometer {
	PIDSourceType type = PIDSourceType.kDisplacement;
	NetworkTable table;

	GearPIDSource() {
		table = NetworkTable.getTable("Sensors on Pi");
	}

	public void setPIDSourceType(PIDSourceType pidSource) {
		type = pidSource;
	}

	public PIDSourceType getPIDSourceType() {
		return type;
	}

	public double pidGet() {
		// TODO Auto-generated method stub
		return table.getNumber("X-Axis Angle", 0);
	}

	public double get() {
		// TODO Auto-generated method stub
		return table.getNumber("X-Axis Angle", 0);
	}

}
