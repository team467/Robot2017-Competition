package org.usfirst.frc.team467.robot.Autonomous;

import org.usfirst.frc.team467.robot.*;

import edu.wpi.first.wpilibj.Timer;

public class Actions {
	static Timer timer = new Timer();
	
//	public static final Action nothingForever = new Action(
//			"Do Nothing Fovever",
//			() -> false, () -> {
//			/* Do Nothing */ });
	
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
				() -> isDurationOver(seconds),
				() -> drive.crabDrive(0, 0.7));
	}
	
	public static Action goBackward(double seconds){
		Drive drive = Drive.getInstance();
		String actionText = "Move backward " + seconds + "seconds";
		new ActionGroup.Duration(seconds);
		timer.reset();
		timer.start();
		return new Action(actionText,
				() -> isDurationOver(seconds),
				() -> drive.crabDrive(0, -0.5));
	}
	
	public static boolean isDurationOver(double seconds){
		if (timer.get() >= seconds){
			return true;
		}else{
			return false;
		}
	}

	public static Action aim(double angle) {
		Drive drive = Drive.getInstance();
		return new Action("Aim", () -> drive.aiming.onTarget(), () -> drive.turnToAngle(angle));
	}
	
	public static Action dispenseGearA() {
		GearDevice gear = GearDevice.getInstance();
		String actionText = "dispense gear";
		new ActionGroup.Duration(1.5);
		timer.reset();
		timer.start();
		return new Action(actionText,
				() -> isDurationOver(1.5),
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
				() -> isDurationOver(1.5),
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
		mode.addAction(aim(angle));
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
		mode.addAction(goForward(1.0));
		mode.addAction(dispenseGearA());
		mode.addAction(dispenseGearB());
		return mode;
	}
	
	public static ActionGroup dropGearFromRight(){
		ActionGroup mode = new ActionGroup("gear");
		mode.addAction(goForward(0.9));
		mode.addAction(aim(-60));
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
