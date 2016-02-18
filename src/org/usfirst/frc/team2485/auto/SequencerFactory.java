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
import org.usfirst.frc.team2485.subsystems.Shooter;
import org.usfirst.frc.team2485.subsystems.Shooter.HoodPosition;

public class SequencerFactory {

	public enum AutoType {
		LOW_BAR_AUTO, RAMPARTS_AUTO, ROUGH_TERRAIN_AUTO, MOAT_AUTO, ROCK_WALL_AUTO, PORTCULLIS_AUTO, CHEVAL_DE_FRISE, REACH_AUTO;
	}

	// Auto
	public static Sequencer createAuto(AutoType autoType) {

		//@formatter:off

		switch (autoType) {
		case REACH_AUTO:
			return new Sequencer(new SequencedItem[] { 
					new DriveTo(50) 
					});

		case LOW_BAR_AUTO:
			return new Sequencer(new SequencedItem[] {
					new SequencedMultipleItem(
							new DriveTo(30), 
							new SetIntakeArm(Intake.INTAKE_POSITION, 2)),
					new DriveTo(50, 4, 0.3),
					new SequencedMultipleItem(
							new DriveTo(100),
							new SetHoodPosition(HoodPosition.LOW_ANGLE), 
							new SpinUpShooter(Shooter.RPM_LONG_SHOT)),
					new RotateTo(60), 
					new ShootHighGoal(5) });
			
		case RAMPARTS_AUTO:
		case ROUGH_TERRAIN_AUTO:
		case MOAT_AUTO:
		case ROCK_WALL_AUTO:
			return new Sequencer(new SequencedItem[] {
					new SequencedMultipleItem(
							new DriveTo(30), 
							new SetIntakeArm(Intake.INTAKE_POSITION, 2), 
							new SpinUpShooter(Shooter.RPM_LONG_SHOT), 
							new SetHoodPosition(HoodPosition.LOW_ANGLE)),
					new DriveTo(50, 4, 0.4),
					new AlignToTower(),
					new ShootHighGoal(5) });
			
		case PORTCULLIS_AUTO:
			return new Sequencer(new SequencedItem[] {
					new SequencedMultipleItem(
							new DriveTo(30), 
							new SetIntakeArm(Intake.LOW_NO_INTAKE_POSITION, 2),
					new SequencedMultipleItem(
							new DriveTo(10, 2, 0.4),
							new SetIntakeArm(Intake.FLOOR_POSITION, 1)),
					new SetIntakeArm(Intake.INTAKE_POSITION, 2),
					new SequencedMultipleItem(
							new DriveTo(10, 4, 0.4),
							new SetIntakeArm(Intake.PORTCULLIS_POSITION, 4)),
					new SequencedMultipleItem(
							new DriveTo(20, 4, 0.4),
							new SetIntakeArm(Intake.INTAKE_POSITION, 2), 
							new SetHoodPosition(HoodPosition.LOW_ANGLE))),
					new AlignToTower(),
					new SpinUpShooter(),
					new ShootHighGoal(5)
			});
			
		case CHEVAL_DE_FRISE:
			return new Sequencer(new SequencedItem[] {
							new DriveTo(30),
							new SetIntakeArm(Intake.FLOOR_POSITION, 2),
							new DriveTo(5),
							new SequencedMultipleItem(
									new DriveTo(50, 4, 0.5),
									new SetIntakeArm(Intake.FULL_UP_POSITION, 2)),
							new SequencedMultipleItem(
									new SetIntakeArm(Intake.INTAKE_POSITION, 2),
									new SetHoodPosition(HoodPosition.LOW_ANGLE)),	
							new AlignToTower(),
							new SpinUpShooter(),
							new ShootHighGoal(5)
			});
		}
		return new Sequencer();
	}

	public static Sequencer getShootHighGoalSequence() {
		return new Sequencer(new SequencedItem[] { new ShootHighGoal(),
				new SequencedPause(0.5), new SetStager(Position.NEUTRAL) });
	}

	public static Sequencer getShootLowGoalSequence() {
		return new Sequencer(new SequencedItem[] {
				new SetIntakeArm(Intake.INTAKE_POSITION, 2),
				new ShootLowGoal(), new SetRollers(0, 0),
				new SetStager(Position.NEUTRAL) });
	}

	public static Sequencer getAutoAimSequence() {
		return new Sequencer(new SequencedItem[] { new AlignToTower() });
	}
	// @formatter:on
}
