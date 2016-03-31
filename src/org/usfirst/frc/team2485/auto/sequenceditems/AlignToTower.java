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
	// to be on-target
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
			
			System.out.println("AlignToTower: Current Turn done, recalulating");
			
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

							if (Math.abs(angleOffset) <= 1) {
//								System.out
//										.println("AlignToTower: New angle is less than 1");
								finished = true;
							}

						} catch (GRIPTargetNotFoundException e) {
							e.printStackTrace();
						}
					}
				}, 250);
			}
		} else {
			curTurnDone = Hardware.driveTrain.rotateTo(angleTarget);

//			System.out.println("AlignToTower: Target Angle: " + angleTarget
//					+ " Current Angle: " + Hardware.ahrs.getYaw());
		}
	}

	@Override
	public double duration() {

//		if (finished)
//			System.out.println("Finished is true in AlignToTower");
		return finished ? 0 : timeout;
	}
}
