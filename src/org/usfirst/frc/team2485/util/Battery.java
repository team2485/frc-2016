package org.usfirst.frc.team2485.util;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Wrapper class to log PDP data
 * @author Ben Clark
 */
public class Battery extends PowerDistributionPanel implements Loggable {

	public Battery() {
		super();
	}
	
	@Override
	public Map<String, Object> getLogData() {
		Map<String, Object> logData = new HashMap<String, Object>();
		
		logData.put("Name", "Battery");
		logData.put("Voltage", getVoltage());
		logData.put("Temperature", getTemperature());
		logData.put("Total Current", getTotalCurrent());
		logData.put("Power", getTotalPower());
		
		return logData;
	}	
}