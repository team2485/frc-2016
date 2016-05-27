package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Loggable;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;
import org.usfirst.frc.team2485.util.WarlordsPIDController;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

	//Adjusted by ConstantsIO
	public static double RPS_LONG_SHOT = 95, 
		RPS_BATTER_SHOT = 80;
	
	private static final HoodPosition DEFAULT_HOOD_POSITION = HoodPosition.HIGH_ANGLE;
	private SpeedControllerWrapper shooterMotors;
	private HoodPosition currHoodPosition;
	public WarlordsPIDController ratePID;

	public Shooter() {
		

		Hardware.rightShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		Hardware.leftShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		
		shooterMotors = new SpeedControllerWrapper(new SpeedController[] { Hardware.leftShooterMotor, 
				Hardware.rightShooterMotor }, new int[] { 0, 0 });

		ratePID = new WarlordsPIDController(ConstantsIO.kP_Shooter, ConstantsIO.kI_Shooter, ConstantsIO.kD_Shooter,
				ConstantsIO.kF_Shooter, Hardware.shooterEnc, shooterMotors);
		ratePID.setBufferLength(3);
		ratePID.setOutputRange(0, 1);

		currHoodPosition = DEFAULT_HOOD_POSITION;
		
		RPS_BATTER_SHOT = ConstantsIO.kBatterShotRPS;
		RPS_LONG_SHOT = ConstantsIO.kLongShotRPS;
		
		SmartDashboard.putNumber("RPS Batter Shot", RPS_BATTER_SHOT);
		SmartDashboard.putNumber("RPS Long Shot", RPS_LONG_SHOT);

		disableShooter();
		

	}
	
	public void updateConstants() {
		
		ratePID.setPID(ConstantsIO.kP_Shooter, ConstantsIO.kI_Shooter, ConstantsIO.kD_Shooter,
				ConstantsIO.kF_Shooter);
		
		RPS_BATTER_SHOT = ConstantsIO.kBatterShotRPS;
		RPS_LONG_SHOT = ConstantsIO.kLongShotRPS;
		
	}

	public void setHoodPosition(final HoodPosition newHoodPosition) {
		
		if (newHoodPosition == HoodPosition.LOW_ANGLE) {
			if (currHoodPosition == HoodPosition.HIGH_ANGLE) {
				Hardware.upperSolenoid.set(true); // This should extend the upper piston
			} else if (currHoodPosition == HoodPosition.STOWED) {
				Hardware.lowerSolenoid.set(false); // Retracting the lower piston pulls open the shooter
				
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						Hardware.upperSolenoid.set(true);
					}
				}, 250);
			}
		} else if (newHoodPosition == HoodPosition.HIGH_ANGLE) {
			if (currHoodPosition == HoodPosition.LOW_ANGLE) {
				Hardware.upperSolenoid.set(false);

			} else if (currHoodPosition == HoodPosition.STOWED) {
				Hardware.lowerSolenoid.set(false);
			}
		} else { // setting to stowed

			if (currHoodPosition == HoodPosition.LOW_ANGLE) {

				Hardware.upperSolenoid.set(false);

				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						Hardware.lowerSolenoid.set(true);
					}
				}, 250);

			} else if (currHoodPosition == HoodPosition.HIGH_ANGLE) {
				Hardware.lowerSolenoid.set(true);
			}
		}

		currHoodPosition = newHoodPosition;
		
	}

	public void resetHood() {
		setHoodPosition(DEFAULT_HOOD_POSITION);
	}

	public void setTargetSpeed(double setpoint) {
		
		if (setpoint == RPS_BATTER_SHOT) {
			RPS_BATTER_SHOT = SmartDashboard.getNumber("RPS Batter Shot");
			setpoint = RPS_BATTER_SHOT;
		}
		
		if (setpoint == RPS_LONG_SHOT) {
			RPS_LONG_SHOT = SmartDashboard.getNumber("RPS Long Shot");
			setpoint = RPS_LONG_SHOT;
		}

		ratePID.setSetpoint(setpoint);
		if (!ratePID.isEnabled()) {
			ratePID.enable();
		}

	}

	public double getSetpoint() {
		return ratePID.getSetpoint();
	}
	
	public HoodPosition getHoodPosition() {
		return currHoodPosition;
	}

	public void setPWM(double pwm) {

		if (ratePID.isEnabled()) {
			ratePID.disable();
		}
		shooterMotors.set(pwm);

	}

	public void disableShooter() {

		if (ratePID.isEnabled()) {
			ratePID.disable();
		}
		shooterMotors.emergencyStop();

	}

	public double getRate() {

		return Hardware.shooterEnc.getRate();

	}

	public double getError() {

		return getSetpoint() - getRate();
		
	}
	
	public double getAvgError() {
		
		return ratePID.getAvgError();
		
	}

	public boolean isOnTarget(double maxError) {

		return isPIDEnabled() && Math.abs(getError()) < maxError;

	}

	/**
	 * Checks if the current error is within the percentage given from the last
	 * setpoint
	 * 
	 * @param maxPercentError
	 *            the maximum error, as a percent, from 0.0 to 1.0
	 */
	public boolean isOnTargetPercentage(double maxPercentError) {
		
		return isOnTarget(maxPercentError * getSetpoint());
		
	}

	public boolean isPIDEnabled() {

		return ratePID.isEnabled();
		
	}

	public boolean isReadyToFire() {
		return isOnTargetPercentage(0.08) && currHoodPosition != HoodPosition.STOWED;
	}

	public double getCurrentPower() {

		return shooterMotors.get();
		
	}

	@Override
	public Map<String, Object> getLogData() {

		Map<String, Object> logData = new HashMap<String, Object>();

		logData.put("Name", "Shooter");
		logData.put("RPM", Hardware.shooterEnc.getRate());
		logData.put("Setpoint", ratePID.getSetpoint());

		return logData;
		
	}
	
}