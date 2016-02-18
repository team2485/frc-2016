package org.usfirst.frc.team2485.robot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2485.util.ConstantsIO;

public class Constants {
	
	public static int kLeftShooterCAN = 3, kRightShooterCAN = 2;

	public static int kShooterHoodSolenoid1Port = 4, kShooterHoodSolenoid2Port = 5;
	public static int kBoulderStagerSolenoid1Port = 6, kBoulderStagerSolenoid2Port = 7;
	
	public static int[] kLeftDrivePWM = {4, 3, 2}, 

						kLeftDrivePDP = {15, 3, 2}, 
						kRightDrivePWM = {5, 6, 7}, 
						kRightDrivePDP = {12, 13, 14};
	
	public static int[] kIntakeArmPWM = {0, 1} , kIntakeArmPDP = {0, 1};
	
	public static int kLateralRollerPWM = 9, kLateralRollerPDP = 9,
			kIntakeRollerPWM = 8, kIntakeRollerPDP = 10;
	
	public static int[] kLeftDriveEncoder = {2, 3}, 
			kRightDriveEncoder = {4, 5};
	
	public static int kIntakeArmAbsEncoder = 0;
	
	public static int[] kUltrasonicDIO = {0, 1};

	public static double WHEEL_RADIUS_INCHES = 2.25;
	
	public static float kMoveIntakeManuallyDeadband = 0.3f;
	
	
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
