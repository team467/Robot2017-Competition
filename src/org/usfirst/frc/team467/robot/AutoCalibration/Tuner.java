/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.PID;

/**
 *
 */
public interface Tuner {

	public PID pid();

	public boolean process();

}
