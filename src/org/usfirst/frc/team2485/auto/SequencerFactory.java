package org.usfirst.frc.team2485.auto;

import org.usfirst.frc.team2485.auto.sequenceditems.AlignToTower;
import org.usfirst.frc.team2485.auto.sequenceditems.DriveTo;
import org.usfirst.frc.team2485.auto.sequenceditems.RotateTo;
import org.usfirst.frc.team2485.auto.sequenceditems.SpinUpShooter;

public class SequencerFactory {

	public enum AutoType {
		BASIC, AUTO_AIM_NO_THANKS_LIDAR, AUTO_AIM_YES_PLEASE_LIDAR;
	}

	// Auto
	public static Sequencer createAuto(AutoType autoType) {

		switch (autoType) {
		case BASIC:
			return new Sequencer(
					new SequencedItem[] { new RotateTo(360, 10) });
			
		case AUTO_AIM_NO_THANKS_LIDAR:
			return new Sequencer(
					new SequencedItem[] { new AlignToTower() });
			
		case AUTO_AIM_YES_PLEASE_LIDAR:
			return new Sequencer(
					new SequencedItem[] { new AlignToTower(), new SpinUpShooter(3) });
		}
		return new Sequencer();

	}

	// Teleop Sequences
}