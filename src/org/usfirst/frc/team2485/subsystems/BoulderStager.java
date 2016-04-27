package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.Loggable;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * 
 * @author Amanda Wasserman
 * @author Vicky Comunale
 * @author Ben Clark
 *
 */

public class BoulderStager implements Loggable {
	// Declare different pneumatics parts

	private Solenoid solenoid1, solenoid2;

	private StagerPosition position;

	public enum StagerPosition {
		INTAKE, NEUTRAL, SHOOTING;
	}

	public BoulderStager() {
		solenoid1 = Hardware.boulderStagerSolenoid1;
		solenoid2 = Hardware.boulderStagerSolenoid2;

		setPosition(StagerPosition.NEUTRAL);

	}

	public void setPosition(StagerPosition position) {
		
//		System.out.println("BoulderStager: SetPos: " + position.toString());

		this.position = position;

		switch (position) {

		case INTAKE:
			solenoid1.set(false);
			solenoid2.set(true);
			break;

		case SHOOTING:
			solenoid1.set(true);
			solenoid2.set(false);
			break;

		case NEUTRAL:
			solenoid1.set(false);
			solenoid2.set(false);
			break;

		}

	}

	public StagerPosition getPosition() {
		return position;
	}

	public String getPositionString() {
		if (position == StagerPosition.INTAKE) {
			return "Intake";
		} else if (position == StagerPosition.NEUTRAL) {
			return "Neutral";
		} else {
			return "Shooting";
		}
	}

	@Override
	public Map<String, Object> getLogData() {

		Map<String, Object> logData = new HashMap<String, Object>();

		logData.put("Name", "BoulderStager");
		logData.put("Position", getPositionString());

		return logData;
	}

}
