package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;

import org.usfirst.frc.team467.robot.PIDCalibration.WheelPod;

public class PIDTuningCycleCharacteristics  extends BaseTuner implements Tuner {


    private boolean goingUp;
    private double lastSpeed;
	private long startTime;
	private double maxAmplitude;
    private ArrayList<Long> cycleTimes;

    public PIDTuningCycleCharacteristics(WheelPod wheelPod, boolean findVelocityPID) {
		super(wheelPod, findVelocityPID);
    	System.out.println("Characterizing PID values.");
    	cycleTimes = new ArrayList<Long>();
    	clear();
    	currentValue = 0.1;
    	previousValue = currentValue;
    	increaseFactor = 0.01;
    	pid(currentValue, 0, 0);
    	f(0);
    	if (findVelocityPID) {
        	wheelPod.speedMode();
    	} else {
    		wheelPod.positionMode();
    	}
    	cycleTimes.clear();
    	count = 0;
	}

	@Override
	public boolean process() {
    	double speed = wheelPod.readSensor();
    	double error = Math.abs(wheelPod.error());
    	long time = System.currentTimeMillis();
    	if (count == 0) {
    		startTime = time;
    		cycleTimes.clear();
	        wheelPod.set(setpoint);
        	count++;
    	} else if (error < DEFAULT_ALLOWABLE_ERROR) {
    		set(0);
    		wheelPod.percentVoltageBusMode();
    		System.out.println("Max Overshoot: " + maxAmplitude + " Converge Time: " + ((time - startTime)/1000));
    	} else if (count >= HOLD_PERIOD ) {
    		System.out.println("Did not converge in hold period.");
    		set(0);
    		wheelPod.percentVoltageBusMode();
    		return true;
    	} else {
    		if (goingUp) {
    			if (speed  < lastSpeed) {
    				// Found peek
    				if (error > maxAmplitude) {
    					maxAmplitude = error;
    				}
    				goingUp = false;
    			}
    		} else { // Going down
       			if (speed > lastSpeed) {
    				// Found trough
       				if (maxAmplitude > error) {
    					maxAmplitude = error;
       				}
    				goingUp = true;
    			}
    		}
    	}
    	lastSpeed = speed;
    	count++;
    	return false;
    }

}
