package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;

public class ShootLowGoal implements SequencedItem {

	public static double lowGoalRollerSpeed = -0.5;
	
	@Override
	public void run() {
		
		Hardware.boulderStager.setPosition(Position.INTAKE);
		Hardware.intake.startRollers(0.0, lowGoalRollerSpeed);
		
	}

	@Override
	public double duration() {
		return 1.5;
	}
	
	
}
