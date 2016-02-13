package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.Shooter.HoodPosition;

/**
 * @author Jeremy McCulloch
 */
public class SetHoodPosition implements SequencedItem {
	private HoodPosition desiredHoodPosition;
	public SetHoodPosition(HoodPosition desiredHoodPosition) {
		this.desiredHoodPosition = desiredHoodPosition;
	}

	@Override
	public void run() {
		Hardware.shooter.setHoodPosition(desiredHoodPosition);
	}

	@Override
	public double duration() {
		return 0.05;
	}
	
}