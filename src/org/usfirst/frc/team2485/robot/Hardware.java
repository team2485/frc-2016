package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.subsystems.BoulderDetector;
import org.usfirst.frc.team2485.subsystems.BoulderStager;
import org.usfirst.frc.team2485.subsystems.DriveTrain;
import org.usfirst.frc.team2485.subsystems.Intake;
import org.usfirst.frc.team2485.subsystems.Shooter;
import org.usfirst.frc.team2485.util.Battery;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.InvertedAbsoluteEncoder;
import org.usfirst.frc.team2485.util.LidarWrapper;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Ultrasonic.Unit;
import edu.wpi.first.wpilibj.VictorSP;

public class Hardware {

	public static Battery battery;

	// Speed Controllers
	public static VictorSP[] rightDriveVictorSPs, leftDriveVictorSPs;

	public static SpeedControllerWrapper rightDrive, leftDrive;

	public static CANTalon leftShooterMotor, rightShooterMotor;

	public static VictorSP intakeArmVictorSP;
	public static SpeedControllerWrapper intakeArmSC;

	public static VictorSP lateralVictorSP;
	public static VictorSP intakeVictorSP;

	// Solenoids
	public static Solenoid shooterHoodSolenoid1, shooterHoodSolenoid2,
			boulderStagerSolenoid1, boulderStagerSolenoid2;

	// Sensors
	public static Encoder leftDriveEnc, rightDriveEnc, shooterEnc;
	public static InvertedAbsoluteEncoder intakeAbsEncoder;
	public static AHRS ahrs;

	public static Ultrasonic sonic;

	public static LidarWrapper lidar;

	// Sequences && Auto
	public static Sequencer autoSequence;

	// Compressor
	public static Relay compressorSpike;
	public static DigitalInput pressureSwitch;

	// Subsystems
	public static DriveTrain driveTrain;

	public static Shooter shooter;

	public static Intake intake;

	public static BoulderStager boulderStager;

	public static BoulderDetector boulderDetector;

	public static void init() {

		battery = new Battery();

		// Victor SPs
		rightDriveVictorSPs = new VictorSP[3];
		rightDriveVictorSPs[0] = new VictorSP(Constants.kRightDrivePWM[0]);
		rightDriveVictorSPs[1] = new VictorSP(Constants.kRightDrivePWM[1]);
		rightDriveVictorSPs[2] = new VictorSP(Constants.kRightDrivePWM[2]);

		leftDriveVictorSPs = new VictorSP[3];
		leftDriveVictorSPs[0] = new VictorSP(Constants.kLeftDrivePWM[0]);
		leftDriveVictorSPs[1] = new VictorSP(Constants.kLeftDrivePWM[1]);
		leftDriveVictorSPs[2] = new VictorSP(Constants.kLeftDrivePWM[2]);

		rightDrive = new SpeedControllerWrapper(rightDriveVictorSPs,
				Constants.kRightDrivePDP, new double[] {1.45, 1.45, 1});

		leftDrive = new SpeedControllerWrapper(leftDriveVictorSPs,
				Constants.kLeftDrivePDP, new double[] {1.45, 1.45, 1});

		lateralVictorSP = new VictorSP(Constants.kLateralRollerPWM);
		intakeVictorSP = new VictorSP(Constants.kIntakeRollerPWM);

		intakeArmVictorSP = new VictorSP(Constants.kIntakeArmPWM);
		intakeArmSC = new SpeedControllerWrapper(intakeArmVictorSP,
				Constants.kIntakeArmPDP);

		leftShooterMotor = new CANTalon(Constants.kLeftShooterCAN);
		rightShooterMotor = new CANTalon(Constants.kRightShooterCAN);

		compressorSpike = new Relay(Constants.kCompressorSpikePort);
		pressureSwitch = new DigitalInput(Constants.kPressureSwitchPort);

		shooterHoodSolenoid1 = new Solenoid(
				Constants.kShooterHoodSolenoidLowerPort);
		shooterHoodSolenoid2 = new Solenoid(
				Constants.kShooterHoodSolenoidUpperPort);

		boulderStagerSolenoid1 = new Solenoid(
				Constants.kBoulderStagerSolenoid1Port);
		boulderStagerSolenoid2 = new Solenoid(
				Constants.kBoulderStagerSolenoid2Port);

		leftDriveEnc = new Encoder(Constants.kLeftDriveEncoder[0],
				Constants.kLeftDriveEncoder[1]);
		rightDriveEnc = new Encoder(Constants.kRightDriveEncoder[0],
				Constants.kRightDriveEncoder[1]);
		shooterEnc = new Encoder(Constants.kShooterEncoder[0], Constants.kShooterEncoder[1],
				false, EncodingType.k1X);

		intakeAbsEncoder = new InvertedAbsoluteEncoder(
				new AnalogPotentiometer(Constants.kIntakeArmAbsEncoder));

		ahrs = new AHRS(SPI.Port.kMXP);

		sonic = new Ultrasonic(Constants.kUltrasonicPING,
				Constants.kUltrasonicECHO, Unit.kInches);

		lidar = new LidarWrapper(Port.kMXP);

		boulderDetector = new BoulderDetector();

		rightDrive.setInverted(false);
		rightDrive.setRampMode(true);

		leftDrive.setInverted(true);
		leftDrive.setRampMode(true);

		leftDriveEnc.setDistancePerPulse(0.01295 * 4);
		rightDriveEnc.setDistancePerPulse(0.01295 * 4);

		//		leftDriveEnc.setReverseDirection(true);

		shooterEnc.setDistancePerPulse(1.0/250);
		shooterEnc.setPIDSourceType(PIDSourceType.kRate);
		shooterEnc.setReverseDirection(true);

		leftShooterMotor.setInverted(false);
		rightShooterMotor.setInverted(true);
		rightShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		leftShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);

		ahrs.zeroYaw();

		sonic.setAutomaticMode(true);

		driveTrain = new DriveTrain(true);
		shooter = new Shooter();
		intake = new Intake();
		boulderStager = new BoulderStager();

	}
	
	public static void updateConstants() {
		
		
		rightDrive.setRampRate(ConstantsIO.kDriveVoltageRamp);
		leftDrive.setRampRate(ConstantsIO.kDriveVoltageRamp);
		driveTrain.updateConstants();
		shooter.updateConstants();
		intake.updateConstants();
		
	}

}
