package org.usfirst.frc.team467.robot.Autonomous;

import org.usfirst.frc.team467.robot.*;

public class Actions {
	public static final Action nothingForever = new Action(
			"Do Nothing Fovever",
			() -> false, () -> {
			/* Do Nothing */ });
	
	public static final Action example1 = new Action(
			"Example 1",
			new ActionGroup.Duration(2), 
			() -> { System.out.println("Running Example Autonomous Action #1: " + System.currentTimeMillis()); });

	public static final Action example2 = new Action("Example 2", new ActionGroup.Duration(2), () -> {
		System.out.println("Running Example Autonomous Action #2: " + System.currentTimeMillis());
	});

	public static final Action moveForward = new Action(
			"Move Forward 2 seconds",
			new ActionGroup.Duration(2),
			() -> Drive.getInstance().crabDrive(0, 0.5));
	
	public static final Action moveBackward = new Action(
			"Move Backward 2 seconds",
			new ActionGroup.Duration(2),
			() -> Drive.getInstance().crabDrive(0, -0.5));

	public static Action aim(double angle) {
		Drive drive = Drive.getInstance();
		return new Action("Aim", () -> drive.aiming.onTarget(), () -> drive.turnToAngle(angle));
	}
	
	public static ActionGroup aimProcess(double angle) {
		ActionGroup mode = new ActionGroup("Aim");
		mode.addAction(aim(angle));
		return mode;
	}
	
	// Private method makes process, but for only one public variable
	private static ActionGroup getBasicProcess() {
		ActionGroup mode = new ActionGroup("Basic Auto");
		mode.addAction(example1);
		mode.addAction(moveForward);
		mode.addAction(moveBackward);
		return mode;
	}
	public static ActionGroup basicProcess = getBasicProcess();

	private static ActionGroup getExampleProcess() {
		ActionGroup mode = new ActionGroup("Example Auto");
		mode.addAction(example1);
		mode.addAction(example2);
		return mode;
	}
	public static final ActionGroup exampleProcess = getExampleProcess();
}
