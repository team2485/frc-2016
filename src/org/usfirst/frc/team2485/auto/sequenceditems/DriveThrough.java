package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 * Closed loop driving like driveTo but does not attempt to stop before it finishes
 */
public class DriveThrough implements SequencedItem {

	private double setpoint, timeout, finishedAt, maxSpeed, startAngle;
	private boolean shouldSetStartAngle = true;
	
	/**
	 * Closed loop driving like driveTo but does not attempt to stop before it finishes
	 * @param setpoint where to try to stop (>finished at)
	 * @param timeout how long until it times out
	 * @param finishedAt how far to drive before sequenceditem finished
	 * @param maxSpeed max speed of the center of the robot in inches / second
	 * @param angle heading to mainta
	 */
	public DriveThrough(double setpoint, double timeout, double finishedAt, double maxSpeed, double angle) {
		this.setpoint = setpoint;
		this.timeout = timeout;
		this.finishedAt = finishedAt;
		this.maxSpeed = maxSpeed;
		this.startAngle = angle;
		shouldSetStartAngle = false;
	}
	
	/**
	 * Closed loop driving like driveTo but does not attempt to stop before it finishes. Maintains current heading.
	 * @param setpoint where to try to stop (>finished at)
	 * @param timeout how long until it times out
	 * @param finishedAt how far to drive before sequenceditem finished
	 * @param maxSpeed max speed of the center of the robot in inches / second
	 */
	public DriveThrough(double setpoint, double timeout, double finishedAt, double maxAbsVelocity) {
		this.setpoint = setpoint;
		this.timeout = timeout;
		this.finishedAt = finishedAt;
		this.maxSpeed = maxAbsVelocity;
		shouldSetStartAngle = true;
	}
	
	@Override
	public void run() {
		if (shouldSetStartAngle) {
			shouldSetStartAngle = false;
			startAngle = Hardware.ahrs.getYaw();
		}
		Hardware.driveTrain.driveToAndRotateTo(setpoint, startAngle, startAngle, maxSpeed);

	}

	@Override
	public double duration() {
		double dist = (Hardware.leftDriveEnc.getDistance() + Hardware.rightDriveEnc.getDistance()) / 2;
		return Math.abs(dist - finishedAt) < 2.0 ? 0.0 : timeout;
	}

}
