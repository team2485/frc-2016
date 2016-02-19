package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.DummyOutput;
import org.usfirst.frc.team2485.util.Loggable;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;
import org.usfirst.frc.team2485.util.ThresholdHandler;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;

/**
 * @author Aidan Fay
 */
public class DriveTrain implements Loggable {

	private SpeedControllerWrapper leftDrive, rightDrive;
	private Encoder encoder;

	private final double NORMAL_SPEED_RATING = 0.8, FAST_SPEED_RATING = 1.0, SLOW_SPEED_RATING = 0.6;

	private double driveSpeed = NORMAL_SPEED_RATING;

	private double lastLeft, lastRight;

	// private final double MAX_PWM_DELTA = 1.0 / 20; // 20 cycles to get to
	// full speed from rest

	// AUTONOMOUS
	private DummyOutput dummyAhrsOutput;
	private DummyOutput dummyEncoderOutput;
	private DummyOutput dummyDriveStraightOutput;

	public PIDController ahrsPID;
	public PIDController driveStraightPID;
	public PIDController encPID;

	private int ahrsOnTargetCounter = 0;
	private final int MINIMUM_AHRS_ON_TARGET_ITERATIONS = 10;

	// public static double
	// kP_G_Rotate = 0.03,
	// kI_G_Rotate = 0.0,
	// kD_G_Rotate = 0.0;
	public static double kP_G_Drive, kI_G_Drive, kD_G_Drive;
	// public static double
	// kP_E = 0.03,
	// kI_E,
	// kD_E;

	private final double AbsTolerance_Imu_DriveTo = 2.0;
	private final double AbsTolerance_Imu_TurnTo = 1;
	private final double AbsTolerance_Enc = 5;

	private double lowEncRate = 5;

	// W.A.R. LORD DRIVE
	private double oldSteering = 0.0;
	private double quickStopAccumulator = 0.0;
	private final double THROTTLE_DEADBAND = 0.1;
	private final double STEERING_DEADBAND = 0.1;

	private final double SENSITIVITY_HIGH = 0.85;
	private final double SENSITIVITY_LOW = 0.75;
	private boolean isQuickTurn = false;

	private AHRS ahrs;

	/**
	 *
	 * Constructor with IMU.
	 *
	 * @param useAhrs
	 */

	public DriveTrain(boolean useAhrs) {
		this.leftDrive = Hardware.leftDrive;
		this.rightDrive = Hardware.rightDrive;
		this.encoder = Hardware.rightDriveEnc;

		if (useAhrs) {
			ahrs = Hardware.ahrs;
			if (ahrs != null) {
				dummyAhrsOutput = new DummyOutput();
				ahrsPID = new PIDController(ConstantsIO.kP_RotateLargeAngle, ConstantsIO.kI_RotateLargeAngle,
						ConstantsIO.kD_RotateLargeAngle, ahrs, dummyAhrsOutput);
				ahrsPID.setAbsoluteTolerance(AbsTolerance_Imu_TurnTo);
				ahrsPID.setOutputRange(-0.8, 0.8);

				dummyDriveStraightOutput = new DummyOutput();
				driveStraightPID = new PIDController(ConstantsIO.kP_RotateLargeAngle, ConstantsIO.kI_RotateLargeAngle,
						ConstantsIO.kD_RotateLargeAngle, ahrs, dummyDriveStraightOutput);
				driveStraightPID.setAbsoluteTolerance(AbsTolerance_Imu_DriveTo);
				driveStraightPID.setOutputRange(-0.8, 0.8);
			}
		} else {
			this.ahrs = null;
		}

		dummyEncoderOutput = new DummyOutput();
		encPID = new PIDController(ConstantsIO.kP_DriveTo, ConstantsIO.kI_DriveTo, ConstantsIO.kD_DriveTo, encoder,
				dummyEncoderOutput);
		encPID.setAbsoluteTolerance(AbsTolerance_Enc);

		encoder.reset();
	}

	/**
	 * W.A.R. Lord Drive This drive method is based off of Team 254's Ultimate
	 * Ascent cheesyDrive code.
	 *
	 * @param controllerY
	 *            controllerY should be positive for forward motion
	 * @param controllerX
	 */
	public void warlordDrive(double controllerY, double controllerX) {

		// System.out.println("ControllerY: " + controllerY);

		boolean isHighGear = isQuickTurn;

		double steeringNonLinearity;

		double steering = ThresholdHandler.deadbandAndScale(controllerX, STEERING_DEADBAND, 0.01, 1);
		double throttle = ThresholdHandler.deadbandAndScale(controllerY, THROTTLE_DEADBAND, 0.01, 1);

		double negInertia = steering - oldSteering;
		oldSteering = steering;

		if (isHighGear) {
			steeringNonLinearity = 0.6;
			// Apply a sin function that's scaled to make it feel better.
			steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
					/ Math.sin(Math.PI / 2.0 * steeringNonLinearity);
			steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
					/ Math.sin(Math.PI / 2.0 * steeringNonLinearity);
		} else {
			steeringNonLinearity = 0.5;
			// Apply a sin function that's scaled to make it feel better.
			steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
					/ Math.sin(Math.PI / 2.0 * steeringNonLinearity);
			steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
					/ Math.sin(Math.PI / 2.0 * steeringNonLinearity);
			steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
					/ Math.sin(Math.PI / 2.0 * steeringNonLinearity);
		}

		double leftPwm, rightPwm, overPower;
		double sensitivity = 1.7;

		double angularPower;
		double linearPower;

		// Negative inertia!
		double negInertiaAccumulator = 0.0;
		double negInertiaScalar;
		if (isHighGear) {
			negInertiaScalar = 5.0;
			sensitivity = SENSITIVITY_HIGH;
		} else {
			if (steering * negInertia > 0) {
				negInertiaScalar = 2.5;
			} else {
				if (Math.abs(steering) > 0.65) {
					negInertiaScalar = 5.0;
				} else {
					negInertiaScalar = 3.0;
				}
			}
			sensitivity = SENSITIVITY_LOW;
		}
		double negInertiaPower = negInertia * negInertiaScalar;
		negInertiaAccumulator += negInertiaPower;

		steering = steering + negInertiaAccumulator;
		linearPower = throttle;

		// Quickturn!
		if (isQuickTurn) {
			if (Math.abs(linearPower) < 0.2) {
				double alpha = 0.1;
				steering = steering > 1 ? 1.0 : steering;
				quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha * steering * 0.5;
			}
			overPower = 1.0;
			if (isHighGear) {
				sensitivity = 1.0;
			} else {
				sensitivity = 1.0;
			}
			angularPower = steering;
		} else {
			overPower = 0.0;
			angularPower = throttle * steering * sensitivity - quickStopAccumulator;// changed
																					// from
																					// Math.abs(throttle)
			if (quickStopAccumulator > 1) {
				quickStopAccumulator -= 1;
			} else if (quickStopAccumulator < -1) {
				quickStopAccumulator += 1;
			} else {
				quickStopAccumulator = 0.0;
			}
		}

		rightPwm = leftPwm = linearPower;

		leftPwm += angularPower;
		rightPwm -= angularPower;

		if (leftPwm > 1.0) {
			rightPwm -= overPower * (leftPwm - 1.0);
			leftPwm = 1.0;
		} else if (rightPwm > 1.0) {
			leftPwm -= overPower * (rightPwm - 1.0);
			rightPwm = 1.0;
		} else if (leftPwm < -1.0) {
			rightPwm += overPower * (-1.0 - leftPwm);
			leftPwm = -1.0;
		} else if (rightPwm < -1.0) {
			leftPwm += overPower * (-1.0 - rightPwm);
			rightPwm = -1.0;
		}

		setLeftRight(leftPwm, rightPwm);
	}

	/**
	 * Sets the drive to quick turn mode
	 * 
	 * @param isQuickTurn
	 */
	public void setQuickTurn(boolean isQuickTurn) {
		this.isQuickTurn = isQuickTurn;
	}

	/**
	 * Sends outputs values to the left and right side of the drive base.
	 *
	 * The parameters should both be positive to move forward. One side has
	 * inverted motors...do not send a negative to one side and a positive to
	 * the other for forward or backwards motion.
	 *
	 * @param leftOutput
	 * @param rightOutput
	 */
	public void setLeftRight(double leftOutput, double rightOutput) {

		leftOutput *= driveSpeed;
		rightOutput *= driveSpeed;

		if (leftOutput - lastLeft > ConstantsIO.kDriveVoltageRamp) {
			leftOutput = lastLeft + ConstantsIO.kDriveVoltageRamp;
		} else if (leftOutput - lastLeft < -ConstantsIO.kDriveVoltageRamp) {
			leftOutput = lastLeft - ConstantsIO.kDriveVoltageRamp;
		}

		if (rightOutput - lastRight > ConstantsIO.kDriveVoltageRamp) {
			rightOutput = lastRight + ConstantsIO.kDriveVoltageRamp;
		} else if (rightOutput - lastRight < -ConstantsIO.kDriveVoltageRamp) {
			rightOutput = lastRight - ConstantsIO.kDriveVoltageRamp;
		}

		lastLeft = leftOutput;
		lastRight = rightOutput;

		System.out.println("DriveTrain SetLeftRight: Left: " + leftOutput + " Right: " + rightOutput);

		leftDrive.set(leftOutput);
		rightDrive.set(rightOutput);
	}

	/**
	 * Switch into high speed mode
	 */
	public void setHighSpeed() {
		driveSpeed = FAST_SPEED_RATING;
	}

	/**
	 * Switch into low speed mode
	 */
	public void setLowSpeed() {
		driveSpeed = SLOW_SPEED_RATING;
	}

	/**
	 * Switch to normal speed mode
	 */
	public void setNormalSpeed() {
		driveSpeed = NORMAL_SPEED_RATING;
	}

	public void resetSensors() {
		encoder.reset();
		ahrs.zeroYaw();
	}

	public void resetEncoder() {
		encoder.reset();
	}

	public double getEncoderOutput() {
		return encoder.getDistance();
	}

	public double getAngle() {
		if (ahrs == null)
			return 0;
		return ahrs.getYaw();
	}

	public void setPIDGyroDrive() {
		if (ahrs != null) {
			ahrsPID.setPID(kP_G_Drive, kI_G_Drive, kD_G_Drive);
			ahrsPID.setAbsoluteTolerance(AbsTolerance_Imu_DriveTo);
		}
	}

	public void initPIDGyroRotate() {
		if (ahrs != null) {
			ahrsPID.setPID(ConstantsIO.kP_RotateLargeAngle, ConstantsIO.kI_RotateLargeAngle,
					ConstantsIO.kD_RotateLargeAngle);
			ahrsPID.setAbsoluteTolerance(AbsTolerance_Imu_TurnTo);
		}
	}

	public void initPIDEncoder() {
		encPID.setPID(ConstantsIO.kP_DriveTo, ConstantsIO.kI_DriveTo, ConstantsIO.kD_DriveTo);
	}

	public void disableAhrsPID() {
		if (ahrs != null) {
			ahrsPID.disable();
		}
		setLeftRight(0, 0);
	}

	public void disableDriveToPID() {
		encPID.disable();
		driveStraightPID.disable();
	}

	public boolean driveTo(double inches, double maxAbsOutput) {

		if (!encPID.isEnabled()) {
			encPID.enable();
			System.out.println("|DriveTrain.driveTo| Enabling driveStraight PID in driveTo " + encoder.getDistance()
					+ " , " + inches);
			encPID.setSetpoint(inches);

			driveStraightPID.enable();
			driveStraightPID.setSetpoint(ahrs.getYaw());
		}

		encPID.setOutputRange(-maxAbsOutput, maxAbsOutput);

		System.out.println(encPID.getError());

		double encoderOutput = dummyEncoderOutput.get();
		double driveStraightOutput = dummyDriveStraightOutput.get();

		System.out.println("|DriveTrain.driveTo| Encoder Output: " + encoderOutput);

		double leftOutput = encoderOutput + driveStraightOutput;
		double rightOutput = encoderOutput - driveStraightOutput;

		setLeftRight(leftOutput, rightOutput);

		// done?
		if (encPID.onTarget() && Math.abs(encoder.getRate()) < lowEncRate) {
			setLeftRight(0.0, 0.0);
			encPID.disable();
			driveStraightPID.disable();
			return true;
		}
		return false;
	}

	public boolean rotateTo(double angle) { // may need to check for moving to
											// fast when pid is on target
		if (ahrsPID == null)
			throw new IllegalStateException("can't rotateTo when ahrs is null");

		if (!ahrsPID.isEnabled() && Math.abs(Hardware.ahrs.getYaw() - angle) < 15) {
			ahrsPID.setPID(ConstantsIO.kP_RotateSmallAngle, ConstantsIO.kI_RotateSmallAngle,
					ConstantsIO.kD_RotateSmallAngle);
		} else if (!ahrsPID.isEnabled()) {
			ahrsPID.setPID(ConstantsIO.kP_RotateLargeAngle, ConstantsIO.kI_RotateLargeAngle,
					ConstantsIO.kD_RotateLargeAngle);

		}

		// Check to see if we're on target

		System.out.println(
				"|DriveTrain.rotateTo| Angle Error: " + ahrsPID.getError() + "\t Output: " + dummyAhrsOutput.get());

		if (ahrsPID.onTarget())
			ahrsOnTargetCounter++;
		else
			ahrsOnTargetCounter = 0;

		if (ahrsOnTargetCounter >= MINIMUM_AHRS_ON_TARGET_ITERATIONS) {
			setLeftRight(0, 0);
			ahrsPID.disable();
			return true;
		}

		double ahrsOutput = dummyAhrsOutput.get();

		// left and right are opposite on porpoise
		setLeftRight(ahrsOutput, -ahrsOutput);
		return false;
	}

	@Override
	public Map<String, Object> getLogData() {

		Map<String, Object> logData = new HashMap<String, Object>();

		logData.put("Name", "DriveTrain");
		logData.put("DriveSpeed", driveSpeed);
		logData.put("QuickTurn", isQuickTurn);
		logData.put("EncoderDistance", encoder.getDistance());
		logData.put("Angle", getAngle());
		logData.put("RightPWM", rightDrive.get());
		logData.put("LeftPWM", leftDrive.get());
		logData.put("QuickStopAccumulator", quickStopAccumulator);

		return logData;

	}
}
