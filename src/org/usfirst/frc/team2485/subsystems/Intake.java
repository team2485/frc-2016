package org.usfirst.frc.team2485.subsystems;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.frc.team2485.robot.Constants;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.InvertedAbsoluteEncoder;
import org.usfirst.frc.team2485.util.Loggable;
import org.usfirst.frc.team2485.util.SpeedControllerWrapper;
import org.usfirst.frc.team2485.util.ThresholdHandler;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.VictorSP;

/**
 * @author Amanda Wasserman
 * @author Vicky Comunale
 * @author Nicholas Contreras
 * @author Jeremy McCulloch
 */
public class Intake implements Loggable {

	private PIDController armPID;

	private SpeedControllerWrapper armSpeedControllerWrapper;

	private VictorSP intakeVictorSP, lateralVictorSP;

	private InvertedAbsoluteEncoder absEncoder;

	public static final double ABSOLUTE_TOLERANCE = 0.01;

	public static double FLOOR_POSITION = 0.122,

	LOW_NO_INTAKE_POSITION = (FLOOR_POSITION + 0.06) % 1,
			INTAKE_POSITION = (FLOOR_POSITION + 0.09) % 1,
			PORTCULLIS_POSITION = (FLOOR_POSITION + 0.261) % 1,
			FULL_UP_POSITION = (FLOOR_POSITION + 0.320) % 1;

	public Intake() {

		this.armSpeedControllerWrapper = Hardware.intakeArmSC;

		this.intakeVictorSP = Hardware.intakeVictorSP;
		this.lateralVictorSP = Hardware.lateralVictorSP;

		this.lateralVictorSP.setInverted(true);
		this.intakeVictorSP.setInverted(false);

		this.absEncoder = Hardware.intakeAbsEncoder;

		armPID = new PIDController(ConstantsIO.kP_IntakeArm,
				ConstantsIO.kI_IntakeArm, ConstantsIO.kD_IntakeArm, absEncoder,
				armSpeedControllerWrapper, 00.0100);
		armPID.setAbsoluteTolerance(ABSOLUTE_TOLERANCE);

		armPID.setInputRange(0.0, 1.0);
		armPID.setContinuous();

		armPID.setOutputRange(-0.22, 0.55);

	}
	
	public void updateConstants() {
		armPID.setPID(ConstantsIO.kP_IntakeArm,
				ConstantsIO.kI_IntakeArm, ConstantsIO.kD_IntakeArm);
	}

	public void startRollers(double lateralValue, double intakeValue) {

		lateralVictorSP.set(lateralValue);
		intakeVictorSP.set(intakeValue);

	}

	public void stopRollers() {

		startRollers(0, 0);

	}

	public void setManual(double pwm) {

		if (armPID.isEnabled()) {
			armPID.disable();
		}

		if (pwm > 0) {
			pwm = ThresholdHandler.deadbandAndScale(pwm,
					Constants.kMoveIntakeManuallyDeadband, 0.05, 0.6);
		} else {
			pwm = ThresholdHandler.deadbandAndScale(pwm,
					Constants.kMoveIntakeManuallyDeadband, 0.00, 0.2);
		}

		double encoderPos = absEncoder.get();

		// safeguards to prevent manually driving into ground or robot
		boolean disableDownwards = false;
		if (FLOOR_POSITION > 0.1) {
			if (encoderPos < FLOOR_POSITION
					&& Math.abs(encoderPos - FLOOR_POSITION) < 0.1) {
				disableDownwards = true;
			}
		} else {
			if (encoderPos < FLOOR_POSITION
					|| Math.abs((encoderPos - 1) - FLOOR_POSITION) < 0.1) {
				disableDownwards = true;
			}
		}

		boolean disableUpwards = false;
		if (FULL_UP_POSITION < 0.9) {
			if (encoderPos > FULL_UP_POSITION
					&& Math.abs(encoderPos - FULL_UP_POSITION) < 0.1) {
				disableUpwards = true;
			}
		} else {
			if (encoderPos > FULL_UP_POSITION
					|| Math.abs((encoderPos + 1) - FULL_UP_POSITION) < 0.1) {
				disableUpwards = true;
			}
		}

		if (pwm < 0) {
			if (!disableDownwards) {
				armSpeedControllerWrapper.set(pwm);
			} else {
				armSpeedControllerWrapper.set(0);
			}
		} else {
			if (disableUpwards) {
				armSpeedControllerWrapper.set(0);
			} else {
				armSpeedControllerWrapper.set(pwm);
			}
		}
	}

	public void setSetpoint(double setpoint) {

		armPID.enable();
		armPID.setSetpoint(setpoint);

	}

	public double getCurrentPosition() {
		return absEncoder.get();
	}

	public double getSetpoint() {
		return armPID.getSetpoint();
	}

	/**
	 * Sets setpoint and turns rollers on or off
	 * @param setpoint setpoint for arm
	 * @param rollersOn whether rollers should be running
	 */
	public void setSetpoint(double setpoint, boolean rollersOn) {

		setSetpoint(setpoint);

		if (rollersOn) {
			startRollers(ConstantsIO.kLateralRollerSpeed,
					ConstantsIO.kIntakeRollerSpeed);
		} else {
			stopRollers();
		}
	}

	public boolean isPIDEnabled() {
		return armPID.isEnabled();
	}

	@Override
	public Map<String, Object> getLogData() {

		Map<String, Object> logData = new HashMap<String, Object>();

		logData.put("Name", "Intake");
		logData.put("Intake Roller Speed", intakeVictorSP.get());
		logData.put("Lateral Roller Speed", lateralVictorSP.get());
		logData.put("Arm Setpoint", armPID.getSetpoint());
		logData.put("Arm Position", absEncoder.get());
		logData.put("Arm Motor PWM", armSpeedControllerWrapper.get());
		logData.put("Arm PID isOnTarget", armPID.onTarget());

		return logData;

	}

}
