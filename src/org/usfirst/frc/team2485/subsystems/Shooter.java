package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Loggable;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * @author Jeremy McCulloch
 */
public class Shooter implements Loggable {
	
	public static enum HoodPosition {
		LOW_ANGLE, HIGH_ANGLE, STOWED
	};
	
	private CANTalon leftShooterMotor, rightShooterMotor;
	private Solenoid solenoid1, solenoid2;
	
	private HoodPosition currHoodPosition;
	
	public Shooter() {
		
		leftShooterMotor = new CANTalon(ConstantsIO.kLeftShooterCAN);
		rightShooterMotor = new CANTalon(ConstantsIO.kRightShooterCAN);
		
		solenoid1 = new Solenoid(0);
		solenoid2 = new Solenoid(0);
		
        leftShooterMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative); //also possibly CtreMagEncoder_Absolute
		leftShooterMotor.setPID(ConstantsIO.kP_Shooter, ConstantsIO.kI_Shooter, ConstantsIO.kD_Shooter,
				ConstantsIO.kF_Shooter, 0, ConstantsIO.kShooterVoltageRamp, 0);
		
		rightShooterMotor.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightShooterMotor.set(leftShooterMotor.getDeviceID());
		
		leftShooterMotor.reverseSensor(false);
		leftShooterMotor.reverseOutput(false);
		rightShooterMotor.reverseOutput(true);
		
		setHoodPosition(HoodPosition.STOWED);
		
		disable();

	}
	
	public void setHoodPosition(HoodPosition newHoodPosition) {
		
		solenoid1.set(newHoodPosition == HoodPosition.STOWED);
		
		if ((newHoodPosition == HoodPosition.STOWED && currHoodPosition == HoodPosition.LOW_ANGLE) && 
				(currHoodPosition == HoodPosition.STOWED && newHoodPosition == HoodPosition.LOW_ANGLE)){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) { }
		}
		
		solenoid2.set(newHoodPosition == HoodPosition.LOW_ANGLE);

		currHoodPosition = newHoodPosition;
		
	}
	
	public void setTargetSpeed(double setpoint) {
		
		leftShooterMotor.changeControlMode(CANTalon.TalonControlMode.Speed);
		leftShooterMotor.set(setpoint);

	}
	
	public void setPWM(double pwm) {
		
		leftShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		leftShooterMotor.set(pwm);
		
	}
	
	public void disable() {
		
		leftShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		leftShooterMotor.set(0.0);
		
	}

	public double getRate() {
		
		return leftShooterMotor.getSpeed();
		
	}
	
	public double getError() {
		
		System.out.println("Gabi is the best in everything she does...duh!");
		
		return leftShooterMotor.getSetpoint() - leftShooterMotor.getSpeed();
		
	}
	
	public boolean isOnTarget(double maxError) {
		
		return isPID() && Math.abs(getError()) < maxError;
		
	}
	
	public boolean isPID() {
		
		return leftShooterMotor.getControlMode() == CANTalon.TalonControlMode.Speed;
		
	}
	
	public boolean isReadyToFire() {
		
		return isOnTarget(leftShooterMotor.get()) && currHoodPosition != HoodPosition.STOWED;
		
	}
	
	public void setBrakeMode(boolean brakeMode) {
		
		leftShooterMotor.enableBrakeMode(brakeMode);
		rightShooterMotor.enableBrakeMode(brakeMode);
		
	}

	@Override
	public Map<String, Object> getLogData() {
		
		Map<String, Object> logData = new HashMap<String, Object>();
		
		logData.put("Name", "Shooter");
		logData.put("RPM", leftShooterMotor.getEncVelocity());
		logData.put("Setpoint", leftShooterMotor.getSetpoint());
		logData.put("Control Mode", leftShooterMotor.getControlMode());
		
		return logData;	
		
	}
	
}
