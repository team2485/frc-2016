package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

	/**
	 * Low Angle = Long shot <br>
	 * High Angle = Batter Shot <br>
	 * Stowed = Don't Shoot <br>
	 */
	public static enum HoodPosition {
		LOW_ANGLE, HIGH_ANGLE, STOWED
	};

	public static final double RPM_LONG_SHOT = 5500, RPM_BATTER_SHOT = 4650,
			RPM_LOW_GOAL_SHOT = 3500;

	public static final HoodPosition DEFAULT_HOOD_POSITION = HoodPosition.HIGH_ANGLE;

	private CANTalon rightShooterMotor, leftShooterMotor;
	private Solenoid lowerSolenoid, upperSolenoid;

	private HoodPosition currHoodPosition;

	public Shooter() {

		rightShooterMotor = Hardware.rightShooterMotor;
		leftShooterMotor = Hardware.leftShooterMotor;

		lowerSolenoid = Hardware.shooterHoodSolenoid1;
		upperSolenoid = Hardware.shooterHoodSolenoid2;

		rightShooterMotor
				.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative); // also
																			// possibly
																			// CtreMagEncoder_Absolute
		rightShooterMotor.setPID(ConstantsIO.kP_Shooter,
				ConstantsIO.kI_Shooter, ConstantsIO.kD_Shooter,
				ConstantsIO.kF_Shooter, 0, ConstantsIO.kShooterVoltageRamp, 0);
		rightShooterMotor.setVoltageRampRate(ConstantsIO.kShooterVoltageRamp);
		rightShooterMotor.configPeakOutputVoltage(12.0, -12.0);

		leftShooterMotor.changeControlMode(CANTalon.TalonControlMode.Follower);
		leftShooterMotor.set(rightShooterMotor.getDeviceID());

		rightShooterMotor.reverseSensor(true);
		rightShooterMotor.reverseOutput(false); // This reverses direction of
												// shooter
		leftShooterMotor.reverseOutput(true); // always reversed because it's
												// relative to master

		currHoodPosition = DEFAULT_HOOD_POSITION;

		disableShooter();

	}

	public void setHoodPosition(final HoodPosition newHoodPosition) {
		System.out.println("Shooter: set hood position "
				+ newHoodPosition.toString());

		if (newHoodPosition == HoodPosition.LOW_ANGLE) {
			if (currHoodPosition == HoodPosition.HIGH_ANGLE) {
				upperSolenoid.set(true); // This should extend the upper
											// piston
			} else if (currHoodPosition == HoodPosition.STOWED) {
				lowerSolenoid.set(false); // Retracting the lower piston pulls
											// open the shooter

				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						upperSolenoid.set(true);
					}
				}, 250);
			}
		} else if (newHoodPosition == HoodPosition.HIGH_ANGLE) {
			if (currHoodPosition == HoodPosition.LOW_ANGLE) {
				upperSolenoid.set(false);

			} else if (currHoodPosition == HoodPosition.STOWED) {
				lowerSolenoid.set(false);
				System.out.println("Shooter: stowed -> high angle");
			}
		} else { // setting to stowed

			System.out
					.println("Shooter: Hood is being set to stowed, cur pos is: "
							+ currHoodPosition);

			if (currHoodPosition == HoodPosition.LOW_ANGLE) {

				upperSolenoid.set(false);

				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						lowerSolenoid.set(true);
					}
				}, 250);

			} else if (currHoodPosition == HoodPosition.HIGH_ANGLE) {
				lowerSolenoid.set(true);
			}
		}

		currHoodPosition = newHoodPosition;
	}

	public void resetHood() {

		setHoodPosition(DEFAULT_HOOD_POSITION);

	}

	public void setTargetSpeed(double setpoint) {

		rightShooterMotor.changeControlMode(CANTalon.TalonControlMode.Speed);
		rightShooterMotor.set(setpoint);

	}

	/**
	 * Set shooter speed based distance read of Lidar <br>
	 * Currently not working
	 */
	public void setSpeedOffLidar() {
		setTargetSpeed(0.0 /* Lidar magic */);
	}

	public double getSetpoint() {
		return rightShooterMotor.getSetpoint();
	}

	public void setPWM(double pwm) {

		rightShooterMotor
				.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		rightShooterMotor.set(pwm);

	}

	public void disableShooter() {

		rightShooterMotor
				.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
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

	/**
	 * Checks if the current error is within the percentage given from the last
	 * set-point
	 * 
	 * @param maxPercentError
	 *            the maximum error, as a percent, from 0.0 to 1.0
	 */
	public boolean isOnTargetPercentage(double maxPercentError) {
		return isOnTarget(maxPercentError * getSetpoint());
	}

	public boolean isPID() {

		return rightShooterMotor.getControlMode() == CANTalon.TalonControlMode.Speed;

	}

	public boolean isReadyToFire() {

		return isOnTargetPercentage(0.10)
				&& currHoodPosition != HoodPosition.STOWED;

	}

	public double getCurrentPower() {

		// Calls get on the left motor (the follower) which returns the
		// throttle, because follow is effectively PercentVbus

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