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
	MAX_SPEED,
	ULTIMATE_PROPORTIONAL_TERM,
	FEED_FORWARD_CURVE,
	POSITION,
	CHARACTERIZE;

	public static TuneStage parseString(String tuneStageString) {
		TuneStage stage = NO_TUNING;
		if (tuneStageString.compareToIgnoreCase("characterize") == 0) {
			stage =  CHARACTERIZE;
		} else if (tuneStageString.compareToIgnoreCase("proportional gain") == 0) {
			stage =  ULTIMATE_PROPORTIONAL_TERM;
		} else if (tuneStageString.compareToIgnoreCase("feed forward") == 0) {
			stage =  INITIAL_FEED_FORWARD;
		} else if (tuneStageString.compareToIgnoreCase("max speed") == 0) {
			stage =  MAX_SPEED;
		} else if (tuneStageString.compareToIgnoreCase("position") == 0) {
			stage =  POSITION;
		} else if (tuneStageString.compareToIgnoreCase("none") == 0) {
			stage =  NO_TUNING;
		}
		return stage;
	}
}
