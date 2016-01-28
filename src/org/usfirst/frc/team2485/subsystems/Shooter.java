package org.usfirst.frc.team2485.subsystems;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;

public class Shooter {
	
	private CANTalon shooterMotor1, shooterMotor2;
	private static final double P = 0.0001, I = 0.0, D = 0.0, F = 0.5, rampRate = 5;
	
	public Shooter() {
		
		shooterMotor1 = new CANTalon(0);
		shooterMotor2 = new CANTalon(0);
		
		shooterMotor1.setPID(P, I, D, F, 0, rampRate, 0);
		shooterMotor2.setPID(P, I, D, F, 0, rampRate, 0);
		
        shooterMotor1.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        shooterMotor2.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);

		shooterMotor1.reverseSensor(false);
		shooterMotor1.reverseOutput(false);
		shooterMotor2.reverseSensor(true);
		shooterMotor2.reverseOutput(true);

	}
	
	public void setSetpoint(double setpoint) {
		
		shooterMotor1.changeControlMode(CANTalon.ControlMode.Speed);
		shooterMotor2.changeControlMode(CANTalon.ControlMode.Speed);
		shooterMotor1.set(setpoint);
		shooterMotor2.set(setpoint);

	}
	
	public void setPWM(double pwm) {
		
		shooterMotor1.changeControlMode(CANTalon.ControlMode.PercentVbus);
		shooterMotor2.changeControlMode(CANTalon.ControlMode.PercentVbus);
		shooterMotor1.set(pwm);
		shooterMotor2.set(pwm);
		
	}
	
	public void disable() {
		
		shooterMotor1.changeControlMode(CANTalon.ControlMode.PercentVbus);
		shooterMotor2.changeControlMode(CANTalon.ControlMode.PercentVbus);
		shooterMotor1.set(0.0);
		shooterMotor2.set(0.0);
		
	}

	public double getRate() {
		
		return Math.max(Math.abs(shooterMotor1.getSpeed()), Math.abs(shooterMotor2.getSpeed()));//returns greater of 2 speeds
		
	}
	
	public double getError() {
		
		return shooterMotor1.getSetpoint() - getRate();
		
	}
	
	public boolean isOnTarget(double maxError) {
		
		return Math.abs(getError()) < maxError;
		
	}
	
	public void setBrakeMode(boolean brakeMode) {
		
		shooterMotor1.enableBrakeMode(brakeMode);
		shooterMotor2.enableBrakeMode(brakeMode);
		
	}
	
}
