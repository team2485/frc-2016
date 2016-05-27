package org.usfirst.frc.team2485.auto.sequenceditems;

import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.GRIPReciever;
import org.usfirst.frc.team2485.util.GRIPReciever.GRIPTargetNotFoundException;

/**
 * @author Nicholas Contreras
 */

public class AlignToTower implements SequencedItem {

	private double timeout;
	private boolean finished = false;
	private boolean firstTime = true;

	// the angle, according to the AHRS, that we should be pointing towards
	// to be on target
	private double angleTarget;

	private Timer refreshTimer;

	private boolean curTurnDone = true;

	public AlignToTower(double timeout) {
		this.timeout = timeout;
	}
	
	public AlignToTower() {
		this(3.0);
	}

	@Override
	public void run() {

		if (firstTime) {
			angleTarget = Hardware.ahrs.getYaw();
			firstTime = false;
		}

		if (curTurnDone) {
						
			if (refreshTimer == null) {
				refreshTimer = new Timer();
				refreshTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						try {

							double angleOffset = GRIPReciever.getAngle();

							angleTarget = Hardware.ahrs.getYaw() + angleOffset;
							refreshTimer = null;
							curTurnDone = false;

							if (Math.abs(angleOffset) <= (GRIPReciever.isLongShot() ? 1 : 2)) {
								finished = true;
							}

						} catch (GRIPTargetNotFoundException e) {
							e.printStackTrace();
						}
					}
				}, 250);
			}
		} else if (!finished) {
			curTurnDone = Hardware.driveTrain.rotateTo(angleTarget, 0.7);
		}
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout;
	}
}
