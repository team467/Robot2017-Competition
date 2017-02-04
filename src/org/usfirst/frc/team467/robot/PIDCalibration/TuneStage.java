/**
 *
 */
package org.usfirst.frc.team467.robot.PIDCalibration;

/**
 * @author Bryan Duerk
 *
 */
public enum TuneStage
{
    NO_TUNING,
    INITIAL_FEED_FORWARD,
    MAX_SPEED,
    ULTIMATE_PROPORTIONAL_TERM,
    FEED_FORWARD_CURVE;
}
