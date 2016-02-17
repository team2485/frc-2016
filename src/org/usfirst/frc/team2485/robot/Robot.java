package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.auto.SequencerFactory;
import org.usfirst.frc.team2485.auto.SequencerFactory.AutoType;
import org.usfirst.frc.team2485.subsystems.Shooter;
import org.usfirst.frc.team2485.subsystems.Shooter.HoodPosition;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Controllers;
import org.usfirst.frc.team2485.util.Logger;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
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

	// private SpeedController leftDriveSC1, leftDriveSC2, leftDriveSC3,
	// rightDriveSC1, rightDriveSC2, rightDriveSC3;
	// private Encoder driveEncoder;

	private Sequencer autonomousSequencer, driverTeleopSequencer, operatorTeleopSequencer;

	private SendableChooser autoChooser;

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

		initAuto();

		System.out.println("initialized");
	}

	private void initAuto() {

		autoChooser = new SendableChooser();

		for (AutoType curType : AutoType.values()) {
			autoChooser.addObject(curType.toString(), curType);
		}

		autoChooser.addDefault(AutoType.REACH_AUTO.toString(), AutoType.REACH_AUTO);
	}

	public void autonomousInit() {

		resetAndDisableSystems();

		ConstantsIO.init();
		Hardware.init();

		Hardware.ahrs.zeroYaw();
		System.out.println("Robot - ahrs reading: " + Hardware.ahrs.getYaw());

		autonomousSequencer = SequencerFactory.createAuto((AutoType) autoChooser.getSelected());

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
	private boolean shooterOn = false;

	public void teleopInit() {
		resetAndDisableSystems();
		ConstantsIO.init();
		Hardware.init();
		Hardware.shooter.setBrakeMode(false);
		shooterOn = false;
	}

	public void teleopPeriodic() {


		driverTeleopControl();

		operatorTeleopControl();

		if (driverTeleopSequencer != null && driverTeleopSequencer.run()) {
			driverTeleopSequencer = null;
		}

		if (operatorTeleopSequencer != null && operatorTeleopSequencer.run()) {
			operatorTeleopSequencer = null;
		}

		Logger.getInstance().logAll();

		updateDashboard();
	}

	private boolean XBOXPressed = false;

	private void driverTeleopControl() {

		// JoyStick Drive

		// Negative on Y to invert throttle
		Hardware.driveTrain.warlordDrive(-Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0),
				Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0));


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


		if (Controllers.getButton(Controllers.XBOX_BTN_A)) {
			if (!XBOXPressed) {
				if (driverTeleopSequencer == null) {
					driverTeleopSequencer = SequencerFactory.getAutoAimSequence();
					XBOXPressed = true;
				}
			}
		} else {
			XBOXPressed = false;
		}

	}

	private boolean joystickPressed = false;

	private void operatorTeleopControl() {

		if (Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_Y, Constants.kMoveIntakeManuallyDeadband) != 0) {// if
			// the
			// joystick
			// is
			// moved


			Hardware.intake.setManual(
					(Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_Y, Constants.kMoveIntakeManuallyDeadband)));// setmanual
			// deadbands
			// &
			// scales
			// TODO add nonlinear ramp to allow driver to lift robot up

		}

		if (Controllers.getJoystickButton(3)) {
			if (!joystickPressed) {
				//Set high angle for batter shot
				Hardware.shooter.setHoodPosition(HoodPosition.HIGH_ANGLE);
				Hardware.shooter.setTargetSpeed(Shooter.RPM_BATTER_SHOT);
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(5)) {
			if (!joystickPressed) {
				//Set low angle for long shot
				Hardware.shooter.setHoodPosition(HoodPosition.LOW_ANGLE);
				Hardware.shooter.setTargetSpeed(Shooter.RPM_LONG_SHOT);
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(0)) {// trigger
			if (!joystickPressed) {
				if (operatorTeleopSequencer == null) {
					operatorTeleopSequencer = SequencerFactory.getShootHighGoalSequence();
					joystickPressed = true;
				}
			}
		} else if (Controllers.getJoystickButton(1)) { // side trigger
			if (!joystickPressed) {
				if (operatorTeleopSequencer == null) {
					operatorTeleopSequencer = SequencerFactory.getShootLowGoalSequence();
					joystickPressed = true;
				}
			}
		} else {
			joystickPressed = false;
		} // int main = void();
		
		Logger.getInstance().logAll();

		updateDashboard();	}

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

		SmartDashboard.putNumber("Target Speed", speedTarget);

		SmartDashboard.putNumber("Current Speed", Hardware.shooter.getRate());

		SmartDashboard.putNumber("Current Error", Hardware.shooter.getError());

		SmartDashboard.putNumber("Throttle", Hardware.shooter.getCurrentPower());

		// System.out.println("Rate: " + Hardware.leftDriveEnc.getRate());

		SmartDashboard.putNumber("Current Slot 5", Hardware.battery.getCurrent(5));
		SmartDashboard.putNumber("Total Current", Hardware.battery.getTotalCurrent());
		// SmartDashboard.putNumber("Battery",
		// DriverStation.getInstance().getBatteryVoltage());
	}
}
