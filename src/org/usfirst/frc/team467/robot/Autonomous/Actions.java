package org.usfirst.frc.team467.robot.Autonomous;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.*;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Timer;

public class Actions {
	private static final Logger LOGGER = Logger.getLogger(Actions.class);
	static Timer timer = new Timer();

	public static final Action nothing(){
		Drive drive = Drive.getInstance();
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> drive.isStopped(),
				() -> drive.crabDrive(0, 0));
	}
	
	public static final Action nothingForever(){
		Drive drive = Drive.getInstance();
		String actionText = "Do Nothing";
		return new Action(actionText,
				() -> false,
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

	public static Action print(String message) {
		return new Action(
				"Print custom message",
				new ActionGroup.RunOnce(() -> LOGGER.info(message)));
	}
	
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
		return new Action(actionText,
				new ActionGroup.Duration(seconds),
				() -> drive.crabDrive(0, -0.5));
	}

	public static Action setPositionMode() {
		Drive drive = Drive.getInstance();
		return new Action("Setting position mode for driving a set distance.",
				() -> isInPositionMode(), // TODO Make Run Once
				() -> drive.setPositionMode());
	}
	
	public static ActionGroup setPositionProcess() {
		ActionGroup mode = new ActionGroup("Set Position mode if sensors are present");
		if (RobotMap.useSpeedControllers) {
			mode.addAction(setPositionMode());
		} else {
			mode.addAction(print("ERROR: NO SPEED SENSORS"));
			mode.addAction(nothingForever());
		}
		return mode;
	}

	/**
	 * Autonomous requires a check to see if something is complete, in this case if the wheel pods are in position mode.
	 *
	 * @return true when the position mode is set
	 */
	public static boolean isInPositionMode() {
		Drive drive = Drive.getInstance();
		if (drive.getControlMode() == TalonControlMode.Position) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Autonomous requires a check to see if something is complete, in this case if the wheel pods are in position mode.
	 *
	 * @return true when the position mode is not set
	 */
	public static boolean isNotInPositionMode() {
		Drive drive = Drive.getInstance();
		if (drive.getControlMode() != TalonControlMode.Position) {
			return true;
		} else {
			return false;
		}
	}

	public static Action setDriveMode() {
		Drive drive = Drive.getInstance();
		return new Action("Setting drive mode",
				() -> isNotInPositionMode(),
				() -> drive.setDefaultDriveMode());
	}

	public static Action moveDistanceForward(double distance) {
		Drive drive = Drive.getInstance();
		String actionText = "Move forward " + distance + " feet";
		return new Action(actionText,
				() -> moveDistanceComplete(distance),
				() -> drive.crabDrive(0, distance));
	}

	public static boolean moveDistanceComplete(double distance) {
		Drive drive = Drive.getInstance();
		double distanceMoved = drive.absoluteDistanceMoved();
		System.out.println("Distances - Target: " + Math.abs(distance) + " Moved: " + distanceMoved);
		if (distanceMoved >= (Math.abs(distance) - RobotMap.POSITION_ALLOWED_ERROR)) {
			System.out.println("Finished moving");
			return true;
		} else {
			System.out.println("Still moving");
			return false;
		}
	}

	public static Action turnAndMoveDistance(double angle, double distance) {
		Drive drive = Drive.getInstance();
		String actionText = "Turn to " + angle + " and move " + distance + " feet";
		return new Action(actionText,
				() -> moveDistanceComplete(distance),
				() -> drive.crabDrive(angle, distance));
	}

	public static ActionGroup turnAndMoveDistanceProcess(double angle, double distance) {
		String actionGroupText = "Turn to " + angle + " and move " + distance + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(setPositionProcess());
		mode.addAction(turnAndMoveDistance(angle, distance));
		mode.addAction(setDriveMode());
		return mode;
	}

	public static ActionGroup moveDistanceForwardProcess(double distance) {
		String actionGroupText = "Move forward " + distance + " feet";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(setPositionProcess());
		mode.addAction(moveDistanceForward(distance));
		mode.addAction(setDriveMode());
		return mode;
	}

	public static ActionGroup moveInSquareTest(double distance) {
		String actionGroupText = "Move in " + distance + " foot square";
		ActionGroup mode = new ActionGroup(actionGroupText);
		mode.addActions(setPositionProcess());
		mode.addAction(turnAndMoveDistance(0, distance));
		mode.addAction(turnAndMoveDistance((Math.PI / 2), distance));
		mode.addAction(turnAndMoveDistance(Math.PI, distance));
		mode.addAction(turnAndMoveDistance((3 * Math.PI / 2), distance));
		mode.addAction(setDriveMode());
		return mode;
	}

	public static Action aim(double angle) {
		Drive drive = Drive.getInstance();
		Action aim =  new Action(
				"Aim",
				() -> drive.aiming.onTarget(),
				() -> drive.turnToAngle(angle));
		return aim;
	}

	public static Action disableAiming() {
		Drive drive = Drive.getInstance();
		Action disableAimingAction = new Action(
				"Disable",
				() -> true,
				() -> drive.aiming.disable());
		return disableAimingAction;
	}

	public static ActionGroup aimAndDisable(double angle) {
		ActionGroup mode = new ActionGroup("Aim");
		mode.addAction(aim(angle));
		mode.addAction(disableAiming());
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
		mode.addActions(aimAndDisable(angle));
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
		mode.addAction(nothing());
		return mode;
	}

	public static ActionGroup dropGearFromLeft(){
		ActionGroup mode = new ActionGroup("gear");
		mode.addAction(goForward(0.9));
		mode.addAction(aim(60));
		mode.addAction(disableAiming());
		mode.addAction(goForward(1.0));
		mode.addAction(dispenseGearA());
		mode.addAction(dispenseGearB());
		return mode;
	}

	public static ActionGroup dropGearFromRight(){
		ActionGroup mode = new ActionGroup("gear");
		mode.addAction(goForward(0.9));
		mode.addAction(aim(-60));
		mode.addAction(disableAiming());
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
		mode.addAction(disableAiming());
		return mode;
	}
	
	public static ActionGroup newDriveDistanceForwardSubdivided() {
		ActionGroup mode = new ActionGroup("Drive 6 feet in 1 foot intervals");
		mode.addActions(setPositionProcess());
		for (int i = 0; i <= 6; i++){
			mode.addAction(moveDistanceForward(1));
		}
		mode.addAction(setDriveMode());
		return mode;
	}
	
	public static ActionGroup newDriveSquareProcess() {
		ActionGroup mode = new ActionGroup("Drive in a 2x2 square clockwise from bottom left");
		mode.addActions(setPositionProcess());
		mode.addAction(turnAndMoveDistance(0, 2));
		mode.addAction(turnAndMoveDistance(90, 2));
		mode.addAction(turnAndMoveDistance(180, 2));
		mode.addAction(turnAndMoveDistance(270, 2));
		mode.addAction(setDriveMode());
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
