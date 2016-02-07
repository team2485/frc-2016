package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.subsystems.DriveTrain;
import org.usfirst.frc.team2485.util.Battery;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;

public class Hardware {
	// Subsystems
	public static Battery battery = new Battery();
//	public static CameraServer camServer;

	// Speed Controllers
	static VictorSP [] leftDriveVictorSPs = {
			new VictorSP(ConstantsIO.kLeftDrivePWM[0]), 
			new VictorSP(ConstantsIO.kLeftDrivePWM[1]),
			new VictorSP(ConstantsIO.kLeftDrivePWM[2])};
	
	
	public static SpeedControllerWrapper leftDrive = 
			new SpeedControllerWrapper(leftDriveVictorSPs, ConstantsIO.kLeftDrivePDP);

	
	static VictorSP [] rightDriveVictorSPs = {
			new VictorSP(ConstantsIO.kRightDrivePWM[0]), 
			new VictorSP(ConstantsIO.kRightDrivePWM[1]),
			new VictorSP(ConstantsIO.kRightDrivePWM[2])};
	
	public static SpeedControllerWrapper rightDrive = 
			new SpeedControllerWrapper(rightDriveVictorSPs, ConstantsIO.kRightDrivePDP);

	// Solenoids

	// Sensors
	public static Encoder leftDriveEnc = 
			new Encoder(ConstantsIO.kLeftDriveEncoder[0], ConstantsIO.kLeftDriveEncoder[1]); 
	public static Encoder rightDriveEnc = 
			new Encoder(ConstantsIO.kRightDriveEncoder[0], ConstantsIO.kRightDriveEncoder[1]);
	
	public static AHRS ahrs = new AHRS(SPI.Port.kMXP);
	
	// Sequences && Auto
	public static Sequencer autoSequence;

	// Compressor
	public static Relay compressorSpike;
	
	public static DriveTrain driveTrain = new DriveTrain(true);
	
	public Hardware() {
		
		rightDriveVictorSPs[0].setInverted(false);
		rightDriveVictorSPs[1].setInverted(false);
		rightDriveVictorSPs[2].setInverted(false);
		
		leftDriveVictorSPs[0].setInverted(true);
		leftDriveVictorSPs[1].setInverted(true);
		leftDriveVictorSPs[2].setInverted(true);
		
		leftDriveEnc.setDistancePerPulse(2 * Math.PI * ConstantsIO.WHEEL_RADIUS_INCHES / 250);
		rightDriveEnc.setDistancePerPulse(2 * Math.PI * ConstantsIO.WHEEL_RADIUS_INCHES / 250);

		ahrs.zeroYaw();
		
	}

}
