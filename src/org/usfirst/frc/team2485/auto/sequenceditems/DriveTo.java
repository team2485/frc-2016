package org.usfirst.frc.team2485.auto.sequenceditems;
 

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;


public class DriveTo implements SequencedItem {

	private double inches; 
	private boolean finished; 
	
	private double timeout; 
	 
	public DriveTo(double inches, double timeout) {
		this.inches = inches;
		this.timeout = timeout;
	}
	
	public DriveTo(double inches) {
		this(inches, 4); 
	}
	
	@Override
	public void run() {
		finished = Hardware.driveTrain.driveTo(inches);
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout; 
	}

	@Override
	public void finish() { }

}
