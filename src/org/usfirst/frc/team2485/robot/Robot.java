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
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
	private SendableChooser autoChooser, autoPosChooser;
	private double targAngle, targDist;

	public void robotInit() {

		ConstantsIO.init();
		Hardware.init();
		Hardware.updateConstants();

		initAutoChooser();

		Controllers.set(new Joystick(0), new Joystick(1));

		GRIPReciever.init();

		// GRIPReciever.setUpCameraSettings();

		// USBCamera cam = new USBCamera("cam0");
		//
		// cam.setBrightness(25);
		// cam.setExposureManual(30);
		// cam.setWhiteBalanceManual(35);

		// CameraServer.getInstance().

		// Logger.getInstance().addLoggable(Hardware.battery);
		Logger.getInstance().addLoggable(Hardware.driveTrain);
		Logger.getInstance().addLoggable(Hardware.shooter);

		// CurrentMonitor.getInstance(); // forces to construct

		// System.out.println("initialized");
	}

	private void initAutoChooser() {

		autoChooser = new SendableChooser();

		for (AutoType curType : AutoType.values()) {
			autoChooser.addObject(curType.toString(), curType);
		}

		autoChooser.addDefault(AutoType.LOW_BAR_AUTO.toString(),
				AutoType.LOW_BAR_AUTO);

		autoPosChooser = new SendableChooser();

		for (int i = 2; i <= 5; i++) {
			autoPosChooser.addObject("Position: " + i, new Integer(i));
		}

		SmartDashboard.putData("Autonomous Defense Chooser", autoChooser);

		SmartDashboard.putData("Autonomous Position Chooser", autoPosChooser);

	}

	private Thread camThread;

	private boolean cameraFound = false;

	private double degsToRot;

	public void autonomousInit() {

		resetAndDisableSystems();

		ConstantsIO.init();
		Hardware.updateConstants();

		Hardware.ahrs.zeroYaw();

		 autonomousSequencer = SequencerFactory.createAuto((AutoType)
		 autoChooser.getSelected(),
		 (Integer) autoPosChooser.getSelected());
		 
		// change to this if SendableChooser breaks
//			autonomousSequencer = SequencerFactory.createAuto(
//					AutoType.values()[ConstantsIO.autoType], ConstantsIO.autoPos);
			
		
		
//		degsToRot = ConstantsIO.kDegsToRot;
//		rotateToTestingFinished = false;

		  
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
					System.out.println("Trying to find the camera");
					if (!CameraServer.getInstance().isAutoCaptureStarted()) {
						CameraServer.getInstance()
								.startAutomaticCapture("cam0");
						cameraFound = true;
						System.out.println("Found the camera");
					}
				}

			}
		});
		camThread.start();

	}

	boolean rotateToTestingFinished = false;

	public void autonomousPeriodic() {
		if (autonomousSequencer != null) {

			if (autonomousSequencer.run()) {
				autonomousSequencer = null;
			}
		}

		// if (!rotateToTestingFinished) {
		// rotateToTestingFinished = Hardware.driveTrain.rotateTo(degsToRot);
		// // Hardware.driveTrain.setLeftRightVelocity(degsToRot, degsToRot);
		// System.out.print("DriveTrain: rotateTo target = " + degsToRot);
		// System.out.println("\t\terror = " + (degsToRot -
		// Hardware.ahrs.getYaw()));
		// // SmartDashboard.putNumber("Left drive rate",
		// Hardware.leftDriveEnc.getRate());
		// // SmartDashboard.putNumber("Right drive rate",
		// Hardware.rightDriveEnc.getRate());
		// }

		// SmartDashboard.putData("PIDController", Hardware.driveTrain.encPID);
		// System.out.println(((PIDController)
		// SmartDashboard.getData("PIDController")).getP());
		updateDashboard();
		updateAllPeriodic();

	}

	public void teleopInit() {
		resetAndDisableSystems();
		ConstantsIO.init();
		Hardware.updateConstants();
		Hardware.shooter.setBrakeMode(false);

	}

	public void teleopPeriodic() {

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

	private boolean XBOXPressed = false;
	private boolean LBumpPressed = false;

	private void driverTeleopControl() {

		// JoyStick Drive

		// Negative on Y to invert throttle

		// Quick turn
		if (Controllers.getButton(Controllers.XBOX_BTN_RBUMP)) {
			Hardware.driveTrain.setQuickTurn(true);
		} else {
			Hardware.driveTrain.setQuickTurn(false);
		}

		if (Controllers.getButton(Controllers.XBOX_BTN_LBUMP)) {
			// Cancel auto-aim (maybe lock drive train?)
			if (driverTeleopSequencer != null) {
				driverTeleopSequencer = null;
				Hardware.driveTrain.reset();
			}
			if (!LBumpPressed) {
				LBumpPressed = true;
				targAngle = Hardware.ahrs.getAngle();
			}
			Hardware.driveTrain.rotateTo(targAngle);
			// Hardware.driveTrain.brake();
		} else {
			if (LBumpPressed) {
				LBumpPressed = false;
				Hardware.driveTrain.reset();
			}

			double inputX = Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0.2f);
			double inputY = -Controllers
					.getAxis(Controllers.XBOX_AXIS_LY, 0.2f);

			Hardware.driveTrain.warlordDrive(inputY, inputX);

		}

		if (Controllers.getAxis(Controllers.XBOX_AXIS_RTRIGGER) > 0.4) {
			Hardware.driveTrain.setHighSpeed();
		} else if (Controllers.getAxis(Controllers.XBOX_AXIS_LTRIGGER) > 0.4) {
			Hardware.driveTrain.setLowSpeed();
		} else {
			Hardware.driveTrain.setNormalSpeed();
		}

		if (Controllers.getButton(Controllers.XBOX_BTN_A)) {
			
			SmartDashboard.putBoolean("Increase Recording FPS", true);
			
			if (!XBOXPressed) {
				// Auto aim
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
			if (!XBOXPressed) {
				
				// Intake
				Hardware.boulderStager.setPosition(StagerPosition.INTAKE);
				Hardware.intake.setSetpoint(Intake.INTAKE_POSITION, true);
				XBOXPressed = true;
			}
		} else if (Controllers.getButton(Controllers.XBOX_BTN_B)) {
			// Prep for Low Bar because button "B" for Low "B"ar... :|
			if (!XBOXPressed) {
				Hardware.intake.setSetpoint(Intake.LOW_NO_INTAKE_POSITION);
				Hardware.shooter.setHoodPosition(HoodPosition.STOWED);
				XBOXPressed = true;
			}
		} else if (Controllers.getButton(Controllers.XBOX_BTN_Y)) {
			// Prep for other defenses
			Hardware.shooter.setPWM(-0.75);
			Hardware.boulderStager.setPosition(StagerPosition.SHOOTING);
			XBOXPressed = true;

		} else {
			XBOXPressed = false;
			if (!Hardware.shooter.isPID()) {
				Hardware.shooter.disableShooter();
			}
		}

	}

	private boolean joystickPressed = false;

	private void operatorTeleopControl() {

		// Axes
		if (Controllers.getOperatorHatSwitch() != -1) {
			if (Hardware.intake.isPIDEnabled()) {
				Hardware.intake.setManual(0);
			}
			Hardware.intakeArmSC.set(Constants.kHatPowerValue);
			Hardware.boulderStager.setPosition(StagerPosition.SHOOTING);
			joystickPressed = true;
		} else if (Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_Y,
				Constants.kMoveIntakeManuallyDeadband) != 0) {

			Hardware.intake.setManual(Controllers.getJoystickAxis(
					Controllers.JOYSTICK_AXIS_Y,
					Constants.kMoveIntakeManuallyDeadband));

		} else if (!Hardware.intake.isPIDEnabled()) {
			Hardware.intake.setSetpoint(Hardware.intake.getCurrentPosition());
		}

		// Buttons
		if (Controllers.getJoystickButton(1)) {// trigger
			operatorTeleopSequencer = SequencerFactory
					.getShootHighGoalSequence();
			joystickPressed = true;
		} else if (Controllers.getJoystickButton(2)) { // side trigger
			if (!joystickPressed) {
				operatorTeleopSequencer = SequencerFactory
						.getShootLowGoalSequence();
				// Hardware.shooter.setTargetSpeed(Shooter.RPM_LOW_GOAL_SHOT);
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(3)) {
			if (!joystickPressed) {
				// Set low angle for Lidar shot
				// Hardware.shooter.setHoodPosition(HoodPosition.LOW_ANGLE);
				// Hardware.shooter.setSpeedOffLidar();

				if (operatorTeleopSequencer == null) {
					operatorTeleopSequencer = new Sequencer(
							new SequencedItem[] { new ShakeBoulderStager() });
					joystickPressed = true;
				}
			}
		} else if (Controllers.getJoystickButton(4)) {
			if (!joystickPressed) {
				// Set high angle for batter shot
				// Hardware.shooter.setHoodPosition(HoodPosition.HIGH_ANGLE);
				Hardware.shooter.setHoodPosition(HoodPosition.HIGH_ANGLE);
				operatorTeleopSequencer = new Sequencer(new SpinUpShooter(
						Shooter.RPS_BATTER_SHOT));
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(5)) {
			if (!joystickPressed) {
				Hardware.shooter.disableShooter();
			}
		} else if (Controllers.getJoystickButton(6)) {
			if (!joystickPressed) {
				// Set low angle for long shot
				Hardware.shooter.setHoodPosition(HoodPosition.LOW_ANGLE);
				operatorTeleopSequencer = new Sequencer(new SpinUpShooter(
						Shooter.RPS_LONG_SHOT));
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(7)) {
			if (!joystickPressed) {
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
		ConstantsIO.init();
		Hardware.updateConstants();

		System.out.println("Robot: Intake arm position: "
				+ Hardware.intake.getCurrentPosition());
		System.out.println("Robot: Sonic Sensor: "
				+ Hardware.sonic.getRangeInches());
	}

	public void testPeriodic() {

		// System.out.println("Robot: EncoderPos: " +
		// Hardware.intakeAbsEncoder.get());

		// double angleToRotate = ConstantsIO.kDegsToRot;
		//
		// Hardware.driveTrain.rotateTo(angleToRotate);
		//
		// System.out.println("Desired angle: " + angleToRotate +
		// " current angle: " + Hardware.ahrs.getAngle());

		// System.out.println(Hardware.leftDriveEnc.getDistance());
		// System.out.println("Intake arm position: " +
		// Hardware.intake.getCurrentPosition());
		System.out.println("Robot: testPeriodic(): Shooter enc: "
				+ Hardware.shooter.getRate());

		// Hardware.shooter.setPWM(ConstantsIO.kF_Shooter *
		// Shooter.RPS_BATTER_SHOT);

		if (Hardware.pressureSwitch.get()) {
			Hardware.compressorSpike.set(Relay.Value.kOff);
		} else {
			Hardware.compressorSpike.set(Relay.Value.kForward);
		}

		updateDashboard();
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

	// private PowerDistributionPanel pdp = new PowerDistributionPanel();

	public void updateDashboard() {

		SmartDashboard.putNumber("Left enc vel",
				Hardware.leftDriveEnc.getRate());

		SmartDashboard.putNumber("Right enc vel",
				Hardware.rightDriveEnc.getRate());
		// System.out.println("Gyro error: " +
		// Hardware.driveTrain.rotateToPID.getError());
		// System.out.println("Encoder value: " +
		// Hardware.driveTrain.getEncoderOutput());
		// System.out.println("Ultrasonic value: " +
		// Hardware.sonic.getRangeInches());

		SmartDashboard.putNumber("Graphable RPM", Hardware.shooter.getRate());

		SmartDashboard.putString("RPM", (int) (Hardware.shooter.getRate() * 60)
				+ "," + (int) Hardware.shooter.getSetpoint() * 60);

		SmartDashboard.putNumber("Current Error", Hardware.shooter.getError());

		SmartDashboard
				.putNumber("Throttle", Hardware.shooter.getCurrentPower());

		SmartDashboard.putBoolean("Boulder Detector",
				Hardware.boulderDetector.hasBoulder());

		// SmartDashboard.putNumber("Lidar Distance",
		// Hardware.lidar.getDistance());

		/*
		 * Expects values separated by a comma: Current Angle,Encoder Reading
		 * for Floor,Reading for Intake,Reading for Full Up
		 */
		SmartDashboard.putString("Intake Arm Angle",
				Hardware.intake.getCurrentPosition() + ","
						+ Intake.FLOOR_POSITION + "," + Intake.INTAKE_POSITION
						+ "," + Intake.FULL_UP_POSITION);

//		SmartDashboard.putData("Autonomous Defense Chooser", autoChooser);
//
//		SmartDashboard.putData("Autonomous Position Chooser", autoPosChooser);

		// SmartDashboard.putNumber("Battery", Hardware.battery.getVoltage());

		// SmartDashboard.putNumber("Total Current",
		// Hardware.battery.getTotalCurrent());

		// if (Hardware.battery.getTotalCurrent() > 0) {
		// System.out.println("Robot: Total Current From Battery: "
		// + Hardware.battery.getTotalCurrent());
		// }
		//
		// if (pdp.getTotalCurrent() > 0) {
		// System.out.println("Robot: Total Current From PDP: "
		// + pdp.getTotalCurrent());
		// }
	}

	/**
	 * Run in teleop, auto and test periodic
	 */
	public void updateAllPeriodic() {

		Logger.getInstance().logAll();
		// CurrentMonitor.getInstance().monitorCurrent();

	}
}
