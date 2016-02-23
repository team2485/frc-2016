package org.usfirst.frc.team2485.util;

import org.usfirst.frc.team2485.robot.Constants;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 */
public class CurrentMonitor {
	
	private static CurrentMonitor instance;
	
	public CurrentMonitorGroup leftDriveMonitor, rightDriveMonitor, intakeArmMonitor;
	
	private CurrentMonitor() {
		
//		leftDriveMonitor = new CurrentMonitorGroup(Constants.kLeftDrivePDP, 60);
//		rightDriveMonitor = new CurrentMonitorGroup(Constants.kRightDrivePDP, 60);
//		intakeArmMonitor = new CurrentMonitorGroup(Constants.kIntakeArmPDP, 40, 
//				0.05, true, 0.25);
		Hardware.intakeArmSC.setCurrentMonitor(intakeArmMonitor);

	}
	
	public static CurrentMonitor getInstance() {
		
		if (instance == null) {
			instance = new CurrentMonitor();
		}
		
		return instance;
		
	}
	
	/**
	 * Should be called every 20 ms
	 */
	public void monitorCurrent() {
		
//		if (Hardware.battery.getTotalCurrent() > 120) {
//			leftDriveMonitor.setMaxCurrent(40);
//			rightDriveMonitor.setMaxCurrent(40);
//		} else {
//			leftDriveMonitor.setMaxCurrent(60);
//			rightDriveMonitor.setMaxCurrent(60);
//		}
		
//		leftDriveMonitor.monitorCurrent();
//		rightDriveMonitor.monitorCurrent();
		
		intakeArmMonitor.monitorCurrent();
		
	}
	
}
