package org.usfirst.frc.team467.robot.Autonomous;

import org.usfirst.frc.team467.robot.Autonomous.Action.Activity;
import org.usfirst.frc.team467.robot.Autonomous.Action.Condition;

public class Action {
	public String description;
	public Condition condition;
	public Activity activity;

	public Action(String description, Condition condition, Activity activity) {
		this.description = description;
		this.condition = condition;
		this.activity = activity;
	}

	public boolean isDone() {
		return condition.isDone();
	}

	public void doIt() {
		activity.doIt();
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
