package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.auto.SequencerFactory;
import org.usfirst.frc.team2485.auto.SequencerFactory.AutoType;
import org.usfirst.frc.team2485.auto.sequenceditems.SetHoodPosition;
import org.usfirst.frc.team2485.auto.sequenceditems.SetStager;
import org.usfirst.frc.team2485.auto.sequenceditems.ShakeBoulderStager;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;
import org.usfirst.frc.team2485.subsystems.Intake;
import org.usfirst.frc.team2485.subsystems.Shooter;
import org.usfirst.frc.team2485.subsystems.Shooter.HoodPosition;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Controllers;
import org.usfirst.frc.team2485.util.CurrentMonitor;
import org.usfirst.frc.team2485.util.LidarWrapper;
import org.usfirst.frc.team2485.util.Logger;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
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

	// private SpeedController leftDriveSC1, leftDriveSC2, leftDriveSC3,
	// rightDriveSC1, rightDriveSC2, rightDriveSC3;
	// private Encoder driveEncoder;

	private Sequencer autonomousSequencer, driverTeleopSequencer, operatorTeleopSequencer;

	private SendableChooser autoChooser, autoPosChooser;

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

		// Logger.getInstance().addLoggable(Hardware.battery);

		Logger.getInstance().addLoggable(Hardware.driveTrain);
		Logger.getInstance().addLoggable(Hardware.shooter);

		CameraServer.getInstance().startAutomaticCapture("cam0");

		CurrentMonitor.getInstance(); // forces to construct

		initAutoChooser();

		System.out.println("initialized");
	}

	private void initAutoChooser() {

		autoChooser = new SendableChooser();

		for (AutoType curType : AutoType.values()) {
			autoChooser.addObject(curType.toString(), curType);
		}

		autoChooser.addDefault(AutoType.REACH_AUTO.toString(), AutoType.REACH_AUTO);

		autoPosChooser = new SendableChooser();

		for (int i = 2; i <= 5; i++) {
			autoPosChooser.addObject("Position: " + i, new Integer(i));
		}

		SmartDashboard.putData("Autonomous Defense Chooser", autoChooser);

		SmartDashboard.putData("Autonomous Position Chooser", autoPosChooser);

	}

	public void autonomousInit() {

		resetAndDisableSystems();

		ConstantsIO.init();
		Hardware.init();

		Hardware.ahrs.zeroYaw();
		System.out.println("Robot - ahrs reading: " + Hardware.ahrs.getYaw());

		autonomousSequencer = SequencerFactory.createAuto((AutoType) autoChooser.getSelected(),
				(Integer) autoPosChooser.getSelected());

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
		updateAllPeriodic();

	}

	public void teleopInit() {
		resetAndDisableSystems();
		ConstantsIO.init();
		Hardware.init();
		Hardware.shooter.setBrakeMode(false);
	}

	public void teleopPeriodic() {

		if (driverTeleopSequencer == null) {
			driverTeleopControl();
		} else if (driverTeleopSequencer.run()) {
			driverTeleopSequencer = null;
		}

		if (operatorTeleopSequencer == null) {
			operatorTeleopControl();
		} else if (operatorTeleopSequencer.run()) {
			operatorTeleopSequencer = null;
		}

		// if (Hardware.intake.isPIDEnabled()) {
		// System.out.println("Robot: arm PID is enabled");
		// }

		updateAllPeriodic();

		updateDashboard();

	}

	private boolean XBOXPressed = false;

	private void driverTeleopControl() {

		// JoyStick Drive

		// Negative on Y to invert throttle

		double inputX = Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0.2f);
		double inputY = -Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0.2f);

		Hardware.driveTrain.warlordDrive(inputY, inputX);

		// Quick turn
		if (Controllers.getButton(Controllers.XBOX_BTN_RBUMP)) {
			Hardware.driveTrain.setQuickTurn(true);
		} else {
			Hardware.driveTrain.setQuickTurn(false);
		}
		
		if (Controllers.getAxis(Controllers.XBOX_AXIS_RTRIGGER) > 0.4) {
			Hardware.driveTrain.setHighSpeed();
		} else if (Controllers.getAxis(Controllers.XBOX_AXIS_LTRIGGER) > 0.4) {
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
		} else if (Controllers.getButton(Controllers.XBOX_BTN_X)) {
			if (!XBOXPressed) {
				Hardware.boulderStager.setPosition(Position.INTAKE);
				Hardware.intake.setSetpoint(Intake.INTAKE_POSITION, true);
				// System.out.println("Robot: X was in fact pressed");
				XBOXPressed = true;
			}
		} else if (Controllers.getButton(Controllers.XBOX_BTN_B)) {
			// Prep for Low Bar because button "B" for Low "B"ar... :|
			if (!XBOXPressed) {
				Hardware.intake.setSetpoint(Intake.INTAKE_POSITION);
				Hardware.shooter.setHoodPosition(HoodPosition.STOWED);
				XBOXPressed = true;
			}
		} else if (Controllers.getButton(Controllers.XBOX_BTN_Y)) {
			// Prep for other defenses
			if (!XBOXPressed) {
				Hardware.intake.setSetpoint(Intake.PORTCULLIS_POSITION);
				XBOXPressed = true;
			}
		} else {
			XBOXPressed = false;
		}

	}

	private boolean joystickPressed = false;

	private void operatorTeleopControl() {

		if (Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_Y, Constants.kMoveIntakeManuallyDeadband) != 0) {

			Hardware.intake.setManual(
					Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_Y, Constants.kMoveIntakeManuallyDeadband));

		}
		// TODO get axis for thumbstick and uncomment once intake arm is fixed
		// else if (Controllers.getJoystickAxis(4) > 0.4) {
		// Hardware.intake.setManual(0.1);
		// }
		else {
			if (!Hardware.intake.isPIDEnabled()) {
				Hardware.intake.setSetpoint(Hardware.intake.getCurrentPosition());
			}
			// Hardware.intake.setManual(0);
		}

		if (Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_THROTTLE) > 0) {

			if (operatorTeleopSequencer == null) {

				operatorTeleopSequencer = new Sequencer(new SequencedItem[] { new ShakeBoulderStager() });

			}
		}

		if (Controllers.getJoystickButton(1)) {// trigger
			operatorTeleopSequencer = SequencerFactory.getShootHighGoalSequence();
			joystickPressed = true;
		} else if (Controllers.getJoystickButton(2)) { // side trigger
			if (!joystickPressed) {
				operatorTeleopSequencer = SequencerFactory.getShootLowGoalSequence();
				joystickPressed = true;
			}
		} else if (Controllers.getJoystickButton(3)) {
			if (!joystickPressed) {
				// Set low angle for Lidar shot
				Hardware.shooter.setHoodPosition(HoodPosition.LOW_ANGLE);
				Hardware.shooter.setSpeedOffLidar();
			}
		} else if (Controllers.getJoystickButton(4)) {
			if (!joystickPressed) {
				// Set high angle for batter shot
				Hardware.shooter.setHoodPosition(HoodPosition.HIGH_ANGLE);
				Hardware.shooter.setTargetSpeed(Shooter.RPM_BATTER_SHOT);
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
				Hardware.shooter.setTargetSpeed(Shooter.RPM_LONG_SHOT);
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
		} // int main = void();

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
		Hardware.init();
		Hardware.intake.FLOOR_POSITION = Hardware.intakeAbsEncoder.get();
		Hardware.intake.LOW_NO_INTAKE_POSITION = (Hardware.intake.FLOOR_POSITION + 0.085) % 1;
		Hardware.intake.INTAKE_POSITION = (Hardware.intake.FLOOR_POSITION + 0.120) % 1;
		Hardware.intake.PORTCULLIS_POSITION = (Hardware.intake.FLOOR_POSITION + 0.261) % 1;
		Hardware.intake.FULL_UP_POSITION = (Hardware.intake.FLOOR_POSITION + 0.348) % 1;

	}

	public void testPeriodic() {

		System.out.println("Robot: EncoderPos: " + Hardware.intakeAbsEncoder.get());

		// if (Hardware.pressureSwitch.get()) {
		// Hardware.compressorSpike.set(Relay.Value.kOff);
		// } else {
		// Hardware.compressorSpike.set(Relay.Value.kForward);
		// }

	}

	private void resetAndDisableSystems() {

		Hardware.driveTrain.disableAhrsPID();
		Hardware.driveTrain.driveStraightPID.disable();
		Hardware.driveTrain.disableDriveToPID();
		Hardware.driveTrain.emergencyStop();
		Hardware.driveTrain.resetEncoder();
		Hardware.intake.setManual(0);
		Hardware.intake.stopRollers();
		Hardware.ahrs.reset();
		Hardware.shooter.resetHood();
		Hardware.shooter.disableShooter();

	}

	private PowerDistributionPanel pdp = new PowerDistributionPanel();

	public void updateDashboard() {

		// System.out.println("Ultrasonic value: " +
		// Hardware.sonic.getRangeInches());

		SmartDashboard.putString("RPM", (int) Hardware.shooter.getRate() + "," + (int) Hardware.shooter.getSetpoint());

		SmartDashboard.putNumber("Current Error", Hardware.shooter.getError());

		SmartDashboard.putNumber("Throttle", Hardware.shooter.getCurrentPower());

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
		CurrentMonitor.getInstance().monitorCurrent();

	}
}
