package org.usfirst.frc.team467.robot;
import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;
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
    private double kP;
    private double kI;
    private double kD;
    CANTalon talon;
    /**
     *
     */
    public TalonTest()
    {
        talon = new CANTalon(TalonTest.TALON_ID);
        kP = DEFAULT_Kp;
        kI = DEFAULT_Ki;
        kD = DEFAULT_Kd;
    }
    public void clearTalon() {
        talon.ClearIaccum();
        talon.clearIAccum();
        talon.clearStickyFaults();
        talon.clearMotionProfileHasUnderrun();
        talon.clearMotionProfileTrajectories();
    }
    public void init() {
        talon.setPID(kP, kI, kD);
        talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        talon.configEncoderCodesPerRev(128);
        talon.changeControlMode(TalonControlMode.Speed);
        talon.setF(3.0001);
        talon.setInverted(true);
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
        System.out.println(currentValuesCompressed());
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
    public String currentValuesCompressed(){
        String values = "";
        //values += "get=" + talon.get();
        //values += " ClosedLoopError=" + talon.getClosedLoopError();
        //values += " Speed=" + talon.getSpeed() + " Position=" + talon.getPosition();
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
        talon.set(setting);
    }
    
    
}