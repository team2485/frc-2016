package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 */
public class SetRollers implements SequencedItem {
	
	private double lateralSpeed, intakeSpeed, time;
	
	public SetRollers(double lateralSpeed, double intakeSpeed, double time) {
		this.lateralSpeed = lateralSpeed;
		this.intakeSpeed = intakeSpeed;
		this.time = time;
	}

	@Override
	public void run() {
		Hardware.intake.startRollers(lateralSpeed, intakeSpeed);
	}

	@Override
	public double duration() {
		return time;
	}

}
