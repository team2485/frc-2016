package org.usfirst.frc.team2485.robot;

import java.io.IOException;
import java.util.HashMap;

import org.usfirst.frc.team2485.util.ConstantsIO;

public class Constants {
	
	private static HashMap<String, String> constantsMap;
	
	public static enum Type {INT, DOUBLE, STRING, BOOLEAN}  
	
	public static Object getConstant(String constantName, Type type) {
		
		switch (type) {
			case INT: 
				return Integer.parseInt(constantsMap.get(constantName)); 
			case DOUBLE: 
				return Double.parseDouble(constantsMap.get(constantName));
			case BOOLEAN: 
				return Boolean.parseBoolean(constantsMap.get(constantName));
			case STRING: 
				return constantsMap.get(constantName);
			default: 
				return (Object)constantsMap.get(constantName); 
		}
	}
	
	public static void update() {
		try {
			constantsMap =	ConstantsIO.parseLoadFile(
							ConstantsIO.readLocalFile(
							ConstantsIO.ROBO_RIO_CONSTANTS_FILE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
