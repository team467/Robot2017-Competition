package org.usfirst.frc.team467.robot.Autonomous;

import java.util.concurrent.atomic.AtomicInteger;

import org.usfirst.frc.team467.robot.*;

import edu.wpi.first.wpilibj.Timer;

public class Actions {	
//	public static final Action nothingForever = new Action(
//			"Do Nothing Fovever",
//			() -> false, () -> {
//			/* Do Nothing */ });
	
	public static final Action nothingForever(){
		Drive drive = Drive.getInstance();
		return new Action("Do Nothing",
				() -> drive.isStopped(),
				() -> drive.crabDrive(0, 0));
	}
	
	public static ActionGroup doNothing() {
		ActionGroup mode = new ActionGroup("Do Nothing");
		mode.addAction(nothingForever());
		return mode;
	}
	
	public static final Action example1 = new Action(
			"Example 1",
			new ActionGroup.Duration(2), 
			() -> { System.out.println("Running Example Autonomous Action #1: " + System.currentTimeMillis()); });

	public static final Action example2 = new Action(
			"Example 2",
			new ActionGroup.Duration(2),
			() -> { System.out.println("Running Example Autonomous Action #2: " + System.currentTimeMillis()); });

	public static final Action moveForward = new Action(
			"Move Forward 2 seconds",
			new ActionGroup.Duration(1),
			() -> Drive.getInstance().crabDrive(0, 0.5));
	
	public static final Action moveBackward = new Action(
			"Move Backward 2 seconds",
			new ActionGroup.Duration(1),
			() -> Drive.getInstance().crabDrive(0, -0.5));
	
	public static final Action moveLeft = new Action(
			"Move Forward 2 seconds",
			new ActionGroup.Duration(1),
			() -> Drive.getInstance().crabDrive(3*Math.PI/2, 0.5));
	
	public static final Action moveRight = new Action(
			"Move Forward 2 seconds",
			new ActionGroup.Duration(1),
			() -> Drive.getInstance().crabDrive(Math.PI/2, 0.5));

	public static final Action forward(double seconds){
		Drive drive = Drive.getInstance();
		String actionText = "Move Forward " + seconds + "seconds";
		return new Action(actionText,
				new ActionGroup.Duration(seconds),
				() -> drive.crabDrive(0, 0.7));
	}
	
	public static Action backward(double seconds){
		Drive drive = Drive.getInstance();
		String actionText = "Move backward " + seconds + "seconds";
		return new Action(actionText,
				new ActionGroup.Duration(seconds),
				() -> drive.crabDrive(0, -0.5));
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
	
	public static Action dispenseGearA() {
		GearDevice gear = GearDevice.getInstance();
		String actionText = "dispense gear";
		return new Action(actionText,
				new ActionGroup.Duration(1.5),
				() -> gear.goDown());
	}
	
	public static Action dispenseGearB(){
		GearDevice gear = GearDevice.getInstance();
		Drive drive = Drive.getInstance();
		String actionText = "dispense gear";
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
	
	public static ActionGroup newAimProcess(double angle) {
		ActionGroup mode = new ActionGroup("Aim");
		mode.addActions(aim(angle));
		mode.addAction(driveToGear(40));
		mode.enable();
		return mode;
	}
	
	public static ActionGroup dropGearFromLeft(){
		ActionGroup mode = new ActionGroup("gear");
		mode.addAction(forward(0.9));
		mode.addActions(aim(60));
		mode.addAction(forward(1.0));
		mode.addAction(dispenseGearA());
		mode.addAction(dispenseGearB());
		return mode;
	}
	
	public static ActionGroup dropGearFromRight(){
		ActionGroup mode = new ActionGroup("gear");
		mode.addAction(forward(0.9));
		mode.addActions(aim(-60));
		mode.addAction(forward(1.0));
		mode.addAction(dispenseGearA());
		mode.addAction(dispenseGearB());
		return mode;
	}
	
	public static ActionGroup getIntoGearPositionFromLeft() {
		ActionGroup mode = new ActionGroup("line up into gear position");
		mode.addAction(forward(2.0));
		mode.addAction(backward(1.3));
//		mode.addAction(aim(60));
		return mode;
	}
	
	public static ActionGroup getIntoGearPositionFromRight() {
		ActionGroup mode = new ActionGroup("line up into gear position");
		mode.addAction(forward(2.0));
		mode.addAction(backward(1.3));
		mode.addActions(aim(-60));
		return mode;
	}
	
	// Private method makes process, but for only one public variable
	public static ActionGroup newBasicProcess() {
		ActionGroup mode = new ActionGroup("Basic Auto");
		mode.addAction(moveForward);
		mode.addAction(moveRight);
		mode.addAction(moveBackward);
		mode.addAction(moveLeft);
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
