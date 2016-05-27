package org.usfirst.frc.team2485.robot;


import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.auto.SequencerFactory;
import org.usfirst.frc.team2485.auto.SequencerFactory.AutoType;
import org.usfirst.frc.team2485.auto.sequenceditems.ShakeBoulderStager;
import org.usfirst.frc.team2485.auto.sequenceditems.SpinUpShooter;
import org.usfirst.frc.team2485.subsystems.BoulderStager.StagerPosition;
import org.usfirst.frc.team2485.subsystems.Intake;
import org.usfirst.frc.team2485.subsystems.Shooter;
import org.usfirst.frc.team2485.subsystems.Shooter.HoodPosition;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Controllers;
import org.usfirst.frc.team2485.util.GRIPReciever;
import org.usfirst.frc.team2485.util.Logger;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Aidan Fay
 * @author Ben Clark
 * @author Anoushka Bose
 * @author Michael Maunu
 * @author Jeremy McCulloch
 * @author Nicholas Contreras
 */
public class Robot extends IterativeRobot {

	private Sequencer autonomousSequencer, driverTeleopSequencer,
			operatorTeleopSequencer;
	
	private Thread camThread;
	private boolean cameraFound = false;
	private double targAngle; //used for brake

	private boolean XBOXPressed = false;
	private boolean LBumpPressed = false;

	public void robotInit() {

		ConstantsIO.init();
		Hardware.init();
		Hardware.updateConstants();
		
		Controllers.set(new Joystick(0), new Joystick(1));

		GRIPReciever.init();
		
		Logger.getInstance().addLoggable(Hardware.driveTrain);
		Logger.getInstance().addLoggable(Hardware.shooter);

	}

	public void autonomousInit() {

		resetAndDisableSystems();

		ConstantsIO.init();
		Hardware.updateConstants();
		Hardware.ahrs.zeroYaw();

		autonomousSequencer = SequencerFactory.createAuto(
				AutoType.LOW_BAR_AUTO, 1);
		  
		camThread = new Thread(new Runnable() {

			@Override
			public void run() {

				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						cameraFound = true;
					}
				}, 60 * 1000);

				while (!cameraFound) {
					if (!CameraServer.getInstance().isAutoCaptureStarted()) {
						CameraServer.getInstance()
								.startAutomaticCapture("cam0");
						cameraFound = true;
					}
				}

			}
		});
		camThread.start();

	}

	public void autonomousPeriodic() {
		if (autonomousSequencer != null) {

			if (autonomousSequencer.run()) {
				autonomousSequencer = null;
			}
		}

		updateDashboard();
		updateAllPeriodic();

	}

	public void teleopInit() {
		
		resetAndDisableSystems();
		
		ConstantsIO.init();
		Hardware.updateConstants();

	}

	public void teleopPeriodic() {

		// Driver cannot drive during teleop sequence unless they cancel it (pressing lbump)
		if (driverTeleopSequencer == null || Controllers.getButton(Controllers.XBOX_BTN_LBUMP)) {
			driverTeleopControl();
		} else if (driverTeleopSequencer.run()) {
			driverTeleopSequencer = null;
		}

		if (operatorTeleopSequencer == null) {
			operatorTeleopControl();
		} else if (operatorTeleopSequencer.run()) {
			operatorTeleopSequencer = null;
		}

		updateAllPeriodic();
		updateDashboard();

	}

	private void driverTeleopControl() {

		// Quick turn
		if (Controllers.getButton(Controllers.XBOX_BTN_RBUMP)) {
			Hardware.driveTrain.setQuickTurn(true);
		} else {
			Hardware.driveTrain.setQuickTurn(false);
		}

		if (Controllers.getButton(Controllers.XBOX_BTN_LBUMP)) { // Cancel auto-aim and lock drive train
			
			if (driverTeleopSequencer != null) {
				driverTeleopSequencer = null;
				Hardware.driveTrain.reset();
			}
			
			if (!LBumpPressed) {
				LBumpPressed = true;
				targAngle = Hardware.ahrs.getAngle();
			}
			Hardware.driveTrain.rotateTo(targAngle);
			
		} else {
			
			if (LBumpPressed) {
				LBumpPressed = false;
				Hardware.driveTrain.reset(); //disable brake
			}

			double inputX = Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0.2f);
			double inputY = -Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0.2f);

			Hardware.driveTrain.warlordDrive(inputY, inputX);

		}

		//Virtual gears
		if (Controllers.getAxis(Controllers.XBOX_AXIS_RTRIGGER) > 0.4) {
			Hardware.driveTrain.setHighSpeed();
		} else if (Controllers.getAxis(Controllers.XBOX_AXIS_LTRIGGER) > 0.4) {
			Hardware.driveTrain.setLowSpeed();
		} else {
			Hardware.driveTrain.setNormalSpeed();
		}

		if (Controllers.getButton(Controllers.XBOX_BTN_A)) { 
			if (!XBOXPressed) { // Auto aim
				if (driverTeleopSequencer == null) {

					if (Hardware.shooter.getHoodPosition() == HoodPosition.STOWED) {
						Hardware.shooter
								.setHoodPosition(HoodPosition.HIGH_ANGLE);

						driverTeleopSequencer = SequencerFactory
								.getAutoAimSequence(true);
					} else {
						driverTeleopSequencer = SequencerFactory
								.getAutoAimSequence(false);
					}
					XBOXPressed = true;
				}
			}
		} else if (Controllers.getButton(Controllers.XBOX_BTN_X)) {
			if (!XBOXPressed) { // Intake
				Hardware.boulderStager.setPosition(StagerPosition.INTAKE);
				Hardware.intake.setSetpoint(Intake.INTAKE_POSITION, true);
				XBOXPressed = true;
			}
		} else if (Controllers.getButton(Controllers.XBOX_BTN_B)) {
			if (!XBOXPressed) { // Prep for Low Bar
				Hardware.intake.setSetpoint(Intake.LOW_NO_INTAKE_POSITION);
				Hardware.shooter.setHoodPosition(HoodPosition.STOWED);
				XBOXPressed = true;
			}
		} else if (Controllers.getButton(Controllers.XBOX_BTN_Y)) {
			// Unjam Shooter
			Hardware.shooter.setPWM(-0.75);
			Hardware.boulderStager.setPosition(StagerPosition.SHOOTING);
			XBOXPressed = true;

		} else {
			XBOXPressed = false;
			if (!Hardware.shooter.isPIDEnabled()) { // Disable unjam
				Hardware.shooter.disableShooter();
			}
		}
	}

	private boolean joystickPressed = false;

	private void operatorTeleopControl() {

		if (Controllers.getOperatorHatSwitch() != -1) {
			//Pressed while crossing defense, holds arm up and ball in
			if (Hardware.intake.isPIDEnabled()) {
				Hardware.intake.setManual(0);
			}
			Hardware.intakeArmSC.set(Constants.kHatPowerValue);
			Hardware.boulderStager.setPosition(StagerPosition.SHOOTING);
			joystickPressed = true;
		} else if (Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_Y,
				Constants.kMoveIntakeManuallyDeadband) != 0) {
			//Manual intake arm control
			Hardware.intake.setManual(Controllers.getJoystickAxis(
					Controllers.JOYSTICK_AXIS_Y,
					Constants.kMoveIntakeManuallyDeadband));

		} else if (!Hardware.intake.isPIDEnabled()) {
			//Hold in place otherwise
			Hardware.intake.setSetpoint(Hardware.intake.getCurrentPosition());
		}

		if (Controllers.getJoystickButton(1)) { // Trigger
			operatorTeleopSequencer = SequencerFactory
					.getShootHighGoalSequence();
			joystickPressed = true;
		} else if (Controllers.getJoystickButton(2)) { // side trigger
			if (!joystickPressed) {
				operatorTeleopSequencer = SequencerFactory
						.getShootLowGoalSequence();
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(3)) { 
			if (!joystickPressed) { // Moves boulder stager back and forth
				if (operatorTeleopSequencer == null) {
					operatorTeleopSequencer = new Sequencer(
							new SequencedItem[] { new ShakeBoulderStager() });
					joystickPressed = true;
				}
			}
		} else if (Controllers.getJoystickButton(4)) {
			if (!joystickPressed) { // Spin up for batter shot
				Hardware.shooter.setHoodPosition(HoodPosition.HIGH_ANGLE);
				operatorTeleopSequencer = new Sequencer(new SpinUpShooter(
						Shooter.RPS_BATTER_SHOT));
				Hardware.intake.startRollers(0, 0);
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(5)) {
			if (!joystickPressed) { // Stop shooter
				Hardware.shooter.disableShooter();
			}
		} else if (Controllers.getJoystickButton(6)) {
			if (!joystickPressed) { // Spin up for long shot
				Hardware.shooter.setHoodPosition(HoodPosition.LOW_ANGLE);
				Hardware.intake.startRollers(0, 0);

				operatorTeleopSequencer = new Sequencer(new SpinUpShooter(
						Shooter.RPS_LONG_SHOT));
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(7)) {
			if (!joystickPressed) { // Manually stop intake rollers
				Hardware.intake.stopRollers();
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(8)) {
			if (!joystickPressed) {
				Hardware.intake.setSetpoint(Intake.FLOOR_POSITION);
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(9)) {
			if (!joystickPressed) {
				Hardware.intake.setSetpoint(Intake.LOW_NO_INTAKE_POSITION);
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(10)) {
			if (!joystickPressed) {
				Hardware.intake.setSetpoint(Intake.INTAKE_POSITION);
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(11)) {
			if (!joystickPressed) {
				Hardware.intake.setSetpoint(Intake.PORTCULLIS_POSITION);
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(12)) {
			if (!joystickPressed) {
				Hardware.intake.setSetpoint(Intake.FULL_UP_POSITION);
				joystickPressed = true;
			}
		} else {
			joystickPressed = false;
		}

		updateDashboard();
	}

	public void disabledInit() {
		resetAndDisableSystems();
	}

	public void disabledPeriodic() {
		updateDashboard();
	}

	public void testInit() {

		resetAndDisableSystems();

	}

	public void testPeriodic() {

		if (Hardware.pressureSwitch.get()) {
			Hardware.compressorSpike.set(Relay.Value.kOff);
		} else {
			Hardware.compressorSpike.set(Relay.Value.kForward);
		}

	}

	private void resetAndDisableSystems() {

		Hardware.driveTrain.reset();
		Hardware.driveTrain.resetEncoders();
		Hardware.intake.setManual(0);
		Hardware.intake.stopRollers();
		Hardware.ahrs.reset();
		Hardware.shooter.resetHood();
		Hardware.shooter.disableShooter();
		Hardware.boulderStager.setPosition(StagerPosition.NEUTRAL);

	}

	public void updateDashboard() {

		SmartDashboard.putNumber("Left enc vel",
				Hardware.leftDriveEnc.getRate());

		SmartDashboard.putNumber("Right enc vel",
				Hardware.rightDriveEnc.getRate());

		SmartDashboard.putNumber("Graphable RPM", Hardware.shooter.getRate());

		SmartDashboard.putString("RPM", (int) (Hardware.shooter.getRate() * 60)
				+ "," + (int) Hardware.shooter.getSetpoint() * 60);

		SmartDashboard.putNumber("Current Error", Hardware.shooter.getError());

		SmartDashboard
				.putNumber("Throttle", Hardware.shooter.getCurrentPower());

		SmartDashboard.putBoolean("Boulder Detector",
				Hardware.boulderDetector.hasBoulder());

		/*
		 * Expects values separated by a comma: Current Angle,Encoder Reading
		 * for Floor,Reading for Intake,Reading for Full Up
		 */
		SmartDashboard.putString("Intake Arm Angle",
				Hardware.intake.getCurrentPosition() + ","
						+ Intake.FLOOR_POSITION + "," + Intake.INTAKE_POSITION
						+ "," + Intake.FULL_UP_POSITION);


	}

	/**
	 * Run in teleop, auto and test periodic
	 */
	public void updateAllPeriodic() {
		Logger.getInstance().logAll();
	}
}
