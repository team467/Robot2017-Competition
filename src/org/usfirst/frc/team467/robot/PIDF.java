/**
 *
 */
package org.usfirst.frc.team467.robot;

/**
 * Holder class for PID and Feed Forward values
 */
public class PIDF {
	/**
	 * Proportional control term. It is the base term for getting to the target
	 * point.
	 */
	public final double p;

	/**
	 * Integral control term, it gets to the target point faster.
	 */
	public final double i;

	/**
	 * Derivative control term. It reduces thrash.
	 */
	public final double d;

	/**
	 * The feed forward term controls for static resistance such as friction.
	 */
	public final double f;

	/*
	 * The values may only be set in the constructor as they are final.
	 */
	PIDF(double p, double i, double d, double f) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
	}
}
