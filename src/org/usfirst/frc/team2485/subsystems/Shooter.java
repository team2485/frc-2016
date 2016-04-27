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
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
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

	//Adjusted from ConstantsIO
	public static double RPS_LONG_SHOT = 95, //should be 95...changed for a test
		RPS_BATTER_SHOT = 80,//changed from 80 for practice bot with 1:1 gearing
			RPS_LOW_GOAL_SHOT = 60;
//	private boolean clearedI;

	public static final HoodPosition DEFAULT_HOOD_POSITION = HoodPosition.HIGH_ANGLE;

	private CANTalon rightShooterMotor, leftShooterMotor;
	private SpeedControllerWrapper shooterMotors;
	private Solenoid lowerSolenoid, upperSolenoid;
	private Encoder enc;
	private HoodPosition currHoodPosition;
	public WarlordsPIDController ratePID;

	public Shooter() {
		
		enc = Hardware.shooterEnc;

		rightShooterMotor = Hardware.rightShooterMotor;
		leftShooterMotor = Hardware.leftShooterMotor;

		lowerSolenoid = Hardware.shooterHoodSolenoid1;
		upperSolenoid = Hardware.shooterHoodSolenoid2;

		rightShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		leftShooterMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		

		shooterMotors = new SpeedControllerWrapper(new SpeedController[] { leftShooterMotor, rightShooterMotor },
				new int[] { 0, 0 });// CANTalons monitor their own current

		ratePID = new WarlordsPIDController(ConstantsIO.kP_Shooter, ConstantsIO.kI_Shooter, ConstantsIO.kD_Shooter,
				ConstantsIO.kF_Shooter, enc, shooterMotors);
		ratePID.setBufferLength(3);
		
//		System.out.println("PID values: " + ConstantsIO.kP_Shooter + "\t\t" + ConstantsIO.kF_Shooter);
		
		ratePID.setOutputRange(0, 1);

		currHoodPosition = DEFAULT_HOOD_POSITION;
		
		RPS_BATTER_SHOT = ConstantsIO.kBatterShotRPS;
		RPS_LONG_SHOT = ConstantsIO.kLongShotRPS;
		
		SmartDashboard.putNumber("RPS Batter Shot", RPS_BATTER_SHOT);
		SmartDashboard.putNumber("RPS Long Shot", RPS_LONG_SHOT);

		disableShooter();
		
//		new ClearTotalErrorThread().start();

	}
	
	public void updateConstants() {
		
		ratePID.setPID(ConstantsIO.kP_Shooter, ConstantsIO.kI_Shooter, ConstantsIO.kD_Shooter,
				ConstantsIO.kF_Shooter);
		
		RPS_BATTER_SHOT = ConstantsIO.kBatterShotRPS;
		RPS_LONG_SHOT = ConstantsIO.kLongShotRPS;
		
	}

	public void setHoodPosition(final HoodPosition newHoodPosition) {
		
//		System.out.println("Shooter: set hood position "
//				+ newHoodPosition.toString());

		if (newHoodPosition == HoodPosition.LOW_ANGLE) {
			if (currHoodPosition == HoodPosition.HIGH_ANGLE) {
				upperSolenoid.set(true); // This should extend the upper piston
			} else if (currHoodPosition == HoodPosition.STOWED) {
				lowerSolenoid.set(false); // Retracting the lower piston pulls open the shooter
				
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
		
		if (setpoint == RPS_BATTER_SHOT) {
			RPS_BATTER_SHOT = SmartDashboard.getNumber("RPS Batter Shot");
			setpoint = RPS_BATTER_SHOT;
		}
		
		if (setpoint == RPS_LONG_SHOT) {
			RPS_LONG_SHOT = SmartDashboard.getNumber("RPS Long Shot");
			setpoint = RPS_LONG_SHOT;
		}

		ratePID.setSetpoint(setpoint);
//		clearedI = false; // clear each time the setpoint changes
		if (!ratePID.isEnabled()) {
			ratePID.enable();
		}

	}

	/*
	 * Set shooter speed based distance read of Lidar <br>
	 * Currently not working
	 */
//	public void setSpeedOffLidar() {
//		
//		setTargetSpeed(0.0 /* Lidar magic */);
//	
//	}

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

		return enc.getRate();

	}

	public double getError() {

		return getSetpoint() - getRate();

	}
	
	public double getAvgError() {
		return ratePID.getAvgError();
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
		return isOnTargetPercentage(0.08) && currHoodPosition != HoodPosition.STOWED;
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
		logData.put("RPM", enc.getRate());
		logData.put("Setpoint", ratePID.getSetpoint());

		return logData;
		
	}
	
//	private class ClearTotalErrorThread extends Thread {
//		@Override
//		public void run() {
//			while (true) {
//				
//				if (!clearedI && getRate() > getSetpoint()) {
//					clearedI = true;
//					ratePID.reset();
//					ratePID.enable();
//				}
//				
//				try {
//					Thread.sleep(20);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				
//			}
//			
//		}
//	}

}