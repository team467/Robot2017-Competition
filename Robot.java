package org.usfirst.frc.team467.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    RobotDrive myRobot = new RobotDrive(0, 1);
    Joystick stick = new Joystick(0);
    Timer timer = new Timer();
    
    TalonTest talontest;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
    }

    /**
     * This function is run once each time the robot enters autonomous mode
     */
    @Override
    public void autonomousInit() {
        talontest = new TalonTest();
        talontest.init();
        timer.reset();
        timer.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    @Override
    public void autonomousPeriodic() {
    	talontest.drive(0.7);
        System.out.println(talontest.currentValuesCompressed(timer.get()));
    	
        // Drive for 1 seconds
//        if (timer.get() >= 0.5) {
//        	talontest.drive(400);
//        }
//        if (timer.get() <= 1) {
//        System.out.println(talontest.currentValuesCompressed());
//        }
    }

    /**
     * This function is called once each time the robot enters tele-operated
     * mode
     */
    @Override
    public void teleopInit() 
    {
    }

    /**
     * This function is called periodically during operator control
     */
    @Override
    public void teleopPeriodic() {
        myRobot.arcadeDrive(stick);
        talontest.currentValues();
        talontest.drive(0.7);
    }

    /**
     * This function is called periodically during test mode
     */
    @Override
    public void testPeriodic() {
        LiveWindow.run();
    }
}