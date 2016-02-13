package org.usfirst.frc.team2485.auto.sequenceditems;
 

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;


public class DriveTo implements SequencedItem {

	private double inches; 
	private double maxAbsOutput;
	
	private boolean finished; 
	private double timeout; 
	 
	public DriveTo(double inches, double timeout, double maxAbsOutput) {
		this.inches = inches;
		this.timeout = timeout;
		this.maxAbsOutput = maxAbsOutput;
	}
	
	public DriveTo(double inches, double timeout) {
		this(inches, timeout, 1.0);
	}
	
	public DriveTo(double inches) {
		this(inches, 4); 
	}
	
	@Override
	public void run() {
		finished = Hardware.driveTrain.driveTo(inches, maxAbsOutput);
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout; 
	}

}
