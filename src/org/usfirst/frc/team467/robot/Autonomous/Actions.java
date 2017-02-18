package org.usfirst.frc.team467.robot.Autonomous;

import org.usfirst.frc.team467.robot.*;

public class Actions {
	
	public static Action example1 = new Action("Example 1", new Process.Duration(1), () -> {
		System.out.println("Running Example Autonomous Action #1: " + System.currentTimeMillis());
	});
	
	public static Action example2 = new Action("Example 2", new Process.Duration(2), () -> {
		System.out.println("Running Example Autonomous Action #2: " + System.currentTimeMillis());
	});
	
	public static Action moveForward = new Action("Move Forward 1 second", new Process.Duration(1), () -> Drive.getInstance().crabDrive(0, 1.0));
	
	public static Action aim(double angle) {
		Drive drive = Drive.getInstance();
		return new Action("Aim", () -> drive.aiming.onTarget(), () -> drive.turnToAngle(angle));
	}
	
	public static Process getBasicProcess()
	{
		Process mode = new Process("Basic Auto");
		mode.addAction(example1);
		mode.addAction(moveForward);
		return mode;
	}
	
	public static Process getExampleProcess()
	{
		Process mode = new Process("Example Auto");
		mode.addAction(example1);
		mode.addAction(example2);
		return mode;
	}
}
