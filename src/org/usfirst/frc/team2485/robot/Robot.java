package org.usfirst.frc.team2485.robot;

import org.usfirst.frc.com.kauailabs.nav6.frc.IMUAdvanced;
import org.usfirst.frc.team2485.auto.Sequencer;
import org.usfirst.frc.team2485.auto.SequencerFactory;
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
