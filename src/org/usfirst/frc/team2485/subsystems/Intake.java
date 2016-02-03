package org.usfirst.frc.team2485.subsystems;

import org.usfirst.frc.team2485.util.SpeedControllerWrapper;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

import edu.wpi.first.wpilibj.PIDController;

public class Intake {
	//private SpeedControllerWrapper 
	
	//private (775 Pro ?) rollerMotorVertical, rollerMotorHorizontal, armMotor;
	
	//private boolean rollersOn=false;
	
	//Potentiometers
	private AnalogPotentiometer pot;

	private PIDController armPID;
	
	public double
		P = 0.01,
		I = 0.0,
		D = 0.0;
	
	private boolean isPID = false;
	
	public static final int ABSOLUTE_TOLERANCE = 25;
	
	public static final double UP_POSITION = 0.0; //assign value
	
	private double currentSetpoint;
	
	
	public Intake(VictorSP intake, VictorSP lateral, VictorSPs arm){
		//  Hardware.   SpeedControllerWrappers
		
		/* rollerMotorVertical = new (775 Pro ?);
		 * rollerMotorHorizontal = new (775 Pro ?);
		 * armMotor = new (775 Pro ?);
		 * 
		 */
		
		armPID.setAbsoluteTolerance(ABSOLUTE_TOLERANCE); //change value of absolute tolerance
		
		currentSetpoint = UP_POSITION;
		
	}
	
	public void startRollers(double value){
		rollerMotorVertical.set(value);
		rollerMotorHorizontal.set(value);
		rollersOn=true;
	}
	
	public void  stopRollers(){
		rollerMotors.set(0);
		rollersOn=false;
	}
	public void setSetPoint(int i){
		 armMotor.set(i);
		 /*
		 * use potentiometers to raise/lower intake (cowCatcher)
		 */
	}
	
	public void setPID(double p, double i, double d){
		this.P = p;
		this.I = i;
		this.D = d;
		
		armPID.setPID(P, I, D);
	}
	
	
	 public boolean setSetpoint(double setpoint) {
	        return setSetpoint(setpoint, true);
	    }

	    /**
	     * Sets setpoint and turns rollers on or off
	     * @param setpoint
	     * @param rollersOn
	     * @return
	     */
	    public boolean setSetpoint(double setpoint, boolean rollersOn) {
	        armPID.setSetpoint(setpoint);
	        currentSetpoint = setpoint;

	        isPID = true;

	        if (!armPID.onTarget())
	            armPID.enable();
	        else
	            armPID.disable();

	        if (currentSetpoint == PICKUP && rollersOn) {
	            startRollers(1.0);
	        }

	        return armPID.onTarget();
	    }


	    /**
	     * Moves arm manually
	     * @param speed
	     */
	    public void moveArm(double speed) {
	        if (Math.abs(speed) > 0.5) {
	            armPID.disable();
	            currentSetpoint = armPID.getSetpoint();
	            isPID = false;

	           /* if ((speed > 0 && potValue > IN_CATAPULT + 50) || (speed < 0 && potValue < LOW_LIMIT)) {
	                armMotor.set(0.0);
	            } else {
	                armMotor.set(speed);
	            }
	            */
	        } if (!armPID.isEnable()) {
	            armMotor.set(0.0);
	        }
	    }
	
}
