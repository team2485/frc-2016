package org.usfirst.frc.team2485.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;

/**
 * Class that writes log data to a file
 * @author Jeremy McCulloch
 *
 */
public class Logger {
	
	private static Logger instance;
	
	private ArrayList<Loggable> components;
	private JSONArray allData;
	
	private File file, tempFile;
	
	private Logger() {
		
		components = new ArrayList<Loggable>();
		allData = new JSONArray();
		
		File logDir = new File("/home/lvuser/logs");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		
		file = new File("/home/lvuser/logs/" + System.currentTimeMillis() + ".json");
		tempFile = new File("/home/lvuser/logs/temp.json");

		new java.util.Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				writeAll();
			}
			
		}, 500, 500);
		
		
	}
	
	public synchronized static Logger getInstance() {
		
		if (instance == null) {
			instance = new Logger();
		}
		
		return instance;
		
	}
	
	public void printErr(String sender, String message) {
		
		JSONObject currData = new JSONObject();
		
		currData.put("Time", Timer.getFPGATimestamp());
		
		if (RobotState.isDisabled()) {
			currData.put("Mode", "Disabled");
		} else if (RobotState.isAutonomous()) {
			currData.put("Mode", "Auto");
		} else if (RobotState.isOperatorControl()) {
			currData.put("Mode", "Teleop");
		} else if (RobotState.isTest()) {
			currData.put("Mode", "Test");
		} else {
			currData.put("Mode", "Other");
		}
		
		currData.put("Type", "printErr");
		currData.put(sender, message);
		
		allData.put(currData);
		
	}
	
	public void addLoggable(Loggable l) {
		components.add(l);
	}
	
	public synchronized void logAll() {
		
		JSONObject thisTimeData = new JSONObject();
		
		thisTimeData.put("Time", Timer.getFPGATimestamp());
		thisTimeData.put("Type", "logAll");
		
		if (RobotState.isDisabled()) {
			thisTimeData.put("Mode", "Disabled");
		} else if (RobotState.isAutonomous()) {
			thisTimeData.put("Mode", "Auto");
		} else if (RobotState.isOperatorControl()) {
			thisTimeData.put("Mode", "Teleop");
		} else if (RobotState.isTest()) {
			thisTimeData.put("Mode", "Test");
		} else {
			thisTimeData.put("Mode", "Other");
		}

		
		for (Loggable component : components) {
					
			Map<String, Object> currData = component.getLogData();
			String name = (String) currData.get("Name");
			
			JSONObject thisComponentData = new JSONObject();
			for (Iterator<String> iterator = currData.keySet().iterator(); iterator.hasNext();) {
				
				String key = iterator.next();
				if (!key.equals("Name")) {
					thisComponentData.put(key, currData.get(key));
				}
				
			}
			
			thisTimeData.put(name, thisComponentData);
			
		}
		
		allData.put(thisTimeData);
	}
	
	public void writeAll() {

		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(tempFile.getAbsoluteFile(), false));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		allData.write(writer);
		
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tempFile.renameTo(file);
		
	}
	
}
