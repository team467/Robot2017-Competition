/**
 *
 */
package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;

import com.ctre.CANTalon;

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
	public InitialFeedForwardTuner(CANTalon talon, boolean reverseDirection, boolean findVelocityPID) {
		super(talon, reverseDirection, findVelocityPID);
        System.out.println("Initializing initial feed forward stage.");
        clear();
        feedForward = 0.0;
        currentValue = 1;
        increaseFactor = 1;
        lastAverageError = Double.MAX_VALUE;
        talon.setPID(0,0,0);
        talon.set(SETPOINT);
    	errors = new ArrayList<Double>();
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc.team467.robot.AutoCalibration.BaseTuner#process()
	 */
	@Override
	public boolean process() {
    	double speed = talon.getSpeed();
    	double position = talon.getPosition();
    	double error = talon.getError();
    	System.out.println(speed + " - " + talon.getSetpoint() + " = " + error);
    	if (count == 0) {
    		errors.clear();
        	talon.setF(currentValue);
        	talon.set(SETPOINT);
        	count++;
    	} else if (count >= HOLD_PERIOD ) {
    		count = 0;
    		talon.set(0);
    		double averageError = averageError();
    		System.out.println("Feed Forward: " + currentValue
    				+ " Speed: " + speed + " Position: " + position
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
        	talon.set(SETPOINT);
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
