package org.usfirst.frc.team467.robot.Autonomous;

import java.util.LinkedList;
import java.util.List;

/**
 * Runs through a set of actions. <br>
 * Can be used in Autonomous and also Teleop routines.
 */
public class Process {
	private int index = 0;
	private LinkedList<Action> agenda;
	private Action action;
	
	public Process() {
		agenda = new LinkedList<Action>();
		 agenda.add(Actions.example);
		action = agenda.pop();
	}

	public void run()
	{
//		if (action.condition.condition()) {
//			System.out.println(action.description);
//			action.activity.doIt();
//		} else {
//			action = agenda.pop();
//		}
		// TODO rename condition method
		if (action.isDone()) {
			// TODO print here
			action = agenda.pop();
		}

		System.out.println(action.description);
		action.activity.doIt();
	}
	
	public void addAction(Action action) {
		agenda.add(action);
	}
	
	public void addActions(List<Action> actions) {
		agenda.addAll(actions);
	}
	
	public static class Duration implements Action.Condition {
		private double duration;
		private double actionStartTimeMS = -1;
		public Duration(double duration) {
			this.duration = duration;
		}
		
		@Override
		public boolean isDone() {
			if (actionStartTimeMS == -1) {
				actionStartTimeMS = System.currentTimeMillis();
			}
			
			return (System.currentTimeMillis() > duration + actionStartTimeMS);
		}
		
	}
}
