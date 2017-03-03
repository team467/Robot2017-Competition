/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.RobotMap;
import org.usfirst.frc.team467.robot.WheelPod;

/**
 *
 */
public class TuneRobot {

	private double position = 6* RobotMap.WHEELPOD_CIRCUMFERENCE /12;
	private double overshoot;
	private WheelPod[] pods;
	private Tuner[] tuners;
	private boolean tuneComplete[];


	/**
	 *
	 */
	public TuneRobot() {
	}

	public void init() {
		System.out.println("Autonomous reset");
		pods = new WheelPod[4];
		tuners = new Tuner[4];
		tuneComplete = new boolean[4];
		for (int i = 0; i < 4; i++) {
			pods[i] = new WheelPod(i);
			tuners[i] = new PIDTuningCycleCharacteristics(pods[i], true);
//			tuners[i] = new PositionTuner(pods[i]);
//			tuners[i] = new WheelPodTuner(pods[i], true);
//			tuners[i] = new UltimateProportionalGainTuner(pods[i]);
//			tuners[i] = new InitialFeedForwardTuner(pods[i], true);
			tuneComplete[i] = false;
			pods[i].checkReversed();
		}
		for (int i = 0; i < 4; i++) {
//			tuneComplete [i] = tuners[i].process();
			pods[i].moveDistance(3);
		}
//		pods[RobotMap.FRONT_LEFT].positionMode();
//		overshoot = 0;
//		pods[RobotMap.FRONT_LEFT].moveDistance(position);
	}

	public void periodic() {
//		double error = ((position * 12 / RobotMap.WHEELPOD_CIRCUMFERENCE) - pods[RobotMap.FRONT_LEFT].motor().getPosition());
//		double absError = Math.abs(error);
//		if (absError > overshoot && error < 0 && absError != (position * 12 / RobotMap.WHEELPOD_CIRCUMFERENCE)) {
//			overshoot = absError;
//		}
//		System.out.println("Position: " + pods[RobotMap.FRONT_LEFT].motor().getPosition() + " Encoder Reading: " + (pods[RobotMap.FRONT_LEFT].motor().getEncPosition()/256) );
//		System.out.println("Position Error: " + error + " Overshoot: " + overshoot);
//		tuners[RobotMap.FRONT_LEFT].process();
			for (int i = 0; i < 4; i++) {
//				tuneComplete [i] = tuners[i].process();
//				pods[i].moveDistance(3);
			}

	}

}
