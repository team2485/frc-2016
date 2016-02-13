package org.usfirst.frc.team2485.subsystems;

import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {
	//private SpeedControllerWrapper 

	//private (775 Pro ?) rollerMotorVertical, rollerMotorHorizontal, armMotor;

	private boolean rollersOn=false;

	private AnalogPotentiometer pot;

	private PIDController armPID;

	private VictorSP intakeVictorSP, lateralVictorSP;

	private SpeedControllerWrapper armSpeedControllerWrapper;

	private static final int POT_SLIPPAGE=100; //better way?

	public double
	kP = 0.01,
	kI = 0.0,
	kD = 0.0;

	private double defaultRollerSpeed = 0.5;

	public static final int ABSOLUTE_TOLERANCE = 25;

	public static final double UP_POSITION = 500.0; //assign value using POT_SLIPPAGE

	public Intake() {
		//  Hardware.   SpeedControllerWrappers
		this.armSpeedControllerWrapper = Hardware.intakeArm;
		
		pot = new AnalogPotentiometer(3);

		this.intakeVictorSP = Hardware.intakeVictorSP;
		this.lateralVictorSP = Hardware.lateralVictorSP;


		/* rollerMotorVertical = new (775 Pro ?);
		 * rollerMotorHorizontal = new (775 Pro ?);
		 * armMotor = new (775 Pro ?);
		 * 
		 */
		armPID = new PIDController(kP, kI, kD, pot, armSpeedControllerWrapper);
		armPID.setAbsoluteTolerance(ABSOLUTE_TOLERANCE); //change value of absolute tolerance
		armPID.setSetpoint(UP_POSITION);

	}

	public void startRollers(double value){
		intakeVictorSP.set(value);
		lateralVictorSP.set(value);
		rollersOn=true;
	}

	public void stopRollers(){
		intakeVictorSP.set(0);
		lateralVictorSP.set(0);
		rollersOn=false;
	}
	
	public void setManual(double i){
		this.setManual(i, false);
		
	}
	
	public void setManual(double i, boolean rollersOn) {
		armSpeedControllerWrapper.set(i);
		armPID.disable();
		
		if(rollersOn){
			startRollers(defaultRollerSpeed);
		}
		else
			stopRollers();
		/*
		 * use potentiometers to raise/lower intake (cowCatcher)
		 */
	}

	public void setPID(double p, double i, double d){
		this.kP = p;
		this.kI = i;
		this.kD = d;

		armPID.setPID(kP, kI, kD);
	}


	public void setSetpoint(double setpoint) {
		setSetpoint(setpoint, true);
	}

	/**
	 * Sets setpoint and turns rollers on or off
	 * @param setpoint
	 * @param rollersOn
	 * @return
	 */
	public void setSetpoint(double setpoint, boolean rollersOn) {
		armPID.setSetpoint(setpoint);

		armPID.enable();

	
        if (rollersOn) {
            startRollers(defaultRollerSpeed );
        }
	 

		
	}



	public void disableArmPID(){
		armPID.disable();
	}

}
