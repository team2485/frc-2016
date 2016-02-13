package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.Loggable;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 *  also known as RockYourPebblesOff
 * 
 * @author Amanda Wasserman
 * @author Vicky Comunale
 * @author Ben Clark
 *
 */

public class BoulderStager implements Loggable {
	//Declare different pneumatics parts

	private Ultrasonic sonic;
	
	private Solenoid solenoid1, solenoid2;
	
	private Position position;

	public enum Position {
		INTAKE, NEUTRAL, SHOOTING;
	}


	public BoulderStager() {
		solenoid1 = Hardware.boulderStagerSolenoid1;
		solenoid2 = Hardware.boulderStagerSolenoid2;

		position = Position.NEUTRAL;
	}

	public void setPosition(Position position) {
		
		this.position = position;
		
		switch(position) {

		case INTAKE:
			solenoid1.set(true);
			solenoid2.set(true);
			break;

		case NEUTRAL:
			solenoid1.set(true);
			solenoid2.set(false);
			break;

		case SHOOTING:
			solenoid1.set(false);
			solenoid2.set(false);
			break;

		}

	}
	
	public Position getPosition() {
		return position;
	}
	
	public String getPositionString() {
		if (position == Position.INTAKE) {
			return "Intake";
		} else if (position == Position.NEUTRAL) {
			return "Neutral";
		} else {
			return "Shooting";
		}
	}
	
	public boolean hasBoulder() {
		return sonic.getRangeInches() < 20; // TODO how far makes this work
	}

	@Override
	public Map<String, Object> getLogData() {

		Map<String, Object> logData = new HashMap<String, Object>();
		
		logData.put("Name", "BoulderStager");
		logData.put("Position", getPositionString());
		logData.put("Has boulder?", hasBoulder());
		
		return logData;
	}
	
}
