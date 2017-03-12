package org.usfirst.frc.team467.robot.Autonomous;

import org.usfirst.frc.team467.robot.Autonomous.ActionGroup.Duration;

public class Action {
	private final String description;
	private final Condition condition;
	private final Activity activity;

	public Action(String description, Condition condition, Activity activity) {
		this.description = description;
		this.condition = condition;
		this.activity = activity;
	}

	public Action(String description, ActionGroup.RunOnce runOnce) {
		this.description = description;
		this.condition = runOnce;
		this.activity = runOnce;
	}

	public void run() {
		if (!isDone()) {
			System.out.println(description + ": not done, running");
			doIt();
		} else {
			System.out.println(description + ": done");			
		}
	}

	public boolean isDone() {
		return condition.isDone();
	}

	public void doIt() {
		activity.doIt();
	}
	
	public void resetCondition() {
		if (condition instanceof Duration) {
			((Duration) condition).reset();
		}
	}

	@FunctionalInterface
	public static interface Condition {
		public boolean isDone();
	}

	@FunctionalInterface
	public static interface Activity {
		public void doIt();
	}
	
	@Override
	public String toString() {
		return "Action [description=" + description + ", isDone=" + isDone() + "]";
	}
}
