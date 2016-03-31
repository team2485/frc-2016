package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Constants;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager.Position;

public class Hat implements SequencedItem {
	
	private boolean start;
	
	public Hat(boolean start) {

		this.start = start;
		
	}
	
	@Override
	public void run() {
		
		if (start) {
			
			if (Hardware.intake.isPIDEnabled()) {
				Hardware.intake.setManual(0); // effectively disables PID
			}
			Hardware.intakeArmSC.set(Constants.kHatPowerValue); // use SC instead of subsystem because setManual deadbands and scales
			
		} else {
			
			Hardware.intake.setSetpoint(Hardware.intake.getCurrentPosition()); 
			
			// sets arm position to what it currently is and terminates (almost) immediately
			
		}
		
	}

	@Override
	public double duration() {
		return 0.05;
	}

}
