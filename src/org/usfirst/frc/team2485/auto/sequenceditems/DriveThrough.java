package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

public class DriveThrough implements SequencedItem {

	private double setpoint, timeout, finishedAt, maxAbsVelocity, startAngle;
	private boolean shouldSetStartAngle = true;
	
	public DriveThrough(double setpoint, double timeout, double finishedAt, double maxAbsVelocity, double angle) {
		this.setpoint = setpoint;
		this.timeout = timeout;
		this.finishedAt = finishedAt;
		this.maxAbsVelocity = maxAbsVelocity;
		this.startAngle = angle;
		shouldSetStartAngle = false;
	}
	
	public DriveThrough(double setpoint, double timeout, double finishedAt, double maxAbsVelocity) {
		this.setpoint = setpoint;
		this.timeout = timeout;
		this.finishedAt = finishedAt;
		this.maxAbsVelocity = maxAbsVelocity;
		shouldSetStartAngle = true;
	}
	
	@Override
	public void run() {
		if (shouldSetStartAngle) {
			shouldSetStartAngle = false;
			startAngle = Hardware.ahrs.getYaw();
		}
		Hardware.driveTrain.driveToAndRotateTo(setpoint, startAngle, startAngle, maxAbsVelocity);

	}

	@Override
	public double duration() {
		double dist = (Hardware.leftDriveEnc.getDistance() + Hardware.rightDriveEnc.getDistance()) / 2;
		return Math.abs(dist - finishedAt) < 2.0 ? 0.0 : timeout;
	}

}
