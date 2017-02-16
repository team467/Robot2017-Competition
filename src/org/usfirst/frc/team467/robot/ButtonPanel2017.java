package org.usfirst.frc.team467.robot;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Joystick;

public class ButtonPanel2017 {
	
	public enum Buttons {
		CLIMBER_UP(1), 
		CLIMBER_DOWN(2),
		SHOOTER_SPIN(3), 
        SHOOTER_FAILSAFE(4),
        INTAKE_IN(5),
        INTAKE_OUT(6),
        AGITATOR_SHOOT(7),
        AGITATOR_REVERSE(8);
		
		public final int id;
		
		Buttons(int id) {
			this.id = id;
		}
	}
	
	Joystick buttonPanel = null;
	
	//Set of button states
	HashMap<Buttons, Boolean> buttonStates = new HashMap<Buttons, Boolean>();
	
	public ButtonPanel2017(int port){
		buttonPanel = new Joystick(port);
		for (Buttons button : Buttons.values()) {
			buttonStates.put(button, false);
		}
	}
	
	public void readInputs(){
		for (Buttons button : Buttons.values()) {
			buttonStates.put(button, buttonPanel.getRawButton(button.id));
		}
	}
	
	public boolean buttonDown(Buttons button){
		return buttonStates.get(button);
	}

}
