/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import org.usfirst.frc.team467.robot.WheelPod;

/**
 *
 */
public class TuneRobot {

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
					tuners[i] = new InitialFeedForwardTuner(pods[i]);
					break;
				case ULTIMATE_PROPORTIONAL_TERM:
					tuners[i] = new UltimateProportionalGainTuner(pods[i]);
					break;
				case CHARACTERIZE:
					tuners[i] = new PIDTuningCycleCharacteristics(pods[i], true);
				default:
					break;
				}
				tuneComplete[i] = false;
				pods[i].checkReversed();
			}
		}
	}

	public void periodic() {
		if (stage != TuneStage.NO_TUNING) {
			for (int i = 0; i < 4; i++) {
				tuneComplete [i] = tuners[i].process();
			}
		}
	}

}
