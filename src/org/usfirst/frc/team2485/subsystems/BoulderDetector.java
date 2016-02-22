package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2485.robot.Constants;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;
import org.usfirst.frc.team2485.util.Loggable;

import edu.wpi.first.wpilibj.Ultrasonic;

public class BoulderDetector implements Loggable {

	private Ultrasonic sonic;

	private Timer stopTimer = null;

	private boolean hasBoulder;
	
	private int numTimesBoulderDetected = 0;
	private static final int MINIMUM_BOULDER_DETECTED_ITERATIONS = 10;

	
	public BoulderDetector() {

		sonic = Hardware.sonic;

		new TimingSystem().start();
	}

	public boolean boulderDetected() {
		return sonic.getRangeInches() < 10;
	}

	private class TimingSystem extends Thread {

		@Override
		public void run() {

			while (true) {

			
				if (boulderDetected()) {
					numTimesBoulderDetected++;
				} else {
					hasBoulder = false;
					numTimesBoulderDetected = 0;
				}
				
				if (numTimesBoulderDetected > MINIMUM_BOULDER_DETECTED_ITERATIONS && !hasBoulder) {
					
					hasBoulder = true;
					Hardware.intake.setSetpoint(Intake.FULL_UP_POSITION, false);
					Hardware.boulderStager.setPosition(Position.NEUTRAL);
					System.out.println("BoulderDetector: setting position to FULL_UP_POSITION");
					
				}

//				if (boulderDetected() && !hasBoulder) {
//
//					hasBoulder = true;
//
//					stopTimer = new Timer();
//					stopTimer.schedule(new TimerTask() {
//
//						@Override
//						public void run() {
//							Hardware.intake.setSetpoint(Intake.LOW_NO_INTAKE, false);
//							Hardware.boulderStager.setPosition(Position.NEUTRAL);
//							System.out.println("BoulderDetector: setting position to LOW_NO_INTAKE");
//							stopTimer = null;
//						}
//					}, 250);
//				} else {
//					hasBoulder = false;
//					numTimesBoulderDetected = 0;
//				}
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public Map<String, Object> getLogData() {

		Map<String, Object> logData = new HashMap<String, Object>();

		logData.put("Name", "BoulderDetector");
		logData.put("Detected Boulder", boulderDetected());
		logData.put("Has Boulder", hasBoulder);
		logData.put("Timer", stopTimer);
		logData.put("Distance", sonic.getRangeInches());

		return logData;
	}
}