package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 */
public class SetRollers implements SequencedItem {
	
	private double speed, time;
	
	public SetRollers(double speed, double time) {
		this.speed = speed;
		this.time = time;
	}

	@Override
	public void run() {
		Hardware.intake.startRollers(speed);
	}

	@Override
	public double duration() {
		return time;
	}

}
