package org.usfirst.frc.team2485.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import edu.wpi.first.wpilibj.vision.AxisCamera.ExposureControl;
import edu.wpi.first.wpilibj.vision.AxisCamera.Resolution;

/**
 * 
 * @author Nicholas Contreras
 *
 */

public class GRIPReciever {

	private static final double FIELD_OF_VIEW = 47.0;

	private static final int IMAGE_WIDTH = 320;
	private static final int IMAGE_HEIGHT = 240;

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

	public static double getAngle() throws GRIPTargetNotFoundException {

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

		double bestCenterX = centerXs[widest];
		double bestCenterY = data.get("centerY")[widest];

		// make relative to center
		// bestCenterX -= IMAGE_WIDTH / 2;
		// bestCenterY -= IMAGE_HEIGHT / 2;

		// double R = IMAGE_WIDTH / (2 * Math.sin(Math.toRadians(FIELD_OF_VIEW /
		// 2)));
		// double Z = Math.sqrt(R * R - bestCenterX * bestCenterX - bestCenterY
		// * bestCenterY);
		// angle = Math.toDegrees(Math.atan(bestCenterX / Z));

		double currentCenterValue = 158; // this is the batter shot
		
		if (bestCenterY > 120) {
			currentCenterValue = 175; /* this is the long shot from the outer
										works */
		}

		System.out.println("GRIPReciever bestCenterX: " + bestCenterX
				+ "\tbestCenterY: " + bestCenterY + "\tcurrentCenterValue: "
				+ currentCenterValue);

		double angle = FIELD_OF_VIEW * (bestCenterX - currentCenterValue) / 320;

		return angle;

	}

	public static class GRIPTargetNotFoundException extends Exception {

		GRIPTargetNotFoundException(String message) {
			super(message);
		}
	}
}
