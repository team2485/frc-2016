package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.subsystems.BoulderStager;
import org.usfirst.frc.team2485.subsystems.DriveTrain;
import org.usfirst.frc.team2485.subsystems.Intake;
import org.usfirst.frc.team2485.subsystems.Shooter;
import org.usfirst.frc.team2485.util.Battery;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;

import com.kauailabs.navx.frc.AHRS;
import com.ni.vision.NIVision.LegFeature;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.VictorSP;

public class Hardware {
	
	public static Battery battery;
	
	//Speed Controllers
	public static VictorSP[] rightDriveVictorSPs, leftDriveVictorSPs;

	public static SpeedControllerWrapper rightDrive, leftDrive;

	public static CANTalon leftShooterMotor, rightShooterMotor;
	
	public static VictorSP []  intakeArmVictorSP;
	public static SpeedControllerWrapper intakeArm;
	
	public static VictorSP lateralVictorSP;
	public static VictorSP intakeVictorSP;
	public static VictorSP[] rollerVictorSPs; 
	public static SpeedControllerWrapper rollers;

	
	// Solenoids
	public static Solenoid shooterHoodSolenoid1, shooterHoodSolenoid2, boulderStagerSolenoid1, boulderStagerSolenoid2;

	// Sensors
	public static Encoder leftDriveEnc, rightDriveEnc;
	public static AnalogPotentiometer intakePot;
	public static AHRS ahrs;

	// Sequences && Auto
	public static Sequencer autoSequence;

	// Compressor
	public static Relay compressorSpike;

	//Subsystems
	public static DriveTrain driveTrain;

	public static Shooter shooter;
	
	public static Intake intake;

	public static BoulderStager boulderStager;

	public static void init() {
		if (battery == null) {

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

			rightDrive = new SpeedControllerWrapper(
					rightDriveVictorSPs, Constants.kRightDrivePDP);

			leftDrive = new SpeedControllerWrapper(
					leftDriveVictorSPs, Constants.kLeftDrivePDP);
			
			rollerVictorSPs = new VictorSP[2];
			rollerVictorSPs[0] = lateralVictorSP = new VictorSP(Constants.kLateralRollerPWM);
			rollerVictorSPs[1] = intakeVictorSP = new VictorSP(Constants.kIntakeRollerPWM);
			int[] rollerPDPs = {Constants.kLateralRollerPDP, Constants.kIntakeRollerPDP};
			rollers = new SpeedControllerWrapper(rollerVictorSPs, rollerPDPs);
			
			intakeArmVictorSP = new VictorSP[2];
			intakeArmVictorSP[0] = new VictorSP(Constants.kIntakeArmPWM[0]);
			intakeArmVictorSP[1] = new VictorSP(Constants.kIntakeArmPWM[1]);
			intakeArm = new SpeedControllerWrapper(intakeArmVictorSP, Constants.kIntakeArmPDP);

			leftShooterMotor = new CANTalon(Constants.kLeftShooterCAN);
			rightShooterMotor = new CANTalon(Constants.kRightShooterCAN);

			shooterHoodSolenoid1 = new Solenoid(Constants.kShooterHoodSolenoid1Port);
			shooterHoodSolenoid2 = new Solenoid(Constants.kShooterHoodSolenoid2Port);
			
			boulderStagerSolenoid1 = new Solenoid(Constants.kBoulderStagerSolenoid1Port);
			boulderStagerSolenoid2 = new Solenoid(Constants.kBoulderStagerSolenoid2Port);

			leftDriveEnc = new Encoder(Constants.kLeftDriveEncoder[0], 
					Constants.kLeftDriveEncoder[1]);
			rightDriveEnc = new Encoder(Constants.kRightDriveEncoder[0], 
					Constants.kRightDriveEncoder[1]);
			
			intakePot = new AnalogPotentiometer(Constants.kIntakeArmPot);
			
			ahrs = new AHRS(SPI.Port.kMXP);
	
		}

		rightDrive.setInverted(false);
		rightDrive.setRampMode(true);
		rightDrive.setRampRate(ConstantsIO.kDriveVoltageRamp);

		leftDrive.setInverted(true);
		leftDrive.setRampMode(true);
		leftDrive.setRampRate(ConstantsIO.kDriveVoltageRamp);
		
		intakeArmVictorSP[0].setInverted(false);
		intakeArmVictorSP[1].setInverted(true);
		
		leftDriveEnc.setDistancePerPulse(2 * Math.PI
				* Constants.WHEEL_RADIUS_INCHES / 250);
		rightDriveEnc.setDistancePerPulse(2 * Math.PI
				* Constants.WHEEL_RADIUS_INCHES / 250);

		ahrs.zeroYaw();

		driveTrain = new DriveTrain(true);
		shooter = new Shooter();
		intake = new Intake();
		boulderStager = new BoulderStager();


	}

}
