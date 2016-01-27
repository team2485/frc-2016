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
	static int [] leftDrivePorts = {1,2,3};
	static VictorSP [] leftDriveVictorSPs = {
			new VictorSP(leftDrivePorts[0]), 
			new VictorSP(leftDrivePorts[1]),
			new VictorSP(leftDrivePorts[2])};
	
	public static SpeedControllerWrapper leftDrive = 
			new SpeedControllerWrapper(leftDriveVictorSPs,leftDrivePorts);

	
	static int [] rightDrivePorts = {4,5,6};
	static VictorSP [] rightDriveVictorSPs = {
			new VictorSP(rightDrivePorts[0]), 
			new VictorSP(rightDrivePorts[1]),
			new VictorSP(rightDrivePorts[2])};
	
	public static SpeedControllerWrapper rightDrive = 
			new SpeedControllerWrapper(leftDriveVictorSPs,leftDrivePorts);
	// Solenoids

	// Sensors
	public static Encoder leftEnc = new Encoder(0,1); 
	public static Encoder rightEnc = new Encoder(2,3);
	
	public static IMUAdvanced imu;
	
	// Sequences && Auto
	public static Sequencer autoSequence;

	// Compressor
	public static Relay compressorSpike;
	
	public static DriveTrain driveTrain = new DriveTrain(false);

}
