package org.usfirst.frc.team467.robot;

import edu.wpi.cscore.HttpCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class CameraStream {

	private HttpCamera cam;
	private CameraServer camServer = CameraServer.getInstance();
	private static CameraStream instance = null;

	public static CameraStream getInstance() {
		if (instance == null) {
			instance = new CameraStream();
		}
		return instance;
	}

	/**
	 * Creates a new Camera Stream
	 */
	private CameraStream() {
		cam = new HttpCamera("Boiler Cam", "http://balin.local:8080/?action=stream");
		camServer.addCamera(cam);
		camServer.startAutomaticCapture(cam);
	}

}
