package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 */
public class SetIntakeArm implements SequencedItem {
	
	private double position, timeout;
	private boolean finished;
		
	public SetIntakeArm(double position, double timeout) {
		this.position = position;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		Hardware.intake.setSetpoint(position);
		finished = Hardware.intake.isOnTarget();
	}

	@Override
	public double duration() {
		return finished ? 0.0 : timeout;
	}

}
