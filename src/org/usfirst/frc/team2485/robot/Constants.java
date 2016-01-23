package org.usfirst.frc.team2485.robot;

import java.io.File;

public class Constants {
	
	
	
	public String getFileLocation() {
		return "~/constants.txt";
	}
	
	public File getFile() {
		String fileLocation = getFileLocation();
		return new File(fileLocation);
	}
}
