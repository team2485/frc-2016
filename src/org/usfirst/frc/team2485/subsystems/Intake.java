package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.robot.Constants;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Loggable;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;
import org.usfirst.frc.team2485.util.ThresholdHandler;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake implements Loggable {
	// private SpeedControllerWrapper

	// private (775 Pro ?) rollerMotorVertical, rollerMotorHorizontal, armMotor;


	private PIDController armPID;

	private SpeedControllerWrapper armSpeedControllerWrapper;
	
	private VictorSP intakeVictorSP, lateralVictorSP;

	private AnalogPotentiometer absEncoder;

	private double POT_SLIPPAGE = 100; //better way?

	public static final double ABSOLUTE_TOLERANCE = 25;

	public static final double 	FLOOR_POSITION = 0.0, 
								INTAKE_POSITION = 0.0, 
								FULL_UP_POSITION = 0.5,
								PORTCULLIS_POSITION = 0.3,
								LOW_NO_INTAKE_POSITION = 0.0;
	

	public Intake() {
		
		this.armSpeedControllerWrapper = Hardware.intakeArm;
		
		this.intakeVictorSP = Hardware.intakeVictorSP;
		this.lateralVictorSP = Hardware.lateralVictorSP;

		this.absEncoder = Hardware.intakeAbsEncoder;

		

		armPID = new PIDController(ConstantsIO.kP_IntakeArm, ConstantsIO.kI_IntakeArm, ConstantsIO.kD_IntakeArm,
				absEncoder, armSpeedControllerWrapper);
		armPID.setAbsoluteTolerance(ABSOLUTE_TOLERANCE); // change value of
															// absolute
															// tolerance
		armPID.setSetpoint(absEncoder.get());

	}

	public void startRollers(double lateralValue, double intakeValue) {
		
		lateralVictorSP.set(lateralValue);
		intakeVictorSP.set(intakeValue);
		
	}

	public void stopRollers() {
		
		startRollers(0, 0);
	
	}
	
	
	public void setManual(double pwm) {
		
		armPID.disable();
//		pwm = ThresholdHandler.deadbandAndScale(pwm, Constants.kMoveIntakeManuallyDeadband, 0.05, 0.5);
		pwm = ThresholdHandler.deadbandAndScaleDualRamp(pwm, Constants.kMoveIntakeManuallyDeadband, 0.05, 0.8, 0.4, 1.0);
		
		armSpeedControllerWrapper.set(pwm);

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
		
		armPID.enable();
		armPID.setSetpoint(setpoint);

	}
	
	public double getSetpoint() {
		return armPID.getSetpoint();
	}

	/**
	 * Sets setpoint and turns rollers on or off
	 * 
	 * @param setpoint
	 * @param rollersOn
	 */
	public void setSetpoint(double setpoint, boolean rollersOn) {
		
		setSetpoint(setpoint);
		
        if (rollersOn) {
			startRollers(ConstantsIO.kLateralRollerSpeed, ConstantsIO.kIntakeRollerSpeed);
        } else {
        	stopRollers();
        }
	}

	public boolean isOnTarget() {
		return armPID.onTarget();
	}

	public void disableArmPID() {
		armPID.disable();
	}

	@Override
	public Map<String, Object> getLogData() {

		Map<String, Object> logData = new HashMap<String, Object>();

		logData.put("Name", "Intake");
		logData.put("Intake Roller Speed", intakeVictorSP.get());
		logData.put("Lateral Roller Speed", lateralVictorSP.get());
		logData.put("Arm Setpoint", armPID.getSetpoint());
		logData.put("Arm Position", absEncoder.get());
		logData.put("Arm Motor PWM", armSpeedControllerWrapper.get());
		logData.put("Arm PID isOnTarget", armPID.onTarget());

		return logData;

	}

}
