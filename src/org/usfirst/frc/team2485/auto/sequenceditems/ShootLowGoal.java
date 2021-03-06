package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager.StagerPosition;

public class ShootLowGoal implements SequencedItem {

	private long startTime = -1;

	public static double lowGoalRollerSpeed = -1.0;

	@Override
	public void run() {

		if (startTime == -1) {
			startTime = System.currentTimeMillis();
		}

		Hardware.intake.startRollers(0.0, lowGoalRollerSpeed);

		long now = System.currentTimeMillis();

		if (now - startTime >= 250) {
			Hardware.boulderStager.setPosition(StagerPosition.INTAKE);
		}
	}

	@Override
	public double duration() {
		return 1;
	}
}
