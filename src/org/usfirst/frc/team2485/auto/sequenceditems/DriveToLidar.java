package org.usfirst.frc.team2485.auto.sequenceditems;
 

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;


/**
 * @author Jeremy McCulloch
 */
public class DriveToLidar implements SequencedItem {

	private double inchesToWall; 
	private double maxAbsOutput;
	
	private boolean finished; 
	private double timeout; 
	
	private static final double DEFAULT_TIMEOUT = 4;
	 
	
	public DriveToLidar(double inchesToWall, double timeout, double maxAbsOutput) {
		
		this.inchesToWall = inchesToWall;
		this.timeout = timeout;
		this.maxAbsOutput = maxAbsOutput;
		
	}
	
	public DriveToLidar(double inchesToObject, double timeout) {
		this(inchesToObject, timeout, 1.0);
	}
	
	public DriveToLidar(double inchesToWall) {
		this(inchesToWall, DEFAULT_TIMEOUT); 
	}
	
	@Override
	public void run() {
		finished = Hardware.driveTrain.driveToLidar(inchesToWall, maxAbsOutput);
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout; 
	}

}
