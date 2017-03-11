package org.usfirst.frc.team467.robot.AutoCalibration;

import java.util.ArrayList;

import org.usfirst.frc.team467.robot.WheelPod;

public class PIDTuningCycleCharacteristics extends BaseTuner implements Tuner {

	private boolean goingUp;
	private double lastSensorReading;
	private long startTime;
	private long cycleStartTime;
	private double maxAmplitude;
	private double lastPeak;
	private long convergeTime;
	private ArrayList<Long> cycleTimes;
	private boolean isComplete;
	private boolean convergedInTime;

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
		convergeTime = 0;
		maxAmplitude = 0;
		cycleStartTime = 0;
		lastPeak = Long.MAX_VALUE;
		isComplete = false;
		convergedInTime = false;
	}

	public double maxAmplitude() {
		return maxAmplitude;
	}

	public long convergeTime() {
		return convergeTime;
	}

	public long lastCycleTime() {
		return cycleTimes.get(cycleTimes.size());
	}

	public boolean convergedInTime() {
		return convergedInTime;
	}

	@Override
	public boolean process() {
		double sensorReading;
		double error;
		if (findVelocityPID) {
			error = Math.round(Math.abs(wheelPod.error()));
			sensorReading = Math.round(wheelPod.readSensor());
		} else {
			error = Math.round(Math.abs(wheelPod.error() / 256));
			sensorReading = Math.round(100*wheelPod.motor().getPosition())/100;
		}
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
				System.out.println(wheelPod.name() + " Max Overshoot: " + maxAmplitude + " Converge Time: " + ((time - startTime)));
				System.out.println("Position: " + sensorReading + " Encoder Reading: " + wheelPod.motor().getEncPosition() + " Position Error: " + (sensorReading - POSITION_SETPOINT));
				isComplete = true;
				convergedInTime = true;
				count = 0;
				return isComplete;
			} else if (count >= HOLD_PERIOD) {
				set(0);
				System.out.println("Did not converge in hold period.");
				wheelPod.percentVoltageBusMode();
				isComplete = true;
				convergedInTime = false;
				count = 0;
				return isComplete;
			} else {
				if (goingUp) {
					if (sensorReading < lastSensorReading) {
						lastPeak = sensorReading;
						cycleStartTime = time;
						System.out.println(wheelPod.name() + " peak = " + sensorReading);
						// Found peek
						if (error > maxAmplitude) {
							maxAmplitude = (long) error;
						}
						goingUp = false;
					}
				} else { // Going down
					if (sensorReading > lastSensorReading) {
						lastPeak = Math.abs(sensorReading);
						cycleTimes.add(time - cycleStartTime);
						System.out.println(wheelPod.name() + " trough = " + sensorReading);
						// Found trough
						if (maxAmplitude > error) {
							maxAmplitude = error;
						}
						goingUp = true;
					}
				}
			}
			lastSensorReading = sensorReading;
			count++;
		}
		return isComplete;
	}

}