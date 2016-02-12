package org.usfirst.frc.team2485.util;

import org.usfirst.frc.team2485.robot.Hardware;

import edu.wpi.first.wpilibj.CANSpeedController;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * 
 * Used to act on multiple speed controllers at once, or to treat many speed controllers as one. Also has the 
 * ability to monitor current and ramp voltage. 
 * 
 * @author Ben Clark
 * @author Patrick Wamsley
 * @author Anoushka Bose
 * @author Jeremy McCulloch 
 */
public class SpeedControllerWrapper implements SpeedController {

	private SpeedController[] speedControllerList;
	private int[] pdpSlotsList;
	private boolean rampMode = false;
	private double rampRate;
	private boolean currentMonitoring = false;
	private CurrentMonitorGroup currentMonitor;
	private double lastPWM;
	
	
	public SpeedControllerWrapper(SpeedController[] speedControllerList, int[] pdpSlotsList) {
		
		if (speedControllerList.length != pdpSlotsList.length) {
			System.err.println("Combined speed controllers need the same number of PDP ports!!!");
		}
		
		for (SpeedController speedController : speedControllerList) {
			if (speedController instanceof CANSpeedController) {
				System.err.println("Speed Controller Wrappers cannot handle CANSpeedControllers");
			}
		}
		
		this.speedControllerList = speedControllerList;
		this.pdpSlotsList = pdpSlotsList;
		
	}
	
	public SpeedControllerWrapper(SpeedController speedController, int pdpSlot) {
		
		if (speedController instanceof CANSpeedController) {
			System.err.println("Speed Controller Wrappers cannot handle CANSpeedControllers");
		}
		
		this.speedControllerList = new SpeedController[] {speedController};
		this.pdpSlotsList = new int[] {pdpSlot};
		
	}
	
	@Override
	public void pidWrite(double output) {	
		set(output); 
	}

	@Override
	public double get() {
		
		double sum = 0;
		
		for (SpeedController s : speedControllerList) 
			sum += s.get(); 
		
		return sum / speedControllerList.length;
	}

	@Override
	public void set(double speed, byte syncGroup) {
		speed = rampAndMonitorCurrent(speed);
		for (SpeedController s : speedControllerList) 
			s.set(speed, syncGroup);
	}

	@Override
	public void set(double speed) {
		speed = rampAndMonitorCurrent(speed);
		for (SpeedController s : speedControllerList) 
			s.set(speed);
	}
	
	@Override
	public void setInverted(boolean isInverted) {
		for (SpeedController s : speedControllerList) 
			s.setInverted(isInverted);
	}
	
	@Override
	public boolean getInverted() {
		return speedControllerList[0].getInverted();
	}
	
	public double getCurrent() {
		double current = 0.0;
		for (int slot : pdpSlotsList) {
			current += Hardware.battery.getCurrent(slot);
		}
		return current;
	}

	public boolean isMoving() {
		return speedControllerList[0].get() > .05; 
	}
	
	@Override
	public void disable() {
		for (SpeedController controller: speedControllerList) {
			controller.disable();
		}
	}
	
	public void setRampMode(boolean rampMode) {
		this.rampMode = rampMode;
	}
	
	public void setRampRate(double rampRate) {
		this.rampRate = rampRate;
		this.rampMode = true;
	}
	
	public boolean isRampMode() {
		return rampMode;
	}
	
	public double getRampRate() {
		return rampRate;
	}
	
	public void setCurrentMonitoring(boolean currentMonitoring) {
		this.currentMonitoring = currentMonitoring;
	}
	
	public void setCurrentMonitor(CurrentMonitorGroup currentMonitor) {
		this.currentMonitor = currentMonitor;
		this.currentMonitoring = true;
	}
	
	public boolean isCurrentMonitoring() {
		return currentMonitoring;
	}
	
	public CurrentMonitorGroup getCurrentMonitor() {
		return currentMonitor;
	}
	/**
	 * 
	 * @param desiredPWM the value that you would like to set the speedcontrollers to 
	 * @return the value that the speed controller should be set to
	 */
	private double rampAndMonitorCurrent(double desiredPWM) {
		
		if (currentMonitoring && currentMonitor != null) {
			
			double maxAbsPWM = currentMonitor.getMaxAbsolutePWMValue();
			
			if (desiredPWM > maxAbsPWM) {
				desiredPWM = maxAbsPWM;
			} else if (desiredPWM < -maxAbsPWM) {
				desiredPWM = -maxAbsPWM;
			}
			
		}
		
		if (rampMode && rampRate > 0) {
			if (desiredPWM - lastPWM > rampRate) {
				desiredPWM = lastPWM + rampRate;
			} else if (desiredPWM - lastPWM < - rampRate) {
				desiredPWM = lastPWM - rampRate;
			}
		}
		
		return desiredPWM;
	}
} 
