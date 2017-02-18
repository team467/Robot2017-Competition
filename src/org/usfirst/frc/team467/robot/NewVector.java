package org.usfirst.frc.team467.robot;

// Simple vector class
public class NewVector {
	private double vx;
	private double vy;

	NewVector(double x, double y) {
		vx = x;
		vy = y;
	}

	/**
	 * Add to this vector - return a new vector as the result
	 *
	 * @param v 
	 * @return
	 */
	NewVector Add(NewVector v) {
		return new NewVector(vx + v.vx, vy + v.vy);
	}

	/**
	 * @return The x value of the vector
	 */
	double getX() {
		return vx;
	}

	/**
	 * @return The y value of the vector
	 */
	double getY() {
		return vy;
	}

	/**
	 * @return The angle of the vector, clockwise from the Y axis
	 */
	double getAngle() {
		double angle = 0;
		// avoid divide by zero issues
		if (vy == 0.0) {
			if (vx < 0) {
				angle = -Math.PI;
			}
		} else if (vy > 0.0) {
			angle = LookUpTable.getArcTan(vx / vy);
		} else if (vx >= 0) {
			angle = LookUpTable.getArcTan(vx / vy) + Math.PI;
		} else {
			angle = LookUpTable.getArcTan(vx / vy) - Math.PI;
		}
		return (angle);
	}

	/**
	 * @return The magnitude value of the vector
	 */
	double getMagnitude() {
		double magnitude = Math.sqrt( (vx * vx) + (vy * vy) );
		return (magnitude);
	}
}
