/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

/**
 *
 * The process for autotuning PID: 1. Determine the max speed of the motor by applying 100% voltage and reading the speed from the
 * Talon SRX. a. Repeat for reverse. 1. Set top and bottom set points. 2. Increase the Feed Forward until the average error is at a
 * minimum. 3. Moving between top and bottom set points a. Increase the P value slowly until the system oscillates. At each
 * increase, double the previous. If it oscillates, go back to the last good value and increase at half until P is dialed in.
 *
 */
public class AutotunePID {
	private double Kp;
	private double Ki;
	private double Kd;

	// Meta data for calculating values

	boolean increaseFeedForward;

	/**
	 *
	 */
	public AutotunePID() {
		Kp = 0.0;
		Ki = 0.0;
		Kd = 0.0;
	}

	public void computePID(TuningRule rule, double Ku, double Tu) {
		switch (rule) {

		case ZIEGLER_NICHOLS_P_STANDARD:
			Kp = 0.5 * Ku;
			Ki = 0.0;
			Kd = 0.0;
			break;

		case ZIEGLER_NICHOLS_PI_STANDARD:
			Kp = 0.45 * Ku;
			Ki = 0.833333 * Tu;
			Kd = 0.0;
			break;

		case ZIEGLER_NICHOLS_PID_STANDARD:
			Kp = 0.6 * Ku;
			Ki = 0.5 * Tu;
			Kd = 0.125 * Tu;
			break;

		case PESSEN_INTEGRAL_RULE:
			Kp = 0.7 * Ku;
			Ki = 0.4 * Tu;
			Kd = 0.15 * Tu;
			break;

		case ZIEGLER_NICHOLS_PID_SOME_OVERSHOOT:
			Kp = 0.333333 * Ku;
			Ki = 0.5 * Tu;
			Kd = 0.333333 * Tu;
			break;

		case ZIEGLER_NICHOLS_PID_NO_OVERSHOOT:
			Kp = 0.2 * Ku;
			Ki = 0.5 * Tu;
			Kd = 0.333333 * Tu;
			break;

		default:
			Kp = 0.0;
			Ki = 0.0;
			Kd = 0.0;
		}
	}

}
