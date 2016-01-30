package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.subsystems.DriveTrain;
import org.usfirst.frc.team2485.util.Battery;
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

	private static double WHEEL_RADIUS_INCHES = 1.53;

	// Speed Controllers
	static int [] leftDrivePortsPWM = {4,3,2};
	static int [] leftDriveSlotsPDP = {4,3,2};
	static VictorSP [] leftDriveVictorSPs = {
			new VictorSP(leftDrivePortsPWM[0]), 
			new VictorSP(leftDrivePortsPWM[1]),
			new VictorSP(leftDrivePortsPWM[2])};
	
	
	public static SpeedControllerWrapper leftDrive = 
			new SpeedControllerWrapper(leftDriveVictorSPs,leftDriveSlotsPDP);

	static int [] rightDrivePortsPWM = {9,8,7};
	static int [] rightDriveSlotsPDP = {9,10,11};
	static VictorSP [] rightDriveVictorSPs = {
			new VictorSP(rightDrivePortsPWM[0]), 
			new VictorSP(rightDrivePortsPWM[1]),
			new VictorSP(rightDrivePortsPWM[2])};
	
	public static SpeedControllerWrapper rightDrive = 
			new SpeedControllerWrapper(rightDriveVictorSPs,rightDriveSlotsPDP);

	// Solenoids

	// Sensors
	public static Encoder leftDriveEnc = new Encoder(8, 9); 
	public static Encoder rightDriveEnc = new Encoder(6, 7);
	
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
		
		leftDriveEnc.setDistancePerPulse(2 * Math.PI * WHEEL_RADIUS_INCHES / 250);
		rightDriveEnc.setDistancePerPulse(2 * Math.PI * WHEEL_RADIUS_INCHES / 250);

		ahrs.zeroYaw();
	}

}
