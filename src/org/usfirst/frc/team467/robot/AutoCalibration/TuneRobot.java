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
	private TuneStage stage;


	/**
	 *
	 */
	public TuneRobot(String tuneStageString) {
		stage = TuneStage.parseString(tuneStageString);
	}

	public void init() {
		if (stage != TuneStage.NO_TUNING) {
			pods = new WheelPod[4];
			tuners = new Tuner[4];
			tuneComplete = new boolean[4];
			for (int i = 0; i < 4; i++) {
				pods[i] = new WheelPod(i);
				switch (stage) {
				case INITIAL_FEED_FORWARD:
					tuners[i] = new InitialFeedForwardTuner(pods[i], true);
					break;
				case ULTIMATE_PROPORTIONAL_TERM:
					tuners[i] = new UltimateProportionalGainTuner(pods[i]);
					break;
				case MAX_SPEED:
					tuners[i] = new MaxSpeedTuner(pods[i]);
					break;
				case POSITION:
					tuners[i] = new PositionTuner(pods[i]);
					break;
				case CHARACTERIZE:
					tuners[i] = new PIDTuningCycleCharacteristics(pods[i], true);
				default:
					break;
				}
				tuneComplete[i] = false;
				pods[i].checkReversed();
			}
//			for (int i = 0; i < 4; i++) {
//				tuneComplete [i] = tuners[i].process();
//				pods[i].moveDistance(3);
//			}
//			pods[RobotMap.FRONT_LEFT].positionMode();
//			overshoot = 0;
//			pods[RobotMap.FRONT_LEFT].moveDistance(position);
		}
	}

	public void periodic() {
		if (stage != TuneStage.NO_TUNING) {
			for (int i = 0; i < 4; i++) {
				tuneComplete [i] = tuners[i].process();
			}
		}
//		double error = ((position * 12 / RobotMap.WHEELPOD_CIRCUMFERENCE) - pods[RobotMap.FRONT_LEFT].motor().getPosition());
//		double absError = Math.abs(error);
//		if (absError > overshoot && error < 0 && absError != (position * 12 / RobotMap.WHEELPOD_CIRCUMFERENCE)) {
//			overshoot = absError;
//		}
//		System.out.println("Position: " + pods[RobotMap.FRONT_LEFT].motor().getPosition() + " Encoder Reading: " + (pods[RobotMap.FRONT_LEFT].motor().getEncPosition()/256) );
//		System.out.println("Position Error: " + error + " Overshoot: " + overshoot);
//		tuners[RobotMap.FRONT_LEFT].process();
	}

}
