package org.usfirst.frc.team2485.auto;

import org.usfirst.frc.team2485.auto.sequenceditems.AlignToTower;
import org.usfirst.frc.team2485.auto.sequenceditems.DriveThrough;
import org.usfirst.frc.team2485.auto.sequenceditems.DriveTo;
import org.usfirst.frc.team2485.auto.sequenceditems.Hat;
import org.usfirst.frc.team2485.auto.sequenceditems.ResetDriveTrain;
import org.usfirst.frc.team2485.auto.sequenceditems.RotateTo;
import org.usfirst.frc.team2485.auto.sequenceditems.SetHoodPosition;
import org.usfirst.frc.team2485.auto.sequenceditems.SetIntakeArm;
import org.usfirst.frc.team2485.auto.sequenceditems.SetRollers;
import org.usfirst.frc.team2485.auto.sequenceditems.SetStager;
import org.usfirst.frc.team2485.auto.sequenceditems.ShakeBoulderStager;
import org.usfirst.frc.team2485.auto.sequenceditems.ShootHighGoal;
import org.usfirst.frc.team2485.auto.sequenceditems.ShootLowGoal;
import org.usfirst.frc.team2485.auto.sequenceditems.SpinUpShooter;
import org.usfirst.frc.team2485.auto.sequenceditems.WaitForBoulder;
import org.usfirst.frc.team2485.auto.sequenceditems.ZeroDriveEncoder;
import org.usfirst.frc.team2485.subsystems.BoulderStager.StagerPosition;
import org.usfirst.frc.team2485.subsystems.Intake;
import org.usfirst.frc.team2485.subsystems.Shooter;
import org.usfirst.frc.team2485.subsystems.Shooter.HoodPosition;
import org.usfirst.frc.team2485.util.ConstantsIO;

public class SequencerFactory {

	public enum AutoType {
		LOW_BAR_AUTO, RAMPARTS_AUTO, ROUGH_TERRAIN_AUTO, MOAT_AUTO, ROCK_WALL_AUTO,

		PORTCULLIS_AUTO, CHEVAL_DE_FRISE_AUTO, REACH_AUTO, TWO_BALL_SPY_AUTO, NO_AUTO;
	}

	/**
	 * Creates auto sequencer based on defense and position
	 * @param autoType defense or auto type
	 * @param defenseLocation location of defense (1 - 5 where 1 is low bar)
	 * @return sequencer that must be run
	 */
	public static Sequencer createAuto(AutoType autoType, int defenseLocation) {
		double distPreTurn = 150;
		double degreesToTurn = 0.0;
		double distPostTurn = 10;

		// @formatter:off

		switch (defenseLocation) {
		
			case 2:
				distPreTurn = 175;
				degreesToTurn = 40;
				distPostTurn = 22;
				break;
				
			case 3:
				distPreTurn = 170;
				degreesToTurn = 10;
				distPostTurn = 0;
				break;
				
			case 4:
				distPreTurn = 165;
				degreesToTurn = -5;
				distPostTurn = -2;
				break;
				
			case 5:
				distPreTurn = 170;
				degreesToTurn = -30;
				distPostTurn = 0;
				break;
				
		}

		switch (autoType) {
			case REACH_AUTO:
				return new Sequencer(new SequencedItem[] { new DriveTo(30) });
	
			case LOW_BAR_AUTO:
				return new Sequencer(new SequencedItem[] {
						new SetHoodPosition(HoodPosition.STOWED), 
						new SetIntakeArm(Intake.LOW_NO_INTAKE_POSITION),
						new SequencedPause(0.5),
						new SequencedMultipleItem(
								new DriveThrough(206, 10, 100, 40, 0),
								new ShakeBoulderStager()
							),
						new DriveTo(206, 10, 100, 0),
						new ResetDriveTrain(),

						new SequencedMultipleItem(
								new RotateTo(57, 2), 
								new SetHoodPosition(HoodPosition.LOW_ANGLE)
								),
						new ResetDriveTrain(),
						new ZeroDriveEncoder(),
						new ShakeBoulderStager(),
						new ResetDriveTrain(), 
						new AlignToTower(),
						new ResetDriveTrain(),
						new SpinUpShooter(Shooter.RPS_LONG_SHOT),
						new SequencedPause(2),
						new ShootHighGoal(5) 
					});
	
			case RAMPARTS_AUTO:
				if (defenseLocation == 4) {
					degreesToTurn = -8;
				}
				return new Sequencer(new SequencedItem[] {
						new SequencedMultipleItem(
								new DriveTo(distPreTurn, 8, 0.6), 
								new Hat(true),
								new SetHoodPosition(HoodPosition.HIGH_ANGLE)
							),
						new ResetDriveTrain(),
						new SetIntakeArm(Intake.INTAKE_POSITION),
						new ShakeBoulderStager(),
						new RotateTo(degreesToTurn, 3),
						new ResetDriveTrain(),
						new ZeroDriveEncoder(),
						new DriveTo(distPostTurn, 3, 0.65), 
						new ResetDriveTrain(),
						new SequencedMultipleItem(
								new SpinUpShooter(Shooter.RPS_BATTER_SHOT), 
								new AlignToTower(3.0)
							),
						new ResetDriveTrain(), 
						new ShootHighGoal(5) 
					});
			case ROUGH_TERRAIN_AUTO:
			case MOAT_AUTO:
			case ROCK_WALL_AUTO:
				return new Sequencer(new SequencedItem[] {
						new SequencedMultipleItem(
								new DriveTo(distPreTurn, 8, 75, 2), // changed to 4.5 seconds from 4
								new Hat(true),
								new SetHoodPosition(HoodPosition.LOW_ANGLE)
							),
						new ResetDriveTrain(),
						new SetIntakeArm(Intake.INTAKE_POSITION),
						new ShakeBoulderStager(),
						new RotateTo(degreesToTurn),
						new ShakeBoulderStager(),
						new ResetDriveTrain(),
						new ZeroDriveEncoder(),
						new ShakeBoulderStager(),
						new SequencedMultipleItem(
								new DriveTo(distPostTurn, 3, 100),
								new SequencedPause(1)),
						new ResetDriveTrain(),

						new AlignToTower(4.0),
						new SpinUpShooter(Shooter.RPS_LONG_SHOT), 
						new SequencedPause(1.5),
						new ResetDriveTrain(), 
						new ShootHighGoal(5) 
				});
	
			case PORTCULLIS_AUTO:
				
				distPreTurn = 140;
				
				if (defenseLocation == 2) {
					distPostTurn = 25;
				}
		
				return new Sequencer(new SequencedItem[] {
						new SetIntakeArm(Intake.LOW_NO_INTAKE_POSITION),
						new SetHoodPosition(HoodPosition.STOWED),
						new DriveTo(45, 3, 30),
						new ResetDriveTrain(),
						new SequencedMultipleItem(
								new DriveThrough(distPreTurn, 3, 85, 50),
								new SetIntakeArm(Intake.FLOOR_POSITION),
								new ShakeBoulderStager()),
						new SetIntakeArm(Intake.INTAKE_POSITION),
						new DriveTo(distPreTurn, 2, 30),
						
						
						new ResetDriveTrain(), 
						new RotateTo(degreesToTurn),
						new ResetDriveTrain(),
						
						new SetHoodPosition(HoodPosition.LOW_ANGLE),
						new ShakeBoulderStager(),
						
						new ZeroDriveEncoder(),
						new DriveTo(distPostTurn, 2, 40),
						new ResetDriveTrain(),
						
						new SequencedMultipleItem(
								new AlignToTower(),
								new ShakeBoulderStager()),
								
						new ResetDriveTrain(),
						
						new SpinUpShooter(Shooter.RPS_LONG_SHOT),
						new SequencedPause(1.5),
						new ShootHighGoal(5) 
				});
	
			case CHEVAL_DE_FRISE_AUTO:
	
	
				return new Sequencer(new SequencedItem[] {
						new DriveTo(30),
						new SetIntakeArm(Intake.FLOOR_POSITION),
						new ResetDriveTrain(),
						new DriveTo(40, 1),
						new ResetDriveTrain(),
						new SequencedMultipleItem(new DriveTo(90, 4, 0.5),
								new SetIntakeArm(Intake.FULL_UP_POSITION)),
						new SequencedMultipleItem(new SetIntakeArm(
								Intake.INTAKE_POSITION), new SetHoodPosition(
								HoodPosition.HIGH_ANGLE)),
						new SpinUpShooter(Shooter.RPS_BATTER_SHOT),
						new DriveTo(distPreTurn), new ResetDriveTrain(),
						new RotateTo(degreesToTurn), new ResetDriveTrain(),
						new ZeroDriveEncoder(), new DriveTo(distPostTurn),
						new ResetDriveTrain(), 
						new AlignToTower(),
						new ResetDriveTrain(), 
						new ShootHighGoal(5) 
						});
			case TWO_BALL_SPY_AUTO:
				return new Sequencer(new SequencedItem[] {
						new SequencedMultipleItem(
								new SetHoodPosition(HoodPosition.LOW_ANGLE),
								new ShakeBoulderStager(),
								new SetIntakeArm(Intake.INTAKE_POSITION)
							),
						new SpinUpShooter(Shooter.RPS_LONG_SHOT),
						new SequencedPause(1),
						new ShootHighGoal(),
						new SequencedPause(0.25),
						new SetHoodPosition(HoodPosition.STOWED),
						new SpinUpShooter(0),
						new DriveTo(50, 3, 100, 0, 120),
						new ResetDriveTrain(),
						new RotateTo(90, 2),
						new ResetDriveTrain(),
						new ZeroDriveEncoder(),
						new DriveThrough(190, 4, 100, 120, 90),
						new DriveThrough(190, 4, 140, 40, 90), 
						new SequencedMultipleItem(
								new SetRollers(ConstantsIO.kLateralRollerSpeed,
										ConstantsIO.kIntakeRollerSpeed),
								new SetIntakeArm(Intake.INTAKE_POSITION),
								new DriveTo(190, 4, 25, 90)
						),
						new WaitForBoulder(),
						new SetRollers(0, 0),
						new DriveTo(0, 6, 100, 90),
						new ResetDriveTrain(),
						new SetHoodPosition(HoodPosition.LOW_ANGLE),
						new RotateTo(-45, 2.5),
						new ResetDriveTrain(),
						new AlignToTower(),
						new SpinUpShooter(Shooter.RPS_LONG_SHOT),
						new SequencedPause(1),
						new ShootHighGoal(5)			
				});
				default: 
					return new Sequencer();
		}
	}

	public static Sequencer getShootHighGoalSequence() {
		return new Sequencer(new SequencedItem[] { new ShootHighGoal(),
				new SequencedPause(0.5), new SetStager(StagerPosition.NEUTRAL) });
	}

	public static Sequencer getShootLowGoalSequence() {
		return new Sequencer(new SequencedItem[] {
				new SetIntakeArm(Intake.FULL_UP_POSITION), 
				new ShootLowGoal(),
				new SetRollers(0, 0), 
				new SetStager(StagerPosition.NEUTRAL) });
	}

	public static Sequencer getAutoAimSequence(boolean wait) {
		
		if (wait) {
			return new Sequencer(new SequencedItem[] {
					new SequencedPause(0.5),
					new AlignToTower(),
					new ResetDriveTrain() });
		} else {
		
			return new Sequencer(new SequencedItem[] {
					new AlignToTower(),
					new ResetDriveTrain() });
		}
	}
	// @formatter:on
}
