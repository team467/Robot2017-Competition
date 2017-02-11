/**
 * 
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.PIDCalibration.WheelPod;

/**
 * @author duerkb
 *
 */
public class WheelPodTuner extends BaseTuner implements Tuner {

	/**
	 * @param wheelPod
	 * @param findVelocityPID
	 */
	public WheelPodTuner(WheelPod wheelPod, boolean findVelocityPID) {
		super(wheelPod, findVelocityPID);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.AutoCalibration.BaseTuner#process()
	 */
	@Override
	public boolean process() {
		// TODO Auto-generated method stub
		return false;
	}

}
