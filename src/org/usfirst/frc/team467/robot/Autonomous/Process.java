package org.usfirst.frc.team467.robot.Autonomous;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Drive;

/**
 * Runs through a set of actions. <br>
 * Can be used in Autonomous and also Teleop routines.
 */
public class Process {
	private static final Logger LOGGER = Logger.getLogger(Process.class);
	private String name;
	private LinkedList<Action> agenda;
	private final LinkedList<Action> master;
	private Action action = null;
	
	public Process(String name) {
		this.name = name;
		master = new LinkedList<>();
		agenda = new LinkedList<>();
		reset();
	}
	
	/**
	 * Run periodically to perform the Actions
	 */
	public void run()
	{
//		if (action.condition.condition()) {
//			System.out.println(action.description);
//			action.activity.doIt();
//		} else {
//			action = agenda.pop();
//		}
		if (action == null || action.isDone()) {
			try {
				LOGGER.debug("Next action");
				action = agenda.pop();
			} catch (NoSuchElementException e) {
				LOGGER.debug("Ran out of actions");
				// Stop everything forever
				action = new Action("Process Complete", () -> {return false;}, () -> { Drive.getInstance().stop();} );
			}
			System.out.println("----- Starting action: " + action.description + " -----");
		}

		System.out.println(action.description);
		action.activity.doIt();
	}
	
	public void addAction(Action action) {
		master.add(action);
		reset();
	}
	
	public void addActions(List<Action> actions) {
		master.addAll(actions);
		reset();
	}
	
	public void reset() {
		for (Action act : master) {
			if (act.condition == (Duration) act.condition) {
				((Duration) act.condition).reset();
			}
		}
		// Copy master (not reference)
		agenda = new LinkedList<>(master);
		action = null;
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
		
		public void reset() {
			actionStartTimeMS = -1;
		}
	}
	
	public String getName() {
		return name;
	}
}
