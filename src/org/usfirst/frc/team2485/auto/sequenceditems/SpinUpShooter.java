package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

public class SpinUpShooter implements SequencedItem {
	
	private double rpm, timeout;
	private boolean finished;
	
	public SpinUpShooter(double rpm, double timeout) {
		this.rpm = rpm;
		this.timeout = timeout;
	}
	
	public SpinUpShooter(double timeout) {
		this(0.0 /*lidar magic*/, timeout);
	}

	@Override
	public void run() {
		Hardware.shooter.setTargetSpeed(rpm);
		finished = Hardware.shooter.isOnTargetPercentage(0.05);//no
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout;
	}
}
