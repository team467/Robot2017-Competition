package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.Autonomous.Actions;
import org.usfirst.frc.team467.robot.Autonomous.Process;

public class AutonomousTest {
	private Process autonomous = Actions.getExampleProcess();

	public void autonomousTest() {
		while (true) {
			autonomous.run();
		}
	}

}
