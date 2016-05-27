package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

public class WaitForBoulder implements SequencedItem {

	private double timeout = 3;

	@Override
	public void run() {	}

	@Override
	public double duration() {
		return Hardware.boulderDetector.hasBoulder() ? 0 : timeout;
	}
}
 