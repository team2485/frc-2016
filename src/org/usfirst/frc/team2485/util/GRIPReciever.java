package org.usfirst.frc.team2485.util;

import java.util.HashMap;
import java.util.Set;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import edu.wpi.first.wpilibj.vision.AxisCamera.ExposureControl;
import edu.wpi.first.wpilibj.vision.AxisCamera.Resolution;

/**
 * Communicates with GRIP on driver station to output location of goal
 * @author Nicholas Contreras
 */

public class GRIPReciever {

	private static final double FIELD_OF_VIEW = 47.0;
	private static final int IMAGE_WIDTH = 320;
	
	private static int batterShotAlignX = 152, longShotAlignX = 155;
	
	private static double bestCenterX, bestCenterY;
	
	public static void init() { // add to SmartDashboard so they can be tuned
		SmartDashboard.putNumber("Batter Shot Alignment", batterShotAlignX);
		SmartDashboard.putNumber("Long Shot Alignment", longShotAlignX);
	}

	public static void setUpCameraSettings() {
		AxisCamera camera = new AxisCamera("10.24.85.11");

		camera.writeColorLevel(100);
		camera.writeBrightness(20);
		camera.writeResolution(Resolution.k320x240);
		camera.writeExposureControl(ExposureControl.kFlickerFree50Hz);
	}

	public static void resetCameraSettings() {
		AxisCamera camera = new AxisCamera("10.24.85.11");

		camera.writeColorLevel(50);
		camera.writeBrightness(50);
		camera.writeResolution(Resolution.k320x240);
		camera.writeExposureControl(ExposureControl.kAutomatic);
	}
	
	public static boolean isLongShot() {
		return bestCenterY > 100;
	}

	public static double getAngle() throws GRIPTargetNotFoundException {
		
		NetworkTable alignmentTable = NetworkTable.getTable("SmartDashboard");
		
		batterShotAlignX = (int) alignmentTable.getNumber("Batter Shot Alignment", batterShotAlignX);
		longShotAlignX = (int) alignmentTable.getNumber("Long Shot Alignment", longShotAlignX);
		NetworkTable table = NetworkTable.getTable("GRIP");

		ITable goalsTable = table.getSubTable("goals");

		double[] widths = goalsTable.getNumberArray("width",
				new double[] { -1000 });

		Set<String> keySet = goalsTable.getKeys();

		HashMap<String, Double[]> data = new HashMap<String, Double[]>();

		for (String curKey : keySet) {

			double[] oldDoubles = goalsTable.getNumberArray(curKey,
					new double[] { -1000 });

			Double[] newDoubles = new Double[oldDoubles.length];

			for (int i = 0; i < oldDoubles.length; i++) {
				newDoubles[i] = oldDoubles[i];
			}

			data.put(curKey, newDoubles);
		}

		Double[] centerXs = data.get("centerX");

		int widest = -1;
		double widestWidth = -1;

		for (int i = 0; i < widths.length; i++) {

			if (widestWidth < widths[i]) {
				widest = i;
				widestWidth = widths[i];
			}
		}

		if (widest == -1 || widest >= centerXs.length) {
			throw new GRIPTargetNotFoundException("No High-Goal Found");
		}

		// first finds widest to determine if longshot
		bestCenterX = centerXs[widest];
		bestCenterY = data.get("centerY")[widest];

		double currentCenterValue = batterShotAlignX;
		if(isLongShot())
			currentCenterValue = longShotAlignX; 
		
		
		//then finds closest to prevent GRIP from switching between goals
		int closest = 0;

		for (int i = 0; i < centerXs.length; i++) {
			
			if (Math.abs(centerXs[i] - currentCenterValue) <
					Math.abs(centerXs[closest] - currentCenterValue)) {
				closest = i;
			}
		}

	

		bestCenterX = centerXs[closest];
		bestCenterY = data.get("centerY")[closest];
		


		double angle = FIELD_OF_VIEW * (bestCenterX - currentCenterValue) / IMAGE_WIDTH;
		
		return angle;

	}
	
	@SuppressWarnings("serial")
	public static class GRIPTargetNotFoundException extends Exception {

		GRIPTargetNotFoundException(String message) {
			super(message);
		}
	}
}
