package org.usfirst.frc.team2485.util;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * Represents a fake sensor
 * @author Jeremy McCulloch
 * @see DummyOutput
 */
public class DummyInput implements PIDSource {

	private double input;
	private PIDSourceType pidSource;
	
	public DummyInput(PIDSourceType pidSource) {
		this.pidSource = pidSource;
	}
	
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		this.pidSource = pidSource;
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return pidSource;
	}

	@Override
	public double pidGet() {
		return input;
	}

	public void set(double input) {
		this.input = input;
	}
}
