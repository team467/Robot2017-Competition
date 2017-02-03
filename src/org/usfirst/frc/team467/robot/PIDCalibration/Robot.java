/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */

/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team467.robot.PIDCalibration;

import edu.wpi.first.wpilibj.IterativeRobot;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot
{

    // Robot objects
    public static WheelPod[] wheelPod;

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
        wheelPod = new WheelPod[4];
        for (int i=0; i<4; i++) {
            wheelPod[i] = new WheelPod(Pod.idToPod(i+1));
            System.out.println("Wheel Pod Name " + Pod.idToPod(i+1).name);
//            wheelPod[i].setPositionMode();
        }
    }

    public void disabledInit()
    {
    }

    public void disabledPeriodic()
    {
    }

    public void autonomousInit()
    {
        wheelPod[1].tune();
    }

    public void teleopInit()
    {
    }

    public void testInit()
    {
    }

    public void testPeriodic()
    {
    }

    public void autonomousPeriodic()
    {
        for (WheelPod pod : wheelPod) {
            pod.update();
            pod.error();
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic()
    {
    }
}
