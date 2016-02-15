package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;

public class ShootLowGoal implements SequencedItem {

	public static double lowGoalRollerSpeed = -0.5;
	
	public double time;
	
	public ShootLowGoal(double time) {
		this.time = time;
	}

	@Override
	public void run() {
		Hardware.boulderStager.setPosition(Position.INTAKE);
		Hardware.intake.startRollers(lowGoalRollerSpeed);
	}

	@Override
	public double duration() {
		return time;
	}
	
	
}
