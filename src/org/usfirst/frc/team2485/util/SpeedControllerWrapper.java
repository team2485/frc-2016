package org.usfirst.frc.team2485.util;

import org.usfirst.frc.team2485.robot.Hardware;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * 
 * Used to act on multible speed controllers at once, or to treat many speed controllers as one. 
 * 
 * @author Ben Clark
 * @author Patrick Wamsley
 * @author Anoushka Bose
 */
public class SpeedControllerWrapper implements SpeedController {

	private SpeedController[] speedControllerList;
	private int[] pdpSlotsList;
	private boolean isInverted = false;
	
	public SpeedControllerWrapper(SpeedController[] speedControllerList, int[] pdpSlotsList) {
		if (!(speedControllerList.length == pdpSlotsList.length)) {
			System.err.println("Combined speed controllers need the same number of PDP ports!!!");
		}
		this.speedControllerList = speedControllerList;
		this.pdpSlotsList = pdpSlotsList;
	}
	
	public SpeedControllerWrapper(SpeedController speedController, int pdpSlot) {
		this.speedControllerList = new SpeedController[] {speedController};
		this.pdpSlotsList = new int[] {pdpSlot};
	}
	
	private int sign() {
		return isInverted ? -1 : 1;
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
		for (SpeedController s : speedControllerList) 
			s.set(speed * sign(), syncGroup);
	}

	@Override
	public void set(double speed) {
		for (SpeedController s : speedControllerList) 
			s.set(speed * sign());
	}
	
	public void setInverted(boolean isInverted) {
		this.isInverted = isInverted;
	}
	
	public boolean getInverted() {
		return isInverted;
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
}
