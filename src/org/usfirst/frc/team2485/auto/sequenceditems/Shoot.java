package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 */
public class Shoot implements SequencedItem {

	@Override
	public void run() {
		if (Hardware.shooter.isReadyToFire()) {
			
		}
	}

	@Override
	public double duration() {
		return 0.05;
	}
	

}
