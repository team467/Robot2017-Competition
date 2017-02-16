package org.usfirst.frc.team467.robot.Autonomous;

import java.util.LinkedList;

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
		// agenda.add(Actions.example);
		action = agenda.pop();
	}

	public void run()
	{
		if (action.condition.condition()) {
			System.out.println(action.description);
			action.activity.doIt();
		} else {
			action = agenda.pop();
		}
	}
	
	public void addAction(Action action) {
		agenda.add(action);
	}
	
	public void addActions(LinkedList<Action> actions) {
		agenda.addAll(actions);
	}
}
