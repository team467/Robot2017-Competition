package org.usfirst.frc.team467.robot.Autonomous;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.usfirst.frc.team467.robot.Drive;
import org.usfirst.frc.team467.robot.RobotMap;

/**
 * Runs through a set of actions. <br>
 * Can be used in Autonomous and also Teleop routines.
 */
public class ActionGroup {
	private static final Logger LOGGER = Logger.getLogger(ActionGroup.class);
	private String name;
	private LinkedList<Action> agenda;
	private final LinkedList<Action> master;
	private Action action = null;

	public ActionGroup(String name) {
		this.name = name;
		master = new LinkedList<>();
		agenda = new LinkedList<>();
	}

	/**
	 * Run periodically to perform the Actions
	 */
	public void run() {
		if (action == null || action.isDone()) {
			try {
				LOGGER.debug("Next action");
				if (!agenda.isEmpty()) {
					action = agenda.pop();
					LOGGER.info("----- Starting action: " + action.description + " -----");
				} else {
					// Stop everything forever
					LOGGER.info("----- Final action completed -----");
					action = null;
					return;
				}
			} catch (NoSuchElementException e) {
				LOGGER.error("Ran out of actions!", e);
			}
		}
		
		LOGGER.info("run " + action);
		action.doIt();
	}
	
	public boolean isComplete() {
		return action == null && agenda.isEmpty();
	}
	
	public void terminate() {
		LOGGER.debug("Terminating Process");
		agenda.clear();
		action = null;
		Drive.getInstance().aiming.reset();
	}

	public void addAction(Action action) {
		master.add(action);
	}

	public void addActions(List<Action> actions) {
		master.addAll(actions);
	}
	
	public void addActions(ActionGroup actions) {
		master.addAll(actions.master);
	}

	public void enable() {
		LOGGER.debug("Enabling Process");
		for (Action act : master) {
			if (act.condition instanceof Duration) {
				LOGGER.debug("Resetting Duration");
				((Duration) act.condition).reset();
			}
		}
		// Copy master (not reference)
		agenda = new LinkedList<>(master);
		action = null;
	}
	
	static class RunOnce implements Action.Combined {
		boolean isDone = false;
		final Action.Activity activity;
		
		public RunOnce(Action.Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public boolean isDone() {
			return isDone;
		}

		@Override
		public void doIt() {
			activity.doIt();
			isDone = true;
		}
	}
	
	static class Duration implements Action.Condition {
		private double durationMS;
		private double actionStartTimeMS = -1;

		/**
		 * @param duration
		 *            in Seconds
		 */
		public Duration(double duration) {
			durationMS = duration * 1000;
		}

		@Override
		public boolean isDone() {
			if (actionStartTimeMS < 0) {
				actionStartTimeMS = System.currentTimeMillis();
			}

			return System.currentTimeMillis() > durationMS + actionStartTimeMS;
		}

		public void reset() {
			actionStartTimeMS = -1;
		}
	}
	
	static class ReachDistance implements Action.Condition {
		private double distance = 0.0;
		private double currentPosition = 0.0;
		private double lastPosition = 0.0;
		private double increment = 0.0;
		private Drive drive = Drive.getInstance();
		public ReachDistance(double distance) {
			this.distance = distance;
		}

		@Override
		public boolean isDone() {
			lastPosition = currentPosition;
			currentPosition = drive.absoluteDistanceMoved();
			LOGGER.debug("Distances - Target: " + Math.abs(distance) + " Moved: " + currentPosition);
			if (currentPosition > 0.0 && lastPosition == currentPosition) {
				increment++;
			}
			if (increment >= 5) {
				return true;
			} else if (currentPosition >= (Math.abs(distance) - RobotMap.POSITION_ALLOWED_ERROR)) {
				LOGGER.debug("Finished moving");
				return true;
			} else {
				LOGGER.debug("Still moving");
				return false;
			}
		}		
	}

	public String getName() {
		return name;
	}
}
