package org.usfirst.frc.team467.robot;

import org.usfirst.frc.team467.robot.ButtonPanel2017.Buttons;

import edu.wpi.first.wpilibj.Spark;

public class BallIntake {
	private static BallIntake ballintake = null;
	private static Spark spark;
	private static ButtonPanel2017 buttonPanel;

	public static BallIntake getInstance() {
		if (ballintake == null) {
			ballintake = new BallIntake();
		}
		return ballintake;
	}

	public enum Mode {
		ON, OFF, REVERSE;
	}

	Mode mode = Mode.ON;

	private BallIntake() {
		spark = new Spark(RobotMap.INTAKE_MOTOR);
		buttonPanel = DriverStation2017.getInstance().getButtonPanel();
	}

	public void startIntake() {
		spark.set(1.0);
	}

	public void reverse() {
		// reverse should be slower than intake
		spark.set(-0.1);
	}

	public void stop() {
		spark.set(0.0);
	}

	public void updateMode() {
		if (buttonPanel.buttonDown(Buttons.INTAKE_IN)) {
			if (mode == Mode.ON) {
				mode = Mode.OFF;
			} else {
				mode = Mode.ON;
			}
		} else if (buttonPanel.buttonDown(Buttons.INTAKE_OUT)) {
			if (mode == Mode.REVERSE) {
				mode = Mode.OFF;
			} else {
				mode = Mode.REVERSE;
			}
		}

	}

	public void update() {
		updateMode();
		switch (mode) {
		case ON:
			startIntake();
			break;
		case REVERSE:
			reverse();
			break;
		case OFF:
			stop();
			break;
		}

	}

}
