package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;

/**
 * @author Jeremy McCulloch
 */
public class ShootHighGoal implements SequencedItem {

	private boolean finished = false;

	private double timeout;

	public ShootHighGoal() {
		timeout = 0.5;
	}

	/**
	 * timeout is not working yet, will always finish instantly
	 * @param timeout
	 */
	public ShootHighGoal(double timeout) {
		this.timeout = timeout;
	}

	@Override
	public void run() {

//		if (Hardware.shooter.isReadyToFire()) {
			finished = true;
			Hardware.boulderStager.setPosition(Position.SHOOTING);
//		}
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout;
	}
}
