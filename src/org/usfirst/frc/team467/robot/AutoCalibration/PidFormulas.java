/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import com.ctre.CANTalon;

/**
 *
 */
public enum PidFormulas {

	ZIEGLER_NICHOLS_P,
	ZIEGLER_NICHOLS_PI,
	ZIEGLER_NICHOLS_PID,
	ZIEGLER_NICHOLS_PID_NO_OVERSHOOT;

	public static void set(CANTalon motor, PidFormulas formula, double Pu, double Tu) {
		switch (formula) {

		case ZIEGLER_NICHOLS_PID:
			motor.setPID((Pu * 0.6), (1.2*Tu/1000/50), (3*Pu*(Tu/1000/50)/40));
			break;

		case ZIEGLER_NICHOLS_PID_NO_OVERSHOOT:
			motor.setPID((Pu * 0.2), (1.2*Tu/1000), (3*Pu*(Tu/1000)/40));
			break;

		default:

		}
	}

}
