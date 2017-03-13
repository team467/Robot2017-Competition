/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

/**
 * @author Bryan Duerk
 *
 */
public enum TuneStage {
	NO_TUNING,
	CHECK_SENSORS,
	INITIAL_FEED_FORWARD,
	ULTIMATE_PROPORTIONAL_TERM,
	FEED_FORWARD_CURVE,
	POSITION,
	CHARACTERIZE;

	public static TuneStage parseString(String tuneStageString) {
		TuneStage stage = NO_TUNING;
		if (tuneStageString.compareToIgnoreCase("characterize") == 0) {
			stage =  CHARACTERIZE;
		} else if (tuneStageString.compareToIgnoreCase("proportional_gain") == 0) {
			stage =  ULTIMATE_PROPORTIONAL_TERM;
		} else if (tuneStageString.compareToIgnoreCase("feed_forward") == 0) {
			stage =  INITIAL_FEED_FORWARD;
		} else if (tuneStageString.compareToIgnoreCase("position") == 0) {
			stage =  POSITION;
		} else if (tuneStageString.compareToIgnoreCase("none") == 0) {
			stage =  NO_TUNING;
		}
		System.out.println("Tune stage set to " + tuneStageString);
		return stage;
	}
}
