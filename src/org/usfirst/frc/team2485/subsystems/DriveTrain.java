package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.DummyOutput;
import org.usfirst.frc.team2485.util.Loggable;
import org.usfirst.frc.team2485.util.MultipleEncoderWrapper;
import org.usfirst.frc.team2485.util.MultipleEncoderWrapper.MultipleEncoderWrapperMode;
import org.usfirst.frc.team2485.util.RampRate;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;
import org.usfirst.frc.team2485.util.ThresholdHandler;
import org.usfirst.frc.team2485.util.WarlordsPIDController;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * @author Aidan Fay
 */
public class DriveTrain implements Loggable {

	private SpeedControllerWrapper leftDrive, rightDrive;

	private final double NORMAL_SPEED_RATING = 0.8, FAST_SPEED_RATING = 1.0,
			SLOW_SPEED_RATING = 0.6;

	private double driveSpeed = NORMAL_SPEED_RATING;

//	private boolean smallAngleMode;
	
	// private double lastLeft, lastRight;

	// private final double MAX_PWM_DELTA = 1.0 / 20; // 20 cycles to get to
	// full speed from rest

	// AUTONOMOUS
	private DummyOutput dummyRotateToOutput;
	private DummyOutput dummyDriveToEncoderOutput;
	private DummyOutput dummyDriveStraightOutput;
//	private DummyOutput dummyDriveToLidarOutput;

	public WarlordsPIDController rotateToPID;
	private WarlordsPIDController leftVelocityPID, rightVelocityPID;
	public WarlordsPIDController driveStraightPID;
	public WarlordsPIDController driveToEncoderPID;
	public WarlordsPIDController driveToLidarPID;

	private RampRate leftVelocityRamp, rightVelocityRamp;
	
	private MultipleEncoderWrapper minDriveEncoderDist;
	
	private int ahrsOnTargetCounter = 0;
	private static final int MINIMUM_AHRS_ON_TARGET_ITERATIONS = 4;
//	private static final double ROTATE_TO_MIN_OUTPUT = 0.1;

	// public static double
	// kP_G_Rotate = 0.03,
	// kI_G_Rotate = 0.0,
	// kD_G_Rotate = 0.0;
	public static double kP_G_Drive, kI_G_Drive, kD_G_Drive;
	// public static double
	// kP_E = 0.03,
	// kI_E,
	// kD_E;

	private static final double ABS_TOLERANCE_DRIVESTRAIGHT = 2.0;
	private static final double ABS_TOLERANCE_ROTATETO = 1;
	private static final double ABS_TOLERANCE_DRIVETO = 5;

	private double lowEncRate = 5;

	// W.A.R. LORD DRIVE
	private double oldSteering = 0.0;
	private double quickStopAccumulator = 0.0;
	private static final double THROTTLE_DEADBAND = 0.1;
	private static final double STEERING_DEADBAND = 0.1;

	private static final double SENSITIVITY_HIGH = 0.85;
	private static final double SENSITIVITY_LOW = 0.75;
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
		
		rightVelocityRamp = new RampRate(ConstantsIO.kDriveVelocityUpRamp, ConstantsIO.kDriveVelocityDownRamp);
		leftVelocityRamp = new RampRate(ConstantsIO.kDriveVelocityUpRamp, ConstantsIO.kDriveVelocityDownRamp);

		minDriveEncoderDist = new MultipleEncoderWrapper(PIDSourceType.kDisplacement, 
				MultipleEncoderWrapperMode.MIN, Hardware.leftDriveEnc,
				Hardware.rightDriveEnc);
		
		if (useAhrs) {
			ahrs = Hardware.ahrs;
			if (ahrs != null) {
				dummyRotateToOutput = new DummyOutput();
				rotateToPID = new WarlordsPIDController(
						ConstantsIO.kP_RotateLargeAngle,
						ConstantsIO.kI_RotateLargeAngle,
						ConstantsIO.kD_RotateLargeAngle, ahrs,
						dummyRotateToOutput);
				rotateToPID.setAbsoluteTolerance(ABS_TOLERANCE_ROTATETO);
				rotateToPID.setOutputRange(-10, 10);
				rotateToPID.setInputRange(-180, 180);
				rotateToPID.setContinuous(true);

				dummyDriveStraightOutput = new DummyOutput();
				driveStraightPID = new WarlordsPIDController(
						ConstantsIO.kP_RotateLargeAngle,
						ConstantsIO.kI_RotateLargeAngle,
						ConstantsIO.kD_RotateLargeAngle, ahrs,
						dummyDriveStraightOutput);
				driveStraightPID
						.setAbsoluteTolerance(ABS_TOLERANCE_DRIVESTRAIGHT);
				driveStraightPID.setOutputRange(-0.8, 0.8);
			}
		} else {
			this.ahrs = null;
		}

		dummyDriveToEncoderOutput = new DummyOutput();
		driveToEncoderPID = new WarlordsPIDController(ConstantsIO.kP_DriveTo,
				ConstantsIO.kI_DriveTo, ConstantsIO.kD_DriveTo, minDriveEncoderDist,
				dummyDriveToEncoderOutput);
		driveToEncoderPID.setAbsoluteTolerance(ABS_TOLERANCE_DRIVETO);

//		dummyDriveToLidarOutput = new DummyOutput();
//		driveToLidarPID = new PIDController(ConstantsIO.kP_DriveTo,
//				ConstantsIO.kI_DriveTo, ConstantsIO.kD_DriveTo, Hardware.lidar,
//				dummyDriveToLidarOutput, .01);
//		driveToLidarPID.setAbsoluteTolerance(ABS_TOLERANCE_DRIVETO);

		rightVelocityPID = new WarlordsPIDController(ConstantsIO.kP_DriveVelocity, ConstantsIO.kI_DriveVelocity, 
				ConstantsIO.kD_DriveVelocity, ConstantsIO.kF_DriveVelocity, 
				Hardware.rightRateEncoder, rightDrive);
		leftVelocityPID = new WarlordsPIDController(ConstantsIO.kP_DriveVelocity, ConstantsIO.kI_DriveVelocity, 
				ConstantsIO.kD_DriveVelocity, ConstantsIO.kF_DriveVelocity, 
				Hardware.leftRateEncoder, leftDrive);
		
		Hardware.leftDriveEnc.reset();
		Hardware.rightDriveEnc.reset();

	}
	
	public void updateConstants() {
		
		driveToEncoderPID.setPID(ConstantsIO.kP_DriveTo,
				ConstantsIO.kI_DriveTo, ConstantsIO.kD_DriveTo, 0);
		driveStraightPID.setPID(ConstantsIO.kP_RotateLargeAngle,
						ConstantsIO.kI_RotateLargeAngle, ConstantsIO.kD_RotateLargeAngle, 0);
		rotateToPID.setPID(ConstantsIO.kP_RotateLargeAngle,
						ConstantsIO.kI_RotateLargeAngle, ConstantsIO.kD_RotateLargeAngle, 0);
	}

	/**
	 * Resets DriveTrain but does not zero encoders
	 */
	public void reset() {
		disableAhrsPID();
		disableDriveToPID();
		emergencyStop();
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

		boolean isHighGear = isQuickTurn;

		double steeringNonLinearity;

		double steering = ThresholdHandler.deadbandAndScale(controllerX,
				STEERING_DEADBAND, 0.01, 1);
		double throttle = ThresholdHandler.deadbandAndScale(controllerY,
				THROTTLE_DEADBAND, 0.01, 1);

		double negInertia = steering - oldSteering;
		oldSteering = steering;

		if (isHighGear) {
			steeringNonLinearity = 0.6;
			// Apply a sin function that's scaled to make it feel better.
			steering = Math
					.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
					/ Math.sin(Math.PI / 2.0 * steeringNonLinearity);
			steering = Math
					.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
					/ Math.sin(Math.PI / 2.0 * steeringNonLinearity);
		} else {
			steeringNonLinearity = 0.5;
			// Apply a sin function that's scaled to make it feel better.
			steering = Math
					.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
					/ Math.sin(Math.PI / 2.0 * steeringNonLinearity);
			steering = Math
					.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
					/ Math.sin(Math.PI / 2.0 * steeringNonLinearity);
			steering = Math
					.sin(Math.PI / 2.0 * steeringNonLinearity * steering)
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
				quickStopAccumulator = (1 - alpha) * quickStopAccumulator
						+ alpha * steering * 0.5;
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
			angularPower = throttle * steering * sensitivity
					- quickStopAccumulator;// changed
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

	public void setLeftRightVelocity(double leftOutput, double rightOutput) {
		
//		System.out.println("Set Left Right Vel: " + leftOutput + ", " + rightOutput);
		
		leftVelocityPID.setPID(ConstantsIO.kP_DriveVelocity, ConstantsIO.kI_DriveVelocity, 
				ConstantsIO.kD_DriveVelocity, ConstantsIO.kF_DriveVelocity);
		rightVelocityPID.setPID(ConstantsIO.kP_DriveVelocity, ConstantsIO.kI_DriveVelocity, 
				ConstantsIO.kD_DriveVelocity, ConstantsIO.kF_DriveVelocity);
		
		leftVelocityPID.enable();
		rightVelocityPID.enable();
		leftVelocityPID.setSetpoint(leftOutput);
		rightVelocityPID.setSetpoint(rightOutput);
	}
	
	public void brake() {
		
		leftVelocityPID.setPID(ConstantsIO.kP_DriveBrake, ConstantsIO.kI_DriveBrake, 
				ConstantsIO.kD_DriveBrake, ConstantsIO.kF_DriveBrake);
		rightVelocityPID.setPID(ConstantsIO.kP_DriveBrake, ConstantsIO.kI_DriveBrake, 
				ConstantsIO.kD_DriveBrake, ConstantsIO.kF_DriveBrake);
		
		leftVelocityPID.enable();
		rightVelocityPID.enable();
		
		leftVelocityPID.setSetpoint(0);
		rightVelocityPID.setSetpoint(0);
		
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

		leftVelocityPID.disable();
		rightVelocityPID.disable();
		
		leftOutput *= driveSpeed;
		rightOutput *= driveSpeed;

		// now done within speedcontrollerwrapper
		// if (leftOutput - lastLeft > ConstantsIO.kDriveVoltageRamp) {
		// leftOutput = lastLeft + ConstantsIO.kDriveVoltageRamp;
		// } else if (leftOutput - lastLeft < -ConstantsIO.kDriveVoltageRamp) {
		// leftOutput = lastLeft - ConstantsIO.kDriveVoltageRamp;
		// }
		//
		// if (rightOutput - lastRight > ConstantsIO.kDriveVoltageRamp) {
		// rightOutput = lastRight + ConstantsIO.kDriveVoltageRamp;
		// } else if (rightOutput - lastRight < -ConstantsIO.kDriveVoltageRamp)
		// {
		// rightOutput = lastRight - ConstantsIO.kDriveVoltageRamp;
		// }

		// lastLeft = leftOutput;
		// lastRight = rightOutput;

		// System.out.println("DriveTrain SetLeftRight: Left: " + leftOutput +
		// " Right: " + rightOutput);

		leftDrive.set(leftOutput);
		rightDrive.set(rightOutput);
	}
	
	public void emergencyStop() {

		leftVelocityPID.disable();
		rightVelocityPID.disable();
		
		leftDrive.emergencyStop();
		rightDrive.emergencyStop();
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

//	public void resetSensors() {
//		Hardware.leftDriveEnc.reset();
//		ahrs.zeroYaw();
//	}

	public void resetEncoders() {
		Hardware.leftDriveEnc.reset();
		Hardware.rightDriveEnc.reset();
	}

//	public double getEncoderOutput() {
//		return encoder.getDistance();
//	}

	public double getAngle() {
		if (ahrs == null)
			return 0;
		return ahrs.getYaw();
	}

	public void setPIDGyroDrive() {
		if (ahrs != null) {
			driveStraightPID.setPID(kP_G_Drive, kI_G_Drive, kD_G_Drive, 0);
			driveStraightPID.setAbsoluteTolerance(ABS_TOLERANCE_DRIVESTRAIGHT);
		}
	}

	public void initPIDGyroRotate() {
		if (ahrs != null) {
			rotateToPID.setPID(ConstantsIO.kP_RotateLargeAngle,
					ConstantsIO.kI_RotateLargeAngle,
					ConstantsIO.kD_RotateLargeAngle, 0);
			rotateToPID.setAbsoluteTolerance(ABS_TOLERANCE_ROTATETO);
		}
	}

	public void initPIDEncoder() {
		driveToEncoderPID.setPID(ConstantsIO.kP_DriveTo,
				ConstantsIO.kI_DriveTo, ConstantsIO.kD_DriveTo, 0);
	}

	public void disableAhrsPID() {
		if (ahrs != null) {
			rotateToPID.disable();
		}
	}

	public void disableDriveToPID() {
		driveToEncoderPID.disable();
		driveStraightPID.disable();
		rotateToPID.disable();
	}
	
	public boolean driveToAndRotateTo(double inches, double startAngle, 
			double endAngle, double maxAbsVelocity) {
		if (!driveToEncoderPID.isEnabled()) {
			driveToEncoderPID.enable();
			rotateToPID.enable();
			rotateToPID.setSetpoint(startAngle);
		}
		driveToEncoderPID.setSetpoint(inches);

		driveToEncoderPID.setOutputRange(-maxAbsVelocity, maxAbsVelocity);
		rotateToPID.setOutputRange(-maxAbsVelocity, maxAbsVelocity);
		
		double percentDone = (Hardware.leftDriveEnc.getDistance() + 
				Hardware.rightDriveEnc.getDistance()) / (inches+0.00000001) / 2;
		if (percentDone > 1) {
			percentDone = 1;
		} else if (percentDone < 0) {
			percentDone = 0;
		}
		rotateToPID.setSetpoint(startAngle + (endAngle - startAngle) * percentDone); 
		
		double encoderOutput = dummyDriveToEncoderOutput.get();
		double rotateToOutput = dummyRotateToOutput.get();
		
		double leftVelocity = encoderOutput + rotateToOutput;
		double rightVelocity = encoderOutput - rotateToOutput;
		
//		System.out.println("DriveTrain:driveTo: pre-ramp rightVel " + rightVelocity + "leftVel:" + leftVelocity);

		leftVelocity = leftVelocityRamp.getNextValue(leftVelocity);
		rightVelocity = rightVelocityRamp.getNextValue(rightVelocity);

//		System.out.println("DriveTrain:driveTo: post-ramp rightVel " + rightVelocity + "leftVel:" + leftVelocity);
		setLeftRightVelocity(leftVelocity, rightVelocity);
		
		if (Math.abs(rotateToPID.getError()) < ABS_TOLERANCE_DRIVESTRAIGHT) {
			ahrsOnTargetCounter++;
		} else {
			
			ahrsOnTargetCounter = 0;
			
//			if (ahrsOutput > 0 && ahrsOutput < ROTATE_TO_MIN_OUTPUT) {
//				ahrsOutput = ROTATE_TO_MIN_OUTPUT;
//			} else if (ahrsOutput < 0 && ahrsOutput > -ROTATE_TO_MIN_OUTPUT) {
//				ahrsOutput = -ROTATE_TO_MIN_OUTPUT;
//			}
			
		}
		
		double avgVelocity = (Hardware.leftDriveEnc.getRate() + Hardware.rightDriveEnc.getRate()) / 2;
		

//		System.out.println("DriveTrain:driveTo:target " + driveToEncoderPID.getSetpoint() + "error " + driveToEncoderPID.getError() );
//		System.out.println("DriveTrain:driveTo:targAngle " + rotateToPID.getSetpoint() + "error " + rotateToPID.getError() );
		if (Math.abs(driveToEncoderPID.getError()) < ABS_TOLERANCE_DRIVETO 
				&& Math.abs(avgVelocity) < lowEncRate &&
				ahrsOnTargetCounter >= MINIMUM_AHRS_ON_TARGET_ITERATIONS) {
			setLeftRightVelocity(0.0, 0.0);
			driveToEncoderPID.disable();
			driveStraightPID.disable();
			rotateToPID.disable();
			return true;
		}
		
		return false;
		
	}

	public boolean rotateTo(double angle) {
		return rotateTo(angle, ABS_TOLERANCE_ROTATETO);
	}
	public boolean rotateTo(double angle, double tolerance) { // may need to check for moving to
											// fast when pid is on target
		if (rotateToPID == null)
			throw new IllegalStateException("can't rotateTo when ahrs is null");

		double SMALL_LARGE_THRESHOLD = 5;
		
		if (!rotateToPID.isEnabled() /*|| 
				Math.abs(Hardware.ahrs.getYaw() - angle) >= SMALL_LARGE_THRESHOLD*/) {
			
//			rotateToPID.setPID(ConstantsIO.kP_RotateLargeAngle,
//					ConstantsIO.kI_RotateLargeAngle,
//					ConstantsIO.kD_RotateLargeAngle);
			rotateToPID.enable();
//			smallAngleMode = false;
			
			
		}
		
		rotateToPID.setOutputRange(-15, 15);
//		if (!smallAngleMode && 
//				Math.abs(Hardware.ahrs.getYaw() - angle) < SMALL_LARGE_THRESHOLD) {
//			
//			smallAngleMode = true;
//			rotateToPID.setPID(ConstantsIO.kP_RotateSmallAngle,
//					ConstantsIO.kI_RotateSmallAngle,
//					ConstantsIO.kD_RotateSmallAngle);
//			rotateToPID.reset();
//			rotateToPID.enable();
//			
//		}
		
		rotateToPID.setSetpoint(angle);

		// Check to see if we're on target

		// System.out.println(
		// "|DriveTrain.rotateTo| Angle Error: " + rotateToPID.getError() +
		// "\t Output: " + dummyRotateToOutput.get());
		
//		System.out.println("DriveTrain: RotateTo target: " + rotateToPID.getSetpoint());
//		System.out.println("DriveTrain: RotateTo error: " + rotateToPID.getError());		
//		System.out.println("DriveTrain: RotateTo angle: " + ahrs.getYaw());		
//		System.out.println("DriveTrain: RotayeTo Is on Target?: " + rotateToPID.onTarget());

		double ahrsOutput = dummyRotateToOutput.get();

		
		if (Math.abs(rotateToPID.getError()) < tolerance) {
			ahrsOnTargetCounter++;
		} else {
			
			ahrsOnTargetCounter = 0;
			
//			if (ahrsOutput > 0 && ahrsOutput < ROTATE_TO_MIN_OUTPUT) {
//				ahrsOutput = ROTATE_TO_MIN_OUTPUT;
//			} else if (ahrsOutput < 0 && ahrsOutput > -ROTATE_TO_MIN_OUTPUT) {
//				ahrsOutput = -ROTATE_TO_MIN_OUTPUT;
//			}
			
		}
		
//		System.out.println("DriveTrain: On Target Iterations: " + ahrsOnTargetCounter);

		if (ahrsOnTargetCounter >= MINIMUM_AHRS_ON_TARGET_ITERATIONS) {
			setLeftRightVelocity(0, 0);
//			leftVelocityPID.disable();
//			rightVelocityPID.disable();
			rotateToPID.disable();
			return true;
		}
		
//		System.out.println("AHRS Output: " + ahrsOutput);

		// left and right are opposite on porpoise
		double leftVelocity = leftVelocityRamp.getNextValue(ahrsOutput);
		double rightVelocity = rightVelocityRamp.getNextValue(Math.abs(rotateToPID.getError()) > SMALL_LARGE_THRESHOLD ? -ahrsOutput : 0);
		setLeftRightVelocity(leftVelocity, rightVelocity);
		return false;
	}
	
	public boolean driveTo(double inches, double maxAbsOutput) {
	
		if (!driveToEncoderPID.isEnabled()) {
			driveToEncoderPID.enable();
			// System.out.println("|DriveTrain.driveTo| Enabling driveStraight PID in driveTo "
			// + encoder.getDistance()
			// + " , " + inches);
			driveToEncoderPID.setSetpoint(inches);
	
			driveStraightPID.enable();
			driveStraightPID.setSetpoint(ahrs.getYaw());
		}
	
		driveToEncoderPID.setOutputRange(-maxAbsOutput, maxAbsOutput);
	
		// System.out.println(encPID.getError());
	
		double encoderOutput = dummyDriveToEncoderOutput.get();
		double driveStraightOutput = dummyDriveStraightOutput.get();
	
		// System.out.println("|DriveTrain.driveTo| Encoder Output: " +
		// encoderOutput);
	
		double leftOutput = encoderOutput + driveStraightOutput;
		double rightOutput = encoderOutput - driveStraightOutput;
	
		setLeftRight(leftOutput, rightOutput);
	
		// done?
		if (Math.abs(driveToEncoderPID.getError()) < ABS_TOLERANCE_DRIVETO
				&& Math.abs(Hardware.leftDriveEnc.getRate()) < lowEncRate) {
			setLeftRight(0.0, 0.0);
			driveToEncoderPID.disable();
			driveStraightPID.disable();
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> getLogData() {

		Map<String, Object> logData = new HashMap<String, Object>();

		logData.put("Name", "DriveTrain");
		logData.put("DriveSpeed", driveSpeed);
		logData.put("QuickTurn", isQuickTurn);
//		logData.put("EncoderDistance", encoder.getDistance());
		logData.put("Angle", getAngle());
		logData.put("RightPWM", rightDrive.get());
		logData.put("LeftPWM", leftDrive.get());
		logData.put("QuickStopAccumulator", quickStopAccumulator);

		return logData;

	}
	
	
}
