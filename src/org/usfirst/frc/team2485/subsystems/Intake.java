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

	private AnalogPotentiometer pot;

	private PIDController armPID;

	private SpeedControllerWrapper armSpeedControllerWrapper;
	
	private VictorSP intakeVictorSP, lateralVictorSP;

	private double POT_SLIPPAGE = 100; //better way?

	public static final double ABSOLUTE_TOLERANCE = 25;

	public static final double UP_POSITION = 500.0; //assign value using POT_SLIPPAGE

	public Intake() {
		
		this.armSpeedControllerWrapper = Hardware.intakeArm;
		
		this.pot = Hardware.intakePot;
		
		this.intakeVictorSP = Hardware.intakeVictorSP;
		this.lateralVictorSP = Hardware.lateralVictorSP;

		armPID = new PIDController(ConstantsIO.kP_IntakeArm, ConstantsIO.kI_IntakeArm, 
				ConstantsIO.kD_IntakeArm, pot, armSpeedControllerWrapper);
		armPID.setAbsoluteTolerance(ABSOLUTE_TOLERANCE); //change value of absolute tolerance
		armPID.setSetpoint(pot.get());

	}

	public void startRollers(double lateralValue, double intakeValue) {
		
		lateralVictorSP.set(lateralValue);
		intakeVictorSP.set(intakeValue);
		
	}

	public void stopRollers() {
		
		startRollers(0, 0);
	
	}
	
	public void setManual(double i) {
		armSpeedControllerWrapper.set(i);
//		armPID.disable();		
	}
	
	public void setManual(double i, boolean rollersOn) {
		
		setManual(i);
		
		
		if (rollersOn) {
			startRollers(ConstantsIO.kLateralRollerSpeed, ConstantsIO.kIntakeRollerSpeed);
		} else {
			stopRollers();
			
		}
		
	}

	public void setPID(double p, double i, double d) {

		armPID.setPID(p, i, d);
		
	}


	public void setSetpoint(double setpoint) {
		
		armPID.setSetpoint(setpoint);
		armPID.enable();
		
	}
	
	public double getSetpoint() {
		return armPID.getSetpoint();
	}

	/**
	 * Sets setpoint and turns rollers on or off
	 * @param setpoint
	 * @param rollersOn
	 */
	public void setSetpoint(double setpoint, boolean rollersOn) {
		
		armPID.setSetpoint(setpoint);
		
        if (rollersOn) {
			startRollers(ConstantsIO.kLateralRollerSpeed, ConstantsIO.kIntakeRollerSpeed);
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
		logData.put("Intake Roller Speed", intakeVictorSP.get());
		logData.put("Lateral Roller Speed", lateralVictorSP.get());
		logData.put("Arm Setpoint", armPID.getSetpoint());
		logData.put("Arm Position", pot.get());
		logData.put("Arm Motor PWM", armSpeedControllerWrapper.get());
		logData.put("Arm PID isOnTarget", armPID.onTarget());

		return logData;
		
	}

}
