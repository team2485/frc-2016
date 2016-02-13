package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.subsystems.DriveTrain;
import org.usfirst.frc.team2485.subsystems.Shooter;
import org.usfirst.frc.team2485.util.Battery;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;

import com.kauailabs.navx.frc.AHRS;
import com.ni.vision.NIVision.LegFeature;

<<<<<<< HEAD
import edu.wpi.first.wpilibj.AnalogPotentiometer;
=======
>>>>>>> 7e251e2794125bcc4a15162e5f2764e6b3b22d07
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.VictorSP;

public class Hardware {
	// Subsystems
	public static Battery battery;
	// public static CameraServer camServer;

	// Speed Controllers
	// static VictorSP [] leftDriveVictorSPs = {
	// new VictorSP(ConstantsIO.kLeftDrivePWM[0]),
	// new VictorSP(ConstantsIO.kLeftDrivePWM[1]),
	// new VictorSP(ConstantsIO.kLeftDrivePWM[2])};

	// public static SpeedControllerWrapper leftDrive =
	// new SpeedControllerWrapper(leftDriveVictorSPs,
	// ConstantsIO.kLeftDrivePDP);

<<<<<<< HEAD
	public static AnalogPotentiometer intakePot;
	
=======
>>>>>>> 7e251e2794125bcc4a15162e5f2764e6b3b22d07
	public static VictorSP[] rightDriveVictorSPs, leftDriveVictorSPs;

	public static SpeedControllerWrapper rightDrive, leftDrive;

	public static CANTalon leftShooterMotor, rightShooterMotor;

<<<<<<< HEAD
	static int [] intakeArmPortsPWM = {1,2};
	
	static int [] intakeArmSlotsPDP = {1,2};
	
	static VictorSP []  intakeArmVictorSP= {
			new VictorSP(intakeArmPortsPWM[0]), 
			new VictorSP(intakeArmPortsPWM[1])};
	
	public static SpeedControllerWrapper intakeArm = 
			new SpeedControllerWrapper(intakeArmVictorSP,intakeArmSlotsPDP);
	
	public static SpeedControllerWrapper rollers;
	
	public static VictorSP lateral=new VictorSP(100);
	public static VictorSP intake=new VictorSP(100);
	public static VictorSP[] rollerVictorSPs;
	public static int[] rollerPDPs;
	
	// Solenoids
	public static Solenoid shooterHoodSolenoid1, shooterHoodSolenoid2, boulderStagerSolenoid1, boulderStagerSolenoid2;
=======
	// Solenoids
	public static Solenoid shooterHoodSolenoid1, shooterHoodSolenoid2;
>>>>>>> 7e251e2794125bcc4a15162e5f2764e6b3b22d07

	// Sensors
	public static Encoder leftDriveEnc, rightDriveEnc;

	public static AHRS ahrs;

	// Sequences && Auto
	public static Sequencer autoSequence;

	// Compressor
	public static Relay compressorSpike;

	public static DriveTrain driveTrain;

	public static Shooter shooter;

	public static void init() {
		if (battery == null) {

			battery = new Battery();
			
			intakePot = new AnalogPotentiometer(3);

			rightDriveVictorSPs = new VictorSP[3];
			rightDriveVictorSPs[0] = new VictorSP(ConstantsIO.kRightDrivePWM[0]);
			rightDriveVictorSPs[1] = new VictorSP(ConstantsIO.kRightDrivePWM[1]);
			rightDriveVictorSPs[2] = new VictorSP(ConstantsIO.kRightDrivePWM[2]);

			leftDriveVictorSPs = new VictorSP[3];
			leftDriveVictorSPs[0] = new VictorSP(ConstantsIO.kLeftDrivePWM[0]);
			leftDriveVictorSPs[1] = new VictorSP(ConstantsIO.kLeftDrivePWM[1]);
			leftDriveVictorSPs[2] = new VictorSP(ConstantsIO.kLeftDrivePWM[2]);

			rightDrive = new SpeedControllerWrapper(
					rightDriveVictorSPs, ConstantsIO.kRightDrivePDP);

			leftDrive = new SpeedControllerWrapper(
					leftDriveVictorSPs, ConstantsIO.kLeftDrivePDP);

			rightDriveVictorSPs[0].setInverted(false);
			rightDriveVictorSPs[1].setInverted(false);
			rightDriveVictorSPs[2].setInverted(false);

			leftDriveVictorSPs[0].setInverted(true);
			leftDriveVictorSPs[1].setInverted(true);
			leftDriveVictorSPs[2].setInverted(true);
			rightDrive.setInverted(false);
			rightDrive.setRampMode(true);
			rightDrive.setRampRate(ConstantsIO.kDriveVoltageRamp);

			leftDrive.setInverted(true);
			leftDrive.setRampMode(true);
			leftDrive.setRampRate(ConstantsIO.kDriveVoltageRamp);

			leftShooterMotor = new CANTalon(ConstantsIO.kLeftShooterCAN);
			rightShooterMotor = new CANTalon(ConstantsIO.kRightShooterCAN);

			shooterHoodSolenoid1 = new Solenoid(ConstantsIO.kShooterHoodSolenoid1Port);
			shooterHoodSolenoid2 = new Solenoid(ConstantsIO.kShooterHoodSolenoid2Port);
			
			boulderStagerSolenoid1 = new Solenoid(1738);
			boulderStagerSolenoid2 = new Solenoid(1739);

			leftDriveEnc = new Encoder(ConstantsIO.kLeftDriveEncoder[0], 
					ConstantsIO.kLeftDriveEncoder[1]);
			rightDriveEnc = new Encoder(ConstantsIO.kRightDriveEncoder[0], 
					ConstantsIO.kRightDriveEncoder[1]);

			ahrs = new AHRS(SPI.Port.kMXP);
			
			
			rollerVictorSPs[0] = lateral;
			rollerVictorSPs[1] = intake;
			rollerPDPs[0] = 0;
			rollerPDPs[1] = 1;
			rollers = new SpeedControllerWrapper(rollerVictorSPs, rollerPDPs);


		}

		leftDriveEnc.setDistancePerPulse(2 * Math.PI
				* ConstantsIO.WHEEL_RADIUS_INCHES / 250);
		rightDriveEnc.setDistancePerPulse(2 * Math.PI
				* ConstantsIO.WHEEL_RADIUS_INCHES / 250);

		ahrs.zeroYaw();

		driveTrain = new DriveTrain(true);
		shooter = new Shooter();


	}

}
