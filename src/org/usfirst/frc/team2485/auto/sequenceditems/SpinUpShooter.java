package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

public class SpinUpShooter implements SequencedItem {
	
	private double rpm;
	private long startTime = -1;
	private boolean finished;
	
	public SpinUpShooter(double rpm) {
		this.rpm = rpm;
	}


	@Override
	public void run() {
		
		if (startTime == -1) {
			startTime = System.currentTimeMillis();
		}
		
		
		long runTime = System.currentTimeMillis() - startTime;
		
		//runs shooter backwards to unjam
		if (runTime < 250 && !Hardware.shooter.isPIDEnabled()) {
			Hardware.shooter.setPWM(-0.6);
		} else {
			Hardware.shooter.setTargetSpeed(rpm);
			finished = true;
		}
		
	}

	@Override
	public double duration() {
		return finished ? 0 : 0.3;
	}
}
