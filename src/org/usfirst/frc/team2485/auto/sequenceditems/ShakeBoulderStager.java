package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;

public class ShakeBoulderStager implements SequencedItem {

	private long startTime = -1;
	
	@Override
	public void run() {
		
		if (startTime == -1) {
			startTime = System.currentTimeMillis();
		}
		
		long runTime = System.currentTimeMillis() - startTime;
		
		if (runTime < 150) {
			Hardware.boulderStager.setPosition(Position.SHOOTING);
		} else {
			Hardware.boulderStager.setPosition(Position.NEUTRAL);
		}
		
	}

	@Override
	public double duration() {
		return 0.3;
	}

}
