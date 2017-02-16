package org.usfirst.frc.team467.robot.Autonomous;

public class Action {
	public String description;
	public Condition condition;
	public Activity activity;
	
	public Action(String description, Condition condition, Activity activity) {
		this.description = description;
		this.condition = condition;
		this.activity = activity;
	}
	
	@FunctionalInterface
	public interface Condition {
		public boolean condition();
	}
	
	@FunctionalInterface
	public interface Activity {
		public void doIt();
	}
}
