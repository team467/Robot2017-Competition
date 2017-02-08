/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;

import org.usfirst.frc.team467.robot.PIDCalibration.WheelPod;

/**
 *
 */
public class InitialFeedForwardTuner extends BaseTuner implements Tuner {

    private ArrayList<Double> errors;
    private double feedForward;
    private double lastAverageError;
    private double FEED_FORWARD_ALLOWABLE_ERROR = 5.0;


	/**
	 * @param talon
	 * @param reverseDirection
	 */
	public InitialFeedForwardTuner(WheelPod wheelPod, boolean findVelocityPID) {
		super(wheelPod, findVelocityPID);
        System.out.println("Initializing initial feed forward stage.");
        clear();
        feedForward = 0.0;
        currentValue = 1;
        increaseFactor = 1;
        lastAverageError = Double.MAX_VALUE;
        wheelPod.pid(0,0,0);
        wheelPod.set(SETPOINT);
    	errors = new ArrayList<Double>();
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.AutoCalibration.BaseTuner#process()
	 */
	@Override
	public boolean process() {
    	double reading = wheelPod.readSensor();
    	double error = wheelPod.error();
    	System.out.println(reading + " - " + SETPOINT + " = " + error);
    	if (count == 0) {
    		errors.clear();
    		wheelPod.f(currentValue);
    		wheelPod.set(SETPOINT);
        	count++;
    	} else if (count >= HOLD_PERIOD ) {
    		count = 0;
    		wheelPod.set(0);
    		double averageError = averageError();
    		System.out.println("Feed Forward: " + currentValue
    				+ " Speed or Position: " + wheelPod
    				+ " Average Error: " + averageError + " Last Error: " + lastAverageError);
    		if (Math.abs(averageError) > Math.abs(lastAverageError)) {
    			// Getting worse -- unstable
    			decreaseValue();
    		} else {
    			increaseValue();
    		}
    		lastAverageError = averageError;
    		if (Math.abs(averageError()) < FEED_FORWARD_ALLOWABLE_ERROR) {
    			feedForward = currentValue;
    	        System.out.println("Initial feed forward set to " + feedForward);
    			return true;
    		}
    	} else {
    		wheelPod.set(SETPOINT);
        	errors.add(error);
    		count++;
    	}
    	return false;
	}

    private double averageError() {
    	double sumErrors = 0.0;
    	for (double error : errors) {
    		sumErrors += error;
    	}
    	if (errors.size() > 0) {
        	return (sumErrors / (double) errors.size());
    	} else {
    		return 0.0;
    	}
    }

}
