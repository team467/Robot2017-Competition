package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;

import org.usfirst.frc.team467.robot.WheelPod;

public class PIDTuningCycleCharacteristics extends BaseTuner implements Tuner {

	private boolean goingUp;
	private long lastSpeed;
	private long startTime;
	private long maxAmplitude;
	private long lastPeak;
	private ArrayList<Long> cycleTimes;
	private boolean isComplete;

	public PIDTuningCycleCharacteristics(WheelPod wheelPod, boolean findVelocityPID) {
		super(wheelPod, findVelocityPID);
		System.out.println("Characterizing PID values");
		cycleTimes = new ArrayList<Long>();
		if (findVelocityPID) {
			wheelPod.speedMode();
		} else {
			wheelPod.positionMode();
		}
		cycleTimes.clear();
		count = 0;
		lastPeak = Long.MAX_VALUE;
		isComplete = false;
	}

	@Override
	public boolean process() {
		long speed = Math.round(wheelPod.readSensor());
		long error = Math.round(Math.abs(wheelPod.error()));
		long time = System.currentTimeMillis();
		if (!isComplete) {
			if (count == 0) {
				System.out.println(wheelPod.name() + ": " + wheelPod.pidfValueString());
				startTime = time;
				cycleTimes.clear();
				wheelPod.set(setpoint);
				count++;
			} else if (Math.abs((lastPeak - setpoint)) < 3) {
				set(0);
				wheelPod.percentVoltageBusMode();
				System.out.println(wheelPod.name() + " Max Overshoot: " + maxAmplitude + " Converge Time: "
						+ ((time - startTime)));
				isComplete = true;
				return isComplete;
			} else if (count >= HOLD_PERIOD) {
				set(0);
				wheelPod.percentVoltageBusMode();
				isComplete = true;
				return isComplete;
			} else {
				if (goingUp) {
					if (speed < lastSpeed) {
						lastPeak = speed;
						System.out.println(wheelPod.name() + " peak = " + speed);
						// Found peek
						if (error > maxAmplitude) {
							maxAmplitude = error;
						}
						goingUp = false;
					}
				} else { // Going down
					if (speed > lastSpeed) {
						lastPeak = Math.abs(speed);
						System.out.println(wheelPod.name() + " trough = " + speed);
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
		}
		return isComplete;
	}

}