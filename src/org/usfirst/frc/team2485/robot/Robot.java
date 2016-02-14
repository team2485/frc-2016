package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.auto.SequencerFactory;
import org.usfirst.frc.team2485.auto.SequencerFactory.AutoType;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Controllers;
import org.usfirst.frc.team2485.util.Logger;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Aidan Fay
 * @author Ben Clark
 * @author Anoushka Bose
 * @author Michael Maunu
 * @author Patrick Wamsley
 */
public class Robot extends IterativeRobot {

	// private SpeedController leftDriveSC1, leftDriveSC2, leftDriveSC3,
	// rightDriveSC1, rightDriveSC2, rightDriveSC3;
	// private Encoder driveEncoder;

	private Sequencer autonomousSequencer;

	public void robotInit() {

		ConstantsIO.init();
		Hardware.init();

		Controllers.set(new Joystick(0), new Joystick(1));

		// Logger.getInstance().addLoggable(Hardware.driveTrain);

		// leftDriveSC1 = new SpeedControllerWrapper(new VictorSP(6), 0);
		// leftDriveSC2 = new SpeedControllerWrapper(new VictorSP(5), 0);
		// leftDriveSC3 = new SpeedControllerWrapper(new VictorSP(1), 0);
		// rightDriveSC1 = new SpeedControllerWrapper(new VictorSP(0), 0);

		// driveEncoder = new Encoder(0, 0);
		// driveEncoder.setDistancePerPulse(Math.PI*2*WHEEL_RADIUS_INCHES /
		// 250.0);

		 Logger.getInstance().addLoggable(Hardware.battery);
		 Logger.getInstance().addLoggable(Hardware.driveTrain);
		 Logger.getInstance().addLoggable(Hardware.shooter);

		System.out.println("initialized");
	}

	public void autonomousInit() {

		resetAndDisableSystems();

		ConstantsIO.init();
		Hardware.init();
		
		Hardware.ahrs.zeroYaw();
		System.out.println("Robot - ahrs reading: " + Hardware.ahrs.getYaw());

		autonomousSequencer = SequencerFactory.createAuto(AutoType.BASIC);

	}

	public void autonomousPeriodic() {
		if (autonomousSequencer != null) {
			
			if (autonomousSequencer.run()) {
				autonomousSequencer = null;
			}
		}
		// SmartDashboard.putData("PIDController", Hardware.driveTrain.encPID);
		// System.out.println(((PIDController)
		// SmartDashboard.getData("PIDController")).getP());
		updateDashboard();
	}

	private int speedTarget = 1000;
	private boolean pressed = false;
	private boolean shooterOn = false;

	public void teleopInit() {
		resetAndDisableSystems();
		ConstantsIO.init();
		Hardware.init();
		Hardware.shooter.setBrakeMode(false);
		shooterOn = false;
	}

	public void teleopPeriodic() {

//		System.out.println(ConstantsIO.data.toString());

		// JoyStick Drive

		// Negative on Y to invert throttle
		Hardware.driveTrain.warlordDrive(
				-Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0),
				Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0));
		
		// Trigger Drive

		// Hardware.driveTrain.warlordDrive(
		// Controllers.getAxis(Controllers.XBOX_AXIS_RTRIGGER)
		// - Controllers.getAxis(Controllers.XBOX_AXIS_LTRIGGER),
		// Controllers.getAxis(Controllers.XBOX_AXIS_LX));

		// Quick turn
		if (Controllers.getButton(Controllers.XBOX_BTN_RBUMP)) {
			Hardware.driveTrain.setQuickTurn(true);
		} else {
			Hardware.driveTrain.setQuickTurn(false);
		}

		if (Controllers.getButton(Controllers.XBOX_BTN_LBUMP)) {
			Hardware.driveTrain.setHighSpeed();
		} else if (Controllers.getAxis(Controllers.XBOX_AXIS_LTRIGGER) > 0.5) {
			Hardware.driveTrain.setLowSpeed();
		} else {
			Hardware.driveTrain.setNormalSpeed();
		}

		if (Controllers.getButton(Controllers.XBOX_BTN_Y)) {
			if (!pressed) {
				speedTarget += 250;
				pressed = true;
			}
		} else if (Controllers.getButton(Controllers.XBOX_BTN_X)) {
			if (!pressed) {
				speedTarget -= 250;
				pressed = true;
			}

		} else if (Controllers.getButton(Controllers.XBOX_BTN_A)) {
			if (!pressed) {
				shooterOn = true;
				pressed = true;
			}

		} else if (Controllers.getButton(Controllers.XBOX_BTN_B)) {
			if (!pressed) {
				shooterOn = false;
				pressed = true;
			}

		} else {
			pressed = false;
		}
		if (shooterOn) {
			Hardware.shooter.setTargetSpeed(speedTarget);
		} else {
			Hardware.shooter.disable();
		}

		SmartDashboard.putNumber("Target Speed", speedTarget);

		SmartDashboard.putNumber("Current Speed", Hardware.shooter.getRate());

		SmartDashboard.putNumber("Current Error", Hardware.shooter.getError());

		SmartDashboard
				.putNumber("Throttle", Hardware.shooter.getCurrentPower());

//		System.out.println("Rate: " + Hardware.leftDriveEnc.getRate());

		SmartDashboard.putNumber("Current Slot 5",
				Hardware.battery.getCurrent(5));
		SmartDashboard.putNumber("Total Current",
				Hardware.battery.getTotalCurrent());

		Logger.getInstance().logAll();
		
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

	}

	public void testPeriodic() {

	}

	private void resetAndDisableSystems() {
		Hardware.driveTrain.disableAhrsPID();
	}

	public void updateDashboard() {

		// SmartDashboard.putNumber("Battery",
		// DriverStation.getInstance().getBatteryVoltage());
	}
}
