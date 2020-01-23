package ru.prolib.bootes.protos;

import ru.prolib.bootes.lib.robo.s3.S3RobotState;

public class PROTOSRobotState extends S3RobotState {
	protected final String id;
	
	public PROTOSRobotState(String robot_id) {
		this.id = robot_id;
	}
	
	public String getRobotID() {
		return id;
	}
	
	public PROTOSDataHandler getSessionDataHandler() {
		return (PROTOSDataHandler) super.getSessionDataHandler();
	}
	
}
