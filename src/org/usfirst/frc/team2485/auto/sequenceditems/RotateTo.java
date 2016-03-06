package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

public class RotateTo implements SequencedItem {
	
	private double angle; 
	private boolean finished; 
	
	private double timeout; 
	 
	public RotateTo(double angle, double timeout) {
		this.angle = angle;
		this.timeout = timeout;
	}
	
	public RotateTo(double angle) {
		this(angle, 3); 
	}
	
	@Override
	public void run() {
		finished = Hardware.driveTrain.rotateTo(angle);
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout; 
	}
}
