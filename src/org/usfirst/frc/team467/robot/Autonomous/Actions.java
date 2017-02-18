package org.usfirst.frc.team467.robot.Autonomous;

import org.usfirst.frc.team467.robot.*;

public class Actions {
	private static Drive drive = Drive.getInstance();
	
	public static Action example = new Action("Example", new Process.Duration(1000), () -> {
		System.out.println("Running Example Autonomous Action: " + System.currentTimeMillis());
	});
	
	public static Action moveForward = new Action("Move Forward 1 second", new Process.Duration(1000), () -> drive.crabDrive(0, 1.0));
	
	public static Action aim(double angle) {
		return new Action("Aim", () -> drive.aiming.onTarget(), () -> drive.turnToAngle(angle));
	}
	
	public static Process getExampleProcess()
	{
		Process mode = new Process("Example Auto");
		mode.addAction(example);
		mode.addAction(moveForward);
		return mode;
	}
}
