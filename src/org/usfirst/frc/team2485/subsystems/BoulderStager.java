package org.usfirst.frc.team2485.subsystems;

import org.usfirst.frc.team2485.robot.Hardware;

import edu.wpi.first.wpilibj.Solenoid;

/**
 *  also known as RockYourPebblesOff
 * 
 * @author Amanda Wasserman
 * @author Vicky Comunale
 *
 */

public class BoulderStager {
	//Declare different pneumatics parts

	private Solenoid solenoid1, solenoid2;

	public enum Position {
		INTAKE, NEUTRAL, SHOOTING;
	}


	public BoulderStager() {
		solenoid1=Hardware.boulderStagerSolenoid1;
		solenoid2=Hardware.boulderStagerSolenoid2;


	}

	public void setPosition(Position pos){
		switch(pos){

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
	
}
