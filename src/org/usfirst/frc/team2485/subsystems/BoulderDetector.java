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

	private Timer stopTimer = null;

	private boolean hasBoulder;

	public BoulderDetector() {

		sonic = Hardware.sonic;

		new TimingSystem().start();
	}

	public boolean boulderDetected() {
		return sonic.getRangeInches() < 10;
	}

	// public void stopListeningForBalls() {
	// stop = true;
	// }

	private class TimingSystem extends Thread {

		@Override
		public void run() {

			while (!stop) {

				if (boulderDetected() && !hasBoulder) {

					hasBoulder = true;

					stopTimer = new Timer();
					stopTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							Hardware.intake.stopRollers();
							stopTimer = null;
						}
					}, 250);
				} else if (!boulderDetected()) {
					hasBoulder = false;
				}

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