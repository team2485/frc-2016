package org.usfirst.frc.team2485.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Static class to interface IO between the RoboRio and the Driver Station. 
 * Used to save constants to a file rather than being hard coded. 
 * 
 * @author Ben Clark
 * @author Patrick Wamsley
 * @author Jeremy McCulloch
 */
public class ConstantsIO {

	public static final String ROBO_RIO_CONSTANTS_FILE_PATH = "/home/lvuser/Constants.txt", 
			DRIVER_STATION_CONSTANTS_FILE_PATH = "C:\\Users\\2485\\Documents\\frc-2016\\Constants.txt";
	
	public static HashMap<String, String> data;
	
	public static double kP_Shooter, kI_Shooter, kD_Shooter, kF_Shooter;
	public static double kP_DriveTo, kI_DriveTo, kD_DriveTo;
	public static double kP_Rotate, kI_Rotate, kD_Rotate;

	public static double kDriveVoltageRamp, kShooterVoltageRamp;

	public static int kLeftShooterCAN, kRightShooterCAN;

	public static int kShooterHoodSolenoid1Port, kShooterHoodSolenoid2Port;
	
	public static int[] kLeftDrivePWM, kLeftDrivePDP, kRightDrivePWM, kRightDrivePDP;

	public static int[] kLeftDriveEncoder, kRightDriveEncoder;

	public static double WHEEL_RADIUS_INCHES;
	
	public static void init() {
		
		try {
			data = parseLoadFile(readLocalFile(ROBO_RIO_CONSTANTS_FILE_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			
		}
		
		System.out.println(data);
		
		kP_Shooter = Double.parseDouble(data.get("kP_Shooter"));
		kI_Shooter = Double.parseDouble(data.get("kI_Shooter"));
		kD_Shooter = Double.parseDouble(data.get("kD_Shooter"));
		kF_Shooter = Double.parseDouble(data.get("kF_Shooter"));
		
		kP_DriveTo = Double.parseDouble(data.get("kP_DriveTo"));
		kI_DriveTo = Double.parseDouble(data.get("kI_DriveTo"));
		kD_DriveTo = Double.parseDouble(data.get("kD_DriveTo"));
		
		kP_Rotate = Double.parseDouble(data.get("kP_Rotate"));
		kI_Rotate = Double.parseDouble(data.get("kI_Rotate"));
		kD_Rotate = Double.parseDouble(data.get("kD_Rotate"));
		
		kDriveVoltageRamp = Double.parseDouble(data.get("kDriveVoltageRamp"));
		kShooterVoltageRamp = Double.parseDouble(data.get("kShooterVoltageRamp"));
		
		kLeftShooterCAN = Integer.parseInt(data.get("kLeftShooterCAN"));
		kRightShooterCAN = Integer.parseInt(data.get("kRightShooterCAN"));
		
		kShooterHoodSolenoid1Port = Integer.parseInt(data.get("kShooterHoodSolenoid1Port"));
		kShooterHoodSolenoid2Port = Integer.parseInt(data.get("kShooterHoodSolenoid2Port"));
		
		kLeftDrivePWM = parseIntArray(data.get("kLeftDrivePWM"), ",");
		kLeftDrivePDP = parseIntArray(data.get("kLeftDrivePDP"), ",");
		kRightDrivePWM = parseIntArray(data.get("kRightDrivePWM"), ",");
		kRightDrivePDP = parseIntArray(data.get("kRightDrivePDP"), ",");

		kLeftDriveEncoder = parseIntArray(data.get("kLeftDriveEncoder"), ",");
		kRightDriveEncoder = parseIntArray(data.get("kRightDriveEncoder"), ",");
		
		WHEEL_RADIUS_INCHES = Double.parseDouble(data.get("WHEEL_RADIUS_INCHES"));

	}

	/**
	 * Used to read a file locally. 
	 * @param filePath
	 */
	public static String readLocalFile(String filePath) throws IOException {
		File file = new File(filePath); 
		String fileString; 

		StringBuilder fileContents = new StringBuilder((int)file.length()); 
		Scanner scanner = new Scanner(file); 
		String lineSeperator = "\n"; 

		try {
			while (scanner.hasNextLine())
				fileContents.append(scanner.nextLine() + lineSeperator);
			fileString = fileContents.toString(); 
			//remove the added "\n" 
			fileString = fileString.substring(0, fileString.length() - 1); 
		} finally {
			scanner.close();
		}
		return fileString; 
	}

	/**
	 * @param loadFileContents
	 * @return HashMap containing constant names and their values as declared in the load file. 
	 */
	public static HashMap<String, String> parseLoadFile(String fileContents) {

		HashMap<String, String> constantsMap = new HashMap<String, String>(); 
		Scanner scanner = new Scanner(fileContents); 
		
		while (scanner.hasNextLine()) {

			String currLine = scanner.nextLine().trim(); 

			if (currLine.contains("=")) {

				String constantName	 = currLine.substring(0, currLine.indexOf("=")).trim(); 
				String constantValue = currLine.substring(currLine.indexOf("=") + 1).trim();  

				constantsMap.put(constantName, constantValue);
			}
		}
		return constantsMap; 
	}

	/**
	 * NEEDS TO BE WRITTEN AND DEPLOTED FROM ELSE WHERE: WIDGITS? 
	 */
	public static void writeConstantsToRoboRio(String loadFileContents) {

		PrintWriter printWriter = null; 

		try {
			printWriter = new PrintWriter(new FileOutputStream("ftp://roborio-2485-frc.local" + ROBO_RIO_CONSTANTS_FILE_PATH)); //definitely won't work 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 

		if (printWriter != null) {
			printWriter.write(loadFileContents); 
			printWriter.flush();
			printWriter.close();
		} else {
			System.err.println("PrintWriting failed to init, unable to write constants.");
		}

	}
	
	private static int[] parseIntArray(String s, String delimiter) {
		
		String[] split = s.split(delimiter);
		
		int[] ret = new int[split.length];
		for (int i = 0; i < split.length; i++) {
			ret[i] = Integer.parseInt(split[i]);
		}
		
		return ret;
		
	}
}

