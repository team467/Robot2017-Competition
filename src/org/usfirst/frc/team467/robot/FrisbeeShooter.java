package org.usfirst.frc.team467.robot;

import org.apache.log4j.Logger;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;

public class FrisbeeShooter {
	private static final Logger LOGGER = Logger.getLogger(FrisbeeShooter.class);
	private Compressor compressor;
	private Solenoid solenoid;
	private CANTalon flyWheel;
	
	public FrisbeeShooter(int solChannel, int wheelChannel) {
		compressor = new Compressor();
		solenoid = new Solenoid(solChannel);
		flyWheel = new CANTalon(wheelChannel);
	}
	
	public void setCompressor() {
		compressor.setClosedLoopControl(true);
	}
	
	public void stopCompressor() {
		compressor.setClosedLoopControl(false);
	}
	
	public void setFlyWheel(int voltage) {
		flyWheel.set(voltage);
	}
	
	public void setSolenoid(boolean on) {
		if (solenoid.isBlackListed()) {
			solenoid.set(false);
			LOGGER.fatal("SOLENOID IS SHORTED");
			return;
		}
		solenoid.set(on);
	}
	
	public void disable() {
		stopCompressor();
		setFlyWheel(0);
		setSolenoid(false);
	}
}
