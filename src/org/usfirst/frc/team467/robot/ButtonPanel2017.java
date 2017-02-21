package org.usfirst.frc.team467.robot;

import java.util.HashMap;
import edu.wpi.first.wpilibj.Joystick;

public class ButtonPanel2017 {

	public enum Buttons {
		VISION_ALIGN_SHOOT(1),
		SHOOTER_SHOOT(2),
		SHOOTER_FAILSAFE(3),
		CLIMBER_UP(4),
		CLIMBER_REVERSE(5),
		GEAR_DOWN(6),
		TOGGLE_SWITCH_UP(7),
		VISION_ALIGN_GEAR(8),
		TOGGLE_SWITCH_DOWN(9),
		INTAKE_OUT(10),
		INTAKE_IN(11);

		public final int id;

		Buttons(int id) {
			this.id = id;
		}
	}

	Joystick buttonPanel = null;

	// Set of button states
	HashMap<Buttons, Boolean> buttonStates = new HashMap<Buttons, Boolean>();

	public ButtonPanel2017(int port) {
		buttonPanel = new Joystick(port);
		for (Buttons button : Buttons.values()) {
			buttonStates.put(button, false);
		}
	}

	public void readInputs() {
		for (Buttons button : Buttons.values()) {
			buttonStates.put(button, buttonPanel.getRawButton(button.id));
		}
	}

	public boolean buttonDown(Buttons button) {
		return buttonStates.get(button);
	}

}
