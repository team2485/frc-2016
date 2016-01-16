package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.com.kauailabs.nav6.frc.IMUAdvanced;
import org.usfirst.frc.team2485.auto.Sequencer;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;

public class Hardware {
	// Subsystems

	// private CameraServer camServer;

	// Speed Controllers


	// Solenoids

	// Sensors
	private Encoder leftEnc, rightEnc;

	private IMUAdvanced imu;
	
	// Sequences && Auto
	private Sequencer autoSequence;

	// Compressor
	private Relay compressorSpike;

}
