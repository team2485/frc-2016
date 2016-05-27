package org.usfirst.frc.team2485.auto.sequenceditems;
 

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Hardware;

/**
 * @author Jeremy McCulloch
 */
public class DriveTo implements SequencedItem {

	private double inches; 
	private double maxSpeed;
	
	private boolean finished, shouldSetStartAngle = true; 
	private double timeout; 
	private double startAngle;
	private double endAngle;
	
	private static final double DEFAULT_TIMEOUT = 4;
	
	/**
	 * Closed loop driving on a curve
	 * @param setpoint where to approach
	 * @param timeout how long until it times out
	 * @param maxSpeed max speed of the center of the robot in inches / second
	 * @param startAngle heading at beginning of curve
	 * @param endAngle desired heading at end of curve
	 */
	public DriveTo(double inches, double timeout, double maxSpeed, double startAngle, double endAngle) {
		this.inches = inches;
		this.timeout = timeout;
		this.maxSpeed = maxSpeed;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		shouldSetStartAngle = false;
	}
	
	/**
	 * Closed loop driving in a straight path
	 * @param setpoint where to approach
	 * @param timeout how long until it times out
	 * @param maxSpeed max speed of the center of the robot in inches / second
	 * @param angle heading to maintain
	 */
	public DriveTo(double inches, double timeout, double maxAbsOutput, double angle) {
		this(inches, timeout, maxAbsOutput, angle, angle);
	}

	/**
	 * Closed loop driving in a straight path
	 * @param setpoint where to approach
	 * @param timeout how long until it times out
	 * @param maxSpeed max speed of the center of the robot in inches / second
	 */
	public DriveTo(double inches, double timeout, double maxAbsOutput) {
		
		this.inches = inches;
		this.timeout = timeout;
		this.maxSpeed = maxAbsOutput;
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
		if (shouldSetStartAngle) {
			shouldSetStartAngle = false;
			startAngle = Hardware.ahrs.getYaw();
			endAngle = startAngle;
		}
		finished = Hardware.driveTrain.driveToAndRotateTo(inches, startAngle, endAngle, maxSpeed);
	}

	@Override
	public double duration() {
		return finished ? 0 : timeout; 
	}

}
