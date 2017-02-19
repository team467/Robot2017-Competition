package org.usfirst.team467.robot;

import org.junit.Test;
import org.usfirst.frc.team467.robot.autonomous.Actions;
import org.usfirst.frc.team467.robot.autonomous.Process;

public class AutonomousTest {
	private Process autonomous = Actions.getExampleProcess();
	
	@Test
	public void autonomousTest() {
		while (true) {
			autonomous.run();
		}
	}
	
}
