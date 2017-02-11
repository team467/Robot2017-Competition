/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.PIDCalibration.WheelPod;

/**
 *
 */
public class WheelPodTuner extends BaseTuner implements Tuner {

	private Tuner tuner;
	private TuneStage stage;
	private boolean tuneInProgress;

	/**
	 * @param wheelPod
	 * @param findVelocityPID
	 */
	public WheelPodTuner(WheelPod wheelPod, boolean findVelocityPID) {
		super(wheelPod, findVelocityPID);
		wheelPod.pidf(0.0, 0.0, 0.0, 0.0);
		stage = TuneStage.CHECK_SENSORS;
		tuneInProgress = false;
	}


	/**
	 *
	 */
	@Override
	public boolean process() {
		switch (stage) {

		case CHECK_SENSORS:
			wheelPod.pidf(2, 0, 0, 3);
			if (wheelPod.checkSensor()) {
				stage = TuneStage.ULTIMATE_PROPORTIONAL_TERM;
			} else {
				stage = TuneStage.NO_TUNING;
			}
			wheelPod.pidf(0, 0, 0, 0);
			break;

		case ULTIMATE_PROPORTIONAL_TERM:
			if (!tuneInProgress) {
				tuner = new UltimateProportionalGainTuner(wheelPod, findVelocityPID);
				tuneInProgress = true;
			} else {
				if (tuner.process()) {
					stage = TuneStage.INITIAL_FEED_FORWARD;
					tuneInProgress = false;
					break;
				}
			}
			break;

		case INITIAL_FEED_FORWARD:
			if (!tuneInProgress) {
				tuner = new InitialFeedForwardTuner(wheelPod, findVelocityPID);
				tuneInProgress = true;
			} else {
				if (tuner.process()) {
					stage = TuneStage.MAX_SPEED;
					tuneInProgress = false;
					break;
				}
			}
			break;

		case MAX_SPEED:
			if (!tuneInProgress) {
				if (findVelocityPID) {
					tuner = new MaxSpeedTuner(wheelPod);
					tuneInProgress = true;
				} else {
					stage = TuneStage.NO_TUNING;
					break;
				}
			}
			break;

		case FEED_FORWARD_CURVE:
		case NO_TUNING:
		default:
			return true;
		}

		return false;
	}

}
