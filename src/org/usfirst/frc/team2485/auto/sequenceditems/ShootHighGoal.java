package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;

/**
 * @author Jeremy McCulloch
 */
public class ShootHighGoal implements SequencedItem {

	@Override
	public void run() {
		
		if (Hardware.shooter.isReadyToFire()) {
			Hardware.boulderStager.setPosition(Position.SHOOTING);
		}
	}

	@Override
	public double duration() {
		return 0.05;
	}
}
