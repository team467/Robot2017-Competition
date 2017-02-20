package org.usfirst.team467.robot;

import org.junit.Test;
import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.Autonomous.ActionGroup;

public class AutonomousTest {
	private ActionGroup autonomous = Actions.exampleProcess;

	@Test
	public void autonomousTest() {
		while (true) {
			autonomous.run();
		}
	}

}
