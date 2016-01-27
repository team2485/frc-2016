package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.com.kauailabs.nav6.frc.IMUAdvanced;
import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.auto.SequencerFactory;
import org.usfirst.frc.team2485.subsystems.DriveTrain;
import org.usfirst.frc.team2485.util.*; 

import edu.wpi.first.wpilibj.*; 
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Aidan Fay
 * @author Ben Clark
 * @author Anoushka Bose
 * @author Michael Maunu
 * @author Patrick Wamsley
 */
public class Robot extends IterativeRobot {



	public void robotInit() {
		new Hardware();
		Controllers.set(new Joystick(0),new Joystick(1),new Joystick(2));
		
		System.out.println("initialized");
	}

	public void autonomousInit() {
		
		resetAndDisableSystems();
	}

	public void autonomousPeriodic() {

		updateDashboard();
	}

	public void teleopInit() {
		
		resetAndDisableSystems();
	}

	public void teleopPeriodic() {
    
		Hardware.driveTrain.warlordDrive(
                Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0.2f),
                Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0.2f));

        // Quick turn
        if (Controllers.getButton(Controllers.XBOX_BTN_RBUMP)) {
        	Hardware.driveTrain.setQuickTurn(true);
        } else {
        	Hardware.driveTrain.setQuickTurn(false);
        }

        if (Controllers.getButton(Controllers.XBOX_BTN_LBUMP)) {
        	Hardware.driveTrain.setHighSpeed();
        } else {
        	Hardware.driveTrain.setNormalSpeed();
        }
		
    	updateDashboard();
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
		
	}

	private void resetAndDisableSystems() {

	}
	
	public void updateDashboard() {

		SmartDashboard.putNumber("Battery", DriverStation.getInstance().getBatteryVoltage());
	}
}
