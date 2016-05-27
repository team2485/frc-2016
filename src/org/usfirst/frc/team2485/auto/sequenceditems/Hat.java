package org.usfirst.frc.team2485.auto.sequenceditems;

import org.usfirst.frc.team2485.auto.SequencedItem;
import org.usfirst.frc.team2485.robot.Constants;
import org.usfirst.frc.team2485.robot.Hardware;
import org.usfirst.frc.team2485.subsystems.BoulderStager.StagerPosition;

/**
 * SequencedItem simlar to pressing the hat switch on the joystick. Preps for defenses. 
 * @author Jeremy McCulloch
 */
public class Hat implements SequencedItem {
	
	private boolean start;
	
	/**
	 * SequencedItem simlar to pressing the hat switch on the joystick. Preps for defenses.
	 * @param start true if beginning to cross defense, false if finishing crossing
	 */
	public Hat(boolean start) {
		this.start = start;
	}
	
	@Override
	public void run() {
		
		if (start) {
			
			if (Hardware.intake.isPIDEnabled()) {
				Hardware.intake.setManual(0); // disables PID
			}
			Hardware.intakeArmSC.set(Constants.kHatPowerValue); // use SC instead of subsystem because setManual deadbands and scales
			Hardware.boulderStager.setPosition(StagerPosition.SHOOTING);
			
		} else {
			
			Hardware.intake.setSetpoint(Hardware.intake.getCurrentPosition()); 
			Hardware.boulderStager.setPosition(StagerPosition.NEUTRAL);
			// sets arm position to what it currently is and terminates (almost) immediately
			
		}
		
	}

	@Override
	public double duration() {
		return 0.05;
	}

}
