package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager;

/**
 * @author Jeremy McCulloch
 */
public class SetStager implements SequencedItem {
	
	private BoulderStager.StagerPosition position;
	
	public SetStager(BoulderStager.StagerPosition position) {
		this.position = position;
	}

	@Override
	public void run() {
		Hardware.boulderStager.setPosition(position);
	}

	@Override
	public double duration() {
		return 0.05;
	}
	
}
