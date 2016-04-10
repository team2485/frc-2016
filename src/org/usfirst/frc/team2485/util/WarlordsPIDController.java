package org.usfirst.frc.team2485.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * Class that implements a standard PID Control Loop without the abnormalities of WPI's PIDController class
 * @author Jeremy McCulloch
 */
public class WarlordsPIDController {
	
	private double kP, kI, kD, kF;
	private PIDSource source;
	private PIDOutput output;
	
	private double totalError = 0, lastError;
	private double setpoint;
	private double sensorVal, result;
	
	private boolean enabled = false;
	
	private double minOutput = -1, maxOutput = 1;
	private double minITerm = 0, maxITerm = 0; // if min >= max then integral term is not clamped
	
	private double percentTolerance = 0.0, absoluteTolerance = 0.0;
	private boolean usesPercentTolerance = false;
	
	private Queue<Double> errorBuffer;
	private int bufferLength;
	private static final int DEFAULT_BUFFER_LENGTH = 1;
	
	private double minInput, maxInput;
	private boolean continuous;
	
	private long period;
	private static final long DEFAULT_PERIOD = 10;
	
	private Timer pidTimer;
	
	/**
	 * 
	 * @param kP proportional term, multiplied by the current error
	 * @param kI integral term, multiplied by the total (sum) error
	 * @param kD derivative term, multiplied by the change of the error
	 * @param kF feedforward term, multiplied by the setpoint, (usually) only used in rate control
	 * @param source input device / sensor used to monitor progress towards setpoint
	 * @param output output device / motor  used to approach setpoint
	 * @param period how often PID calculation is done (millis)
	 * @param bufferLength number of values used to calculate averageError
	 */
	public WarlordsPIDController(double kP, double kI, double kD, double kF, 
			PIDSource source, PIDOutput output, long period, int bufferLength) {
		
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
		this.kF = kF;
		
		this.source = source;
		this.output = output;
		this.period = period;
		
		pidTimer = new Timer();
		pidTimer.schedule(new PIDTask(), 0, period);
		
		this.bufferLength = bufferLength;
		this.errorBuffer = new LinkedList<Double>();
	}
	
	
	/**
	 * 
	 * @param kP proportional term, multiplied by the current error
	 * @param kI integral term, multiplied by the total (sum) error
	 * @param kD derivative term, multiplied by the change of the error
	 * @param source input device / sensor used to monitor progress towards setpoint
	 * @param output output device / motor  used to approach setpoint
	 * @param period how often PID calculation is done (millis)
	 * @param bufferLength number of values used to calculate averageError
	 */
	public WarlordsPIDController(double kP, double kI, double kD, 
			PIDSource source, PIDOutput output, long period, int bufferLength) {
		this(kP, kI, kD, 0, source, output, period, bufferLength);
	}
	
	/**
	 * Constructor that uses default period
	 * @param kP proportional term, multiplied by the current error
	 * @param kI integral term, multiplied by the total (sum) error
	 * @param kD derivative term, multiplied by the change of the error
	 * @param kF feedforward term, multiplied by the setpoint, (usually) only used in rate control
	 * @param source input device / sensor used to monitor progress towards setpoint
	 * @param output output device / motor  used to approach setpoint
	 * @param period how often PID calculation is done (millis)
	 */
	public WarlordsPIDController(double kP, double kI, double kD, double kF, 
			PIDSource source, PIDOutput output, long period) {
		this(kP, kI, kD, kF, source, output, period, DEFAULT_BUFFER_LENGTH);
	}
	
	/**
	 * Constructor that uses default period
	 * @param kP proportional term, multiplied by the current error
	 * @param kI integral term, multiplied by the total (sum) error
	 * @param kD derivative term, multiplied by the change of the error
	 * @param source input device / sensor used to monitor progress towards setpoint
	 * @param output output device / motor  used to approach setpoint
	 * @param period how often PID calculation is done (millis)
	 */
	public WarlordsPIDController(double kP, double kI, double kD, 
			PIDSource source, PIDOutput output, long period) {
		this(kP, kI, kD, source, output, period, DEFAULT_BUFFER_LENGTH);
	}
	
	/**
	 * Constructor that uses default period and bufferlength
	 * @param kP proportional term, multiplied by the current error
	 * @param kI integral term, multiplied by the total (sum) error
	 * @param kD derivative term, multiplied by the change of the error
	 * @param kF feedforward term, multiplied by the setpoint, (usually) only used in rate control
	 * @param source input device / sensor used to monitor progress towards setpoint
	 * @param output output device / motor  used to approach setpoint
	 */
	public WarlordsPIDController(double kP, double kI, double kD, double kF, 
			PIDSource source, PIDOutput output) {
		this(kP, kI, kD, kF, source, output, DEFAULT_PERIOD);
	}
	
	/**
	 * Constructor that uses default period and bufferlength
	 * @param kP proportional term, multiplied by the current error
	 * @param kI integral term, multiplied by the total (sum) error
	 * @param kD derivative term, multiplied by the change of the error
	 * @param source input device / sensor used to monitor progress towards setpoint
	 * @param output output device / motor  used to approach setpoint
	 */
	public WarlordsPIDController(double kP, double kI, double kD, 
			PIDSource source, PIDOutput output) {
		this(kP, kI, kD, source, output, DEFAULT_PERIOD);
	}
	
	/**
	 * @return time between PID calculations (millis)
	 */
	public long getPeriod() {
		return period;
	}
	
	/**
	 * @return true if PID is currently controlling the output motor
	 */
	public boolean isEnabled() {
		return enabled;
	} 
	
	/**
	 * @param enabled true to make PID start controlling the output motor
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Sets range for the output values calculated by the PID Controller
	 * @param minOutput minimum value to set the output motor to 
	 * @param maxOutput maximum value to set the output motor to 
	 */
	public void setOutputRange(double minOutput, double maxOutput) {
		this.minOutput = minOutput;
		this.maxOutput = maxOutput;
	}
	
	/**
	 * Clamps the integral term between minITerm and maxITerm, does not clamp if min >= max
	 * @param minITerm min value of integral term
	 * @param maxITerm max value of integral term
	 */
	public void setIZone(double minITerm, double maxITerm) {
		this.minITerm = minITerm;
		this.maxITerm = maxITerm;
	}
	
	/**
	 * Sets input range to be used in continuous mode
	 * @param minInput minimum value of sensor and setpoint
	 * @param maxInput maximum value of sensor and setpoint
	 */
	public void setInputRange(double minInput, double maxInput) {
		this.minInput = minInput;
		this.maxInput = maxInput;
	}
	
	/**
	 * Sets whether sensor loops from minInput to maxInput
	 * @param continuous true if loops
	 */
	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}
	
	/**
	 * Returns whether sensor loops from minInput to maxInput
	 * @return true if loops
	 */
	public boolean isContinuous() {
		return continuous;
	}
	
	/**
	 * Sets setpoint and clears history of errors
	 * @param setpoint new setpoint 
	 */
	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
		errorBuffer = new LinkedList<Double>();
	}
	
	/**
	 * @return error as calculated by the PID control loop
	 */
	public double getError() {
		return setpoint - source.pidGet();
	}
	
	/**
	 * @param bufferLength number of values used to calculate averageError
	 */
	public void setBufferLength(int bufferLength) {
		this.bufferLength = bufferLength;
	}
	
	/**
	 * @return number of values used to calculate averageError
	 */
	public double getBufferLength() {
		return bufferLength;
	}
	
	/**
	 * @return average of the last bufferlength errors, or fewer if not enough are available
	 */
	public double getAvgError() {
		double sum = 0;
		for (Iterator<Double> iterator = errorBuffer.iterator(); iterator.hasNext();) {
			sum += (double) iterator.next();
		}
		return sum / errorBuffer.size();
	}
	
	/**
	 * @param tolerance considered on target when within tolerance of setpoint
	 */
	public void setAbsoluteTolerance(double tolerance) {
		this.absoluteTolerance = tolerance; 
		usesPercentTolerance = false;
	}
	
	/**
	 * @param tolerance considered on target when within tolerance*setpoint of setpoint
	 */
	public void setPercentTolerance(double tolerance) {
		this.percentTolerance = tolerance;
		usesPercentTolerance = true;
	}
	
	/**
	 * Compares the average error to the specified tolerance
	 * @return true if within specified tolerance of setpoint
	 */
	public boolean isOnTarget() {
		if (usesPercentTolerance) {
			return Math.abs(getAvgError()) < setpoint * percentTolerance;
		} else {
			return Math.abs(getAvgError()) < absoluteTolerance;
		}
	}
	
	/**
	 * Frees all resources related to PID calculations
	 */
	public void free() {
		pidTimer.cancel();
		pidTimer = null;
		source = null;
		output = null;
	}
	
	/**
	 * Calculates output based on sensorVal but does not read from source or write to output directly
	 */
	private synchronized void calculate() {
		
		double error = setpoint - sensorVal;
		while (continuous && Math.abs(error) > (maxInput - minInput) / 2) {
			if (error > 0) {
				error -= maxInput - minInput;
			} else {
				error += maxInput - minInput;
			}
			
		}
		
		double deltaError = error - lastError;
		totalError += error;
		
		if (kI != 0 && maxITerm > minITerm) {
			if (totalError*kI < minITerm) {
				totalError = minITerm / kI;
			} else if (totalError*kI > maxITerm) {
				totalError = maxITerm / kI;
			}
		}
		
		result = kP * error + kI * totalError + kD * deltaError + kF * setpoint;
		
		if (result < minOutput) {
			result = minOutput;
		} else if (result > maxOutput) {
			result = maxOutput;
		}
		
		errorBuffer.add(error);
		while (errorBuffer.size() > bufferLength) {
			errorBuffer.remove();
		}
		
		lastError = error;
	}
	
	private class PIDTask extends TimerTask {

		@Override
		public void run() {
			
			if (enabled) {
				
				sensorVal = source.pidGet();
				calculate();
				output.pidWrite(result);
				
				System.out.println(sensorVal + " " + result);
				
			}
			
		}
		
	}
}
