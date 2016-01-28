package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.com.kauailabs.nav6.frc.IMUAdvanced;
import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.subsystems.DriveTrain;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.VictorSP;

public class Hardware {
	// Subsystems
	public static PowerDistributionPanel pdp = new PowerDistributionPanel();
//	public static CameraServer camServer;

	// Speed Controllers
	static int [] leftDrivePortsPWM = {1,2,3};
	static int [] leftDriveSlotsPDP = {1,2,3};
	static VictorSP [] leftDriveVictorSPs = {
			new VictorSP(leftDrivePortsPWM[0]), 
			new VictorSP(leftDrivePortsPWM[1]),
			new VictorSP(leftDrivePortsPWM[2])};
	
	public static SpeedControllerWrapper leftDrive = 
			new SpeedControllerWrapper(leftDriveVictorSPs,leftDriveSlotsPDP);

	
	static int [] rightDrivePortsPWM = {1,2,3};
	static int [] rightDriveSlotsPDP = {1,2,3};
	static VictorSP [] rightDriveVictorSPs = {
			new VictorSP(rightDrivePortsPWM[0]), 
			new VictorSP(rightDrivePortsPWM[1]),
			new VictorSP(rightDrivePortsPWM[2])};
	
	public static SpeedControllerWrapper rightDrive = 
			new SpeedControllerWrapper(rightDriveVictorSPs,rightDriveSlotsPDP);

	// Solenoids

	// Sensors
	public static Encoder leftDriveEnc = new Encoder(0,1); 
	public static Encoder rightDriveEnc = new Encoder(2,3);
	
	public static IMUAdvanced imu;
	
	// Sequences && Auto
	public static Sequencer autoSequence;

	// Compressor
	public static Relay compressorSpike;
	
	public static DriveTrain driveTrain = new DriveTrain(false);

}
