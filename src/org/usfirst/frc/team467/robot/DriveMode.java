package org.usfirst.frc.team467.robot;

public enum DriveMode {
	CRAB, // Crab Drive
	TURN, // Turn in Place
	UNWIND, // Unwind the wheel pods
	STRAFE, // Strafe drive: robot drives sideways
	FIELD_ALIGN, // Drive field align
	VECTOR, // Drives field aligned, but allows turns during movement
	XB_SPLIT, // left joystick is front, back, right joystick is left, right
}
