package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.robot.Hardware;
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
	
	private CANTalon rightShooterMotor, leftShooterMotor;
	private Solenoid solenoid1, solenoid2;
	
	private HoodPosition currHoodPosition;
		
	public Shooter() {
		
		rightShooterMotor = Hardware.rightShooterMotor;
		leftShooterMotor = Hardware.leftShooterMotor;
		
		solenoid1 = Hardware.shooterHoodSolenoid1;
		solenoid2 = Hardware.shooterHoodSolenoid2;
		
        rightShooterMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative); //also possibly CtreMagEncoder_Absolute
		rightShooterMotor.setPID(ConstantsIO.kP_Shooter, ConstantsIO.kI_Shooter, ConstantsIO.kD_Shooter,
				ConstantsIO.kF_Shooter, 0, ConstantsIO.kShooterVoltageRamp, 0);
		rightShooterMotor.setVoltageRampRate(ConstantsIO.kShooterVoltageRamp);
		rightShooterMotor.configPeakOutputVoltage(12.0, -12.0);
		
		leftShooterMotor.changeControlMode(CANTalon.TalonControlMode.Follower);
		leftShooterMotor.set(rightShooterMotor.getDeviceID());
		
		rightShooterMotor.reverseSensor(true);
		rightShooterMotor.reverseOutput(true);
		leftShooterMotor.reverseOutput(true);
		
//		setHoodPosition(HoodPosition.STOWED);
		
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
		
		rightShooterMotor.changeControlMode(CANTalon.TalonControlMode.Speed);
		rightShooterMotor.set(setpoint);

	}
	
	public void setPWM(double pwm) {
		
		rightShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		rightShooterMotor.set(pwm);
		
	}
	
	public void disable() {
		
		rightShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		rightShooterMotor.set(0.0);
		
	}

	public double getRate() {
		
		return rightShooterMotor.getSpeed();
		
	}
	
	public double getError() {
				
		return rightShooterMotor.getSetpoint() - rightShooterMotor.getSpeed();
		
	}
	
	public boolean isOnTarget(double maxError) {
		
		return isPID() && Math.abs(getError()) < maxError;
		
	}
	
	public boolean isPID() {
		
		return rightShooterMotor.getControlMode() == CANTalon.TalonControlMode.Speed;
		
	}
	
	public boolean isReadyToFire() {
		
		return isOnTarget(rightShooterMotor.get()) && currHoodPosition != HoodPosition.STOWED;
		
	}
	
	public double getCurrentPower() {
		return leftShooterMotor.get();
	}
	
	public void setBrakeMode(boolean brakeMode) {
		
		rightShooterMotor.enableBrakeMode(brakeMode);
		leftShooterMotor.enableBrakeMode(brakeMode);
		
	}

	@Override
	public Map<String, Object> getLogData() {
		
		Map<String, Object> logData = new HashMap<String, Object>();
		
		logData.put("Name", "Shooter");
		logData.put("RPM", rightShooterMotor.getEncVelocity());
		logData.put("Setpoint", rightShooterMotor.getSetpoint());
		logData.put("Control Mode", rightShooterMotor.getControlMode());
		
		return logData;	
	}
	
}