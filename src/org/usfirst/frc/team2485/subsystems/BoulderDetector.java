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
	private Timer shakeTimer = null;

	private boolean hasBoulder;

	private int numTimesBoulderDetected = 0, numTimesBoulderNotDetected = 0;
	private static final int MINIMUM_BOULDER_DETECTED_ITERATIONS = 5,
			MINIMUM_BOULDER_NOT_DETECTED_ITERATIONS = 5; // TODO tune value

	public BoulderDetector() {

		sonic = Hardware.sonic;

		new TimingSystem().start();
	}

	private boolean boulderDetected() {
		return sonic.getRangeInches() < 10;
	}

	/**
	 * Determines whether we have a boulder based on sonic sensor, may be
	 * delayed by 100 ms
	 * 
	 * @return true if we have a boulder
	 */
	public boolean hasBoulder() {
		return hasBoulder;
	}

	private class TimingSystem extends Thread {

		@Override
		public void run() {

			while (true) {

				if (boulderDetected()) {
					numTimesBoulderDetected++;
					numTimesBoulderNotDetected = 0;
				} else {
					numTimesBoulderDetected = 0;
					numTimesBoulderNotDetected++;
				}

				if (numTimesBoulderDetected > MINIMUM_BOULDER_DETECTED_ITERATIONS
						&& !hasBoulder) {

					hasBoulder = true;

					// Hardware.intake.setSetpoint(Intake.FULL_UP_POSITION,
					// false);
					Hardware.intake.stopRollers();

					if (Hardware.shooter.getSetpoint() == 0) {
						Hardware.boulderStager.setPosition(Position.SHOOTING);

						if (shakeTimer == null) {

							shakeTimer = new Timer();
							shakeTimer.schedule(new TimerTask() {

								@Override
								public void run() {
									Hardware.boulderStager
											.setPosition(Position.NEUTRAL);
									shakeTimer = null;
								}
							}, 500);

						}
					} else {
						Hardware.boulderStager.setPosition(Position.NEUTRAL);
					}

				} else if (numTimesBoulderNotDetected > MINIMUM_BOULDER_NOT_DETECTED_ITERATIONS
						&& hasBoulder) {
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