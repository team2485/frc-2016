package org.usfirst.frc.team2485.auto;

import org.usfirst.frc.team2485.auto.sequenceditems.AlignToTower;
import org.usfirst.frc.team2485.auto.sequenceditems.DriveTo;
import org.usfirst.frc.team2485.auto.sequenceditems.RotateTo;
import org.usfirst.frc.team2485.auto.sequenceditems.SetHoodPosition;
import org.usfirst.frc.team2485.auto.sequenceditems.SetIntakeArm;
import org.usfirst.frc.team2485.auto.sequenceditems.SetRollers;
import org.usfirst.frc.team2485.auto.sequenceditems.SetStager;
import org.usfirst.frc.team2485.auto.sequenceditems.ShootHighGoal;
import org.usfirst.frc.team2485.auto.sequenceditems.ShootLowGoal;
import org.usfirst.frc.team2485.auto.sequenceditems.SpinUpShooter;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;
import org.usfirst.frc.team2485.subsystems.Intake;

public class SequencerFactory {

	public enum AutoType {
		BASIC, AUTO_AIM_NO_THANKS_LIDAR, AUTO_AIM_YES_PLEASE_LIDAR, SHOOT_HIGH_GOAL, SHOOT_LOW_GOAL;
	}

	// Auto
	public static Sequencer createAuto(AutoType autoType) {

		switch (autoType) {
		case BASIC:
			return new Sequencer(
					new SequencedItem[] { new RotateTo(360, 10) });
			
		case AUTO_AIM_NO_THANKS_LIDAR:
			return new Sequencer(
					new SequencedItem[] { new AlignToTower() });
			
		case AUTO_AIM_YES_PLEASE_LIDAR:
			return new Sequencer(
					new SequencedItem[] { new AlignToTower(), new SpinUpShooter(3) });
			
		case SHOOT_HIGH_GOAL:
			return new Sequencer(
					new SequencedItem[] { new ShootHighGoal() });
			
		case SHOOT_LOW_GOAL:
			return new Sequencer(
					new SequencedItem[] { 
							new SetIntakeArm(Intake.INTAKE_POSITION, 0.5), 
							new SetStager(Position.INTAKE),
							new SetRollers(-0.5, 3.0)
						}
					); //<-- sad winky face
		}
		return new Sequencer();

	}

	// Teleop Sequences
}