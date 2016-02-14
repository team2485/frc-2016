package org.usfirst.frc.team2485.auto;

import org.usfirst.frc.team2485.auto.sequenceditems.DriveTo;
import org.usfirst.frc.team2485.auto.sequenceditems.RotateTo;

public class SequencerFactory {

	public enum AutoType {
		BASIC
	}

	// Auto
	public static Sequencer createAuto(AutoType autoType) {

		switch (autoType) {
		case BASIC:
			return new Sequencer(
					new SequencedItem[] { new RotateTo(360, 10) });
		}
		return new Sequencer();

	}

	// Teleop Sequences
}