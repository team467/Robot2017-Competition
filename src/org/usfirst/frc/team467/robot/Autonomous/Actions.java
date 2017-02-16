package org.usfirst.frc.team467.robot.Autonomous;

public class Actions {
	
	public static Action example = new Action("Example", new Process.Duration(1000.0), () -> {
		System.out.println("Running Example Autonomous Action");
	});
}
