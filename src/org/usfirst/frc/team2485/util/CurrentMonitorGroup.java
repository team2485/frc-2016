package org.usfirst.frc.team2485.util;

import org.usfirst.frc.team2485.robot.Hardware;

import edu.wpi.first.wpilibj.Timer;


public class CurrentMonitorGroup {
	
	private int[] pdpSlots;
	private double maxCurrent; 
	private double timeOverCurrent;
	private double lastTimeUpdated; 
	private double maxTimeOverCurrent;
	private boolean usesEmergencyStop;
	private double timeout;
	public static final double EMERGENCY_STOP_FLAG = -1;
	
	/**
	 * 
	 * @param pdpSlots
	 * @param maxCurrent
	 * @param maxTimeOverCurrent max time before starts limiting current in seconds
	 * @param usesEmergencyStop 
	 * @param timeout time in seconds after over current before allowed 
	 * to resume normal operation
	 */
	public CurrentMonitorGroup(int[] pdpSlots, double maxCurrent, 
			double maxTimeOverCurrent, boolean usesEmergencyStop, double timeout) {
		
		this.pdpSlots = pdpSlots;
		this.maxCurrent = maxCurrent;
		this.timeOverCurrent = 0;
		this.lastTimeUpdated = -1;
		this.maxTimeOverCurrent = maxTimeOverCurrent;
		this.usesEmergencyStop = usesEmergencyStop;
		this.timeout = timeout;
		
	}
	
	/**
	 * Called every 20 ms by CurrentMonitor
	 */
	public void monitorCurrent() {
		
		double currTime = Timer.getFPGATimestamp();
		
		if (lastTimeUpdated < 0) {
			lastTimeUpdated = currTime;
		}
		
		double timeSinceLastUpdate = currTime - lastTimeUpdated;

		double current = 0.0;
		for (int i : pdpSlots) {
			current += Hardware.battery.getCurrent(i);
		}

		if (current > maxCurrent) {
			timeOverCurrent += timeSinceLastUpdate;
			if (timeOverCurrent > maxTimeOverCurrent) {
				timeOverCurrent = maxTimeOverCurrent + timeout;
				System.out.println("CurrentMonitorGroup: monitorCurrent: overcurrent");
			}
		} else {
			timeOverCurrent -= timeSinceLastUpdate;
			if (timeOverCurrent < 0) {
				timeOverCurrent = 0;
			}
		}
//		System.out.println("CurrentMonitorGroup: monitorCurrent: current = " + current);
//
//		System.out.println("CurrentMonitorGroup: monitorCurrent: total current = " + Hardware.battery.getTotalCurrent());
		lastTimeUpdated = currTime;

	}
	
	public void setMaxCurrent(double maxCurrent) {
		
		this.maxCurrent = maxCurrent;
		
	}
	
	public double getMaxCurrent() {
		
		return maxCurrent;
		
	}
	
	/**
	 * 
	 * @return maximum magnitude of PWM
	 */
	public double getMaxAbsolutePWMValue() {
		
		if (timeOverCurrent > maxTimeOverCurrent) {
			return usesEmergencyStop ? EMERGENCY_STOP_FLAG : 0.0;
		} else {
			return 1.0;
		}
		
	}
	
	


}