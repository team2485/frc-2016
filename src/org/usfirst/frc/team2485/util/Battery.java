package org.usfirst.frc.team2485.util;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Wrapper class to log PDP data
 * @author Ben Clark
 */
public class Battery implements Loggable {

	private PowerDistributionPanel pdp;
	public Battery() {
		pdp = new PowerDistributionPanel();
	}
	
	
	
	@Override
	public Map<String, Object> getLogData() {
		Map<String, Object> logData = new HashMap<String, Object>();
		
		logData.put("Name", "Battery");
		logData.put("Voltage", pdp.getVoltage());
		logData.put("Temperature", pdp.getTemperature());
		logData.put("Total Current", pdp.getTotalCurrent());
		logData.put("Power", pdp.getTotalPower());
		
		return logData;
	}



	public double getCurrent(int slot) {
		return pdp.getCurrent(slot);
	}

	public double getTemperature() {
		return pdp.getTemperature();
	}
	
	public double getTotalPower() {
		return pdp.getTotalPower();
	}
	
	public double getVoltage() {
		return pdp.getVoltage();
	}

	public double getTotalCurrent() {
		return pdp.getTotalCurrent();
	}	
}