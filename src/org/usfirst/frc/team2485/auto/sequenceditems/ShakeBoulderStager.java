package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager.StagerPosition;

public class ShakeBoulderStager implements SequencedItem {

	private long startTime = -1;
	
	@Override
	public void run() {
		
		if (startTime == -1) {
			startTime = System.currentTimeMillis();
		}
		
		long runTime = System.currentTimeMillis() - startTime;
		

		if (runTime < 300) {
			Hardware.boulderStager.setPosition(StagerPosition.SHOOTING);
		} else if (runTime < 600) {
			Hardware.boulderStager.setPosition(StagerPosition.INTAKE);
		}
		else {
			Hardware.boulderStager.setPosition(StagerPosition.NEUTRAL);
		}
		
	}

	@Override
	public double duration() {
		return 0.7;
	}

}
