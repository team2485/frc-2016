package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

public class SpinUpShooter implements SequencedItem {
	
	private double rpm;
	
	public SpinUpShooter(double rpm) {
		this.rpm = rpm;
	}
	
	/**
	 * Uses Lidar <br>
	 * Do not use right now	
	 */
	public SpinUpShooter() {
		this(0.0 /*lidar magic*/);
	}

	@Override
	public void run() {
		Hardware.shooter.setTargetSpeed(rpm);
	}

	@Override
	public double duration() {
		return 0.05;
	}
}
