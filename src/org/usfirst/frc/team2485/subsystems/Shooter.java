package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.util.Loggable;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;

public class Shooter implements Loggable {
	
	private CANTalon shooterMotor1, shooterMotor2;
	private static final double P = 0.0001, I = 0.0, D = 0.0, F = 0.5, rampRate = 5;
	
	public Shooter() {
		
		shooterMotor1 = new CANTalon(0);
		shooterMotor2 = new CANTalon(0);
		
        shooterMotor1.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative); //also possibly CtreMagEncoder_Absolute
		shooterMotor1.setPID(P, I, D, F, 0, rampRate, 0);
		
		shooterMotor2.changeControlMode(CANTalon.TalonControlMode.Follower);
		shooterMotor2.set(shooterMotor1.getDeviceID());
		
		shooterMotor1.reverseSensor(false);
		shooterMotor1.reverseOutput(false);
		shooterMotor2.reverseSensor(true);
		shooterMotor2.reverseOutput(true);

	}
	
	public void setSetpoint(double setpoint) {
		
		shooterMotor1.changeControlMode(CANTalon.TalonControlMode.Speed);
		shooterMotor1.set(setpoint);

	}
	
	public void setPWM(double pwm) {
		
		shooterMotor1.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		shooterMotor1.set(pwm);
		
	}
	
	public void disable() {
		
		shooterMotor1.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		shooterMotor1.set(0.0);
		
	}

	public double getRate() {
		
		return shooterMotor1.getSpeed();
		
	}
	
	public double getError() {
		
		return shooterMotor1.getSetpoint() - shooterMotor1.getSpeed();
		
	}
	
	public boolean isOnTarget(double maxError) {
		
		return Math.abs(getError()) < maxError;
		
	}
	
	public void setBrakeMode(boolean brakeMode) {
		
		shooterMotor1.enableBrakeMode(brakeMode);
		shooterMotor2.enableBrakeMode(brakeMode);
		
	}

	@Override
	public Map<String, Object> getLogData() {
		
		Map<String, Object> logData = new HashMap<String, Object>();
		
		logData.put("Name", "Shooter");
		logData.put("RPM", shooterMotor1.getEncVelocity());
		logData.put("Setpoint", shooterMotor1.getSetpoint());
		logData.put("Control Mode", shooterMotor1.getControlMode());
		
		return logData;	
		
	}
	
}
