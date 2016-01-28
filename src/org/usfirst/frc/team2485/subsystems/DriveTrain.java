package org.usfirst.frc.team2485.subsystems;

import org.usfirst.frc.com.kauailabs.nav6.frc.IMU;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.DummyOutput;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;
import org.usfirst.frc.team2485.util.ThresholdHandler;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;

/**
 * @author Aidan Fay
 */
public class DriveTrain {

    private SpeedControllerWrapper leftDrive, rightDrive;
    private Encoder encoder;

    private final double
            NORMAL_SPEED_RATING = 0.8,
            FAST_SPEED_RATING   = 1.0,
            SLOW_SPEED_RATING   = 0.6;

    private double driveSpeed = NORMAL_SPEED_RATING;

    // AUTONOMOUS
    private DummyOutput dummyImuOutput;
    private DummyOutput dummyEncoderOutput;
    private PIDController imuPID;
    private PIDController encPID;
    public static double
            kP_G_Rotate = 0.028,
            kI_G_Rotate = 0.0,
            kD_G_Rotate = 0.0;
    public static double kP_G_Drive, kI_G_Drive, kD_G_Drive;
    public static double
            kP_E = 0.075,
            kI_E,
            kD_E;

    private final double AbsTolerance_Imu_DriveTo = 2.0;
    private final double AbsTolerance_Imu_TurnTo = 3.0;
    private final double AbsTolerance_Enc = 5;

    // W.A.R. LORD DRIVE
    private double oldSteering = 0.0;
    private double quickStopAccumulator = 0.0;
    private final double THROTTLE_DEADBAND = 0.1;
    private final double STEERING_DEADBAND = 0.1;

    private final double SENSITIVITY_HIGH = 0.85;
    private final double SENSITIVITY_LOW = 0.75;
    private boolean isQuickTurn = false;

    private IMU imu;
	private boolean useIMU;

    /**
     *
     * Constructor with IMU.
     *
     * @param useIMU
     */
    
    
    public DriveTrain(boolean useIMU) {
        this.leftDrive      = Hardware.leftDrive;
        this.rightDrive     = Hardware.rightDrive;
        this.encoder        = Hardware.rightDriveEnc;
        this.useIMU 		= useIMU;
        
        
        if (useIMU) {
        	this.imu            = Hardware.imu;
        	if (imu != null) {
        		dummyImuOutput = new DummyOutput();
        		imuPID = new PIDController(kP_G_Rotate, kI_G_Rotate, kD_G_Rotate, imu, dummyImuOutput);
        		imuPID.setAbsoluteTolerance(AbsTolerance_Imu_DriveTo);
        	}
        }
        
        dummyEncoderOutput = new DummyOutput();
        encPID = new PIDController(kP_E, kI_E, kD_E, encoder, dummyEncoderOutput);
        encPID.setAbsoluteTolerance(AbsTolerance_Enc);

        encoder.reset();
    }

   
    /**
     * W.A.R. Lord Drive
     * This drive method is based off of Team 254's Ultimate Ascent
     * cheesyDrive code.
     *
     * @param controllerY
     * @param controllerX
     */
    public void warlordDrive(double controllerY, double controllerX) {
        boolean isHighGear = isQuickTurn;

        double steeringNonLinearity;

        double steering = ThresholdHandler.deadbandAndScale(controllerX, STEERING_DEADBAND,0.01,1);
        double throttle = -ThresholdHandler.deadbandAndScale(controllerY, THROTTLE_DEADBAND,0.01,1);

        double negInertia = steering - oldSteering;
        oldSteering = steering;

        if (isHighGear) {
            steeringNonLinearity = 0.6;
            // Apply a sin function that's scaled to make it feel better.
            steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering) /
                    Math.sin(Math.PI / 2.0 * steeringNonLinearity);
            steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering) /
                    Math.sin(Math.PI / 2.0 * steeringNonLinearity);
        } else {
            steeringNonLinearity = 0.5;
            // Apply a sin function that's scaled to make it feel better.
            steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering) /
                    Math.sin(Math.PI / 2.0 * steeringNonLinearity);
            steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering) /
                    Math.sin(Math.PI / 2.0 * steeringNonLinearity);
            steering = Math.sin(Math.PI / 2.0 * steeringNonLinearity * steering) /
                    Math.sin(Math.PI / 2.0 * steeringNonLinearity);
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
                quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha *
                        steering * 0.5;
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
            angularPower = Math.abs(throttle) * steering * sensitivity - quickStopAccumulator;
            if (quickStopAccumulator > 1) {
                quickStopAccumulator -= 1;
            } else if (quickStopAccumulator < -1) {
                quickStopAccumulator += 1;
            } else {
                quickStopAccumulator = 0.0;
            }
        }

        rightPwm = leftPwm = linearPower;

        leftPwm  += angularPower;
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
     * @param isQuickTurn
     */
    public void setQuickTurn(boolean isQuickTurn) {
        this.isQuickTurn = isQuickTurn;
    }

    /**
     * Sends outputs values to the left and right side
     * of the drive base.
     *
     * @param leftOutput
     * @param rightOutput
     */
    private void setLeftRight(double leftOutput, double rightOutput) {
        leftDrive.set(leftOutput * driveSpeed);
        rightDrive.set(-rightOutput * driveSpeed);
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
        imu.zeroYaw();
    }

    public double getEncoderOutput() {
        return encoder.getDistance();
    }

    public double getAngle() {
        if (imu == null) return 0;
        return imu.getYaw();
    }
    
    public void setPIDGyroDrive() {
    	if (useIMU) {
    		imuPID.setPID(kP_G_Drive, kI_G_Drive, kD_G_Drive);
        	imuPID.setAbsoluteTolerance(AbsTolerance_Imu_DriveTo);
    	}
    }

    public void initPIDGyroRotate() {
    	if (useIMU) {
    		imuPID.setPID(kP_G_Rotate, kI_G_Rotate, kD_G_Rotate);
    		imuPID.setAbsoluteTolerance(AbsTolerance_Imu_TurnTo);
    	}
    }

    public void initPIDEncoder() {
        encPID.setPID(kP_E, kI_E, kD_E);
    }

    public void disableIMUPID() {
    	if (useIMU) {
    		imuPID.disable();
    	}
    }

    public void disableEncoderPID() {
        encPID.disable();
    }


}