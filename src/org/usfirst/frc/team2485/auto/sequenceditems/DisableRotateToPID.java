package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

public class DisableRotateToPID implements SequencedItem {

	@Override
	public void run() {
		Hardware.driveTrain.disableAhrsPID();
	}

	@Override
	public double duration() {
		return 0.03;
	}

}
