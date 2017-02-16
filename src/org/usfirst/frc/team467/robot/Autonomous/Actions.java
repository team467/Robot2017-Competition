package org.usfirst.frc.team467.robot.Autonomous;

public class Actions {
	
	public static Action example = new Action("Example", () -> {return false;}, () -> {
		System.out.println("Running Example Autonomous Action");
	});
}
