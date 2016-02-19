package org.usfirst.frc.team2485.util;

import org.usfirst.frc.team2485.robot.Hardware;

import edu.wpi.first.wpilibj.Timer;

/**
 * @author Jeremy McCulloch
 */
public class CurrentMonitorGroup {
	
	private int[] pdpSlots;
	private double maxCurrent; 
	private double timeOverCurrent;
	private double lastTimeUpdated; 
	private double maxTimeOverCurrent;
	
	public CurrentMonitorGroup(int[] pdpSlots, double maxCurrent, double maxTimeOverCurrent) {
		
		this.pdpSlots = pdpSlots;
		this.maxCurrent = maxCurrent;
		this.timeOverCurrent = 0;
		this.lastTimeUpdated = -1;
		this.maxTimeOverCurrent = maxTimeOverCurrent;
		
	}
	
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
		} else {
//			timeOverCurrent = 0;
			timeOverCurrent -= timeSinceLastUpdate;
			if (timeOverCurrent < 0) {
				timeOverCurrent = 0;
			}
		}

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
		
		return timeOverCurrent > maxTimeOverCurrent ? 0.0 : 1.0;
		
	}


}