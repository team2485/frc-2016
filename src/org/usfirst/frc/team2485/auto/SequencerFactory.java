package org.usfirst.frc.team2485.auto;

import org.usfirst.frc.team2485.auto.SequencerFactory.AutoType;
import org.usfirst.frc.team2485.auto.sequenceditems.AlignToTower;
import org.usfirst.frc.team2485.auto.sequenceditems.DisableDriveToPID;
import org.usfirst.frc.team2485.auto.sequenceditems.DisableRotateToPID;
import org.usfirst.frc.team2485.auto.sequenceditems.DriveTo;
import org.usfirst.frc.team2485.auto.sequenceditems.Hat;
import org.usfirst.frc.team2485.auto.sequenceditems.RotateTo;
import org.usfirst.frc.team2485.auto.sequenceditems.SetHoodPosition;
import org.usfirst.frc.team2485.auto.sequenceditems.SetIntakeArm;
import org.usfirst.frc.team2485.auto.sequenceditems.SetRollers;
import org.usfirst.frc.team2485.auto.sequenceditems.SetStager;
import org.usfirst.frc.team2485.auto.sequenceditems.ShakeBoulderStager;
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
		LOW_BAR_AUTO, RAMPARTS_AUTO, ROUGH_TERRAIN_AUTO, MOAT_AUTO, ROCK_WALL_AUTO, PORTCULLIS_AUTO, CHEVAL_DE_FRISE_AUTO, REACH_AUTO;
	}

	// Auto
	public static Sequencer createAuto(AutoType autoType, int defenseLocation) {

		double distPreTurn = 150;
		double degreesToTurn = 0.0;
		double distPostTurn = 65;

		// @formatter:off

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

		switch (autoType) {
			case REACH_AUTO:
				return new Sequencer(new SequencedItem[] { new DriveTo(30) });
	
			case LOW_BAR_AUTO:
				return new Sequencer(new SequencedItem[] {
						new SetIntakeArm(Intake.INTAKE_POSITION),
						new SequencedPause(0.6),
						new SequencedMultipleItem(
								new DriveTo(206, 5, 0.5),
								new SetHoodPosition(HoodPosition.STOWED)
							),
						new DisableDriveToPID(),
						new SequencedMultipleItem(
								new RotateTo(57), // Added 2 degrees, Friday morning (SD)
								new SetHoodPosition(HoodPosition.HIGH_ANGLE)
							),
						new DisableRotateToPID(),
						new ZeroDriveEncoder(),
						new ShakeBoulderStager(),
						new SequencedPause(0.4),
						new SequencedMultipleItem(
								new DriveTo(61, 3.5, 0.65),
								new SpinUpShooter(Shooter.RPS_BATTER_SHOT)
							),
						new DisableDriveToPID(), 
						new AlignToTower(),
						new DisableRotateToPID(), 
						new ShootHighGoal(5) 
					});
	
			case ROCK_WALL_AUTO:
			case RAMPARTS_AUTO:
				
				if (defenseLocation == 4) {
					degreesToTurn = -8;
				}
				
				return new Sequencer(new SequencedItem[] {
						new SequencedMultipleItem(
								new DriveTo(distPreTurn, 6, 0.6), // changed to 6 seconds from 4
								new Hat(true),
								new SetHoodPosition(HoodPosition.HIGH_ANGLE)
							),
						new DisableDriveToPID(),
						new Hat(false),
						new SetIntakeArm(Intake.INTAKE_POSITION),
						new RotateTo(degreesToTurn, 3),
						new DisableRotateToPID(),
						new ZeroDriveEncoder(),
						new SequencedMultipleItem(
								new SpinUpShooter(Shooter.RPS_BATTER_SHOT),
								new DriveTo(distPostTurn, 3, 0.65) // Approaches batter, may be removed when long shot works
							),
						new DisableDriveToPID(),
						new AlignToTower(),
						new DisableRotateToPID(), 
						new ShootHighGoal(5) 
					});
				
			case ROUGH_TERRAIN_AUTO:
			case MOAT_AUTO:
	
				return new Sequencer(new SequencedItem[] {
						new SequencedMultipleItem(
								new DriveTo(distPreTurn, 5, 0.6), // changed to 4.5 seconds from 4
								new Hat(true),
								new SetHoodPosition(HoodPosition.HIGH_ANGLE)
							),
						new DisableDriveToPID(),
						new Hat(false),
						new SetIntakeArm(Intake.INTAKE_POSITION),
						new RotateTo(degreesToTurn),
						new DisableRotateToPID(),
						new ZeroDriveEncoder(),
						new SequencedMultipleItem(
								new DriveTo(distPostTurn, 3, 0.75), // Approaches batter, may be removed when long shot works
								new SpinUpShooter(Shooter.RPS_BATTER_SHOT)
							),
						new DisableDriveToPID(),
						new AlignToTower(),
						new DisableRotateToPID(), 
						new ShootHighGoal(5) 
					});
	
			case PORTCULLIS_AUTO:
	
				// Not zeroing drive encoders until after distPreTurn
	
				return new Sequencer(new SequencedItem[] {
						new SequencedMultipleItem(
								new SetIntakeArm(Intake.LOW_NO_INTAKE_POSITION),
								new SetHoodPosition(HoodPosition.STOWED)
							),

						new DriveTo(30),
						new DisableDriveToPID(),
						new SequencedMultipleItem(
								new DriveTo(40, 2, 0.4),
								new SetIntakeArm(Intake.FLOOR_POSITION)
							),
						new DisableDriveToPID(),
						new SequencedMultipleItem(
								new DriveTo(50, 2, 0.4),
								new SetIntakeArm(Intake.PORTCULLIS_POSITION)
							),
						new DisableDriveToPID(),
						new SequencedMultipleItem(
								new DriveTo(70, 2, 0.4),
								new SetIntakeArm(Intake.INTAKE_POSITION)
							),
						new SequencedMultipleItem(
								new SpinUpShooter(Shooter.RPS_BATTER_SHOT), 
								new SetHoodPosition(HoodPosition.HIGH_ANGLE)
							),
						new DriveTo(distPreTurn), // avoid zeroing encoders such that distPreTurn remains absolute and correct
						new DisableDriveToPID(), 
						new RotateTo(degreesToTurn),
						new DisableRotateToPID(), 
						new ZeroDriveEncoder(),
						new DriveTo(distPostTurn), 
						new DisableDriveToPID(),
						new AlignToTower(), 
						new DisableRotateToPID(),
						new ShootHighGoal(5) 
					});
	
			case CHEVAL_DE_FRISE_AUTO:
	
				// Not zeroing drive encoders until after distPreTurn
	
				return new Sequencer(new SequencedItem[] {
						new DriveTo(30),
						new SetIntakeArm(Intake.PORTCULLIS_POSITION),
						new DisableDriveToPID(),
						new DriveTo(40, 1),
						new DisableDriveToPID(),
						new SequencedMultipleItem(
								new DriveTo(90, 3),
								new Hat(true)
							),
						new Hat(false),
						new SequencedMultipleItem(
								new SetIntakeArm(Intake.INTAKE_POSITION),
								new SetHoodPosition(HoodPosition.HIGH_ANGLE)
							),
						new SpinUpShooter(Shooter.RPS_BATTER_SHOT),
						new DriveTo(distPreTurn), 
						new DisableDriveToPID(),
						new RotateTo(degreesToTurn), 
						new DisableRotateToPID(),
						new ZeroDriveEncoder(), 
						new DriveTo(distPostTurn),
						new DisableDriveToPID(), 
						new AlignToTower(),
						new DisableRotateToPID(), 
						new ShootHighGoal(5) 
					});
						
				default: 
					return new Sequencer();
		}
	}

	public static Sequencer getShootHighGoalSequence() {
		return new Sequencer(new SequencedItem[] { 
				new ShootHighGoal(),
				new SequencedPause(0.5), 
				new SetStager(Position.NEUTRAL) 
			});
	}

	public static Sequencer getShootLowGoalSequence() {
		return new Sequencer(new SequencedItem[] {
				new SetIntakeArm(Intake.FULL_UP_POSITION), 
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
	//@formatter:on
}
