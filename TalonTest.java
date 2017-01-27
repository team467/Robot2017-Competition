package org.usfirst.frc.team467.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import java.io.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * @author Bryan Duerk
 *
 */
public class TalonTest
{
    public static final int TALON_ID = 1;
    public static final double DEFAULT_Kp = 0.5;
    public static final double DEFAULT_Ki = 0.0;
    public static final double DEFAULT_Kd = 0.0;
    private static double kP;
    private static double kI;
    private static double kD;
    private static double F;
    private static double maxSpeed;
    private static CANTalon talon;
    private static double temp;
    private static boolean isReversed;
    Writer writer;
    SmartDashboard dashboard;

    /**
     *
     */
   
    public TalonTest()
    {
        //talon = new CANTalon(Integer.parseInt(SmartDashboard.getString("DB/String 5", "1")));
        talon = new CANTalon(TALON_ID);
    	int read = Integer.parseInt(SmartDashboard.getString("DB/String 6", "0"));
        if (read == 0){
        	isReversed = false;
        }
        else {
        	isReversed = true;
        }
        kP = DEFAULT_Kp;
        kI = DEFAULT_Ki;
        kD = DEFAULT_Kd;
        maxSpeed = 400;
        F = 3.0001;
        dashboard = new SmartDashboard();
        dashboard.clearPersistent("Data");
        
    }
    public void clearTalon() {
        talon.ClearIaccum();
        talon.clearIAccum();
        talon.clearStickyFaults();
        talon.clearMotionProfileHasUnderrun();
        talon.clearMotionProfileTrajectories();
    }
    public void init() {
    	kP = Double.parseDouble(SmartDashboard.getString("DB/String 1", "0.0"));
    	kI = Double.parseDouble(SmartDashboard.getString("DB/String 2", "0.0"));
    	kD = Double.parseDouble(SmartDashboard.getString("DB/String 3", "0.0"));
    	
    	
    	
    	System.out.println(temp);
    	try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/home/lvuser/tuning.csv"), "utf-8"));
			try {
				writer.write("ID, time, Set, Actual, Error, F, P, I, D, V, \n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block= new BufferedWriter(new OutputStreamWriter(new FileOutputStream("filename.txt"), "utf-8"));
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        talon.setPID(kP, kI, kD);
        talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        talon.configEncoderCodesPerRev(128);
        talon.changeControlMode(TalonControlMode.Speed);
        talon.setF(F);
        if (isReversed){
        	talon.reverseSensor(true);
        }
        //talon.setInverted(true);
        /*
         * Valid Talon control modes:
         * Current
         * Disabled
         * Follower
         * MagicMotion
         * MotionProfile
         * PercentVbus
         * Position
         * Speed
         * Voltage
         */
//        talon.changeControlMode(TalonControlMode.Speed);
//        talon.configMaxOutputVoltage(5.0);
//        talon.configEncoderCodesPerRev(codesPerRev);
//        talon.configNominalOutputVoltage(forwardVoltage, reverseVoltage);
//        talon.configPeakOutputVoltage(forwardVoltage, reverseVoltage);
//        talon.configPotentiometerTurns(turns);
//        talon.ConfigRevLimitSwitchNormallyOpen(normallyOpen);
//      System.out.println(configValues());
        System.out.println(currentValuesCompressed(0.0));
    }
    public void enable() {
        talon.enable();
    }
    public void disable() {
        talon.disableControl();
        talon.disable();
    }
    public String configValues() {
        String values = "Config Values\n";
        values += "Description: " + talon.getDescription() + "\n";
        values += "Device ID: " + talon.getDeviceID() + "\n";
        values += "Smart Dashboard Type: " + talon.getSmartDashboardType() + "\n";
        values += "Firmware Version: " + talon.GetFirmwareVersion() + "\n";
        values += "PID: P=" + talon.getP() + " I=" + talon.getI() + " D=" + talon.getD() + "\n";
        values += "Is enabled? " + talon.isEnabled() + "\n";
        values += "Is control enabled? " + talon.isControlEnabled() + "\n";
        values += "Is safety enabled? " + talon.isSafetyEnabled()+ "\n";
        values += "Is alive? " + talon.isAlive() + "\n";
//        values += "Is forward soft limit enabled?" + talon.isForwardSoftLimitEnabled() + "\n";
        values += "Is reverse soft limit enabled? " + talon.isReverseSoftLimitEnabled() + "\n";
        values += "Is reverse limit switch closed? " + talon.isRevLimitSwitchClosed() + "\n";
        values += "Is motion profile top level buffer full?" + talon.isMotionProfileTopLevelBufferFull() + "\n";
        values += "Is zero sensor position on forward limit enabled? " + talon.isZeroSensorPosOnFwdLimitEnabled() + "\n";
        values += "Is zero sensor position on index enabled? " + talon.isZeroSensorPosOnIndexEnabled() + "\n";
        values += "Is zero sensor position on reverse limit enabled? " + talon.isZeroSensorPosOnRevLimitEnabled() + "\n";
//        values += "" + talon + "\n";
        return values;
    }
    public String currentValues() {
        String values = "Current Sensor Values\n";
        values += "Output Current: " + talon.getOutputCurrent() + "\n";
        values += "Output Voltage: " + talon.getOutputVoltage() + "\n";
        values += "Error: " + talon.getError() + "\n";
        values += "Last Error: " + talon.getLastError() + "\n";
        values += "Analog in position: " + talon.getAnalogInPosition() + "\n";
        values += "Analog in row: " + talon.getAnalogInRaw() + "\n";
        values += "Analog in velocity: " + talon.getAnalogInVelocity() + "\n";
        values += "Bus voltage: " + talon.getBusVoltage() + "\n";
        values += "Closed loop error: " + talon.getClosedLoopError() + "\n";
        values += "Closed loop ramp rate: " + talon.getCloseLoopRampRate() + "\n";
        values += "Encoder position: " + talon.getEncPosition() + "\n";
        values += "Encoder velocity: " + talon.getEncVelocity() + "\n";
        values += "Forward soft limit: " + talon.getForwardSoftLimit() + "\n";
        values += "I Zone: " + talon.getIZone() + "\n";
        values += "Number of quad idx rises: " + talon.getNumberOfQuadIdxRises() + "\n";
        return values;
    }
    
    //TODO:make class
    public String currentValuesCompressed(double time){
        String values = "";
        //values += "get=" + talon.get();
        values += " ClosedLoopError=" + talon.getClosedLoopError();
        values += " Target=" + talon.getSetpoint();
        values += " Speed=" + talon.getSpeed();
//        values += " Position=" + talon.getPosition();
        SmartDashboard.putNumber("Data", talon.getClosedLoopError());
        {
        	String datadump = "";
        	datadump += TALON_ID + ", ";
        	datadump += time + ", ";
        	datadump += talon.getSetpoint() + ", ";
        	datadump += talon.getSpeed() + ", ";
        	datadump += talon.getClosedLoopError() + ", ";
        	datadump += F + ", ";
        	datadump += kP + ", ";
        	datadump += kI + ", ";
        	datadump += kD + ", ";
        	datadump += talon.getBusVoltage() + ",\n";
        	
        	try {
				writer.write(datadump);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return values;
    }
    /*
     * Tests Talon functions.
     */
    public void talonFunctions() {
    }
    public String toString() {
        return currentValues();
    }
    
    public void drive(double setting){
    	setting = Double.parseDouble(SmartDashboard.getString("DB/String 0", "0.0"));
        talon.set(setting * maxSpeed);
    }
    
    
}