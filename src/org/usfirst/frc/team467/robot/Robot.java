/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot;

<<<<<<< HEAD
import org.usfirst.frc.team467.robot.AutoCalibration.InitialFeedForwardTuner;
import org.usfirst.frc.team467.robot.AutoCalibration.MaxSpeedTuner;
import org.usfirst.frc.team467.robot.AutoCalibration.Tuner;
import org.usfirst.frc.team467.robot.AutoCalibration.UltimateProportionalGainTuner;
import org.usfirst.frc.team467.robot.AutoCalibration.WheelPodTuner;
import org.usfirst.frc.team467.robot.PIDCalibration.WheelPod;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

=======
import com.analog.adis16448.frc.ADIS16448_IMU;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
>>>>>>> master
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
<<<<<<< HEAD
public class Robot extends IterativeRobot {
	private static final double MIN_DRIVE_SPEED = 0.1;

	// Robot objects
	private DriverStation2015 driverstation;
	private Drive drive;

	private Tuner autotuner[];

	private WheelPod wheelPods[];

	int session;

	/**
	 * Time in milliseconds
	 */
	double time;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		// Initialize logging framework.

		// Make robot objects
		driverstation = DriverStation2015.getInstance();
//		drive = Drive.getInstance();
//		Calibration.init();

	}

	public void disabledInit() {
	}

	public void disabledPeriodic() {
	}

	public void teleopInit() {
		drive.setSpeedMode();
	}

	boolean isTuningComplete;

	public void testInit() {
	}

	public void testPeriodic() {

	}

	CANTalon talon;

	public void autonomousInit() {

//		talon = new CANTalon(3);
//		talon.setP(0.01);
//		talon.changeControlMode(TalonControlMode.Position);
//		talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);

		wheelPods = new WheelPod[4];
		autotuner = new Tuner[4];
		for (int i = 0; i < 4; i++) {
			wheelPods[i] = new WheelPod(i+1, new PIDF(0,0,0,0));
			autotuner[i] = new WheelPodTuner(wheelPods[i], true);
//			autotuner[i] = new MaxSpeedTuner(wheelPods[i]);
//			autotuner = new InitialFeedForwardTuner(wheelPods[0], true);
//			autotuner[i] = new UltimateProportionalGainTuner(wheelPods[i], false);
			wheelPods[i].set(0);
//			wheelPods[i].positionMode();
//			wheelPods[i].zeroPosition();
		}
//		wheelPods[1].positionMode();
//		wheelPods[1].zeroPosition();
		isTuningComplete = false;
	}

	public void autonomousPeriodic() {

		wheelPods[1].readSensor();
		if (!isTuningComplete) {
//			talon.set(50);
			for (Tuner tuner : autotuner) {
				isTuningComplete = tuner.process();
			}
//			autotuner[0].process();
			wheelPods[1].set(256);
			isTuningComplete = true;
		} else {
//			for (WheelPod pod : wheelPods) {
//				pod.set(0);
//			}
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		// Read driverstation inputs
		driverstation.readInputs();

		if (driverstation.getCalibrate()) {
			// Calibrate Mode
			Calibration.updateCalibrate();
		} else {
			// Drive Mode
			updateDrive();
		}
	}

	/**
	 * called once per iteration to perform any necessary updates to the drive
	 * system.
	 */
	private void updateDrive() {
		DriveMode driveMode = driverstation.getDriveMode();
		switch (driveMode) {
		case UNWIND:
			for (Steering wheelpod : Drive.getInstance().steering) {
				wheelpod.setAbsoluteAngle(0);
			}
			break;

		case TURN:
			drive.turnDrive(-driverstation.getDriveJoystick().getTwist() / 2);
			break;

		case CRAB:
			if (driverstation.getDriveJoystick().getStickDistance() < MIN_DRIVE_SPEED) {
				// Don't start driving until commanded speed greater than
				// minimum
				drive.stop();
			} else {
				drive.crabDrive(driverstation.getDriveJoystick().getStickAngle(),
							    driverstation.getDriveJoystick().getStickDistance());
			}
			break;

		case STRAFE:
			drive.strafeDrive(driverstation.getDriveJoystick().getPOV());
			break;

		default:
			drive.stop(); // If no drive mode specified, don't drive!
		}
	}
}
=======
public class Robot extends IterativeRobot
{
    private static final double MIN_DRIVE_SPEED = 0.1;

    // Robot objects
    private DriverStation2015 driverstation;
    private Drive drive;
    private Joystick467 stick;
    private XBJoystick xbstick;
    private ADIS16448_IMU gyro;

    int session;

    /**
     * Time in milliseconds
     */
    double time;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {
        // Initialize logging framework.

        // Make robot objects
        driverstation = DriverStation2015.getInstance();
        drive = Drive.getInstance();
        Calibration.init();
        stick = new Joystick467(0);
        xbstick = new XBJoystick(0);
        gyro = Gyrometer.getInstance();
        LookUpTable table = LookUpTable.getInstance();
    }

    public void disabledInit()
    {
    }

    public void disabledPeriodic()
    {
    	SmartDashboard.putData("IMU", gyro);
//    	System.out.println("x:" + gyro.getAngleX() + "y:" + gyro.getAngleY() + "z:" + gyro.getAngleZ());
//    	gyro.reset();
    }

    public void autonomousInit()
    {
    	gyro.reset();
    	gyro.calibrate();
    }

    public void teleopInit()
    {
    	gyro.reset();
    	gyro.calibrate();
    	
    	
    }

    public void testInit()
    {
    }

    public void testPeriodic()
    {
    }

    public void autonomousPeriodic()
    {
        driverstation.readInputs();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic()
    {  
        // Read driverstation inputs
        driverstation.readInputs();
        
        if(driverstation.getGyroReset())
        {
        	gyro.reset();
        }

        if (driverstation.getCalibrate())
        {
            // Calibrate Mode
            Calibration.updateCalibrate();
        }
        else
        {
            // Drive Mode
            updateDrive();
        }
    }

    /**
     * called once per iteration to perform any necessary updates to the drive
     * system.
     */
    private void updateDrive()
    {
    	DriveMode driveMode = driverstation.getDriveMode();
        switch (driveMode)
        {
            case UNWIND:
                for (Steering wheelpod : Drive.getInstance().steering)
                {
                    wheelpod.setAbsoluteAngle(0);
                }
                break;

            case TURN:
                drive.turnDrive(-driverstation.getDriveJoystick().getTwist()/2);
                break;

            case CRAB:
                if (driverstation.getDriveJoystick().getStickDistance() < MIN_DRIVE_SPEED)
                {
                    // Don't start driving until commanded speed greater than minimum
                    drive.stop();
                }
                else
                {
                    drive.crabDrive(driverstation.getDriveJoystick().getStickAngle(),
                    				driverstation.getDriveJoystick().getStickDistance());
                }
                break;
            case STRAFE:
            	if (driverstation.getDriveJoystick().getStickDistance() < MIN_DRIVE_SPEED)
                {
                    // Don't start driving until commanded speed greater than minimum
                    drive.stop();
                }
                else
                {
                	drive.strafeDrive(driverstation.getDriveJoystick().getPOV());

                }
               	break;
//            case XB_SPLIT:
//            	drive.xbSplit(driverstation.getDriveJoystick().getStickAngle(),
//            				-driverstation.getRightDriveJoystick().getTurn() / 2,
//            				driverstation.getDriveJoystick().getStickDistance());
//            	break;
            case FIELD_ALIGN:
            	//angle Z is taken from the ADIS 16448 gyrometer
            	drive.fieldAlignDrive(driverstation.getDriveJoystick().getStickAngle(),
            			driverstation.getDriveJoystick().getStickDistance());
            	System.out.println("WHYY");
            	break;
            case VECTOR:
    			drive.setSpeedMode();
    			//drive.vectorDrive(driverstation.getDriveJoystick().getStickX(), driverstation.getDriveJoystick().getStickY(),
    			//		driverstation.getDriveJoystick().getTwist());
    			drive.vectorDrive(driverstation.getDriveJoystick().getStickAngle(),
    				    driverstation.getDriveJoystick().getStickDistance(), driverstation.getDriveJoystick().getTwist());
    			break;
		default:
			 if (driverstation.getDriveJoystick().getStickDistance() < MIN_DRIVE_SPEED)
             {
                 // Don't start driving until commanded speed greater than minimum
                 drive.stop();
             }
             else
             {
                 drive.crabDrive(driverstation.getDriveJoystick().getStickAngle(),
                 				driverstation.getDriveJoystick().getStickDistance());
             }
			break;

        }
    }
}
>>>>>>> master
