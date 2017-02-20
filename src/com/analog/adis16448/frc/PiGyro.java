//*----------------------------------------------------------------------------*/
// Copyright (c) FIRST 2016. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.analog.adis16448.frc;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
//import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
//import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.InterruptableSensorBase;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;

/**
 * This class is for the ADIS16448 IMU that connects to the RoboRIO MXP port.
 */
public class PiGyro {
	NetworkTable table;
	
	public PiGyro() {
		table = NetworkTable.getTable("Sensors on Pi");
	}
	
	public double getAngleX() {
		return table.getNumber("X-Axis Angle", 0);
	}
	public double getAngleZ() {
		return table.getNumber("Y-Axis Angle", 0);
	}
	public double getAngleY() {
		return table.getNumber("Z-Axis Angle", 0);
	}
	
	public void reset() {
		return;
	}
	
	public void calibrate() {
		return;
	}
}
