package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2485.robot.Constants;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.Loggable;

import edu.wpi.first.wpilibj.Ultrasonic;

public class BoulderDetector implements Loggable {

	private Ultrasonic sonic;

	private boolean stop;

	public BoulderDetector() {

		sonic = new Ultrasonic(Constants.kUltrasonicDIO[0], Constants.kUltrasonicDIO[1]);
		
		new TimingSystem().start();
	}

	public boolean hasBoulder() {
		return sonic.getRangeInches() < 10;
	}

	public void stopListeningForBalls() {
		stop = true;
	}

	private class TimingSystem extends Thread {

		private Timer stopTimer = null;

		@Override
		public void run() {

			while (!stop) {

				if (hasBoulder() && stopTimer == null) {

					stopTimer = new Timer();
					stopTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							Hardware.intake.stopRollers();
						}
					}, 250);
				}
			}
		}
	}

	@Override
	public Map<String, Object> getLogData() {
		
		Map<String, Object> logData = new HashMap<String, Object>();
		
		logData.put("Name", "BoulderDetector");
		logData.put("Has boulder?", hasBoulder());
		
		return logData;
	}
}