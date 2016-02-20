package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 */
public class SetIntakeArm implements SequencedItem {
	
	private double position;
		
	public SetIntakeArm(double position) {
		this.position = position;
	}

	@Override
	public void run() {
		Hardware.intake.setSetpoint(position);
	}

	@Override
	public double duration() {
		return 0.05;
	}

}
