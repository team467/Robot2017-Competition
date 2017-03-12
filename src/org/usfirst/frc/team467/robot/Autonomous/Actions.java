package org.usfirst.frc.team467.robot.Autonomous;

import java.util.concurrent.atomic.AtomicInteger;

import org.usfirst.frc.team467.robot.*;

import edu.wpi.first.wpilibj.Timer;

public class Actions {

	static Timer timer = new Timer();

	public static final Action nothingForever(){
		Drive drive = Drive.getInstance();
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> drive.isStopped(),
				() -> drive.crabDrive(0, 0));
	}

	public static final Action example1 = new Action(
			"Example 1",
			new ActionGroup.Duration(2),
			() -> { System.out.println("Running Example Autonomous Action #1: " + System.currentTimeMillis()); });

	public static final Action example2 = new Action("Example 2", new ActionGroup.Duration(2), () -> {
		System.out.println("Running Example Autonomous Action #2: " + System.currentTimeMillis());
	});

	public static final Action moveForward = new Action(
			"Move Forward 2 seconds",
			new ActionGroup.Duration(1),
			() -> Drive.getInstance().crabDrive(0, 0.5));

	public static final Action moveBackward = new Action(
			"Move Backward 2 seconds",
			new ActionGroup.Duration(2),
			() -> Drive.getInstance().crabDrive(0, -0.5));

	public static final Action goForward(double seconds){
		Drive drive = Drive.getInstance();
		String actionText = "Move Forward " + seconds + "seconds";
		new ActionGroup.Duration(seconds);
		timer.reset();
		timer.start();
		return new Action(actionText,
				new ActionGroup.Duration(seconds),
				() -> drive.crabDrive(0, 0.7));
	}

	public static Action goBackward(double seconds){
		Drive drive = Drive.getInstance();
		String actionText = "Move backward " + seconds + "seconds";
		new ActionGroup.Duration(seconds);
		timer.reset();
		timer.start();
		return new Action(actionText,
				new ActionGroup.Duration(seconds),
				() -> drive.crabDrive(0, -0.5));
	}

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

	public static Action aim(double angle) {
		System.out.println("Creating aim action, angle=" + angle + ", gyro heading=" + Gyrometer.getInstance().pidGet());
		
		Drive drive = Drive.getInstance();
		return new Action(
				"Aim",
				() -> drive.aiming.onTarget(),
				() -> drive.turnToAngle(angle));
	}

	public static Action disableAiming = new Action(
				"Disable",
				new ActionGroup.RunOnce(), // Done immediately
				() -> Drive.getInstance().aiming.disable());

	public static ActionGroup newAimAndDisable(double angle) {
		ActionGroup mode = new ActionGroup("Aim and Disable");
		mode.addAction(aim(angle));
		mode.addAction(disableAiming);
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

	public static Action dispenseGearA() {
		GearDevice gear = GearDevice.getInstance();
		String actionText = "dispense gear";
		new ActionGroup.Duration(1.5);
		timer.reset();
		timer.start();
		return new Action(actionText,
				new ActionGroup.Duration(1.5),
				() -> gear.goDown());
	}

	public static Action dispenseGearB(){
		GearDevice gear = GearDevice.getInstance();
		Drive drive = Drive.getInstance();
		String actionText = "dispense gear";
		new ActionGroup.Duration(1.5);
		timer.reset();
		timer.start();
		return new Action(actionText,
				new ActionGroup.Duration(1.5),
				() -> {
					gear.goDown();
					drive.crabDrive(Math.PI, -0.5);
				});
	}

	public static boolean isGearHolderDown(){
		return false;
	}

	public static ActionGroup aimProcess(double angle) {
		ActionGroup mode = new ActionGroup("Aim");
		mode.addActions(newAimAndDisable(angle));
		mode.addAction(driveToGear(40));
		mode.enable();
		return mode;
	}

	public static ActionGroup goFoward(double seconds){
		ActionGroup mode = new ActionGroup("go");
		mode.addAction(goForward(seconds));
		return mode;
	}

	public static ActionGroup goBackwards(double seconds){
		ActionGroup mode = new ActionGroup("go back");
		mode.addAction(goBackward(seconds));
		return mode;
	}

	public static ActionGroup doNothing(){
		ActionGroup mode = new ActionGroup("none");
		mode.addAction(nothingForever());
		return mode;
	}

	public static ActionGroup dropGearFromLeft(){
		ActionGroup mode = new ActionGroup("gear");
		mode.addAction(goForward(0.9));
		mode.addAction(aim(60));
		mode.addAction(disableAiming);
		mode.addAction(goForward(1.0));
		mode.addAction(dispenseGearA());
		mode.addAction(dispenseGearB());
		return mode;
	}

	public static ActionGroup dropGearFromRight(){
		ActionGroup mode = new ActionGroup("gear");
		mode.addAction(goForward(0.9));
		mode.addAction(aim(-60));
		mode.addAction(disableAiming);
		mode.addAction(goForward(1.0));
		mode.addAction(dispenseGearA());
		mode.addAction(dispenseGearB());
		return mode;
	}

	public static ActionGroup getIntoGearPositionFromLeft() {
		ActionGroup mode = new ActionGroup("line up into gear position");
		mode.addAction(goForward(2.0));
		mode.addAction(goBackward(1.3));
//		mode.addAction(aim(60));
		return mode;
	}

	public static ActionGroup getIntoGearPositionFromRight() {
		ActionGroup mode = new ActionGroup("line up into gear position");
		mode.addAction(goForward(2.0));
		mode.addAction(goBackward(1.3));
		mode.addAction(aim(-60));
		mode.addAction(disableAiming);
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
