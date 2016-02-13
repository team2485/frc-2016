package org.usfirst.frc.team2485.util;

import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 */
public class CurrentMonitor {
	
	private static CurrentMonitor instance;
	
	public CurrentMonitorGroup leftDriveMonitor, rightDriveMonitor;
	
	private CurrentMonitor() {
		
		leftDriveMonitor = new CurrentMonitorGroup(ConstantsIO.kLeftDrivePDP, 60);
		rightDriveMonitor = new CurrentMonitorGroup(ConstantsIO.kRightDrivePDP, 60);

	}
	
	public CurrentMonitor getInstance() {
		
		if (instance == null) {
			instance = new CurrentMonitor();
		}
		
		return instance;
		
	}
	
	public void monitorCurrent() {
		
		if (Hardware.battery.getTotalCurrent() > 120) {
			leftDriveMonitor.setMaxCurrent(40);
			rightDriveMonitor.setMaxCurrent(40);
		} else {
			leftDriveMonitor.setMaxCurrent(60);
			rightDriveMonitor.setMaxCurrent(60);
		}
		
		leftDriveMonitor.monitorCurrent();
		rightDriveMonitor.monitorCurrent();
		
	}
	
}
