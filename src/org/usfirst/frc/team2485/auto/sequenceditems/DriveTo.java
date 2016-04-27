package org.usfirst.frc.team2485.auto.sequenceditems;
 

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 */
public class DriveTo implements SequencedItem {

	private double inches; 
	private double maxAbsOutput;
	
	private boolean finished, shouldSetStartAngle = true; 
	private double timeout; 
	private double startAngle;
	private double endAngle;
	
	private static final double DEFAULT_TIMEOUT = 4;
	
	public DriveTo(double inches, double timeout, double maxAbsOutput, double startAngle, double endAngle) {
		this.inches = inches;
		this.timeout = timeout;
		this.maxAbsOutput = maxAbsOutput;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		shouldSetStartAngle = false;
	}
	
	public DriveTo(double inches, double timeout, double maxAbsOutput, double angle) {
		this(inches, timeout, maxAbsOutput, angle, angle);
	}

	public DriveTo(double inches, double timeout, double maxAbsOutput) {
		
		this.inches = inches;
		this.timeout = timeout;
		this.maxAbsOutput = maxAbsOutput;
		shouldSetStartAngle = true;
		
	}
	public DriveTo(double inches, double timeout) {
		this(inches, timeout, 50);
	}
	
	public DriveTo(double inches) {
		this(inches, DEFAULT_TIMEOUT); 
	}
	
	@Override
	public void run() {
//		finished = Hardware.driveTrain.driveTo(inches, maxAbsOutput);
		if (shouldSetStartAngle) {
			shouldSetStartAngle = false;
			startAngle = Hardware.ahrs.getYaw();
			endAngle = startAngle;
		}
		finished = Hardware.driveTrain.driveToAndRotateTo(inches, startAngle, endAngle, maxAbsOutput);
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout; 
	}

}
