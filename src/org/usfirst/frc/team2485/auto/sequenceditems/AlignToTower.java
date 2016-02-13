package org.usfirst.frc.team2485.auto.sequenceditems;

import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.GRIPReciever;
import org.usfirst.frc.team2485.util.GRIPReciever.GRIPTargetNotFoundException;

/**
 * 
 * @author Nicholas Contreras
 *
 */

public class AlignToTower implements SequencedItem {

	private int timeout = 5;
	private boolean finished = false;
	private boolean firstTime = true;

	//the angle, according to the AHRS, that we should be pointing towards
	//to be on-target
	private double angleTarget;

	private Timer refreshTimer;

	public AlignToTower() {
		
	}

	@Override
	public void run() {

		if(firstTime) {
			angleTarget = Hardware.ahrs.getYaw();
			firstTime = false;
		}
		double currentAngle = Hardware.ahrs.getYaw();

		if (Math.abs(angleTarget - currentAngle) <= 0.5) {
			if (refreshTimer == null) {
				refreshTimer = new Timer();
				refreshTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						try {
							angleTarget = Hardware.ahrs.getYaw() + GRIPReciever.getAngle();
							refreshTimer = null;

							if (Math.abs(angleTarget) <= 0.5) {
								//GRIPReciever.resetCameraSettings();
								finished = true;
							}

						} catch (GRIPTargetNotFoundException e) {
							e.printStackTrace();
						}
					}
				}, 250);
			}
		} else {
			Hardware.driveTrain.rotateTo(angleTarget);
		}
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout;
	}
}
