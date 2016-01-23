package org.usfirst.frc.team2485.util;

import java.util.Map;
/**
 * Interface that represents an object that sends data to the logger
 * @author Jeremy McCulloch
 *
 */
public interface Loggable {
	public Map<String, Object> getLogData(); // Must contain key "Name", name may not be "Time"
}
