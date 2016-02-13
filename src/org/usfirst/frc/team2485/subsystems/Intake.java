package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Loggable;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake implements Loggable {
	//private SpeedControllerWrapper 

	//private (775 Pro ?) rollerMotorVertical, rollerMotorHorizontal, armMotor;

	private boolean rollersOn = false;

	private AnalogPotentiometer pot;

	private PIDController armPID;

	private SpeedControllerWrapper armSpeedControllerWrapper, rollersSpeedControllerWrapper;

	private static double POT_SLIPPAGE = 100; //better way?

	public double kP, kI, kD;

	private double defaultRollerSpeed = 0.5;

	public static final double ABSOLUTE_TOLERANCE = 25;

	public static final double UP_POSITION = 500.0; //assign value using POT_SLIPPAGE

	public Intake() {
		this.armSpeedControllerWrapper = Hardware.intakeArm;
		
		this.pot = Hardware.intakePot;
		
		this.rollersSpeedControllerWrapper = Hardware.rollers;

		kP = ConstantsIO.kP_IntakeArm;
		kI = ConstantsIO.kI_IntakeArm;
		kD = ConstantsIO.kD_IntakeArm;

		armPID = new PIDController(kP, kI, kD, pot, armSpeedControllerWrapper);
		armPID.setAbsoluteTolerance(ABSOLUTE_TOLERANCE); //change value of absolute tolerance
		armPID.setSetpoint(UP_POSITION);

	}

	public void startRollers(double value){
		rollersSpeedControllerWrapper.set(value);
		rollersOn=true;
	}

	public void stopRollers(){
		rollersSpeedControllerWrapper.set(0);
		rollersOn=false;
	}
	
	public void setManual(double i){
		armSpeedControllerWrapper.set(i);
		armPID.disable();		
	}
	
	public void setManual(double i, boolean rollersOn) {
		
		setManual(i);
		
		if(rollersOn){
			startRollers(defaultRollerSpeed);
		} else {
			stopRollers();
		}
		
	}

	public void setPID(double p, double i, double d){
		this.kP = p;
		this.kI = i;
		this.kD = d;

		armPID.setPID(kP, kI, kD);
	}


	public void setSetpoint(double setpoint) {
		armPID.setSetpoint(setpoint);
		armPID.enable();
	}

	/**
	 * Sets setpoint and turns rollers on or off
	 * @param setpoint
	 * @param rollersOn
	 */
	public void setSetpoint(double setpoint, boolean rollersOn) {
		
		armPID.setSetpoint(setpoint);
		
        if (rollersOn) {
            startRollers(defaultRollerSpeed );
        } else {
        	stopRollers();
        }
		
	}
	
	public boolean isOnTarget() {
		return armPID.onTarget();
	}

	public void disableArmPID(){
		armPID.disable();
	}

	@Override
	public Map<String, Object> getLogData() {
		
		Map<String, Object> logData = new HashMap<String, Object>();
		
		logData.put("Name", "Intake");
		logData.put("Roller Speed", rollersSpeedControllerWrapper.get());
		logData.put("Arm Setpoint", armPID.getSetpoint());
		logData.put("Arm Position", pot.get());
		logData.put("Arm Motor PWM", armSpeedControllerWrapper.get());
		logData.put("Arm PID isOnTarget", armPID.onTarget());

		return logData;
		
	}

}
