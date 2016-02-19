package org.usfirst.frc.team2485.auto;

import org.usfirst.frc.team2485.auto.SequencerFactory.AutoType;
import org.usfirst.frc.team2485.auto.sequenceditems.AlignToTower;
import org.usfirst.frc.team2485.auto.sequenceditems.DisableDriveToPID;
import org.usfirst.frc.team2485.auto.sequenceditems.DisableRotateToPID;
import org.usfirst.frc.team2485.auto.sequenceditems.DriveTo;
import org.usfirst.frc.team2485.auto.sequenceditems.RotateTo;
import org.usfirst.frc.team2485.auto.sequenceditems.SetHoodPosition;
import org.usfirst.frc.team2485.auto.sequenceditems.SetIntakeArm;
import org.usfirst.frc.team2485.auto.sequenceditems.SetRollers;
import org.usfirst.frc.team2485.auto.sequenceditems.SetStager;
import org.usfirst.frc.team2485.auto.sequenceditems.ShootHighGoal;
import org.usfirst.frc.team2485.auto.sequenceditems.ShootLowGoal;
import org.usfirst.frc.team2485.auto.sequenceditems.SpinUpShooter;
import org.usfirst.frc.team2485.auto.sequenceditems.ZeroDriveEncoder;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;
import org.usfirst.frc.team2485.subsystems.Intake;
import org.usfirst.frc.team2485.subsystems.Shooter;
import org.usfirst.frc.team2485.subsystems.Shooter.HoodPosition;

public class SequencerFactory {

	public enum AutoType {
		LOW_BAR_AUTO, RAMPARTS_AUTO, ROUGH_TERRAIN_AUTO, MOAT_AUTO, ROCK_WALL_AUTO, PORTCULLIS_AUTO, CHEVAL_DE_FRISE, REACH_AUTO;
	}

	// Auto
	public static Sequencer createAuto(AutoType autoType, int defenseLocation) {

		double distPreTurn = 160;
		double degreesToTurn = 0.0;
		double distPostTurn = 60;

		switch (defenseLocation) {

		case 2:
			distPreTurn = 234;
			degreesToTurn = 55;
			distPostTurn = 20;
			break;
		case 3:
			degreesToTurn = 10;
			break;
		case 4:
			degreesToTurn = -5;
			break;
		case 5:
			distPreTurn = 250;
			degreesToTurn = -60;
			distPostTurn = 0;
			break;
		}

		// @formatter:off
		
		switch (autoType) {
		case REACH_AUTO:
			return new Sequencer(new SequencedItem[] { 
					new DriveTo(50)
			});

		case LOW_BAR_AUTO:
			return new Sequencer(new SequencedItem[] {
					new SequencedMultipleItem(
							new DriveTo(200, 5, 0.5),
							new SetHoodPosition(HoodPosition.STOWED),
							new SetIntakeArm(Intake.INTAKE_POSITION, 2)),
					new DisableDriveToPID(),
					new SequencedMultipleItem(
							new RotateTo(55),
							new SetHoodPosition(HoodPosition.HIGH_ANGLE)),
					new DisableRotateToPID(),
					new ZeroDriveEncoder(),
					new SequencedMultipleItem(
							new DriveTo(80, 4, 0.55),
							new SpinUpShooter(Shooter.RPM_BATTER_SHOT)),
					new DisableDriveToPID(),
					new AlignToTower(),
					new DisableRotateToPID(), 
					new ShootHighGoal(5)
			});
	
			
		case RAMPARTS_AUTO:
		case ROUGH_TERRAIN_AUTO:
		case MOAT_AUTO:
		case ROCK_WALL_AUTO:
			
			return new Sequencer(new SequencedItem[] {
					new SequencedMultipleItem(
							new DriveTo(distPreTurn, 4, 0.55),
							new SetIntakeArm(Intake.LOW_NO_INTAKE_POSITION, 2),
							new SetHoodPosition(HoodPosition.HIGH_ANGLE)),
					new DisableDriveToPID(),
					new RotateTo(degreesToTurn),
					new DisableRotateToPID(),
					new ZeroDriveEncoder(),
					new DriveTo(distPostTurn), //Approaches batter, may be removed when long shot works
					new DisableDriveToPID(),
					new ZeroDriveEncoder(),
					new SequencedMultipleItem(
							new SpinUpShooter(Shooter.RPM_BATTER_SHOT),
							new AlignToTower()),
					new DisableRotateToPID(),
					new ZeroDriveEncoder(),
					new ShootHighGoal(5)
			});

		case PORTCULLIS_AUTO:
			return new Sequencer(new SequencedItem[] {
					new DriveTo(30),
					new DisableDriveToPID(),
					new ZeroDriveEncoder(),
					new SetIntakeArm(Intake.LOW_NO_INTAKE_POSITION, 2),
					new SequencedMultipleItem(
							new DriveTo(10, 2, 0.4),
							new DisableDriveToPID(),
							new ZeroDriveEncoder(),
							new SetIntakeArm(Intake.FLOOR_POSITION, 1)),
					new SetIntakeArm(Intake.INTAKE_POSITION, 2),
					new SequencedMultipleItem(
							new DriveTo(10, 4, 0.4),
							new DisableDriveToPID(),
							new ZeroDriveEncoder(),
							new SetIntakeArm(Intake.PORTCULLIS_POSITION, 4)),
					new SequencedMultipleItem(
							new DriveTo(20, 4, 0.4),
							new DisableDriveToPID(),
							new ZeroDriveEncoder(),
							new SetIntakeArm(Intake.INTAKE_POSITION, 2),
							new SetHoodPosition(HoodPosition.HIGH_ANGLE)),
					new SpinUpShooter(Shooter.RPM_BATTER_SHOT),
					new DriveTo(distPreTurn - 70),
					new DisableDriveToPID(),
					new ZeroDriveEncoder(),
					new RotateTo(degreesToTurn),
					new DisableRotateToPID(),
					new ZeroDriveEncoder(),
					new DriveTo(distPostTurn),
					new DisableDriveToPID(),
					new ZeroDriveEncoder(),
					new AlignToTower(), 
					new DisableRotateToPID(),
					new ZeroDriveEncoder(),
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
							new SetHoodPosition(HoodPosition.HIGH_ANGLE)),
					new SpinUpShooter(Shooter.RPM_BATTER_SHOT),
					new DriveTo(distPreTurn - 80),
					new DisableDriveToPID(),
					new ZeroDriveEncoder(),
					new RotateTo(degreesToTurn),
					new DisableRotateToPID(),
					new ZeroDriveEncoder(),
					new DriveTo(distPostTurn),
					new DisableDriveToPID(),
					new ZeroDriveEncoder(),
					new AlignToTower(), 
					new DisableRotateToPID(),
					new ZeroDriveEncoder(),
					new ShootHighGoal(5) 
			});
		}
		return new Sequencer();
	}

	public static Sequencer getShootHighGoalSequence() {
		return new Sequencer(new SequencedItem[] { 
				new ShootHighGoal(),
				new SequencedPause(0.5), 
				new SetStager(Position.NEUTRAL) 
		});
	}

	public static Sequencer getShootLowGoalSequence() {
		return new Sequencer(
				new SequencedItem[] {
				new SetIntakeArm(Intake.INTAKE_POSITION, 2),
				new ShootLowGoal(), 
				new SetRollers(0, 0),
				new SetStager(Position.NEUTRAL) 
		});
	}

	public static Sequencer getAutoAimSequence() {
		return new Sequencer(new SequencedItem[] { 
				new AlignToTower(),
				new DisableRotateToPID() 
		});
	}
	// @formatter:on
}
