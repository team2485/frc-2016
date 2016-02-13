package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Patrick Wamsley 
 */

public class ResetDriveEncoders implements SequencedItem {

	@Override
	public void run() {
		Hardware.driveTrain.resetEncoder();	
	}

	@Override
	public double duration() {
		return 0.05;
	}

}
