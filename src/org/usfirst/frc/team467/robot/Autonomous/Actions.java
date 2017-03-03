package org.usfirst.frc.team467.robot.Autonomous;

import java.util.concurrent.atomic.AtomicInteger;

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

	public static Action setPositionMode() {
		Drive drive = Drive.getInstance();
		return new Action("Setting position mode for driving a set distance.",
				() -> drive.isInPositionMode(),
				() -> drive.setPositionMode());
	}

	public static Action setDriveMode() {
		Drive drive = Drive.getInstance();
		return new Action("Setting drive mode",
				() -> drive.isNotInPositionMode(),
				() -> drive.returnToDriveMode());
	}

	public static Action moveDistanceForward(double distance) {
		Drive drive = Drive.getInstance();
		String actionText = "Move forward " + distance + " feet";
		return new Action(actionText,
				() -> drive.moveDistanceComplete(),
				() -> drive.moveDistance(distance));
	}

	public static Action turnAndMoveDistanceForward(double angle, double distance) {
		Drive drive = Drive.getInstance();
		String actionText = "Turn to " + angle + " and move " + distance + " feet";
		return new Action(actionText,
				() -> drive.moveDistanceComplete(),
				() -> drive.turnAndMoveDistance(angle, distance));
	}

	public static ActionGroup turnAndMoveDistanceForwardProcess(double angle, double distance) {
		String actionGroupText = "Turn to " + angle + " and move " + distance + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(setPositionMode());
		mode.addAction(turnAndMoveDistanceForward(angle, distance));
		mode.addAction(setDriveMode());
		return mode;
	}

	public static ActionGroup moveDistanceForwardProcess(double distance) {
		String actionGroupText = "Move forward " + distance + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(setPositionMode());
		mode.addAction(moveDistanceForward(distance));
		mode.addAction(setDriveMode());
		return mode;
	}

	public static ActionGroup moveInSquareTest(double distance) {
		String actionGroupText = "Move in " + distance + " foot square";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addAction(setPositionMode());
		mode.addAction(turnAndMoveDistanceForward(0, distance));
		mode.addAction(turnAndMoveDistanceForward((Math.PI / 2), distance));
		mode.addAction(turnAndMoveDistanceForward(Math.PI, distance));
		mode.addAction(turnAndMoveDistanceForward((3 * Math.PI / 2), distance));
		mode.addAction(setDriveMode());
		return mode;
	}

	public static ActionGroup aim(double angle) {
		Drive drive = Drive.getInstance();
		ActionGroup mode = new ActionGroup("Aim");
		Action aim =  new Action("Aim", () -> drive.aiming.onTarget(), () -> drive.turnToAngle(angle));
		Action disable = new Action("Disable", () -> true, () -> drive.aiming.disable());
		mode.addAction(aim);
		mode.addAction(disable);
		return mode;
	}

	public static Action driveToGear(double targetDistance) {
		// Ran once at initialization
		Drive drive = Drive.getInstance();
		VisionProcessing vision = VisionProcessing.getInstance();
		AtomicInteger minimumDistance = new AtomicInteger((int)vision.getDistance());
		// Ran periodically
		return new Action("Drive Distance",
				() -> {
					double currentDistance = vision.getDistance();

					if (minimumDistance.get() > currentDistance) {
						minimumDistance.set((int)currentDistance);
					}

					// If we are currently more than two inches further away than our start
					if (currentDistance > minimumDistance.get() + 4.0) {
						return true;
					}

					return currentDistance <= targetDistance;
					},
				() -> drive.crabDrive(0, 0.3));
	}

	public static ActionGroup newAimProcess(double angle) {
		ActionGroup mode = new ActionGroup("Aim");
		mode.addActions(aim(angle));
		mode.addAction(driveToGear(40));
		mode.enable();
		return mode;
	}

	// Private method makes process, but for only one public variable
	public static ActionGroup newBasicProcess() {
		ActionGroup mode = new ActionGroup("Basic Auto");
		mode.addAction(example1);
		mode.addAction(moveForward);
		mode.addAction(moveBackward);
		mode.enable();
		return mode;
	}
	public static final ActionGroup basicProcess = newBasicProcess();

	private static ActionGroup getExampleProcess() {
		ActionGroup mode = new ActionGroup("Example Auto");
		mode.addAction(example1);
		mode.addAction(example2);
		return mode;
	}
	public static final ActionGroup exampleProcess = getExampleProcess();
}
