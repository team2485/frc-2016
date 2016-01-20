package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.com.kauailabs.nav6.frc.IMUAdvanced;
import org.usfirst.frc.team2485.auto.Sequencer;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;

public class Hardware {
	// Subsystems
	public static PowerDistributionPanel pdp = new PowerDistributionPanel();
//	public static CameraServer camServer;

	// Speed Controllers


	// Solenoids

	// Sensors
	public static Encoder leftEnc, rightEnc;
	public static IMUAdvanced imu;
	
	// Sequences && Auto
	public static Sequencer autoSequence;

	// Compressor
	public static Relay compressorSpike;

}
