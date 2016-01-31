package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.auto.SequencerFactory;
import org.usfirst.frc.team2485.subsystems.DriveTrain;
import org.usfirst.frc.team2485.util.ConstantsIO;
import org.usfirst.frc.team2485.util.Controllers;
import org.usfirst.frc.team2485.util.Logger;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Aidan Fay
 * @author Ben Clark
 * @author Anoushka Bose
 * @author Michael Maunu
 * @author Patrick Wamsley
 */
public class Robot extends IterativeRobot {
	
//	private SpeedController leftDriveSC1, leftDriveSC2, leftDriveSC3, rightDriveSC1, rightDriveSC2, rightDriveSC3;
//	private Encoder driveEncoder;
	
	Sequencer autonomousSequencer;
	
	public void robotInit() {
		new Hardware();
		Controllers.set(new Joystick(0),new Joystick(1));
		
		Logger.getInstance().addLoggable(Hardware.driveTrain);

//		leftDriveSC1 = new SpeedControllerWrapper(new VictorSP(6), 0);
//		leftDriveSC2 = new SpeedControllerWrapper(new VictorSP(5), 0);
//		leftDriveSC3 = new SpeedControllerWrapper(new VictorSP(1), 0);
//		rightDriveSC1 = new SpeedControllerWrapper(new VictorSP(0), 0);

//		driveEncoder = new Encoder(0, 0);
//		driveEncoder.setDistancePerPulse(Math.PI*2*WHEEL_RADIUS_INCHES / 250.0);
		
		System.out.println("initialized");
	}

	public void autonomousInit() {
		
		resetAndDisableSystems();
		
		autonomousSequencer = SequencerFactory.createAuto();
				
	}

	public void autonomousPeriodic() {
		if (autonomousSequencer != null) {
			if (autonomousSequencer.run()) {
				autonomousSequencer = null;
			}
		}		
//		SmartDashboard.putData("PIDController", Hardware.driveTrain.encPID);
//		System.out.println(((PIDController) SmartDashboard.getData("PIDController")).getP());
		updateDashboard();
	}

	public void teleopInit() {
		
		resetAndDisableSystems();
	}

	public void teleopPeriodic() {
		
		System.out.println(ConstantsIO.data.toString());
		
		//Negative on Y to invert throttle
		Hardware.driveTrain.warlordDrive(
                -Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0),
                 Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0));

        // Quick turn
        if (Controllers.getButton(Controllers.XBOX_BTN_RBUMP)) {
        	Hardware.driveTrain.setQuickTurn(true);
        } else {
        	Hardware.driveTrain.setQuickTurn(false);
        }

        if (Controllers.getButton(Controllers.XBOX_BTN_LBUMP)) {
        	Hardware.driveTrain.setHighSpeed();
        } else if (Controllers.getAxis(Controllers.XBOX_AXIS_LTRIGGER) > 0.5) {
			Hardware.driveTrain.setLowSpeed(); 	
        } else { 
        	Hardware.driveTrain.setNormalSpeed();
        }
		
        System.out.println("Rate: " + Hardware.leftDriveEnc.getRate());
        
//		leftDriveSC1.set(Controllers.getAxis(Controllers.XBOX_AXIS_LY));
//		leftDriveSC2.set(Controllers.getAxis(Controllers.XBOX_AXIS_LY));
//		leftDriveSC3.set(Controllers.getAxis(Controllers.XBOX_AXIS_LY));
//		rightDriveSC1.set(Controllers.getAxis(Controllers.XBOX_AXIS_LY));
//		
        SmartDashboard.putNumber("Current Slot 5", Hardware.battery.getCurrent(5));
        SmartDashboard.putNumber("Total Current", Hardware.battery.getTotalCurrent());
        
    	updateDashboard();
    	Logger.getInstance().logAll();
	}

	public void disabledInit() {
		resetAndDisableSystems();
	}

	public void disabledPeriodic() {

		updateDashboard();
	}
	
	public void testInit() {
		
		resetAndDisableSystems();
				
	}
	
	public void testPeriodic() {
		
		LiveWindow.run();

	}

	private void resetAndDisableSystems() {

	}
	
	public void updateDashboard() {

//		SmartDashboard.putNumber("Battery", DriverStation.getInstance().getBatteryVoltage());
	}
}

