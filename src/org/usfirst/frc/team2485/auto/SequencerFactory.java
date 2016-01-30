package org.usfirst.frc.team2485.auto;

import org.usfirst.frc.team2485.auto.sequenceditems.DriveTo;
import org.usfirst.frc.team2485.auto.sequenceditems.RotateTo;

public class SequencerFactory {
	public static Sequencer createAuto() {
		
		return new Sequencer(new SequencedItem[] {
				new RotateTo(30)
		});
		
	}
}
