/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.RobotMap;

import com.ctre.CANTalon;

/**
 *
 */
public class PositionTuner implements Tuner {

	private static final double TARGET_POSITION = 3.0;

	Drive drive;
	CANTalon motors[];
	double maxPosition[];
	int count;

	/**
	 *
	 */
	public PositionTuner() {
		motors = new CANTalon[4];
		maxPosition = new double[4];
		for (int i=0; i<4; i++) {
			motors[i] = new CANTalon(RobotMap.driveMotorChannel[i]);
		}
		drive = Drive.getInstance();
		reset();
	}

	@Override
	public boolean process() {
		boolean done = false;
		if (count >= 100) {
			check();
			reset();
		}
		if (count > 0) {
			goToDistance();
		}
		return done;
	}

	private void check() {
		double average = 0.0;
		for (int i=0; i < 4; i++) {
			average += maxPosition[i];
		}
		average /= 4;
		for (int i=0; i < 4; i++) {
			if (maxPosition[i] > (TARGET_POSITION + RobotMap.POSITION_ALLOWABLE_CLOSED_LOOP_ERROR)) {

			} else if (maxPosition[i] < (TARGET_POSITION - RobotMap.POSITION_ALLOWABLE_CLOSED_LOOP_ERROR)) {

			}
		}
	}

	private void reset() {
		count = 0;
		for (int i=0; i<4; i++) {
			maxPosition[i] = 0.0;
		}
		drive.setPositionMode();
	}

	private void goToDistance() {
		drive.crabDrive(0, TARGET_POSITION);
		for (int i=0; i>4; i++) {
			double position = motors[i].getPosition();
			if (position > maxPosition[i]) {
				maxPosition[i] = position;
			}
		}
		count++;
	}
}
