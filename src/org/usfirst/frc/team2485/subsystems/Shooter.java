package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Loggable;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * @author Jeremy McCulloch
 */
public class Shooter implements Loggable {

	/**
	 * Low Angle = Long Shot <br>
	 * High Angle = Batter Shot <br>
	 * Stowed = Don't Shoot <br>
	 */
	public static enum HoodPosition {
		LOW_ANGLE, HIGH_ANGLE, STOWED
	};

	public static final double RPS_LONG_SHOT = 95, 
		RPS_BATTER_SHOT = 73,//used to be 70 @ drive practice
			RPS_LOW_GOAL_SHOT = 60;

	public static final HoodPosition DEFAULT_HOOD_POSITION = HoodPosition.HIGH_ANGLE;

	private CANTalon rightShooterMotor, leftShooterMotor;
	private SpeedControllerWrapper shooterMotors;
	private Solenoid lowerSolenoid, upperSolenoid;
	private Encoder enc;
	private HoodPosition currHoodPosition;
	public PIDController ratePID;

	public Shooter() {

		enc = Hardware.shooterEnc;

		rightShooterMotor = Hardware.rightShooterMotor;
		leftShooterMotor = Hardware.leftShooterMotor;

		lowerSolenoid = Hardware.shooterHoodSolenoid1;
		upperSolenoid = Hardware.shooterHoodSolenoid2;

		rightShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);

		leftShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		leftShooterMotor.setInverted(true);

		shooterMotors = new SpeedControllerWrapper(new SpeedController[] { leftShooterMotor, rightShooterMotor },
				new int[] { 0, 0 });// CANTalons monitor their own current

		ratePID = new PIDController(ConstantsIO.kP_Shooter, ConstantsIO.kI_Shooter, ConstantsIO.kD_Shooter,
				ConstantsIO.kF_Shooter, enc, shooterMotors);
		ratePID.setOutputRange(0, 1);

		currHoodPosition = DEFAULT_HOOD_POSITION;

		disableShooter();

	}

	public void setHoodPosition(final HoodPosition newHoodPosition) {
		
//		System.out.println("Shooter: set hood position "
//				+ newHoodPosition.toString());

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
			}
		} else { // setting to stowed

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

		ratePID.setSetpoint(setpoint);
		if (!ratePID.isEnabled()) {
			ratePID.enable();
		}

	}

	/**
	 * Set shooter speed based distance read of Lidar <br>
	 * Currently not working
	 */
	public void setSpeedOffLidar() {
		
		setTargetSpeed(0.0 /* Lidar magic */);
	
	}

	public double getSetpoint() {
		
		return ratePID.getSetpoint();
		
	}

	public void setPWM(double pwm) {

		if (ratePID.isEnabled()) {
			ratePID.disable();
		}
		shooterMotors.set(pwm);

	}

	public void disableShooter() {

		setPWM(0);

	}

	public double getRate() {

		return enc.getRate();

	}

	public double getError() {

		return getSetpoint() - getRate();

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

		return ratePID.isEnabled();
		
	}

	public boolean isReadyToFire() {

		return isOnTargetPercentage(0.10) && currHoodPosition != HoodPosition.STOWED;

	}

	public double getCurrentPower() {

		return shooterMotors.get();
		
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
		logData.put("Setpoint", ratePID.getSetpoint());

		return logData;
		
	}

}