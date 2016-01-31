package org.usfirst.frc.team2485.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Static class to interface IO between the RoboRio and the Driver Station. 
 * Used to save constants to a file rather than being hard coded. 
 * 
 * @author Ben Clark
 * @author Patrick Wamsley
 */
public class ConstantsIO {

	public static final String ROBO_RIO_CONSTANTS_FILE_PATH = "/home/lvuser/Constants.txt", 
			DRIVER_STATION_CONSTANTS_FILE_PATH = "C:\\Users\\2485\\Documents\\frc-2016\\Constants.txt";
	public static HashMap<String, String> data;
	
	static {
		
		System.out.println("static block");
		
		try {
			data = parseLoadFile(readLocalFile(ROBO_RIO_CONSTANTS_FILE_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					data = parseLoadFile(readLocalFile(ROBO_RIO_CONSTANTS_FILE_PATH));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, 500);
		
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
}

