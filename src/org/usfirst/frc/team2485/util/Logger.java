package org.usfirst.frc.team2485.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import edu.wpi.first.wpilibj.Timer;

/**
 * Class that writes log data to a file
 * @author Jeremy McCulloch
 *
 */
public class Logger {
	
	private static Logger instance;
	
	private ArrayList<Loggable> components;
	private Queue<JSONObject> queue;
	
	private File file;
	
	private Logger() {
		
		components = new ArrayList<Loggable>();
		queue = new LinkedList<JSONObject>();
		
		file = new File(System.currentTimeMillis() + ".json");
		
		new java.util.Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				writeAll();
				
			}
			
		}, 500, 500);
		
		
	}
	
	public static Logger getInstance() {
		
		if (instance == null) {
			instance = new Logger();
		}
		
		return instance;
		
	}
	
	public void printErr(String sender, String message) {
		
		JSONObject logData = new JSONObject();
		
		logData.put("Time", Timer.getFPGATimestamp());
		logData.put(sender, message);
		
		queue.add(logData);
		
	}
	
	public synchronized void logAll() {
		
		JSONObject allLogData = new JSONObject();
		allLogData.put("Time", Timer.getFPGATimestamp());
		
		for (Loggable component : components) {
					
			Map<String, Object> currData = component.getLogData();
			String name = (String) currData.get("Name");
			
			JSONObject currLogData = new JSONObject();
			for (Iterator<String> iterator = currData.keySet().iterator(); iterator.hasNext();) {
				
				String key = iterator.next();
				if (!key.equals("Name")) {
					currLogData.put(key, currData.get(key));
				}
				
			}
			
			allLogData.put(name, currLogData);
			
		}
		
		queue.add(allLogData);
		
	}
	
	public boolean writeAll() {
		
		boolean failed = false;

		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while (!queue.isEmpty()) {
			
			JSONObject currData = queue.remove();
			
			try {
				currData.write(writer);
			} catch (JSONException e) {
				e.printStackTrace();
				failed = true;
			}
			
		}
		
		return !failed;
		
	}
	
}
